function removeSelect(target) {
    let opts = target.find('option');
    for (let i = 0; i < opts.length; i++) {
        let opt = opts[i];
        if (opt.selected) {
            $(opt).removeAttr('selected');
        }
    }
}

function resetSelect(select){
    let opts = select.find('option[value!=-1]');
    for (let i = 0; i < opts.length; i++) {
        let opt = $(opts[i]);
        opt.remove();
    }
}

class TagType {
    constructor(data) {
        this.id = data.id;
        this.typeName = data.typeName;
    }

    static buildTagTypeByJson(data) {
        return new TagType(data);
    }
}

function errorHandel(e,data,k) {
    console.log(e);
    let text = e.responseText;
    alert(text);
}

class Tag {
    constructor(id, name, tagType) {
        this.id = id;
        this.name = name;
        this.tagType = {};
        if (tagType != null) {
            this.tagType.id = tagType.id
            this.tagType.typeName = tagType.typeName;
        }
    }

    static buildTagFormJson(data) {
        return new Tag(data.id,data.name,data.tagType);
    }
}

export {removeSelect, resetSelect,errorHandel, TagType, Tag}