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
            select:[],
            map:new Map(),
            type:{}
        }
    },
    methods: {
        show(){
            console.log(this.map)
        }
    },
    mounted() {
        this.map.set(1,{id:4,name:'A1'})
        this.map.set(2,{id:5,name:'A2'})
        this.map.set(3,{id:6,name:'A3'})
    },
})

app.component('test',{
    template:template,
    props:{
        modelValue:{
            type:Array,
            default:new Array()
        },
        getList:{
            type:String,
            default:''
        }
    },
    data() {
        return {
            count:5,
            list:[
                {id:2,name:'A2'},
                {id:3,name:'A3'},
                {id:1,name:'A1'},
            ],
        }
    },
    methods:{
        show(){
            alert('xxx')
        }
    },
    watch:{
        ss:{
            handler:function(v,v2){
                this.$emit('update:modelValue',v)
            }
        }
    }
})

app.mount('#app')