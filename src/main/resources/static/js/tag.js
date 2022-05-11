let TAG0_SETTING;

function tag_init(set) {
    TAG0_SETTING = set;
    let way = URLS.tag.getAll;
    let type_way = URLS.tagType.findAll;

    let reset = TAG0_SETTING.button.reset;
    let select_area = TAG0_SETTING.area.select;
    let submit = TAG0_SETTING.button.submit;
    submit.on('click', tag_submit_click);
    reset.on('click', tag_reset_click);
    select_area.on('change', tag_select_rerender_by_selected);

    send_Ajax(null, type_way.method, type_way.url, tag_select_area_success, error);
    send_Ajax(null, way.method, way.url, tag_success, error);
}

function init_form_stat() {
    let form = TAG0_SETTING.area.form;
    let act;
    let data_exists = form.find('input[name="id"]');
    let value = data_exists.attr('value');
    if (value != null && value !== '') {
        act = TAG0_SETTING.act.put;
        tag_btn_yellow();
        tag_select_rerender_by_id();
    } else {
        act = TAG0_SETTING.act.post;
        tag_btn_blue();
        tag_select_rerender_by_id();
    }
    TAG0_SETTING.button.submit.attr('act', act);
}

function tag_select_rerender_by_id() {
    let selectArea = TAG0_SETTING.area.select;
    let selectInput = TAG0_SETTING.area.selectInput;
    let id = selectInput.attr('value');
    reset_selected(selectArea);
    if (id == null) {
        selectArea.find('option[name="default"]').attr('selected', '');
    } else {
        selectArea.find(`option[value="${id}"]`).attr('selected', '');
    }
}

function tag_select_rerender_by_selected() {
    let selectArea = TAG0_SETTING.area.select;
    let selectInput = TAG0_SETTING.area.selectInput;
    let id = selectArea.find('option:selected').attr('value');
    selectInput.attr('value', id);
}

function tag_reset_click() {
    let form = TAG0_SETTING.area.form;
    let act = TAG0_SETTING.button.submit;
    let inputs = form.find('input');
    for (let i = 0; i < inputs.length; i++) {
        let input = $(inputs[i]);
        input.attr('value', '');
    }
    act.attr('act', TAG0_SETTING.act.post);
    tag_select_rerender_by_id();
    tag_btn_blue();
}

function tag_submit_click() {
    let btn = $(this);
    let form = TAG0_SETTING.area.form;
    let url_map = URLS.tag;
    let act = btn.attr('act');
    let data = wrap_by_form(form);
    let method, url;

    switch (act) {
        case TAG0_SETTING.act.post:
            method = url_map.create.method;
            url = url_map.create.url;
            break;
        case TAG0_SETTING.act.put:
            method = url_map.update.method;
            url = url_map.update.url;
            break;
        default:
            alert("Wrong!!")
    }

    send_Ajax(data, method, url, tag_success, error, "application/json");
}

function tag_edit_click() {
    let form = TAG0_SETTING.area.form;
    let act = TAG0_SETTING.button.submit;
    let btn = $(this);
    let tr = btn.parents('tr');
    let ths = tr.find('th');
    for (let i = 0; i < ths.length; i++) {
        let th = $(ths[i]);
        let name = th.attr('name');
        let value = th.attr('value');
        let input = form.find(`input[name="${name}"]`);
        input.attr('value', value);
    }
    act.attr('act', TAG0_SETTING.act.put);
    tag_select_rerender_by_id();
    tag_btn_yellow();
    window.scroll(form.parent('div').position());
}

function tag_btn_yellow() {
    let btn = TAG0_SETTING.button.submit;
    btn.removeClass('btn-success');
    btn.addClass('btn-warning');
}

function tag_btn_blue() {
    let btn = TAG0_SETTING.button.submit;
    btn.addClass('btn-success');
    btn.removeClass('btn-warning');
}

function tag_success(data, stat, xhr) {
    let status = xhr.status;
    if (status == 200) {
        let data_area = TAG0_SETTING.area.data;
        let prefix = TAG0_SETTING.prefix;

        if (Array.isArray(data)) {
            let data_row = TAG0_SETTING.template.data;
            data_area.html('');
            data_area.append(data_row);
            for (let i = 0; i < data.length; i++) {
                let tmp_row = data_row.clone();
                let d = data[i];
                set_data_to_target_with_json(d, tmp_row, 'name', ['text', 'value'], ['th']);
                let id = tmp_row.find('th[name="id"]').attr('value')
                tmp_row.attr('id', `${prefix}${id}`);
                tmp_row.removeAttr('hidden');
                tmp_row.find('button[name="edit"]').on('click', tag_edit_click);
                data_area.append(tmp_row);
            }
        } else {
            let id = data.id;
            let target = data_area.find(`tr[id=${prefix}${id}]`);
            if (target.length == 0) {
                prefix = TAG0_SETTING.prefix;
                target = TAG0_SETTING.template.data.clone();
                set_data_to_target_with_json(data, target, 'name', ['text', 'value'], ['th']);
                let id = target.find('th[name="id"]').attr('id');
                target.attr(id, `${prefix}${id}`);
                target.removeAttr('hidden');
                data_area.append(target);
            } else {
                set_data_to_target_with_json(data, target, 'name', ['text', 'value'], ['th']);
            }
            tag_reset_click();
            scrollToTarget(target);
        }
    }
}

function tag_select_area_success(data) {
    let select = TAG0_SETTING.area.select;
    for (let i = 0; i < data.length; i++) {
        let option = $('<option>');
        let d = data[i];
        option.attr('value', d.id);
        option.text(d.typeName);
        select.append(option);
    }
    init_form_stat();
}

