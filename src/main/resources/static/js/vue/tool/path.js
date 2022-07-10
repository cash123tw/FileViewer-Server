import * as tool from "./type_resolver.js"

const template = `
    <div class="container rounded-2 my-3 border-2 border bg-gradient">
        <div class="row">
            <span class="justify-content-center py-2 ms-2 mt-3 col fw-bold">
                <nav class="col" style="--bs-breadcrumb-divider: '/';">
                    <ol class="breadcrumb" id="path_area">
                        <li v-for="(p,i) in path_to_list(this.path)" class="breadcrumb-item" >
                            <a @click.prevent='isEnd(i)?null:click($event)' :href="parseHref(i)" :path="parsePath(i,p)">
                                {{p}}
                            </a>
                        </li>
                    </ol>
                </nav>
            </span>
        </div>
    </div>
`

export default {
    template : template,
    props: ['path','searchActive'],
    data() {
        return {
        }
    },
    computed:{
        pathLength:{
            set(value){
                return value
            },
            get(){
                return this.path_to_list(this.path).length
            }
        }
    },
    methods:{
        path_to_list:tool.path_to_list,
        split_path(target){
            let len = target.length
            target = this.path.substr(0,this.path.indexOf(target)+len);
            if (target.endsWith('/')){
                target = target.substr(0,target.length-1)
            }
            return target;
        },
        parsePath(i,p){
            if(i==0){
                return '/';
            }
            return this.isEnd(i)?null:this.split_path(p)
        },
        parseHref(i){
            return (this.isEnd(i)?null:'#')
        },
        isEnd(i){
            return (i+1)==this.pathLength?true:false
        },
        click(evn){
            let path = evn.currentTarget.getAttribute('path');
            this.$emit('search',{path:path},'back')
        }
    }
}

