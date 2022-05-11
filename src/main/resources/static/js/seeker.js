//Path will set here dynamic when explore different directory.
let root_path;
let content_area;
let urls;
let name_listen_btn_area;
let name_search_area;
let path_map;
let NOW_OPT = '';
let SETTING = {}
const next = "next";
const back = "back";
const find_one = "find_one";
//[search or list ]
const OPT_SEARCH = 'SEARCH';
const OPT_LIST = 'LIST';

function init_seeker(area,
                     list_file_url,
                     search_file_url,
                     back_url,
                     find_one_url,
                     name_btn_area,
                     search_area,
                     setting) {
    content_area = area;
    urls = {
        list_file: list_file_url,
        search_file: search_file_url,
        back: back_url,
        find_one: find_one_url
    };
    name_listen_btn_area = name_btn_area;
    name_search_area = search_area;
    root_path = '';
    SETTING = setting;
    $(name_search_area).find('button[id=submit]').on('click', search_button_click);
    listFile($('<button>'));
}

function listen_button_click() {
    let area = $(name_listen_btn_area);
    area.on('click', click)
    console.log('Have ' + area.length + ' button is listening');
}

function search_button_click() {
    let search_element
        = $(name_search_area);
    let input
        = search_element.find('input[name=fileName]');
    let data
        = make_form_data();
    data.append('fileName', input.attr('value'));
    data.append('path', root_path);
    NOW_OPT = OPT_SEARCH;
    sendAjaxToServer(urls['search_file'], data, seeker_success, error);
}

function click() {
    let target = $(this);
    let type = checkGoto(target);
    if (type === next) {
        listFile(target)
    } else if (type === back) {
        back0(target);
    } else if (type === find_one) {
        open_new_one(target);
    }
}

function sendAjaxToServer(url, formData, success, error) {

    $.ajax({
        url: url,
        data: formData,
        contentType: false,
        processData: false,
        cache: false,
        type: 'post',
        success: success,
        error: error
    });
}

function mkBtn() {
    let btn = $('<button>');
    return btn;
}

function listFile(target) {
    let file_name
        = target.attr("file_name");
    let data
        = make_form_data();
    data.append("path", file_name == null ? 1 : file_name);
    NOW_OPT = OPT_LIST;
    sendAjaxToServer(urls['list_file'], data, seeker_success, error)
}

function back0(target) {
    let path
        = concatPath('');
    let data
        = make_form_data();
    data.append('path', path);
    NOW_OPT = OPT_LIST;
    sendAjaxToServer(urls['back'], data, seeker_success, error)
}

function back_listen() {
    let target = $(this);
    let path = target.attr('path');
    if (path == null || path === '') {
        path = '';
    }
    back_by_path(path);
}

function back_by_path(path) {
    let data = make_form_data();
    let url = SETTING['back_by_path']
    data.append('path', path);
    NOW_OPT = OPT_LIST;
    sendAjaxToServer(url, data, seeker_success, error)
}

function concatPath(path) {
    path
        = ((root_path == null) ? '' : root_path)
        .concat(path == null ? '' : path);

    return path;
}

function seeker_success(data) {
    let result
        = data.result.toLowerCase();
    let message
        = data.message;

    if (result === "success") {
        root_path
            = message;
        success_good(data.obj);
        listen_button_click();
    } else {
        success_fail(data);
    }
}

function success_good(file_list_ar) {
    re_render();
    let file_path = '';
    content_area.append(forBackBtn());
    content_area.append($('<br>'))

    for (let file_detail in file_list_ar) {
        file_detail = file_list_ar[file_detail];
        file_path = file_detail.file_path;
        let dir = file_detail.dir;
        let file_id = file_detail.file_id;
        let file_name = file_detail.file_name;
        let file_type = file_detail.file_types;

        for (let type in file_type) {
            type = file_type[type].typeName;
            let btn = mkBtn();
            btn.attr('file_id', file_id);
            btn.attr('file_name', file_id);
            btn.attr('file_type', type);
            btn.text(file_name + '.' + type);
            if (type === 'directory') {
                setGoto(btn, next);
            } else {
                setGoto(btn, find_one);
            }
            render_to_page(btn);
        }
    }

    render_to_path(file_path);
}

function success_fail(data) {
    msg1 = data.obj;
    msg2 = data.message;
    alert(msg2.concat("\n詳情請前往控制台 [Ctrl+Shift+I]"));
    console.log(msg1);
}

function error(data) {
    let text = data.responseText;
    if (text != null) {
        console.log(text);
        alert(text)
    }else{
        alert("請聯絡開發人員，或到後台查看資訊")
    }
}

function forBackBtn() {
    let btn = mkBtn();
    btn.text('上一頁');
    setGoto(btn, back);
    return btn;
}

function setGoto(btn, type) {
    btn.attr('goto', type);
}

function make_form_data() {
    return new FormData();
}

function checkGoto(btn) {
    let type = btn.attr('goto');
    if (type === next) {
        return next;
    } else if (type === back) {
        return back;
    } else if (type === find_one) {
        return find_one;
    }
}

function re_render() {
    content_area.html('');
    path_map = {};
}

function render_to_page(target) {
    // if (area == null) {
    //     let p = $('<p>');
    //     area = $('<div>')
    //     p.text('路徑 : ');
    //     p.css('color', 'green')
    //     p.css('font-size', '16px')
    //     p.append(result_path(path));
    //     area.append(p);
    //     area.append($('<br>'));
    //     path_map[path] = area;
    //     content_area.append(area);
    // }

    content_area.append(target);
    content_area.append($('<br>'));
}

function render_to_path(path) {
    let area = SETTING['path_area'];
    area.html('');
    if (NOW_OPT === OPT_SEARCH) {
        let a = mk_a('ROOT');
        a.attr('href', '#');
        let li = mk_li(a);
        area.append(li);
        a.on('click', back_listen);
    } else if (NOW_OPT === OPT_LIST) {
        let list = result_path(path);
        for (let index of list) {
            area.append(index);
        }
    }
}

function open_new_one(target) {
    let file_id
        = target.attr('file_id');
    let url
        = urls[find_one];
    url
        = url.concat("/", file_id);
    location.href = url;
}