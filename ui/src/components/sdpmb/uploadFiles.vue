<template>
    <div>
        <el-upload
            class="upload-demo"
            ref="upload"
            show-file-list
            drag

            :file-list="fileList"
            :action="uploadUrl"
            :data="data"

            :multiple="multiple"
            :limit="limitNum"
            :max-size="maxSize"
            :accept="acceptType"

            :before-upload="handleBeforeUpload"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :on-error="handleError"
            :on-exceed="handleLimitNum">

            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div slot="tip" class="el-upload__tip">{{uploadTip}}</div>
        </el-upload>
    </div>
</template>


<script>
    export default {
        name: 'uploadFiles',
        props: {
            uploadUrl: {
                type: String,
                required: true,
                default: ''
            },
            data: {
                type: Object,
                required: true,
                default: null
            },
            limitNum: {
                type: Number,
                required: false,
                default: 99
            },
            multiple: {
                type: Boolean,
                required: false,
                default: true
            },
            // 判断值和提示值必须对应
            maxSize: {
                type: Number,
                required: false,
                default: 5*1024*1024 // 5M = 5*1024*1024
            },
            maxSizeTip: {
                type: String,
                required: false,
                default: '5M' // 5M = 5*1024*1024
            },
            acceptType: {
                type: String,
                required: false,
                default: ''
            },
            uploadTip:{
                type: String,
                required: false,
                default: '文件大小不超过5M'
            }
        },
        data () {
            return {
                fileList: []
            }
        },
        methods: {
            //上传之前,判断文件数量和大小（return false 终止上传）
            handleBeforeUpload(file){
                console.log(file);
                console.log(this.fileList);
                console.log(this.uploadUrl);
                //文件大小不超过500k
                if(parseInt(file.size) > parseInt(this.maxSize)){
                    this.$message.error("文件太大，应小于" + this.maxSizeTip);
                    return false;
                }
                // //文件查重
                // for(let i=0; i<this.fileList.length; i++){
                //     let fileItem = this.fileList[i];
                //     if(file.name == fileItem.name && file.type == fileItem.raw.type && file.size == fileItem.size){
                //         this.$message.error("文件重复，请选择其他文件上传");
                //         return false;
                //     }
                // }
            },
            //上传成功
            handleSuccess(response, file, fileList) {
                // console.log(response);
                // console.log(file);
                // 更新文件列表
                this.fileList = fileList;
                //this.$message.success("上传成功");
                this.$emit('uploadSuccess', response, fileList);
            },
            //上传失败
            handleError(err) {
                console.log(err);
                this.$message.error("上传失败，请重试");
                this.$emit('uploadError', err);
            },
            //删除文件
            handleRemove(file, fileList) {
                // 更新文件列表
                this.fileList = fileList;
                this.$emit('removeFile', fileList, file);
            },
            //超出最大数量限制
            handleLimitNum() {
                this.$message.error("最多上传" + this.limitNum + "个文件");
            },

            clearFiles(){
//                console.log("clear");
                this.$refs["upload"].clearFiles();
            }
        }
    }
</script>
<style scoped="">
</style>
