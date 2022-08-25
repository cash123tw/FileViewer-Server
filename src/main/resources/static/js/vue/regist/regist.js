import * as Vue from '../vue.js';
import '../axios.min.js'
import '../../init.js'

const app = Vue.createApp({
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
        formSubmit: async function () {
            if (this.check()) {
                try {
                    //check username exists;
                    let res0 = await axios.get(URLS.log.checkExists.url, {
                        params: {
                            userName: this.user.username
                        }
                    });
                    if (res0.status == 200) {
                        if (res0.data.exists) {
                            alert("帳號已存在")
                            return;
                        }
                    } else {
                        console.log(res0.status)
                        alert("出錯囉!!");
                        return;
                    }

                    //register account
                    let res = await axios.post(URLS.log.regist.url, this.user);
                    let data = res.data;
                    alert(data);
                    let toGo = res.headers.Location;
                    window.location = (toGo != null ? toGo : "/");
                    
                } catch (e) {
                    console.log(e);
                    alert("服務器出錯!!")
                }
            }
        },
        check: function () {
            if (empty(this.user.realName)) {
                this.warning = "名字未輸入";
                return false;
            }
            if (empty(this.user.username)) {
                this.warning = "帳號未輸入";
                return false;
            }
            if (empty(this.user.password)) {
                this.warning = "密碼未輸入";
                return false;
            }
            if (this.user.password != this.user.passwordCheck) {
                this.warning = "密碼不一樣";
                return false;
            }
            return true;
        }
    },
});

app.mount('#app')

function empty(str) {
    if (str === '') {
        return true;
    }

    if (str == null) {
        return true;
    }
}