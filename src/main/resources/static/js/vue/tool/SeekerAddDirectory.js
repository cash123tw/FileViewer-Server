const template = `
    <div>
        <button class=" btn btn-outline-warning w-100" data-bs-toggle="modal"
                data-bs-target="#BtnAddDirectory" >新增資料夾</button>
            <div class="modal fade" id="BtnAddDirectory" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false"
                aria-labelledby="staticBackdropLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="staticBackdropLabel">新增資料夾</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body row">
                            <div class="mb-3 col-md-12">
                                <label for="file-name" class="form-label">名稱</label>
                                <input  id="file-name" v-model="name" type="text" class="form-control">
                            </div>
                        </div>
                        <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="submit">新增</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </div>
    </div>
`

export default {
    template: template,
    data() {
        return {
            name: ''
        }
    },
    methods: {
        submit: function () {
            if (null == this.name || '' === this.name.trim()) {
                alert('資料夾名稱不能為空');
            } else {
                this.$emit('add', this.name);
            }

        }
    }
}