export function result_tag(tag) {
    let type = tag.tagType;
    let type_name, type_id;
    let text = tag.name;

    if (type != null) {
        type_name = type.typeName;
        type_id = type.id;
        text = type_name.concat(':', text);
    }

    let a = mk_a(text, tag.id, null);
    a.attr('type_id', type_id);

    return a;
}

export function result_path(path) {

    let path_list = path_to_list(path);
    let length = path_list.length;
    let result = [];
    let tmp = '';
    for (let i = 0; i < length; i++) {
        let text = path_list[i];
        if (text !== 'ROOT') {
            tmp = tmp.concat(text);
        }

        let a = mk_a(text);
        a.attr('path', tmp);
        let li = mk_li(a);

        if (length == i + 1) {
            li.addClass('active')
        } else {
            a.attr('href', '#');
        }
        result.push(li);
        tmp = tmp.concat('/');
    }

    return result;
}

export function result_type(type) {
    let type_id = type.id;
    let type_name = type.typeName;

    let a = mk_a(type_name, type_id, null);
    return a;
}

export function mk_a(text, value, css) {
    let a = $('<a>');
    a.text(text);
    a.attr('value', value);
    a.attr('css', css);
    return a;
}

export function mk_li(a) {
    let li = $('<li>');
    li.append(a);
    addPathCss(li);
    return li;
}

export function addPathCss(target) {
    target.addClass('breadcrumb-item')
}

export function path_to_list(path) {
    let split = '/';
    let header = '', body = path;
    let path_list = [];
    path.replaceAll('\\', split)

    if (path.includes('//')) {
        let tmp = path.split('//');
        path_list.push(tmp[0]);
        body = tmp[1];
    } else {
        path_list.push("ROOT");
    }

    let list = body.split(split);
    for (const i in list) {
        let target = list[i];

        if (path.startsWith(split) && i == 0) {
            continue;
        } else if (target !== '' && target != null) {
            path_list.push(target);
        }
    }

    return path_list;
}
