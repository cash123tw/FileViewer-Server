let areas;

function init_detail(area) {
    areas = area;
    sendToServer();
}

function sendToServer() {
    let url = location.href;
    sendAjaxToServer(url, null, success_detail, error);
}

function success_detail(data) {
    let result = data.result;
    if (result) {
        show_detail(data);
    } else {
        success_fail(data);
    }
}

function show_detail(data) {
    data = data.obj;
    let file_id = data.file_id;
    let file_name = data.file_name;
    let file_path = data.file_path;
    let file_types = data.file_types;
    let file_tags = data.file_tags;

    for (let key in areas) {
        let area
            = areas[key];

        switch (key) {
            case 'file_id':
                area.attr('value',file_id);
                break;
            case 'file_name':
                area.attr('value',file_name);
                break;
            case 'file_path':
                area.attr('value',file_path);
                break;
            case 'file_types':
                for (const indx in file_types) {
                    let a = result_type(file_types[indx]);
                    //btn btn-primary bg-gradient p-1 rounded text-white
                    a.addClass("btn");
                    a.addClass("btn-warning");
                    a.addClass("text-white");
                    a.addClass("btn-gradient");
                    a.addClass("p-1");
                    a.addClass("mx-2");
                    a.addClass("rounded");
                    area.append(a);
                }
                break;
            case 'file_tags':
                for (const indx in file_tags) {
                    let a = result_tag(file_tags[indx]);
                    //btn btn-primary bg-gradient p-1 rounded
                    a.addClass("btn");
                    a.addClass("btn-secondary");
                    a.addClass("text-white");
                    a.addClass("btn-gradient");
                    a.addClass("p-1");
                    a.addClass("mx-2");
                    a.addClass("rounded");
                    area.append(a);
                }
                break;
        }
    }
}

function mk_a(text, value, css_class) {
    let a = $('<a>');
    a.text(text);
    a.attr('value', value);
    a.attr('class', css_class);
}
