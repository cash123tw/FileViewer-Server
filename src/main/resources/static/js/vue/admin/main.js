import * as Vue from '../vue.js';
import '../axios.min.js'
import { urls } from '../../modal/urls.js'
import 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js'
import { getUserDetail } from '../user/detail.js';

const localhost = "//localhost:9090"

const app = Vue.createApp({
    data() {
        return {
            users: new Map(),
            totlePage: 1,
            page: 0,
            size: 10,
            setting_roles: [],
            modal: {
                currentUser: {}
            },
            userInfo: {}
        }
    },
    methods: {
        getAllUser: getAllUser,
        Update: Update,
        Reset: Reset,
        Edit: Edit,
        getAllRole: getAllRole,
        SwitchSize: SwitchSize,
        getUserInfo: getUserInfo,
    },
    mounted() {
        this.getAllRole();
        this.getAllUser();
    },
})

app.mount("#app");

function getAllUser() {
    $.ajax({
        url: localhost + urls.admin.getAllUser.url,
        type: urls.admin.getAllUser.method,
        async: false,
        data: {
            page: this.page,
            size: this.size
        },
        success: (pagableData) => {
            let data = pagableData.content;
            this.totlePage = pagableData.totalPages;
            this.users.clear();

            for (let i = 0; i < data.length; i++) {
                let user = data[i];
                let userInfo = this.getUserInfo();
                if (user.username == userInfo.username) {
                    user.nowerUser = true;
                }
                this.users.set(user.id, user);
            }
        },
        error: function (e) {
            console.log(e);
        }
    })
}

function Update() {
    let s = { ...this.modal.currentUser }

    $.ajax({
        url: localhost + urls.admin.updateUser.url,
        type: urls.admin.updateUser.method,
        contentType: "application/json",
        async: false,
        data: JSON.stringify(s),
        success: (data) => {
            this.users.set(data.id, data)
            $('#modalClose').click();
        },
        error: (e) => {
            console.log(e)
        }
    })
}

function Reset() {
    this.modal.currentUser = {}
}

function Edit(user) {
    let copy = JSON.stringify(user);
    let newUser = JSON.parse(copy);
    this.modal.currentUser = newUser;
}

function getAllRole() {
    $.get(localhost + urls.admin.roles.url,
        { dataType: "json", async: false },
        (data) => {
            this.setting_roles = data;
        })
}

function SwitchSize() {
    this.page = 0;
    this.getAllUser();
}

function getUserInfo() {
    if (this.userInfo.id == null) {
        let res = getUserDetail();
        this.userInfo = JSON.parse(res.responseText);
        
    }
    return this.userInfo;
}
