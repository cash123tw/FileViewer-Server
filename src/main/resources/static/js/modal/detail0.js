import * as util from './util.js'
import {urls} from './urls.js'

let SETTING = {};

$(window).on('load', () => {
    init({
        area: {
            tag: $('#tag_area'),
            form: $('#form'),
            download_form: {
                form: $('#download_select_area'),
                select: $('#download_select'),
                template: $('#download_template')
            },
            tag_form: {
                form: $('#tag_form'),
                new: $('#tag_new'),
                search: $('#tag_search'),
                reset: $('#tag_reset'),
                cancel: $('#tag_cancel'),
                close: $('#tag_close'),
                tagList: $('#tagList'),
                tag_search: $('#tagName'),
                typeName: $('#typeName')
            },
            tag_template: $('#tag_template')
        },
        btn: {
            edit: $('#tag_btn_edit'),
            submit: $('#submit'),
        }
    });
})

function init(set) {
    SETTING = set;
    initTagType();
    regist_downloadType();
    listen_tagForm_on_select();
    listen_tag_btn_edit();
    listen_tagForm_new_click()
    listen_tagForm_search_click();
    listen_tagForm_reset_click();
    listen_tag_click()
    listen_submit();
    listen_download();
}

function initTagType() {
    $.get('/tagType',
        function (data) {
            for (let i = 0; i < data.length; i++) {
                let tagType = util.TagType.buildTagTypeByJson(data[i]);
                let select = $(SETTING.area.tag_form.form.find('select[id="typeName"]'));
                let opt = $('<option>');
                opt.attr('value', tagType.id);
                opt.text(tagType.typeName);
                select.append(opt);
            }
        })
}

function tag_on_select() {
    let tagField = $(this);
    let active = tagField[0].dataset.active;
    if (active === 'active') {
        tagField.removeClass('active');
        tagField[0].dataset.active = '';
    } else {
        tagField[0].dataset.active = 'active';
        tagField.addClass('active');
    }
}

function tagList_on_select(k) {
    let search = SETTING.area.tag_form.tag_search;
    let opt = $(k.target).find(':selected');
    let name = opt.val();
    name = name == -1 ? '' : name;
    search.val(name);
}

function listen_tagForm_new_click() {
    try {
        let tagForm = SETTING.area.tag_form;
        let tags = SETTING.area.tag;
        let btn = tagForm.new;
        let tagList = tagForm.tagList;
        let search = tagForm.tag_search;

        btn.on('click', (target) => {
            let k1 = 'tags.id', k2 = 'tags.name';
            let id = null, name = null;
            let tmp = SETTING.area.tag_template.clone();
            tmp.removeAttr('id');
            let val = search.val();
            let opt = $(tagList.find(`option[value="${val}"]`));
            if (opt.length != 0) {
                id = opt.data.id;
                name = opt.data('name');
            } else {
                name = val;
            }

            let k3 = 'value';
            tmp.find(`input[name="${k1}"]`).attr(k3, id);
            tmp.find(`input[name="${k2}"]`).attr(k3, name);
            tmp.find(`span[name="${k2}"`).text(name);
            tmp.find('li').on('click', tag_on_select);

            tags.append(tmp);
            reset_tag_form();
            SETTING.area.tag_form.close.trigger('click');
        })
    } catch (e) {
        util.errorHandel(e);
    }
}

function listen_tagForm_search_click() {
    try {
        let btn = SETTING.area.tag_form.typeName;
        let way = urls.tag.find_by_tag_type_id;
        let url = way.url;

        btn.on('change', () => {
            let form = SETTING.area.tag_form.form;
            let tagSearch = SETTING.area.tag_form.tag_search;
            let select = $(form.find('select[name="tagType.typeName"]'));
            let opt = $(select.find(":selected"));
            let val = opt.val();
            let name = tagSearch.val();

            $.get(url.replace('id', val), {name: name}, (data) => {
                let tagList = SETTING.area.tag_form.tagList;
                let length = data.length;
                util.resetSelect(tagList);

                if (length != 0) {
                    for (let i = 0; i < length; i++) {
                        let d = data[i];
                        let name = d.name;
                        let opt = $('<option>');
                        opt.val(name);
                        opt.text(name);
                        opt.data.id = d.id;
                        opt.data('name', d.name);
                        tagList.append(opt);
                    }
                } else {
                    alert("查無相關 Tag，新增標籤請直接打上名稱按新增。")
                }
            })

            console.log(`Search TagType ID : ${val} , Name : ${name}`);
        })
    } catch (e) {
        handleException(e);
    }
}

function listen_tagForm_reset_click() {
    let btn = SETTING.area.tag_form.reset;
    btn.on('click', reset_tag_form);
}

function listen_tagForm_on_select() {
    let tagSelect = SETTING.area.tag_form.tagList;
    tagSelect.on('change', tagList_on_select);
}

function listen_tag_btn_edit() {
    try {
        let btn = SETTING.btn.edit;
        btn.on('click', () => {
            let tag = SETTING.area.tag;
            let tags = tag.find("li[data-active='active']");
            for (let i = 0; i < tags.length; i++) {
                let t = tags[i];
                $(t).parent().remove();
            }
            return false;
        })
    } catch (e) {
        util.errorHandel(e)
    }
}

function listen_tag_click() {
    try {
        let lis = SETTING.area.tag.find('li');
        for (let i = 0; i < lis.length; i++) {
            let li = $(lis[i]);
            li.on('click', tag_on_select)
        }
    } catch (e) {
        util.errorHandel(e)
    }
}

function listen_download() {
    let select = SETTING.area.download_form.select;
    let url = urls.download.get.url;
    if (select != null || select.length > 0) {
        let types = SETTING.area.download_form.donwload_type;
        for (let i = 0; i < types.length; i++) {
            let type = types[i];
            let text = type.text;
            let t = type.type;
            let tmp = select.find("form[id='download_template']").clone();
            tmp.removeAttr('hidden');
            tmp.removeAttr('id');
            tmp.find('input[type="submit"]').val(text);
            tmp.find('input[type="submit"]').removeAttr('hidden');
            if (t !== '' || t != null) {
                tmp.find('input[name="type"]').attr('value',t);
            }

            tmp.on('submit', (target) => {
                let form = $(target.target);
                form.attr("action", url);
                form.attr("method", "get");
            })

            select.append(tmp);
        }

    }
}

function listen_submit() {
    let target = SETTING.btn.submit;
    target.on('click', submit);
}

function submit(k) {
    let form = SETTING.area.form;
    let tagKey = 'tags.name';
    let inputs = form.find('input');
    let length = inputs.length;
    try {
        let data = new FormData;
        let tags = [];
        for (let i = 0; i < length; i++) {
            let input = $(inputs[i]);
            let name = input.attr('name');
            let value = input.val();

            if (name === 'file_name') {
                if (value.match("[\\S]*[\\\\/:*?\"<>|.][\\S]*")) {
                    alert("名稱中不能包含以下字元 /\\:*?\"<>|.");
                    throw new Error("名稱中不能包含以下字元 /\\:*?\"<>|.");
                }
            }
            if (value == null) {
                continue;
            }
            if (name.startsWith("tags")) {
                if (name === tagKey) {
                    tags.push(value);
                }
            } else if (name === 'file') {
                let file = input[0].files;
                if (file.length > 0) {
                    data.append('file', file[0]);
                } else {
                    alert("請選擇檔案")
                    throw new Error();
                }
            } else {
                data.append(name, value);
            }
        }
        data.append(tagKey, tags);
        let url, method;
        url = urls.upload.update.url;
        method = form.attr("act");

        $.ajax({
            url: url,
            type: method,
            data: data,
            contentType: false,
            processData: false,
            success: (data) => {
                let id = data.id;
                if (id != null) {
                    let url = urls.detail.findOne.url;
                    location.href = url.concat(id);
                } else {
                    location.reload();
                }
            },
            error: util.errorHandel
        })
    } catch (e) {
        util.errorHandel(e);
    }

    return false;
}

function reset_tag_form() {
    let form = SETTING.area.tag_form.form;
    let tagList = SETTING.area.tag_form.tagList;
    let search = SETTING.area.tag_form.tag_search;
    search.val(null);
    let select = $(form.find('select[name="tagType.typeName"]'));
    util.resetSelect(tagList);
    util.removeSelect(select);
}

function regist_downloadType() {
    SETTING.area.download_form.donwload_type =
        [
            {text: "直接下載"},
            {text: "PDF檢視", type: "pdf"},
            {text: "文字檔案檢視", type: "txt"},
        ]

}

function checkNull(target, defaultValue) {
    return target == null ? defaultValue : target;
}

function handleException(e) {
    console.log(e);
}

