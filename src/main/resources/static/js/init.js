const URLS = {
    'explore': {
        listFile: {method: 'get', url: '/explore'},
        search: {method: 'get', url: '/explore/search'},
        back: {method: 'get', url: 'explore/back'}
    },
    detail: {
        findOne: { method : 'get', url: '/explore/detail/'}
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
        findAll: {method: 'get', 'url': '/tagType'},
        new: {method: 'post', url: '/tagType'},
        edit: {method: 'put', url: '/tagType'}
    },
    'view': {
        explore: '/explore/',
        tag: '/tag0/',
        tagType: '/tagType/'
    }
}