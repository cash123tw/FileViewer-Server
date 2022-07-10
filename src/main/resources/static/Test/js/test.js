import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import 'https://cdnjs.cloudflare.com/ajax/libs/axios/1.0.0-alpha.1/axios.min.js';

const app = Vue.createApp({
    data() {
        return {
            checked: [],
            tags: [],
            typeList:[],
            fileName: null,
            fileType: null,
            tag:{
                deleteActive:false,
                deleteSelect:[]
            }
        }
    },
    methods: {
        show() {
            console.log(this.checked)
        },
        clickCheck(l) {
            l.active = !l.active
        },
        reset() {
            this.list = null;
            this.checked = []
        },
        tagDeleteActive:tagDeleteActive,
        tagDeleteUnActive:tagDeleteUnActive
    },
})

app.mount("#app")

function tagDeleteActive(){
    this.tag.deleteActive = true;
}


function tagDeleteUnActive(){
    this.tag.deleteActive = false;
}