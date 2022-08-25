export const urls = {
    'explore': {
        listFile: {method: 'get', url: '/explore'},
        search: {method: 'post', url: '/explore/findByParam'},
        back: {method: 'get', url: '/explore/back'}
    },
    fileType:{
        findAll:{method:'get',url:'/fileType'},
    },
    detail: {
        findOne: {method: 'get', url: '/explore/detail/'},
        newOne: {method: 'get', url: '/explore/detail/'}
    },
    log:{
      regist:{method:'post',url:'/log/regist'},
      signIn:{method:'post',url:'/log/signIn'},
      checkExists:{method:'get',url:'/log/checkUsername'},
    },
    'tag': {
        find: '/tag/find',
        find_by_tag_type_id: {method: 'get', url: '/tag/relateTagType/id'},
        getAll: {method: 'get', url: '/tag0'},
        create: {method: 'post', url: '/tag0'},
        update: {method: 'put', url: '/tag0'},
        delete: {method: 'delete', url: '/tag0'}
    },
    'tagType': {
        findAll: {method: 'get', url: '/tagType'},
        new: {method: 'post', url: '/tagType'},
        edit: {method: 'put', url: '/tagType'}
    },
    'view': {
        explore: '/explore/',
        tag: '/tag0/',
        tagType: '/tagType/',
        userDetail: '/user',
        logout: '/logout',
        login: '/log/login',
        regist: '/log/regist',
        admin_user:'/admin/user_info',
    },
    directory:{
        addDirectory:{method:'post',url:'/directory/addDirectory'}
    },
    upload: {
        new: {method: 'post', url: '/file'},
        update: {method: 'put', url: '/file'},
        delete: {method: 'delete', url: '/file'},
    },
    download: {
        get: {method:'get',url:'/io'}
    },
    admin:{
        getAllUser:{method:'get',url:'/admin/user_info/all'},
        updateUser:{method:'put',url:'/admin/user_info/'},
        roles:{method:'get',url:'/admin/user_info/'}
    },
    user:{
        detail:{method:'get',url:'/user/'},
        updatePassword:{method:'put',url:'/user/'}
    }
}

