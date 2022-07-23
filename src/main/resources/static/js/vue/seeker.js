import * as Vue from '../vue/vue.js';
import "../vue/vue-cookies.js";
import '../vue/axios.min.js';
import {urls} from '../modal/urls.js'
import path from './tool/path.js';
import SeekerListTable from './tool/SeekerListTable.js'
import SeekerSearchFile from './tool/SeekerSearchFile.js'
import SeekerAddDirectory from './tool/SeekerAddDirectory.js'

const localhost = ''
const {createApp} = Vue

const app = createApp({
    data() {
        return {
            path: '',
            files: null,
            active: false,
            searchStat: {
                active: false,
                param: {page: 0},
                result: [],
                scrollRefresh: false,
                end: false,
            },
            listBuffer: new Map()
        }
    },
    provide() {
        return {
            SearchActive: Vue.computed(() => {
                return this.searchStat.active
            })
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
        submitAddDirectory: submitAddDirectory,
        searchFile: searchFile,
        scrollEnd: scrollEnd,
        addWindowScrollListener:addWindowScrollListener,
        removeWindowScrollListener:removeWindowScrollListener,
    },
    mounted() {
        this.listFile({path: ''})
    },
});

app.use($cookies)
app.config.unwrapInjectedRef = true
app.component('path-area', path);
app.component('seeker-list-table', SeekerListTable);
app.component('btn-search-file', SeekerSearchFile);
app.component('seeker-add-directory', SeekerAddDirectory);

app.mount("#app");

function searchFormFlush(data) {
    if (this.searchStat.active == true) {
        data.pathName = this.searchStat.param.pathName;
    } else {
        data.pathName = this.path;
    }

    if (this.searchStat.active && this.searchStat.param === data) {
        this.path = data.pathName;
        this.files = this.searchStat.result
    } else {
        data.page = 0;
        this.searchStat.scrollRefresh = true;
        this.searchFile(data)
            .then((result) => {
                let list = result.data;

                this.searchStat.result = list;
                this.path = data.pathName;
                this.files = list;
                this.addWindowScrollListener();
            }).catch((e) => {
            console.log(e);
        });
    }
}

async function searchFile(searchParam) {
    let url = urls.explore.search;

    let result = await axios({
        method: url.method,
        url: `${localhost}${url.url}`,
        data: searchParam
    });

    this.searchStat.active = true;
    this.searchStat.param = searchParam;
    return result;
}

function searchGoBackResult() {
    this.searchFormFlush(this.searchStat.param);
}

function searchCancelStat() {
    this.listFile({path: this.path})
    this.searchStat.active = false;
    this.searchStat.scrollRefresh = true;
    this.removeWindowScrollListener();
}

function addWindowScrollListener(){
    window.addEventListener("scroll", this.scrollEnd);
}

function removeWindowScrollListener(){
    window.removeEventListener("scroll", this.scrollEnd);
}

function moveDirectory(item) {
    let id = item.id;
    let path = item.path;
    this.listFile({id: id, path: path})
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
    window.location.href = `${localhost}${urls.detail.findOne.url}${data.id}`;
}

function directUploadPage() {

    if (this.searchStat.active == true) {
        alert("搜尋狀態，請先取消。")
    } else {
        let url = urls.detail.newOne.url;
        window.location.href = `${localhost}${url}?path=${this.path}`
    }
}

async function submitAddDirectory(name) {
    let way = urls.directory.addDirectory;

    let data = new FormData();
    data.append("name", name);
    data.append("path", this.path);

    try {
        let res = await axios({
            data: data,
            url: way.url,
            method: way.method
        })

        let filePath = res.data;
        this.files.push(filePath);
        alert("創建成功!");
    } catch (e) {
        alert(e.response.data);
    }

}

function scrollEnd() {
    let Dis_Top = document.documentElement.scrollTop;
    let window_height = document.documentElement.clientHeight;
    let Dis_Scroll = document.documentElement.scrollHeight;
    let enable = Dis_Scroll != window_height;

    if (enable
        && (Dis_Scroll - 5 < window_height + Dis_Top)
        && this.searchStat.active
        && this.searchStat.scrollRefresh
        && !this.searchStat.end) {

        this.searchStat.scrollRefresh = false;
        let param = this.searchStat.param;
        param.page++;
        this.searchFile(param)
            .then((result) => {
                let list = result.data;
                this.searchStat.result = this.searchStat.result.concat(list);
                this.files = this.files.concat(list);
            }).catch((e) => {
            param.page--;
            console.log(e);
        }).finally(() => {
            this.searchStat.scrollRefresh = true;
        });
    }else if(this.searchStat.end){
        this.removeWindowScrollListener();
    }
}
