import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import 'https://cdnjs.cloudflare.com/ajax/libs/axios/1.0.0-alpha.1/axios.min.js';
import tag from "../../../js/vue/tool/UtilSearchTag.js";

const app = Vue.createApp({
    provide(){
        return{
            localhost:'//localhost:9090'
        }
    },
    components:{
        tag : tag
    }
})

app.mount('#app');