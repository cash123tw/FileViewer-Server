import { urls } from '../../modal/urls.js'
import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import 'https://cdnjs.cloudflare.com/ajax/libs/axios/1.0.0-alpha.1/axios.min.js';
import tagSearch from "../tool/UtilSearchTag.js"

const app = Vue.createApp({
    methods: {
        addTag: addTag,
        setTag: setTag,
        tagAreaDeleteStatTransfer:tagAreaDeleteStatTransfer,
        deleteTagSelect:deleteTagSelect,
        deleteTagSelectReset:deleteTagSelectReset
    },
    provide() {
        return {
            localhost: ""
        }
    },
    data() {
        return {
            tag: {
                name: null
            },
            filePath: {
                tags: new Map(),
            },
            tagArea: {
                deleteActive: false,
                deleteSelect: [],
            },
            select:[]
        }
    },
    computed: {
        tagClass: function () {
            return {
                'border-primary': !this.tagArea.deleteActive,
                'border-danger': this.tagArea.deleteActive,
            }
        },
        tagSelect: {
            type: Object,
            set(v) {
                if (v != null) {
                    this.tag = Object.assign({}, v);
                }
            },
            get() {
                return this.tagSelect;
            }
        },
    },
    mounted(){
        console.log()
    }
})

app.component('tag-search', tagSearch)
app.mount('#app');

function addTag() {
    let tagSelect = this.tagSelect;
    let tag = this.tag;
    let target;

    if (tagSelect != null && (tag.name === tagSelect.name)) {
        target = tagSearch;
    } else {
        target = tag;
    }

    this.filePath.tags.set(target.name, { id: target.id, name: target.name });
}

function setTag() {
    this.tag.id = null;
}

function tagAreaDeleteStatTransfer(){
    let area = this.tagArea;

    if(area.deleteActive){
        area.deleteSelect.forEach(target => {
            let name = target.name;
            this.filePath.tags.delete(name);
        });
        this.deleteTagSelectReset();    
    }else{
        this.deleteTagSelectReset();
    }
    area.deleteActive = !area.deleteActive;
}

function deleteTagSelect(tag){
    if(this.tagArea.deleteActive == true){
        tag.active = !tag.active;
    }
}

function deleteTagSelectReset(){
    this.tagArea.deleteSelect = [];
}