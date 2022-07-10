import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import  "https://unpkg.com/vue-cookies@1.8.1/vue-cookies.js";
import { urls } from '../../../static/js/modal/urls.js'
import 'https://cdnjs.cloudflare.com/ajax/libs/axios/1.0.0-alpha.1/axios.min.js';
import path from './tool/path.js';
import SeekerListTable from './tool/SeekerListTable.js'
import SeekerSearchFile from './tool/SeekerSearchFile.js'

const localhost = '//localhost:9090'
const { createApp, onMounted } = Vue

const app = createApp({
    data() {
        return {
            path: '',
            files: null,
            active: false,
            searchStat: {
                active: false,
                param: {},
                result: [],
            },
            listBuffer: new Map()
        }
    },
    provide() {
        return {
            SearchActive: Vue.computed(() => { return this.searchStat.active })
        }
    },
    methods: {
        search: moveDirectory,
        searchFormFlush: searchFormFlush,
        listFile: ListFile,
        setCurrentStatus: setCurrentStatus,
        fileClickAdapter: fileClickAdapter,
        directUploadPage: directUploadPage,
        searchGoBackResult: searchGoBackResult,
        searchCancelStat: searchCancelStat,
    },
    mounted() {
        this.listFile({ path: '' })
    },
});

app.use($cookies)
app.config.unwrapInjectedRef = true
app.component('path-area', path);
app.component('seeker-list-table', SeekerListTable);
app.component('btn-search-file', SeekerSearchFile);

app.mount("#app");

function searchFormFlush(data) {
    let url = urls.explore.search;

    if (this.searchStat.active == true) {
        data.pathName = this.searchStat.param.pathName;
    } else {
        data.pathName = this.path;
    }

    if (this.searchStat.active && this.searchStat.param === data) {
        this.path = data.pathName;
        this.files = this.searchStat.result
    } else {
        axios({
            method: url.method,
            url: `${localhost}${url.url}`,
            data: data
        })
            .then((result) => {
                let list = result.data;
                this.searchStat.active = true;
                this.searchStat.param = data;
                this.searchStat.result = list;
                this.path = data.pathName;
                this.files = list;
            }).catch((err) => {
                console.log(err);
            });
    }
}

function searchGoBackResult() {
    this.searchFormFlush(this.searchStat.param);
}

function searchCancelStat() {
    this.listFile({ path: this.path })
    this.searchStat.active = false;
}

function moveDirectory(item) {
    let id = item.id;
    let path = item.path;
    this.listFile({ id: id, path: path })
}

function ListFile(data) {
    let path = data.path;
    path = path == null ? '' : path;

    if (this.listBuffer.has(path)) {
        this.setCurrentStatus(path, this.listBuffer.get(path));
    } else {
        axios({
            method: 'get',
            url: `${localhost}${urls.explore.listFile.url}`,
            params: {
                id: data.id,
                path: data.path
            }
        }).then((result) => {
            let list = result.data;
            this.listBuffer.set(path, list);
            this.setCurrentStatus(path, list);
        }).catch((err) => {
            console.log(err)
        });
    }
}

function setCurrentStatus(path, files) {
    this.files = files;
    this.path = path;
}

function fileClickAdapter(item, act) {
    switch (act) {
        case ('forward'):
            this.search(item)
            break;
        case 'back':
            this.search(item)
            break;
        case 'detail':
            openNew(item)
            break;
        default:
            console.log('錯誤操作')
    }
}

function openNew(data) {
    window.location.href=`${localhost}${urls.detail.findOne.url}${data.id}`;
}

function directUploadPage() {

    if (this.searchStat.active == true) {
        alert("搜尋狀態，請先取消。")
    } else {
        let url = urls.detail.newOne.url;
        window.location.href = `${localhost}${url}?path=${this.path}`
    }
}
