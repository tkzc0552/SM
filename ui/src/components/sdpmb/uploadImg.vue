<template>
    <div>
        <el-upload
            :max-num="maxNum"
            :max-size="maxSize"
            :action="uploadUrl"
            :accept="acceptType"
            list-type="picture-card"
            auto-upload
            :before-upload="handleBeforeUpload"
            :on-success="handleSuccess"
            :on-error="handleError"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove">
            <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog v-model="dialogVisible" size="tiny">
            <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
    </div>
</template>


<script>
    export default {
        name: 'uploadImg',
        props: {
            uploadUrl: {
                type: String,
                required: true,
                default: ''
            },
            maxNum: {
                type: Number,
                required: false,
                default: 99
            },
            // 判断值和提示值必须对应
            maxSize: {
                type: Number,
                required: false,
                default: 2*1024*1024 // 2M = 2*1024*1024
            },
            maxSizeTip: {
                type: String,
                required: false,
                default: '2M' // 2M = 2*1024*1024
            },
            acceptType: {
                type: String,
                required: false,
                default: 'jpg/jpeg/png/bmp/gif'
            }
        },
        data () {
            return {
                dialogImageUrl: '',
                dialogVisible: false,
                fileList: []
            }
        },
        methods: {
            //上传之前,判断图片数量和大小（return false 终止上传）
            handleBeforeUpload(file){
                console.log(file);
                // console.log(this.fileList);
                //最多上传5张图片
                if (this.fileList.length > this.maxNum){
                    this.$message.error("最多上传" + this.maxNum + "张图片");
                    return false;
                }
                //图片类型
                if(file.type.split("/")[0] !== "image"){
                    this.$message.error("仅支持上传" + this.acceptType + "类型的图片");
                    return false;
                }
                //图片大小不超过500k
                if(file.size > this.maxSize){
                    this.$message.error("图片文件太大，应小于" + this.maxSizeTip);
                    return false;
                }
            },
            //上传成功
            handleSuccess(response, file, fileList) {
                // console.log(response);
                // console.log(file);
                // 更新图片列表
                this.fileList = fileList;
                this.$emit('uploadSuccess', response, fileList);
            },
            //上传失败
            handleError(err, file, fileList) {
                console.log(err);
                this.$message.error("上传失败，请重试");
                this.$emit('uploadError', err);
            },
            //预览图片
            handlePictureCardPreview(file) {
                console.log("handlePictureCardPreview");
                // 使用本地文件路径预览
                this.dialogImageUrl = file.url;
                this.dialogVisible = true;
            },
            //删除图片
            handleRemove(file, fileList) {
                // 更新图片列表
                this.fileList = fileList;
                this.$emit('removeImg', fileList);
            }
        },
        computed : {
            currentValue: {
                get() {
                    console.log("get - warehouse.value = " + this.value);
                    return this.value;
                },
                set(val) {
                    console.log("warehouse.currentValue.set = " + val);
                    this.$emit('change', val, 'warehouse', this.options);
                }
            },
            parentIdValue: {
                get() {
                    console.group("-----warehouses-------");
                    console.log("value: " + this.value);
                    console.log("parentId: " + this.parentId);
                    console.groupEnd();
                    if(isEmpty(this.parentId)) {
                        console.log("parentId is null");
                        return null;
                    } else {
                        this.fetchOptions(null);
                        return this.parentId;
                    }
                }
            },
            typeValue: {
                get() {
                    console.group("-----warehouses-------");
                    console.log("value: " + this.value);
                    console.log("type: " + this.type);
                    console.groupEnd();
                    if(isEmpty(this.type)) {
                        console.log("type is null");
                        return null;
                    } else {
                        this.fetchOptions(null);
                        return this.type;
                    }
                }
            }
        }

    }
</script>
<style scoped="">
</style>
