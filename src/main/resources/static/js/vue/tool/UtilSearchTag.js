import { urls } from '../../modal/urls.js'
import '../vue-cookies.js';

const template = `
<div class="row">
    <div class="mb-3 col-md-12">
        <label class="form-label">標籤選擇</label>
        <div class="input-group mb-3">
            <span class="input-group-text">類別</span>
            <select class="form-select" v-model="tag.select" @change="listTagByTagType">
                <option v-for="tagType in tagTypes" :value="tagType.id" :label="tagType.typeName">{{tagType.typeName}}
                </option>
            </select>

            <span class="input-group-text">標籤</span>
            <select class="form-select" v-model="searchTag.select" @change="this.$emit('update:modelValue',this.searchTag.select)">
                <option v-for="tag in tagList" :value="tag" :label="tag.name">{{tag.name}}</option>
            </select>

        </div>
    </div>
</div>`

export default {
    template: template,
    methods: {
        listTagByTagType: listTagByTagType,
        listTagType: listTagType,
    },
    data() {
        return {
            tagTypes: [],
            tagList: [],
            tag: {
                select: null,
            },
            searchTag: {
                select: null,
            }
        }
    },
    inject: ['localhost'],
    mounted() {
        this.listTagType();
    },
}

function listTagType() {
    let url = urls.tagType.findAll;
    this.searchTag.select = null;

    axios({
        method: url.method,
        url: `${this.localhost}${url.url}`,
    }).then((result) => {
        let list = result.data;
        list.unshift({ typeName: '所有' })
        this.tagTypes = list;
    }).catch((err) => {
        console.log(err)
    });
}

function listTagByTagType(ev) {
    let url = urls.tag.find_by_tag_type_id;
    let value = ev.target.value;

    value = isNaN(value) ? "" : value;

    axios({
        method: url.method,
        url: `${this.localhost}${url.url.replaceAll('id', value)}`
    }).then((result) => {
        let list = result.data;
        this.tagList = list;
    }).catch((err) => {
        console.log(err)
    });
}