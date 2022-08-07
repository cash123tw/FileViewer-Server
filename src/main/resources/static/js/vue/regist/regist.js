import * as Vue from '../vue.js';
import '../../init.js'

let app = Vue.createApp({
    data() {
        return {
            user: {
                realName: '',
                username: '',
                password: '',
                passwordCheck: ''
            },
            warning: "",
            passwordCheck1: false,
            passwordCheck2: false,
        }
    },
    methods: {
        formSubmit: function () {
            if(this.check()){
                console.log(this.user);
            }
        },
        check: function () {
            if(empty(this.user.realName)){
                this.warning = "名字未輸入";
                return false;
            }
            if(empty(this.user.username)){
                this.warning = "帳號未輸入";
                return false;
            }
            if(empty(this.user.password)){
                this.warning = "密碼未輸入";
                return false;
            }
            if(this.user.password != this.user.passwordCheck){
                this.warning = "密碼不一樣";
                return false;
            }
            return true;
        }
    },
});

app.mount('#app')

function empty(str){
    if(str === ''){
        return true;
    }

    if(str == null){
        return true;
    }
}