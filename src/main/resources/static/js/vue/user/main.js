import * as Vue from '../vue.js';
import { urls } from '../../modal/urls.js'
import 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js'
import { getUserDetail } from '../user/detail.js';

const app = Vue.createApp({
    data() {
        return {
            user:{
                username:"xxx",
                realName:"yyy",
                enabled:true,
                roles:["ADMIN"]
            },
            oldOne:'',
            newPassword:'',
            check:'',
            'check-alert':false
        }
    },
    methods:{
        cancel:cancel,
        updatePassword:updatePassword
    }
})

app.mount("#app")

function updatePassword(){
    if(this.oldOne === '' || this.oldOne == null){
        alert("密碼未輸入")
    }else if(this.newPassword == this.check && (this.newPassword!=null&&this.newPassword!=='')){
        this.cancel();
    }else{
        this['check-alert'] = true 
        alert('新密碼錯誤');
    }
}

function cancel(){
    this.oldOne = null;
    this.newPassword = null;
    this.check = null;
    this['check-alert'] = false;
    let btn = $("#cancelBtn");
    btn.click();
}