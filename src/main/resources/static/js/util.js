function send_Ajax(data, method, url, success, error, contentType) {
    $.ajax({
        url: url,
        data: JSON.stringify(data),
        contentType: contentType,
        cache: false,
        type: method,
        success: success,
        error: error
    });
}

function wrap_by_form(form) {
    let inputs = form.find('input');
    let result = {};
    for (let ii = 0; ii < inputs.length; ii++) {
        let input = $(inputs[ii]);
        let name = input.attr("name");
        let value = input.attr("value");
        let pointer = result;
        let splitDot = split_dot(name);
        let length = splitDot.length;
        for (let i = 0; i < length; i++) {
            if (i == length - 1) {
                pointer[splitDot[i]] = value;
            } else {
                if (pointer[splitDot[i]] == null) {
                    pointer[splitDot[i]] = {};
                    pointer = pointer[splitDot[i]];
                } else {
                    pointer = pointer[splitDot[i]];
                }
            }
        }
    }

    return result;
}

function split_dot(strs) {
    return strs.split(".");
}

/**
 data: use json mode.
 domElement: where data set to,function will find childElement and check rely attribute's value is present in data,
 and set to element attribute `value`.
 relyAttribute: let function know which attribute value you want to find in `data`.
 setToAttribute: which attribute value set to.
 focusElement: let function know which domElement child need to be set data.

 EXAMPLE:
 data = {
                    id = '1',
                    name = 'cash',
                    obj = {
                            text = 'hello'
                        }
                }
 domElement = <form>
 <input name='id'></input>
 <h1 name='name'></h1>
 <p name='obj.text'></p>
 </form>
 relyAttribute='name'
 setToAttribute='text'
 focusElement=['input','p']
 FUNCTION RESULT IS :
 domElement = <form>
 <input name='id' text='1'></input>
 <h1 name='name'></h1>
 <p name='obj.text' text='hello'></p>
 </form>
 **/
function set_data_to_target_with_json(data, domElement, relyAttribute, setToAttribute, focusElement) {

    focusElement = returnArray(focusElement);
    setToAttribute = returnArray(setToAttribute);

    for (let i = 0; i < focusElement.length; i++) {
        let key = focusElement[i];
        let elements = domElement.find(key);

        SET_VALUE:for (let i = 0; i < elements.length; i++) {
            let element = $(elements[i]);
            let find_key = element.attr(relyAttribute);
            let pointer = data[find_key];
            if (pointer == null) {
                pointer = data;
                let strs = split_dot(find_key);
                for (let j = 0; j < strs.length; j++) {
                    let find_key0 = strs[j];
                    if (pointer != null) {
                        pointer = pointer[find_key0]
                    } else {
                        continue SET_VALUE;
                    }
                }
            }

            if (pointer != null) {
                for (let j = 0; j < setToAttribute.length; j++) {
                    let setAttribute = setToAttribute[j];
                    switch (setAttribute) {
                        case "text":
                            element.text(pointer);
                            break;
                        default:
                            element.attr(setAttribute, pointer);
                    }
                }
            }
        }
    }
}

function returnArray(target) {
    if (Array.isArray(target)) {
        return target;
    } else {
        let ar = new Array();
        ar.push(target);
    }
}

function reset_selected(select_area) {
    let opts = select_area.find('option');
    for (let i = 0; i < opts.length; i++) {
        let opt = $(opts[i]);
        opt.removeAttr('selected');
    }
}

function reset_form(area) {
    let inputs = area.find('input');
    for (let i = 0; i < inputs.length; i++) {
        let input = inputs[i];
        $(input).removeAttr('value');
    }
}

function scrollToTarget(target) {
    window.scroll(target.position());
}
