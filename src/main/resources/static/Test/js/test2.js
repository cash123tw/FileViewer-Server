import * as Vue from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';

const template = `
    <div class="bg-primary">
        <input v-for="l in list" type="checkbox" :value="l.id" v-model="ss" />
    </div>
    <button @click="this.list.push({id:this.count++,name:'A'+this.count++})">Add</button>
`

const app = Vue.createApp({
    data() {
        return {
            file:null
        }
    },
    methods: {
        show(){
            console.log(this.file)
        },
        fileUp(evn){
            this.file = evn.target.files[0]
        }
    },
    mounted() {
    },
})

app.mount('#app')