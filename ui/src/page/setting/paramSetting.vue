<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" label-width="120px">
                    <el-form-item label="总参名">
                        <el-input size="small" v-model="filters.paramValue" placeholder="请输入总参名"></el-input>
                    </el-form-item>
                    <el-form-item label="总参编号">
                        <el-input size="small" v-model="filters.paramCode" placeholder="请输入总参编号"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="small" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                        <el-button type="success" size="small" icon="el-icon-plus" @click="handleAdd">添加总参</el-button>
                        <el-button type="success" size="small" icon="el-icon-plus" @click="handleSubAdd" v-if="parentCode">添加子参</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>

        <el-row>
            <el-col :span="12" style="padding-right: 20px;">
                <el-table :data="pagination.content" highlight-current-row v-loading="isLoading" border stripe :height="heightNum" @cell-click="cellMouseEnter">
                    <el-table-column type="index" width="50" align="center"> </el-table-column>
                    <el-table-column prop="paramCode" label="总参编号" min-width="150" align="center"></el-table-column>
                    <el-table-column prop="paramValue" label="总参名" min-width="150" align="center"></el-table-column>
                    <el-table-column prop="paramValueEn" label="总参英文名" min-width="160" align="center"></el-table-column>
                    <!--<el-table-column prop="paramType" :label="$t('pages.paramSetting.table.paramType')" min-width="120" align="center"></el-table-column>-->
                    <el-table-column label="操作" min-width="200" align="center">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" icon="el-icon-delete" plain @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!--分页开始-->
                <div class="pagination" v-if='pagination.totalElements > 0'>
                    <el-pagination layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                                   @current-change="handleCurrentChange" :current-page.sync="pagination.page"
                                   :page-size="size" :page-sizes="[10, 20, 50, 100]"
                                   :total="pagination.totalElements" :disabled="isLoading">
                    </el-pagination>
                </div>
                <!--分页结束-->
            </el-col>
            <el-col :span="12">
                <el-table :data="subPagination.content" highlight-current-row v-loading="isLoading" border stripe :height="heightNum">
                    <el-table-column type="index" width="50" align="center"> </el-table-column>
                    <el-table-column prop="paramDetailCode" label="子参编号" min-width="150" align="center"></el-table-column>
                    <el-table-column prop="paramDetailCname" label="子参名" min-width="160" align="center"></el-table-column>
                    <el-table-column prop="paramDetailEname" label="子参英文名" min-width="165" align="center"></el-table-column>
                    <el-table-column prop="sortNum" label="排序" min-width="100" align="center"></el-table-column>
                    <el-table-column label="操作" min-width="200" align="center">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleSubEdit(scope.$index, scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" icon="el-icon-delete" plain @click="handleSubDelete(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!--分页开始-->
                <div class="pagination" v-if='subPagination.totalElements > 0'>
                    <el-pagination layout="total, sizes, prev, pager, next, jumper" @size-change="handleSubSizeChange"
                                   @current-change="handleSubCurrentChange" :current-page.sync="subPagination.page"
                                   :page-size="subSize" :page-sizes="[10, 20, 50, 100]"
                                   :total="subPagination.totalElements" :disabled="isLoading">
                    </el-pagination>
                </div>
                <!--分页结束-->
            </el-col>
        </el-row>

        <section>
            <!--新增|编辑 界面-->
            <el-dialog custom-class="col1-dialog" :title="formObj.title" :visible.sync="formObj.dialogVisible" :close-on-click-modal="false" @close="handleClose('newParamForm')">
                <el-form :model="formObj.formModel" label-width="140px" label-position="right" :rules="rules" ref="newParamForm">
                    <el-form-item label="总参编号" prop="paramCode">
                        <el-input v-model="formObj.formModel.paramCode" auto-complete="off" @blur="checkCode(formObj.formModel.paramCode, true)"></el-input>
                    </el-form-item>
                    <el-form-item label="总参名" prop="paramValue">
                        <el-input v-model="formObj.formModel.paramValue" auto-complete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="总参英文名" prop="paramValueEn">
                        <el-input v-model="formObj.formModel.paramValueEn" auto-complete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="总参说明" prop="paramNote">
                        <el-input v-model="formObj.formModel.paramNote" auto-complete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('newParamForm')">取消</el-button>
                    <el-button type="primary" @click="handleSubmit('newParamForm')" :loading="isLoading">提交</el-button>
                </div>
            </el-dialog>

            <el-dialog custom-class="col1-dialog" :title="formSubObj.title" :visible.sync="formSubObj.dialogSubVisible" :close-on-click-modal="false" @close="handleSubClose('newParamDetailForm')">
                <el-form :model="formSubObj.formModel" label-width="100px" label-position="right" :rules="childRules" ref="newParamDetailForm">
                    <el-form-item prop="paramDetailCode" label="子参编号" >
                        <el-input v-model="formSubObj.formModel.paramDetailCode" auto-complete="off" @blur="checkCode(formSubObj.formModel.paramDetailCode, false)"></el-input>
                    </el-form-item>
                    <el-form-item prop="paramDetailCname" label="子参名" >
                        <el-input v-model="formSubObj.formModel.paramDetailCname" auto-complete="off"></el-input>
                    </el-form-item>
                    <el-form-item prop="paramDetailEname" label="子参英文名" >
                        <el-input v-model="formSubObj.formModel.paramDetailEname" auto-complete="off"></el-input>
                    </el-form-item>
                    <el-form-item prop="sortNum" label="排序" >
                        <el-input v-model="formSubObj.formModel.sortNum" auto-complete="off"></el-input>
                    </el-form-item>
                    <el-form-item prop="paramDetailNote" label="子参说明" >
                        <el-input v-model="formSubObj.formModel.paramDetailNote" auto-complete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleSubClose('newParamDetailForm')">取消</el-button>
                    <el-button type="primary" @click="handleSubSubmit('newParamDetailForm')" :loading="isLoading">提交</el-button>
                </div>
            </el-dialog>
        </section>
    </div>
</template>


<script>
    import http from '../../axios/http';
    import {isNotEmpty} from "../../filter";

    export default {
        name: 'paramSetting',
        data() {
            return {
                heightNum: 0,
                filters: {
                    paramValue: '',
                    paramCode: ''
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                size: 20,
                subPagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                subSize: 20,
                isLoading: false,
                isSubLoading: false,
                parentCode: '',
                beforeCode: '',

                formObj: {
                    dialogVisible: false,//界面是否显示
                    formModel: {
                        paramNote: '',
                        paramValue:'',
                        paramValueEn:'',
                        paramCode: '',
                        // paramType: ''
                    }
                },
                rules: {
                    paramCode: [
                        { required: true, message: '请输入总参编号', trigger: 'blur' }
                    ],
                    paramValue: [
                        { required: true, message: '请输入总参中文名', trigger: 'blur' }
                    ],
                    paramValueEn: [
                        { required: true, message: '请输入总参英文名', trigger: 'blur' }
                    ]
                },

                formSubObj: {
                    dialogSubVisible: false,
                    formModel: {
                        paramDetailCode: '',
                        paramDetailCname:'',
                        paramDetailEname:'',
                        paramDetailNote: '',
                        paramCode:'',
                        sortNum:''
                    }
                },
                childRules: {
                    paramDetailCode: [
                        { required: true, message: '请输入子参编号', trigger: 'blur' }
                    ],
                    paramDetailCname: [
                        { required: true, message: '请输入子参中文名', trigger: 'blur' }
                    ],
                    paramDetailEname: [
                        { required: true, message: '请输入子参英文名', trigger: 'blur' }
                    ]
                }
            }
        },
        methods: {
            //搜索-执行搜索
            handleSearch() {
                this.handleCurrentChange(1);
            },
            //搜索-总参切换每页条数
            handleSizeChange(val) {
                this.size = val;
                this.loadPagination();
            },
            //搜索-总参切换页码
            handleCurrentChange(val) {
                this.pagination.page = val;
                this.loadPagination();
            },
            //搜索-加载总参列表数据
            loadPagination() {
                const self = this;
                let params = {
                    page: self.pagination.page === 0 ? 1 : self.pagination.page,
                    size: self.size
                };
                if (isNotEmpty(this.filters.paramValue)) {
                    params['search_like_paramValue'] = this.filters.paramValue;
                }
                if (isNotEmpty(this.filters.paramCode)) {
                    params['search_like_paramCode'] = this.filters.paramCode;
                }
                self.isLoading = true;
                params['search_eq_deleteFlag'] = 0;
                http.get("api/params/queryParamsInfo", {params:params}).then(response => {
                    this.isLoading = false;
                    response.page = response.number+1;
                    this.pagination = response;
                });
                this.subPagination = [];
                this.parentCode = null;
            },
            //搜索-子参切换每页条数
            handleSubSizeChange(val) {
                this.size = val;
                this.loadSubPagination();
            },
            //搜索-子参切换页码
            handleSubCurrentChange(val) {
                this.subPagination.page = val;
                this.loadSubPagination();
            },
            //搜索-加载子参列表数据
            loadSubPagination() {
                const self = this;
                let params = {
                    page: self.subPagination.page === 0 ? 1 : self.subPagination.page,
                    size: self.subSize
                };
                if (isNotEmpty(this.parentCode)) {
                    params['search_eq_paramCode'] = this.parentCode;
                }
                params['search_eq_deleteFlag'] = 0;
                self.isSubLoading = true;
                http.get("api/params/queryParamsDetailInfo", {params : params}).then(response => {
                    this.isSubLoading = false;
                    response.page = response.number+1;
                    this.subPagination = response;
                });
            },

            //点击单元格事件，查询对应的子参列表
            cellMouseEnter(row, column, cell, event){
                this.parentCode = row.paramCode;
                this.loadSubPagination();
            },
            //检查code是否重复
            checkCode(code, isParent, callback){
                if (!code) {
                    callback ? callback(false) : null
                    // if(callback){
                    //     callback(false);
                    // }
                    return false;
                }
                console.log("check before:" + this.beforeCode);
                console.log("check now:" + code);
                if (this.beforeCode == code) {
                    callback ? callback(true) : null
                    // if(callback){
                    //     callback(true);
                    // }
                    return true;
                }
                let params = {
                    isParent: isParent
                };
                if (isParent) {
                    params.parentCode = code;
                } else {
                    params.parentCode = this.parentCode;
                    params.sonCode = code;
                }
                http.get("api/params/checkCode", {params: params}).then(response => {
                    if(response == false){
                        if (isParent) {
                            this.formObj.formModel.paramCode = '';
                        }  else {
                            this.formSubObj.formModel.paramDetailCode = '';
                        }
                        this.$message.error("编号重复");
                        // if(callback){
                        //     callback(false)
                        // }
                        callback ? callback(false) : null
                    }else{
                        // if(callback){
                        //     callback(true)
                        // }
                        callback ? callback(true) : null
                    }
                });
            },

            //总参-打开新增界面
            handleAdd() {
                this.formObj = {
                    dialogVisible : true,
                    title : "新增总参",
                    formModel : {
                        paramNote: '',
                        paramValue: '',
                        paramCode: '',
                        // paramType: ''
                    }
                }
                this.beforeCode = null;
            },
            //总参-打开编辑界面
            handleEdit(index, row) {
                this.formObj = {
                    dialogVisible : true,
                    title : "编辑总参",
                    formModel : Object.assign(this.formObj.formModel, row)
                }
                this.beforeCode = row.paramCode;
            },
            //总参-删除行
            handleDelete(index, row) {
                this.$confirm("确认删除？", "提示", {
                    type: 'warning'
                }).then(() => {
                    http.delete("api/params/deleteParamsModel/" + row.paramId).then(response => {
                        if(response == true){
                            this.isLoading = false;
                            this.loadPagination();
                        }
                    });
                })
            },
            //总参-新增/编辑 提交表单
            handleSubmit(ref) {
                const self = this;
                this.$refs[ref].validate((valid) => {
                    this.checkCode(this.formObj.formModel.paramCode, true, function (res) {
                        console.log(res);
                        if(valid && res) {
                            self.isLoading = true;
                            let method = "post"
                            let postUrl = "api/params/insertParamsModel";
                            if(self.formObj.formModel.paramId) {
                                method = "put"
                                postUrl = "api/params/updateParamsModel";
                            }
                            http.postOrPut(postUrl, method, self.formObj.formModel).then(response => {
                                if(response == true){
                                    self.isLoading = false;
                                    // 关闭页面
                                    self.handleClose(ref)
                                    // 加载数据
                                    self.loadPagination();
                                }
                            });
                        } else {
                            self.isLoading = false;
                        }
                    })
                });
            },
            //总参-新增/编辑 关闭表单
            handleClose(ref) {
                this.formObj.dialogVisible = false;
                this.$refs[ref].resetFields();
            },

            //子参-打开新增界面
            handleSubAdd() {
                this.formSubObj = {
                    dialogSubVisible : true,
                    title : "新增子参",
                    formModel : {
                        paramCode: this.parentCode,
                        paramDetailCode: null,
                        paramDetailName:null,
                        paramDetailNote: null,
                        sortNum:this.subPagination.content.length
                    }
                }
                this.beforeCode = null;
            },
            //子参-打开编辑界面
            handleSubEdit(index, row) {
                this.formSubObj = {
                    dialogSubVisible : true,
                    title : "编辑子参",
                    formModel : Object.assign(this.formSubObj.formModel, row)
                }
                this.beforeCode = row.paramDetailCode;
            },
            //子参-删除行
            handleSubDelete(index, row) {
                this.$confirm("确认删除？", "提示", {
                    type: 'warning'
                }).then(() => {
                    http.delete("api/params/deleteParamsDetailModel/" + row.paramDetailId).then(response => {
                        if(response == true){
                            this.isSubLoading = false;
                            this.loadSubPagination();
                        }
                    });
                })
            },
            //子参-新增/编辑 提交表单
            handleSubSubmit(ref) {
                const self = this;
                this.$refs[ref].validate((valid) => {
                    this.checkCode(this.formSubObj.formModel.paramDetailCode, false, function (res) {
                        if(valid && res) {
                            self.isSubLoading = true;
                            let method = "post"
                            let postUrl = "api/params/insertParamsDetailModel";
                            if(self.formSubObj.formModel.paramDetailId) {
                                method = "put"
                                postUrl = "api/params/updateParamsDetailModel";
                            }
                            http.postOrPut(postUrl, method, self.formSubObj.formModel).then(response => {
                                if(response == true){
                                    self.isSubLoading = false;
                                    // 关闭页面
                                    self.handleSubClose(ref) ;
                                    // 加载数据
                                    self.loadSubPagination();
                                }  else {
                                    self.isSubLoading = false;
                                }
                            });
                        }
                    });
                });
            },
            //子参-关闭表单
            handleSubClose(ref) {
                this.formSubObj.dialogSubVisible = false;
                this.$refs[ref].resetFields();
            },

            // 计算表格高度
            initData() {
                let self = this;
                // 2.0版本特殊处理
                setTimeout(function () {
                    self.heightNum = self.utils.calcTableHeight();
                },10);
                // 响应窗口大小改变
                window.onresize = function () {
                    self.heightNum = self.utils.calcTableHeight();
                };
            },
        },
        mounted() {
            this.initData();
            this.loadPagination();
        }
    }
</script>
<style>
</style>
