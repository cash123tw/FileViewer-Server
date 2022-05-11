let TAG_TYPE_SETTING;

function tag_type_init(set) {
    TAG_TYPE_SETTING = set;
    TAG_TYPE_SETTING.button.reset.on('click', tag_type_reset_data);
    TAG_TYPE_SETTING.button.submit.on('click', tag_type_submit_click);
    let url = URLS.tagType.findAll;
    send_Ajax(null, url.method, url.url, tag_type_success_0, error, "application/json");
}

function tag_type_success_0(data, stat, xhr) {
    let status = xhr.status;
    if (status == 200) {
        let data_part = TAG_TYPE_SETTING.template.data;
        let data_area = TAG_TYPE_SETTING.area.data;
        let prefix = TAG_TYPE_SETTING.prefix;
        tag_type_reset_data_table();
        for (let i = 0; i < data.length; i++) {
            let d = data[i];
            let target = data_part.clone();
            let ths = target.find("th");
            let id ;
            for (let j = 0; j < ths.length; j++) {
                let th = $(ths[j]);
                let name = th.attr('name');
                let value = d[name];
                if(name === 'id'){
                    id = value;
                }

                if (value != null) {
                    th.attr('value', value);
                    th.text(value);
                } else if (name = 'act') {
                    let btn = th.find("button[name=edit]");
                    let btn2 = th.find("button[name=relate_tag]");
                    btn.on('click', tag_type_edit_click);
                    btn2.on('click', tag_type_relate_tag_click);
                }
            }
            target.attr('id', prefix.concat(id));
            data_area.append(target);
        }
        tag_type_reset_data();
    } else if (status >= 400 && status < 500) {
        alert(data);
    }
}

function tag_type_success_relate_tag(data) {
    let prefix = TAG_TYPE_SETTING.prefix;
    let data_area = TAG_TYPE_SETTING.area.data;
    let tr, ul, li;
    for (let i = 0; i < data.length; i++) {
        let d = data[i];
        let tag_type_id = d.tagType.id;
        let name = d.name;
        let id = d.id;
        if (tr == null) {
            tr = data_area.find(`tr[id="${prefix}${tag_type_id}"]`);
            ul = tr.find(`ul[name="relate_tag_area"]`);
            li = ul.find(`li[name=template]`);
            li.attr('hidden', '');
        }
        let tmp = li.clone();
        let a = tmp.find('a');
        tmp.attr('name', 'tag');
        tmp.removeAttr('hidden');
        a.attr('value', id);
        a.text(name);
        ul.append(tmp);
    }
    if (ul != null) {
        tr.find('button[name="relate_tag"]').attr('relate_tag','true')
    }
}

function tag_type_reset_data() {
    let button = $(this);
    button.focusout();
    let form = TAG_TYPE_SETTING.area.form;
    let inputs = form.find('input');
    for (let i = 0; i < inputs.length; i++) {
        let input = $(inputs[i]);
        input.attr('value', '');
    }
    tag_type_btn_change_submit();
}

function tag_type_edit_click() {
    let click_button = $(this);
    let form = TAG_TYPE_SETTING.area.form;
    let tr = click_button.parents("tr");
    let inputs = form.find('input');

    for (let i = 0; i < inputs.length; i++) {
        let input = $(inputs[i]);
        let name = input.attr('name');
        let find = `th[name='${name}']`;
        let th = tr.find(find);
        let value = th.attr('value');
        input.attr('value', value == null ? '' : value);
    }
    tag_type_btn_change_edit();
    window.scroll(form.parent('div').position());
}

function tag_type_relate_tag_click() {
    let btn = $(this);
    let url = URLS.tag.find_by_tag_type_id.url;
    let method = URLS.tag.find_by_tag_type_id.method;
    if (btn.attr('relate_tag') === 'false') {
        let tr = btn.parents('tr');
        let id = tr.find('th[name="id"]').attr('value');
        url = url.replace('id', id);
        send_Ajax(null, method, url, tag_type_success_relate_tag, error);
    }
}

function tag_type_btn_change_submit() {
    let btn = TAG_TYPE_SETTING.button.submit;
    btn.text('送出');
    btn.attr('act', TAG_TYPE_SETTING.act.post);
    btn.removeClass('btn-warning');
    btn.addClass('btn-success');
}

function tag_type_btn_change_edit() {
    let btn = TAG_TYPE_SETTING.button.submit;
    btn.text('更改');
    btn.attr('act', TAG_TYPE_SETTING.act.put);
    btn.addClass('btn-warning');
    btn.removeClass('btn-success');
}

function tag_type_submit_click() {
    let button = $(this);
    let act = button.attr('act');
    let url = URLS.tagType;
    let data = [];
    data.push(wrap_by_form(TAG_TYPE_SETTING.area.form));

    switch (act) {
        case TAG_TYPE_SETTING.act.post:
            send_Ajax(data, url.new.method, url.new.url, tag_type_success_0, error, "application/json");
            break;
        case TAG_TYPE_SETTING.act.put:
            send_Ajax(data, url.edit.method, url.edit.url, tag_type_success_0, error, 'application/json');
            break;
        default:
            alert("ACT Type Error");
    }
}

function tag_type_reset_data_table() {
    let data_area = TAG_TYPE_SETTING.area.data;
    data_area.html('');
}