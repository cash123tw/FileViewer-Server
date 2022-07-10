
const template = `
<div class="container border border-2 rounded-2 mt-3 mb-5">
    <div class="row justify-content-center">
        <div class="text-dark col">
            <table class="table table-striped table-sm text-center" >
                <thead>
                    <tr>
                        <th class="col-1" @click="sortByType(0)" :style="styleDefine">
                        <span @click="sortByType(0)" :style="styleDefine">
                            <svg v-if="sortSelect == 0" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="mb-1  bi bi-arrow-down" viewBox="0 0 16 16">
                                <path fill-rule="evenodd" d="M8 1a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L7.5 13.293V1.5A.5.5 0 0 1 8 1z"/>
                            </svg>
                        </span>
                        </th>
                        <th class="col-3">
                            <span @click="sortByType(1)" :style="styleDefine">名稱
                                <svg v-if="sortSelect == 1" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="mb-1  bi bi-arrow-down" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M8 1a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L7.5 13.293V1.5A.5.5 0 0 1 8 1z"/>
                                </svg>
                            </span>
                        </th>
                        <th class="col-2 d-none d-md-table-cell">
                            <span @click="sortByType(0)" :style="styleDefine">類型
                                <svg v-if="sortSelect == 0" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="mb-1  bi bi-arrow-down" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M8 1a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L7.5 13.293V1.5A.5.5 0 0 1 8 1z"/>
                                </svg>
                            </span>
                        </th>
                        <th class="col-3">
                            <span @click="sortByType(2)" :style="styleDefine">時間
                                <svg v-if="sortSelect == 2" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="mb-1  bi bi-arrow-down" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M8 1a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L7.5 13.293V1.5A.5.5 0 0 1 8 1z"/>
                                </svg>
                            </span>
                        </th>
                        <th class="col-3" >標籤</th>
                    </tr>
                </thead>
                <tbody id="data_part" >
                <TrTemplate v-for="file in files" 
                    @fileClick="fileClick" 
                    :file="file" 
                    :data_act="getAct(file)" 
                    :key="file"
                    :isDirectory="isDirectory" 
                    :styleDefine="'cursor: pointer'" >
                    </TrTemplate>
                </tbody>
            </table>
        </div>
    </div>
</div>
`

const trTemplate = {
    name: 'TrTemplate',
    props: {
        file: {

        },
        data_act: {

        },
        styleDefine: {

        },
        isDirectory: {

        },
    },
    methods: {
        clickHandle: function (file, evn) {
            this.$emit('fileClick', file, evn);
        },
        getDateString: function (date) {
            try {
                let d = new Date(date);
                let y = d.getFullYear()
                let m = d.getMonth() + 1
                let dd = d.getDate()
                let h = d.getHours()
                let mm = d.getMinutes()
                return `${y}/${m}/${dd}  ${h}:${mm}`
            } catch (e) {
                return "";
            }
        },
        getFileTypeNum(file) {
            let type = file.fileType.typeName;
            let v = this.icon.file;
            switch (type) {
                case "directory":
                    v = this.icon.directory
                    break;
                case "pdf":
                    v = this.icon.pdf
                    break;
                case "doc":
                    v = this.icon.word;
                    break;
                case "docx":
                    v = this.icon.word;
                    break;
                case "txt":
                    v = this.icon.txt;
                    break;
                case "jpg":
                    v = this.icon.jpg;
                    break;
                case "jpeg":
                    v = this.icon.jpg;
                    break;
                default:
                    v = this.icon.file;
                    break;
            }
            return v;
        },
    },
    inject: ['SearchActive'],
    emits: ['fileClick'],
    data() {
        return {
            icon: {
                directory: `<svg
                xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                    class="bi bi-folder-fill" viewBox="0 0 16 16">
                    <path d="M9.828 3h3.982a2 2 0 0 1 1.992 2.181l-.637 7A2 2 0 0 1 13.174 14H2.825a2 2 0 0 1-1.991-1.819l-.637-7a1.99 1.99 0 0 1 .342-1.31L.5 3a2 2 0 0 1 2-2h3.672a2 2 0 0 1 1.414.586l.828.828A2 2 0 0 0 9.828 3zm-8.322.12C1.72 3.042 1.95 3 2.19 3h5.396l-.707-.707A1 1 0 0 0 6.172 2H2.5a1 1 0 0 0-1 .981l.006.139z"/>
                 </svg>`,
                file: `<svg
                xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                    class="bi bi-file-earmark-text-fill" viewBox="0 0 16 16">
                    <path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM4.5 9a.5.5 0 0 1 0-1h7a.5.5 0 0 1 0 1h-7zM4 10.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm.5 2.5a.5.5 0 0 1 0-1h4a.5.5 0 0 1 0 1h-4z"/>
                </svg>`,
                pdf: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-pdf-fill" viewBox="0 0 16 16">
                    <path d="M5.523 12.424c.14-.082.293-.162.459-.238a7.878 7.878 0 0 1-.45.606c-.28.337-.498.516-.635.572a.266.266 0 0 1-.035.012.282.282 0 0 1-.026-.044c-.056-.11-.054-.216.04-.36.106-.165.319-.354.647-.548zm2.455-1.647c-.119.025-.237.05-.356.078a21.148 21.148 0 0 0 .5-1.05 12.045 12.045 0 0 0 .51.858c-.217.032-.436.07-.654.114zm2.525.939a3.881 3.881 0 0 1-.435-.41c.228.005.434.022.612.054.317.057.466.147.518.209a.095.095 0 0 1 .026.064.436.436 0 0 1-.06.2.307.307 0 0 1-.094.124.107.107 0 0 1-.069.015c-.09-.003-.258-.066-.498-.256zM8.278 6.97c-.04.244-.108.524-.2.829a4.86 4.86 0 0 1-.089-.346c-.076-.353-.087-.63-.046-.822.038-.177.11-.248.196-.283a.517.517 0 0 1 .145-.04c.013.03.028.092.032.198.005.122-.007.277-.038.465z"/>
                    <path fill-rule="evenodd" d="M4 0h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2zm5.5 1.5v2a1 1 0 0 0 1 1h2l-3-3zM4.165 13.668c.09.18.23.343.438.419.207.075.412.04.58-.03.318-.13.635-.436.926-.786.333-.401.683-.927 1.021-1.51a11.651 11.651 0 0 1 1.997-.406c.3.383.61.713.91.95.28.22.603.403.934.417a.856.856 0 0 0 .51-.138c.155-.101.27-.247.354-.416.09-.181.145-.37.138-.563a.844.844 0 0 0-.2-.518c-.226-.27-.596-.4-.96-.465a5.76 5.76 0 0 0-1.335-.05 10.954 10.954 0 0 1-.98-1.686c.25-.66.437-1.284.52-1.794.036-.218.055-.426.048-.614a1.238 1.238 0 0 0-.127-.538.7.7 0 0 0-.477-.365c-.202-.043-.41 0-.601.077-.377.15-.576.47-.651.823-.073.34-.04.736.046 1.136.088.406.238.848.43 1.295a19.697 19.697 0 0 1-1.062 2.227 7.662 7.662 0 0 0-1.482.645c-.37.22-.699.48-.897.787-.21.326-.275.714-.08 1.103z"/>
                </svg>`,
                word: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-word-fill" viewBox="0 0 16 16">
                    <path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM5.485 6.879l1.036 4.144.997-3.655a.5.5 0 0 1 .964 0l.997 3.655 1.036-4.144a.5.5 0 0 1 .97.242l-1.5 6a.5.5 0 0 1-.967.01L8 9.402l-1.018 3.73a.5.5 0 0 1-.967-.01l-1.5-6a.5.5 0 1 1 .97-.242z"/>
                </svg>`,
                txt:`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-filetype-txt" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M14 4.5V14a2 2 0 0 1-2 2h-2v-1h2a1 1 0 0 0 1-1V4.5h-2A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v9H2V2a2 2 0 0 1 2-2h5.5L14 4.5ZM1.928 15.849v-3.337h1.136v-.662H0v.662h1.134v3.337h.794Zm4.689-3.999h-.894L4.9 13.289h-.035l-.832-1.439h-.932l1.228 1.983-1.24 2.016h.862l.853-1.415h.035l.85 1.415h.907l-1.253-1.992 1.274-2.007Zm1.93.662v3.337h-.794v-3.337H6.619v-.662h3.064v.662H8.546Z"/>
                </svg>`,
                jpg:`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-filetype-jpg" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M14 4.5V14a2 2 0 0 1-2 2h-1v-1h1a1 1 0 0 0 1-1V4.5h-2A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v9H2V2a2 2 0 0 1 2-2h5.5L14 4.5Zm-4.34 8.132c.076.153.123.317.14.492h-.776a.797.797 0 0 0-.097-.249.689.689 0 0 0-.17-.19.707.707 0 0 0-.237-.126.96.96 0 0 0-.299-.044c-.285 0-.507.1-.665.302-.156.201-.234.484-.234.85v.498c0 .234.032.439.097.615a.881.881 0 0 0 .304.413.87.87 0 0 0 .519.146.967.967 0 0 0 .457-.096.67.67 0 0 0 .272-.264c.06-.11.091-.23.091-.363v-.255H8.24v-.59h1.576v.798c0 .193-.032.377-.097.55a1.29 1.29 0 0 1-.293.458 1.37 1.37 0 0 1-.495.313c-.197.074-.43.111-.697.111a1.98 1.98 0 0 1-.753-.132 1.447 1.447 0 0 1-.533-.377 1.58 1.58 0 0 1-.32-.58 2.482 2.482 0 0 1-.105-.745v-.506c0-.362.066-.678.2-.95.134-.271.328-.482.582-.633.256-.152.565-.228.926-.228.238 0 .45.033.636.1.187.066.347.158.48.275.133.117.238.253.314.407ZM0 14.786c0 .164.027.319.082.465.055.147.136.277.243.39.11.113.245.202.407.267.164.062.354.093.569.093.42 0 .748-.115.984-.345.238-.23.358-.566.358-1.005v-2.725h-.791v2.745c0 .202-.046.357-.138.466-.092.11-.233.164-.422.164a.499.499 0 0 1-.454-.246.577.577 0 0 1-.073-.27H0Zm4.92-2.86H3.322v4h.791v-1.343h.803c.287 0 .531-.057.732-.172.203-.118.358-.276.463-.475.108-.201.161-.427.161-.677 0-.25-.052-.475-.158-.677a1.176 1.176 0 0 0-.46-.477c-.2-.12-.443-.179-.732-.179Zm.546 1.333a.795.795 0 0 1-.085.381.574.574 0 0 1-.238.24.794.794 0 0 1-.375.082H4.11v-1.406h.66c.218 0 .389.06.512.182.123.12.185.295.185.521Z"/>
                </svg>`
            }
        }
    },
    template: `
    <tr v-if="!file.missing" @click="clickHandle(file,$event)" :data-act="data_act" :style="styleDefine" >
        <td hidden name="id" :value="file.id">#</td>
        <td hidden name="path" :value="file.path">#</td>
        <td hidden name="fileType.id" :value="file.fileType.id">#</td>
        <td class="col-1 align-content-center" name="icon" :rowspan="SearchActive?'2':null" v-html="getFileTypeNum(file)">
        </td>
        <td class="col-3 text-break" name="file_name" >{{file.file_name}}</td>
        <td class="col-2 d-none d-md-table-cell" name="fileType.typeName">{{file.fileType.typeName}}</td>
        <td class="col-3">{{getDateString(file.lastModify)}}</td>
        <td class="col-3" name="tags">
            <span v-for="tag in file.tags" 
                class="bg-warning mx-1 px-1 rounded-2" 
                :value="tag.id">
                {{tag.name}}
            </span>
        </td>
    </tr>
    <tr v-if="SearchActive&&!file.missing" @click="clickHandle(file,$event)" class="table-light opacity-75 " :style="styleDefine" :data-act="data_act" >
        <td class="text-primary text-start text-break ps-2" colspan="3">{{file.path}}</td>
    </tr>
    `,

}

export default {
    template: template,
    props: {
        files: {
            required: true,
        }
    },
    data() {
        return {
            styleDefine: 'cursor: pointer',
            sortSelect: null,
        }
    },
    watch: {
        files: {
            handler: function (newV, oldV) {
                if (newV != oldV) {
                    this.sortSelect = null
                }
            },
            deep: true
        }
    },
    methods: {
        isDirectory(file) {
            let type = file.fileType.typeName;
            if (type === 'directory') {
                return true;
            }
            return false;
        },

        getAct(file) {
            if (this.isDirectory(file)) {
                return "forward";
            }
            return "detail";
        },
        fileClick(item, event) {
            this.$emit('search', item, event.currentTarget.dataset.act)
        },
        sortByType: sortByType
    },
    components: {
        TrTemplate: trTemplate
    }
}

function sortByType(v) {
    {
        this.sortSelect = v;
        this.files.sort((a, b) => {

            switch (v) {
                case 0:
                    a = a.fileType.typeName
                    b = b.fileType.typeName
                    break;
                case 1:
                    a = a.file_name
                    b = b.file_name
                    break;
                case 2:
                    a = new Date(a.lastModify);
                    b = new Date(b.lastModify);
                    break;
            }

            if (a < b) {
                return -1
            } else if (a > b) {
                return 1
            } else {
                return 0
            }
        })
    }
}