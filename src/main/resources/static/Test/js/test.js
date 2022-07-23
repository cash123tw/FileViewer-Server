import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import 'https://cdnjs.cloudflare.com/ajax/libs/axios/1.0.0-alpha.1/axios.min.js';

const app = Vue.createApp({
    data() {
        return {
            
        }
    },
    methods: {
        post:post,
        test:function(){
            console.log('xxxx')
        }
    },
})

app.mount("#app");

async function post(){
    let res = await axios.get("//localhost:9090/explore");
    console.log(res.data);
}