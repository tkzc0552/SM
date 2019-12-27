<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" ref="filterForm">
                    <el-form-item label="用户名" prop="reasonCode">
                        <el-input v-model="filters.userName" placeholder="支持模糊搜索"></el-input>
                    </el-form-item>
                    <el-form-item label="手机号" prop="mobile">
                        <el-input v-model="filters.mobile" placeholder="支持模糊搜索"></el-input>
                    </el-form-item>
                    <el-form-item label="性別" prop="sexStatus">
                        <sd-param-radio-group v-model="filters.sexStatus" type-code="sexStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch('filterForm')">查询</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <!--工具条结束-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true">
                    <el-form-item>
                        <el-button  type="primary" @click="handleAdd">创建</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <!--列表开始-->
        <el-table :data="pagination.content" highlight-current-row v-loading="isLoading" border>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column type="index" label="NO" width="80"></el-table-column>
            <el-table-column prop="userName" label="用户名" min-width="100"></el-table-column>
            <el-table-column prop="sex" label="性别" width="60">
                <template slot-scope="scope">
                    {{scope.row.sex | paramCode2ParamCname("sexStatus")}}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="60">
                <template slot-scope="scope">
                    {{scope.row.status | paramCode2ParamCname("statusType")}}
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="email" label="邮箱" min-width="120"></el-table-column>
            <el-table-column show-overflow-tooltip prop="mobile" label="手机号" min-width="120"></el-table-column>
            <el-table-column show-overflow-tooltip prop="address" label="地址" min-width="100"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column  label="操作" width="350" fixed="right">
                <template slot-scope="scope">
                    <el-button @click="handleView(scope.$index, scope.row)" type="primary">详情</el-button>
                    <el-button @click="handleEdit(scope.$index, scope.row)" type="primary">编辑</el-button>
                    <el-button @click="handleInit(scope.$index, scope.row)" type="primary">初始化密码</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--列表结束-->
        <!--分页开始-->
        <div class="pagination" v-if='pagination.totalElements > 0'>
            <el-pagination layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                           @current-change="handleCurrentChange" :current-page.sync="pagination.page"
                           :page-size="size" :page-sizes="[10, 20, 50, 100]"
                           :total="pagination.totalElements" :disabled="isLoading">
            </el-pagination>
        </div>

        <!--新建/编辑/详情区-->
        <section>
            <!--新增/编辑-->
            <el-dialog v-if="formObj.type!='0'" :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                <el-form :inline="true" :model="formObj.formModel" :rules="sysUserRules" label-width="80px" ref="sysUserForm">
                    <table class="productListTable productListTable-col4">
                        <tr>
                            <td>
                                <el-form-item label="登录名" prop="userName">
                                    <el-input v-model="formObj.formModel.userName"></el-input>
                                </el-form-item>
                            </td>
                            <td>
                                <el-form-item label="登录密码" prop="passWord">
                                    <el-input type="password" v-model="formObj.formModel.passWord"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="邮箱" prop="email">
                                    <el-input v-model="formObj.formModel.email"></el-input>
                                </el-form-item>
                            </td>
                            <td>
                                <el-form-item label="手机号" prop="mobile">
                                    <el-input v-model="formObj.formModel.mobile"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="性别">
                                    <sd-param-radio-group v-model="formObj.formModel.sex" type-code="sexStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                                </el-form-item>
                            </td>
                            <td>
                                <el-form-item label="状态">
                                    <sd-param-radio-group v-model="formObj.formModel.status" type-code="statusType" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="选择部门">
                                    <department-for-all v-model="formObj.formModel.deptId" @change="changeDepartment"></department-for-all>
                                </el-form-item>
                            </td>
                            <td>
                                <el-form-item label="选择角色">
                                    <role v-model="formObj.formModel.roleIds" @change="changeRole"></role>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="地址">
                                    <el-input type="textarea" v-model="formObj.formModel.address"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                    </table>
                    <div style="margin-top: 20px;margin-left: 20px;">
                        用户头像：
                        <br>
                        <el-image class="imgClass" :src="formObj.formModel.avatarImg"></el-image>
                        <el-button type="primary" @click="upLoadMSDS()">上传头像</el-button>
                    </div>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('sysUserForm')">取消</el-button>
                    <el-button type="primary"  @click="handleSubmit('sysUserForm')">提交</el-button>
                </div>
            </el-dialog>
            <!--详情-->
            <el-dialog v-if="formObj.type=='0'"  :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                <el-form :inline="true" :model="formObj.formModel"  label-width="110px" ref="reasonForm">
                    <table class="productListTable productListTable-col4">
                        <tr>
                            <td>
                                <el-form-item label="姓名" prop="userName">
                                    {{formObj.formModel.userName}}
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="邮箱" prop="email">
                                    {{formObj.formModel.email}}
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="手机" prop="mobile">
                                    {{formObj.formModel.mobile}}
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="性别" prop="sex">
                                    {{formObj.formModel.sex|paramCode2ParamCname("sexStatus")}}
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="状态" prop="status">
                                    {{formObj.formModel.status|paramCode2ParamCname("statusType")}}
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="部门" prop="deptName">
                                    {{formObj.formModel.deptId|deptId2DeptName}}
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="地址" prop="address">
                                    <el-popover placement="top-start" trigger="hover">
                                        <div>
                                            <p style="width: 500px; font-size: 14px; margin-bottom: 5px; word-break: break-all;"> {{formObj.formModel.address}}</p>
                                        </div>
                                        <span slot="reference"> {{formObj.formModel.address}}</span>
                                    </el-popover>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="创建时间" prop="createTime">
                                    {{formObj.formModel.createTime}}
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="头像" prop="createTime">
                                   <el-image  class="imgClass" :src="formObj.formModel.avatarImg"></el-image>
                                </el-form-item>
                            </td>
                        </tr>
                    </table>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('reasonForm')">取消</el-button>
                </div>
            </el-dialog>
        </section>

        <!--上传MSDS开始-->
        <section>
            <el-dialog :title="uploadFormObj.title" :visible.sync="uploadFormObj.formVisible" :close-on-click-modal="false">
                <el-row style="border-bottom: 1px solid rgb(224, 224, 224); padding-bottom: 8px;">
                    <el-col :span="50" class="toolbar" style="width: 400px;">
                        <upload-files ref="uploadFiles" :uploadUrl="uploadFormObj.uploadCompressUrl" :data="uploadFormObj.data" uploadTip="最多上传5个文件，每个文件大小不超过5M"
                                      :limitNum="5" :multiple="true" :file-list="uploadFormObj.fileList"
                                      @uploadSuccess="handleUploadSuccess" @uploadError="handleUploadError" @removeFile="handleRemoveFile"></upload-files>
                    </el-col>
                </el-row>
                <div slot="footer" class="dialog-footer">
                    <el-button type="primary" @click.native="saveMSDSCancel('uploadFiles')"
                               :loading="isLoading">保存
                    </el-button>
                    <el-button @click.native="UploadMSDSCancel('uploadFiles')">取消</el-button>
                </div>
            </el-dialog>
        </section>
        <!--上传MSDS结束-->
    </div>
</template>
<script>
    import http from '../../axios/http';
    import ElImage from "../../../node_modules/element-ui/packages/image/src/main";
    export default {
        components: {ElImage},
        name: 'sysUser',
        data() {
            return {
                heightNum: 0,
                uploadFormObj: {// 上传图片/文件
                    id: '',
                    uploadType: 'file',
                    title:"上传MSDS附件",
                    fileList: [],
                    formVisible: false,//编辑界面是否显示
                    uploadCompressUrl: "api/product/uploadCompress",
                    data: null
                },
                filters: {
                    userName:'',
                    sexStatus:'',
                    mobile:'',
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                url:'',
                size: 20,
                isLoading: false,
                selecteds: [],//列表选中列
                //新增和编辑Obj
                formObj: {
                    title: '',
                    type:'1',//1新增，2编辑
                    action: '',
                    formModel: {
                        roleIds:[],
                        sex:'0'
                    },
                    formVisible: false//编辑界面是否显示
                },
                //批量操作Obj
                batchObj:{
                    title: '',
                    action: '',
                    formModel: {
                        ids:[],
                        reasonComment:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                handleSelectionRowIndex: 0,
                sysUserRules: {
                    userName: [
                        {required: true, message: '登录名不能为空', trigger: 'blur'},
                        {max: 40, message:"输入长度不能超过40个字符", trigger: 'blur, change'}

                    ],
                    passWord: [
                        {required: true, message: '登录密码不能为空', trigger: 'blur'},
                        {max: 255, message: "输入长度不能超过255个字符", trigger: 'blur, change'}
                    ],
                    email:[
                        {max: 40, message: "邮箱最大长度不能超过40个字符", trigger: 'blur, change'}
                    ],
                    phone:[
                        {required: true, message: '请输入手机号', trigger: 'blur'},
                        {pattern: /^(0|[1-9][0-9]*)?$/, message: "只能输入数字", trigger: 'blur, change'},
                        {max: 11, message: "输入长度不能超过11个字符", trigger: 'blur, change'}
                    ],
                    sex:[
                        {required: true, message: '强选择一个性别', trigger: 'blur'},
                        {pattern: /^(0|[1-9][0-9]*)?$/, message: "只能输入数字", trigger: 'blur, change'},
                        {max: 11, message: "输入长度不能超过11个字符", trigger: 'blur, change'}
                    ],
                },
                batchRules: {
                    reasonComment: [
                        {required: true, message: '原因内容不能为空', trigger: 'blur'},
                        {max: 255, message: "输入长度不能超过255个字符", trigger: 'blur, change'}
                    ]
                },
            }
        },
        methods: {
            tableRowClassName(row, index) {
                if (row.warning === '1') {
                    console.log("index：" + index);
                    return 'info-row';
                }
                return '';
            },
            //分页查询
            loadPagination() {
                const self = this;
                let params = {
                    page: this.pagination.page === 0 ? 1 : this.pagination.page,
                    size: this.size === 0 ? 20 : this.size
                };
                if (this.sort) {
                    params.sort = this.sort;
                }
                if (this.filters.mobile && this.filters.mobile !== '') {//状态
                    params['search_like_mobile'] = this.filters.mobile.trim();
                }
                if (this.filters.userName && this.filters.userName !== '') {//状态
                    params['search_like_userName'] = this.filters.userName.trim();
                }
                if (this.filters.sexStatus && this.filters.sexStatus !== '') {
                    params['search_eq_sex'] = this.filters.sexStatus;
                }
                self.isLoading = true;
                http.get("api/sysUser", {params: params}).then(response => {
                    console.log(JSON.stringify(response));
                    response.page = response.number+1;
                    this.isLoading = false;
                    this.pagination = response;
                });
            },
            //查询条件查询
            handleSearch(ref) {
                this.$refs[ref].validate((valid) => {
                    if (valid) {
                        this.handleCurrentChange(1);
                    }
                });
            },

            upLoadMSDS(){
                this.uploadFormObj = {// 上传图片/文件
                    id: '',
                    uploadType: 'file',
                    title:"上传头像",
                    fileList: [],
                    formVisible: true,//编辑界面是否显示
                    uploadCompressUrl: "api/sysUser/uploadCompress/",
                    data: {"productId":0}
                }
            },
            //上传成功
            handleUploadSuccess(response, fileList){
                console.log("response:"+response.msg);
                console.log(fileList);
                console.log("fileList:"+response.data);
                this.uploadFormObj.fileList = Object.assign([], fileList);

            },
            //文件上传失败
            handleUploadError(err) {
                console.log(err);
            },
            //删除已上传文件
            handleRemoveFile(fileList){
                console.log(fileList);
                this.uploadFormObj.fileList = Object.assign([], fileList);
                console.log("uploadFormObj.fileList:"+this.uploadFormObj.fileList);
            },
            UploadMSDSCancel(ref){
//                console.log("close");
                this.$refs[ref].clearFiles();
                this.uploadFormObj.formVisible = false;
            },
            saveMSDSCancel(ref){
                console.log("refssss"+JSON.stringify(this.uploadFormObj.fileList[0].response));
                this.formObj.formModel.avatarImg=this.uploadFormObj.fileList[0].response.data;
                this.url=this.formObj.formModel.avatarImg;
                this.uploadFormObj.formVisible = false;
                this.$refs[ref].clearFiles();
            },
            handleClick(tab) {
                for(let j=0; j<tab.$children[0].$el.classList.length; j++){
                    if ("el-table--hidden" === tab.$children[0].$el.classList[j]){
                        tab.$children[0].$el.classList.remove("el-table--hidden");
                        tab.$children[0].$el.children[2].style.height = "635px";
                    }
                }
            },
            //新建页面
            handleAdd() {
                this.formObj = {
                    title: '系统用户-创建',
                    formVisible: true,
                    type:'1',//1新增，2编辑
                    formModel: {
                        roleIds:[],
                        sex:0,
                        avatarImg:"https://img-blog.csdnimg.cn/20190829103618321.jpg"
                    },
                };
            },
            //编辑页面
            handleEdit(index,row){
                let url="/api/sysUser/queryUserRoles/"+row.userId;
                http.get(url).then(response => {
                    console.log("用户角色List"+JSON.stringify(response));
                    row.roleIds=response.data;
                    if(response.code==='00000'){
                        this.formObj = {
                            title: '系统用户-编辑',
                            type:'2',//0，详情，1新增，2编辑
                            formVisible: true,
                            formModel: Object.assign({}, row),
                        };
                    }else{
                        this.$message.error(response.message);
                    }

                }).catch(function (error) {
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });

            },
            //显示详情
            handleView(index,row){
                this.formObj = {
                    title: '系统用户-详情',
                    type:'0',//0，详情，1新增，2编辑
                    formVisible: true,
                    formModel: Object.assign({}, row),
                };
            },
            //初始化密码
            handleInit(index,row){
                console.log("row userId"+row.userId);
                let url ="api/sysUser/initPassword/"+row.userId;
                http.get(url).then(response => {
                    if(response.code==='00000'){
                        this.$message(response.message);
                        this.loadPagination();
                    }else{
                        this.$message.error(response.message);
                    }

                }).catch(function (error) {
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },

            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.size = val;
                this.loadPagination();
            },
            handleCurrentChange(val) {
                //正在加载数据
                if(this.isLoading){
                    return;
                }
                window.console.log('this.pagination.page:' + val);
                this.pagination.page = val;
                window.console.log('排序动作:' + this.sort);
                this.loadPagination();
            },
            handleClose(ref) {//关闭表单
                this.formObj.formVisible = false;
                this.$refs[ref].resetFields();
                this.loadPagination();
            },
            changeDepartment(val,dataList){
                for (let i = 0; i < dataList.length; i++) {
                    let dataItem = dataList[i];
                    if (val === dataItem.deptId) {
                        this.formObj.formModel.deptId=dataItem.deptId;
                    }
                }
                console.groupEnd();
            },
            changeRole(val,dataList){
                for (let i = 0; i < dataList.length; i++) {
                    let dataItem = dataList[i];
                    if (val === dataItem.id) {
                        this.formObj.formModel.roleId=dataItem.id;
                    }
                }
                console.groupEnd();
            },
            //保存与修改
            handleSubmit(ref){
                this.$refs[ref].validate((valid) => {
                    console.log("valid : " + valid);
                    let url = "api/sysUser/addSysUser";
                    let method = "post";
                    if(this.formObj.formModel.userId){
                        method="put";
                        url="api/sysUser/updateSysUser";
                    }
                    console.log(JSON.stringify(this.formObj.formModel));
                    if (valid){
                        http.postOrPut(url, method, this.formObj.formModel).then(response => {
                            if(response.code==='00000'){
                                this.formObj.formVisible = false;
                                this.$refs[ref].resetFields();
                                this.loadPagination();
                            }else{
                                this.$message.error(response.message);
                            }

                        }).catch(function (error) {
                            console.log(JSON.stringify(error));
                            this.$message.error(error['data']);
                        });
                    }else {
                        this.isLoading = false;
                    }
                });
            },
            // 计算表格高度
            initData(){
                let self = this;
                // 2.0版本特殊处理
                setTimeout(function () {
                    self.heightNum = self.utils.calcTableHeight();
                },10);
                // 响应窗口大小改变
                window.onresize = function () {
                    self.heightNum = self.utils.calcTableHeight();
                };
            }
        },
        filters: {
            auditFilter: function (row) {
                if (!row) return ''
                let list=row.piclist;
                let num=0;
                for(let i=0;i<list.length;i++){
                    let info=list[i];
                    if(info.auditStatus!=1){
                        num=num+1;
                    }
                }

                return num;
            }
        },
        mounted() {
            this.initData();
            this.loadPagination();
        }
    }
</script>
<style>
    .imgClass{
        width: 100px;
        height: 100px;
        border-radius: 50%;
    }

</style>
