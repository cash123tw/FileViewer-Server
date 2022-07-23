import { urls } from '../../modal/urls.js'
import '../axios.min.js';

const localhost = ""
const template = `
<div >
<button :class="define" data-bs-toggle="modal"
    data-bs-target="#BtnSearchFile">搜尋檔案</button>
    <div class="modal fade" id="BtnSearchFile" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false"
aria-labelledby="staticBackdropLabel" aria-hidden="true">
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel">檔案搜尋器</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body row">
            <div class="mb-3 col-md-9">
                <label for="file-name" class="form-label">檔案名稱</label>
                <input placeholder="欲搜尋名稱" v-model="fileName" type="text" class="form-control" id="file-name">
            </div>
            <div class="mb-3 col-md-3">
                <label for="file-type" class="form-label">檔案類型</label>
                <select id="file-type" v-model="fileType" class="form-select">
                    <option v-for="type in typeList" :value="type.id" :label="type.typeName">{{type.typeName}}</option>
                </select>
            </div>
            <div class="mb-3 col-md-12">
                <label class="form-label">標籤選擇</label>
                <div class="input-group mb-3">
                    <span class="input-group-text">類別</span>
                    <select class="form-select" v-model="tag.select" @change="listTagByTagType">
                        <option v-for="tagType in tagTypes" :value="tagType.id" :label="tagType.typeName">{{tagType.typeName}}</option>
                    </select>

                    <span class="input-group-text">標籤</span>
                    <select class="form-select" v-model="searchTag.select">
                      <option v-for="tag in tagList" :value="tag" :label="tag.name">{{tag.name}}</option>
                    </select>

                    <button class="btn btn-outline-success" @click="addTag">增加</button>
                </div>
            </div>
            <div class="col-12 mb-3">
                <div class="border border-gray border-1 rounded-2 px-2 py-2">
                    <div v-for="tag in tags" class="p-1 m-1 rounded-2 bg-gradient border trans"
                        :class="[tagShowClass,{'bg-danger text-white':tag[1].active&&deleteActive}]" 
                        :key="tag[1].id" style="display: inline-block;">
                        <label @click="deleteActive?tagDeleteSelect(tag[1]):''" :for="deleteActive?tag[1].name:''" class="point trans" ># {{tag[1].name}}</label>
                        <input type="checkbox" :id="tag[1].name" :value="tag[1]" v-model="checked" hidden/>
                    </div>
                </div>
            </div>
            <div class="col-12 mb-3">
                <div class="col-12" v-if="!deleteActive">
                    <button class="btn btn-outline-danger w-100 py-0" @click="tagDeleteActive">刪除標籤</button>
                </div>
                <div class="btn-group col-12" v-if="deleteActive">
                    <button class="btn btn-outline-success w-100 py-0" @click="tagDeleteFlush">執行</button>
                    <button @click="tagDeleteUnActive" class="btn btn-outline-secondary w-100 py-0">取消</button>
                </div>
            </div>
        </div>
        <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="searchClick">搜尋</button>
        <button class="btn btn-primary opacity-75" @click="reset">重置</button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
        </div>
    </div>
</div>
</div>
</div>
`
export default {
  template: template,
  props: ['define'],
  methods: {
    searchClick: searchClick,
    reset: reset,
    tagDeleteActive: tagDeleteActive,
    tagDeleteUnActive: tagDeleteUnActive,
    tagDeleteSelect: tagDeleteSelect,
    tagDeleteFlush: tagDeleteFlush,
    listFileType: listFileType,
    listTagType: listTagType,
    listTagByTagType: listTagByTagType,
    addTag: addTag
  },
  data() {
    return {
      deleteActive: false,
      checked: [],
      tags: new Map(),
      typeList: [],
      tagTypes: [],
      tagList: [],
      fileName: null,
      fileType: null,
      tag: {
        deleteSelect: []
      },
      searchTag: {
      }
    }
  },
  computed: {
    tagShowClass: tagShowClass
  },
  mounted() {
    this.listTagType();
    this.listFileType();
    this.reset();
  },
}

function searchClick() {
  let data = {
    fileTypeId: this.fileType,
    fileName: this.fileName,
    tags: Array.from(this.tags.values())
  }

  this.$emit("search", data)
}

function reset() {
  this.tagDeleteUnActive()
  this.checked = [];
  this.tags = new Map();
  this.tagList = null;
  this.tag.select = null;
  this.fileType = null;
  this.fileName = null;
}

function tagDeleteActive() {
  this.deleteActive = true;
}

function tagDeleteUnActive() {
  let tar = null;
  this.deleteActive = false;

  while ((tar = this.checked.pop()) != null) {
    tar.active = false;
  }

}

function tagDeleteFlush() {
  let target;

  while ((target = this.checked.pop()) != null) {
    target.active = false;
    this.tags.delete(target.id);
  }

  this.tagDeleteUnActive();
}

function tagDeleteSelect(tag) {
  tag.active = !tag.active;
}

function addTag() {
  let value = this.searchTag.select;
  if (value == null)
    return;
  this.tags.set(value.id, value);
}

function listFileType() {

  let url = urls.fileType.findAll;

  axios({
    method: url.method,
    url: `${localhost}${url.url}`
  })
    .then((result) => {
      let list = result.data;
      list.unshift({ typeName: '無' })
      this.typeList = list;
    }).catch((err) => {
      console.log(err)
    });

}

function listTagType() {
  let url = urls.tagType.findAll;
  this.searchTag.select = null;

  axios({
    method: url.method,
    url: `${localhost}${url.url}`,
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
    url: `${localhost}${url.url.replaceAll('id', value)}`
  }).then((result) => {
    let list = result.data;
    this.tagList = list;
  }).catch((err) => {
    console.log(err)
  });
}

// class define 
function tagShowClass() {
  return {
    'border-secondary': !this.deleteActive,
    'border-danger bg-opacity-75': this.deleteActive,
  }
}