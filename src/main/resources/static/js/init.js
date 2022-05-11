const URLS = {
    'explore':{

    },
    'tag':{
        find:'/tag/find',
        find_by_tag_type_id:{method:'get',url:'/tag/relateTagType/id'},
        getAll:{method:'get',url:'/tag0'},
        create:{method:'post',url:'/tag0'},
        update:{method:'put',url:'/tag0'},
        delete:{method:'delete',url:'/tag0'}
    },
    'tagType':{
        findAll:{method:'get','url':'/tagType'},
        new:{method:'post',url:'/tagType'},
        edit:{method:'put',url:'/tagType'}
    }
}