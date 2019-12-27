<template>
    <div class="app-container">
        <div class="prd-menu-container" :style="{height: containerHeightNum + 'px'}">
            <div class="prd-menu-container-left">
                <el-tree :data="dataInfo" :props="defaultProps" accordion @node-click="handleNodeClick"></el-tree>
            </div>
            <div class="prd-menu-container-right">
                <el-row style="padding-bottom: 0;">
                    <el-col :span="24" class="toolbar">
                        <el-form :inline="true" :model="filters" ref="filterForm">

                            <el-form-item label="部门名称" prop="name">
                                <el-input v-model="filters.name" placeholder="支持模糊查询"></el-input>
                            </el-form-item>
                            <el-form-item label="部门状态" prop="status">
                                <sd-param-radio-group v-model="filters.status" type-code="limitStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="handleSearch('filterForm')">查询</el-button>
                            </el-form-item>
                        </el-form>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24" class="toolbar">
                        <el-form :inline="true">
                            <el-form-item>
                                <el-button type="primary" size="small" @click="handleAdd">添加部门</el-button>
                            </el-form-item>
                        </el-form>
                    </el-col>
                </el-row>
                <el-table :data="pagination.content" :height="heightNum" highlight-current-row v-loading="isLoading" border>
                    <el-table-column type="index" label="NO" width="80"></el-table-column>
                    <el-table-column prop="name" label="名称" width="300"></el-table-column>
                    <el-table-column prop="level" label="层级" width="100"></el-table-column>
                    <el-table-column prop="parentName" label="父部门" width="120"></el-table-column>
                    <el-table-column prop="status" label="状态" width="120">
                        <template slot-scope="scope">
                            <p v-if="scope.row.status===0">启用</p>
                            <p v-else-if="scope.row.status===1">禁用</p>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" min-width="150" fixed="right">
                        <template slot-scope="scope">
                            <el-button type="primary"  @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                            <el-button @click="changeStatus(scope.$index, scope.row)" type="primary">{{scope.row.status==0 ? '禁用' : '启用'}}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pagination" v-if='pagination.totalElements > 0'>
                    <el-pagination layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                                   @current-change="handleCurrentChange" :current-page.sync="pagination.page"
                                   :page-size="size" :page-sizes="[10, 20, 50, 100]"
                                   :total="pagination.totalElements" :disabled="isLoading">
                    </el-pagination>
                </div>
            </div>
        </div>

        <section>
            <el-dialog :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                <el-form :inline="true" :model="formObj.formModel" :rules="categoryRule" label-width="110px" ref="reasonForm">
                    <table class="productListTable productListTable-col4">
                        <tr>
                            <td>
                                <el-form-item label="上级部门" prop="parentName">
                                    <el-input auto-complete="off" disabled="disabled" style="width: 400px;" v-model="formObj.formModel.parentName"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="层级" prop="level">
                                    <el-input  auto-complete="off" disabled="disabled" style="width: 400px;" v-model="formObj.formModel.level"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="部门名称" prop="name">
                                    <el-input auto-complete="off" v-model="formObj.formModel.name"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="状态">
                                    <el-radio-group v-model="formObj.formModel.status">
                                        <el-radio class="radio" :label="0">启用</el-radio>
                                        <el-radio class="radio" :label="1">禁用</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="顺序" prop="orderNum">
                                    <el-input auto-complete="off" v-model="formObj.formModel.orderNum"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                    </table>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('reasonForm')">取消</el-button>
                    <el-button type="primary"  @click="handleSubmit('reasonForm')">提交</el-button>
                </div>
            </el-dialog>
        </section>
    </div>
</template>


<script>
    import http from '../../axios/http';

    export default {
        name: 'sysMenu',
        data() {
            return {
                heightNum: 0,
                containerHeightNum: 0,
                dataInfo: [],
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                filters: {
                    status:'',
                    name: ''
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                size: 20,
                data:{
                    id:'',
                    level:'',
                    name:''
                },
                isLoading: false,
                selecteds: [],//列表选中列
                formObj: {
                    title: '',
                    action: '',
                    formModel: {
                        level:'',
                        orderNum:0,
                        status:'0',
                        name:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                categoryRule: {
                    name: [
                        {required: true, message: '输入部门名称', trigger: 'blur'}
                    ]

                }
            }
        },
        methods: {
            handleNodeClick(data) {
                console.log(data.id+","+data.name+","+data.level);
                this.data.id=data.id;
                this.data.name=data.name;
                this.data.level=data.level;
                let params = {
                    page: this.pagination.page === 0 ? 1 : this.pagination.page,
                    size: this.size === 0 ? 20 : this.size
                };
                if (this.sort) {
                    params.sort = this.sort;
                }
                params['search_eq_deptId'] = data.id;
                http.get("api/sysDept/", {params: params}).then(response => {
                    this.isLoading = false;
                    this.pagination = response;
                    window.console.log("=======================this.pagination: " + JSON.stringify(this.pagination));
                });


            },
            loadPagination() {
                const self = this;
                window.console.log("this.pagination.page:" + self.pagination.page);
                let params = {
                    page: this.pagination.page === 0 ? 1 : this.pagination.page,
                    size: this.size === 0 ? 20 : this.size
                };
                if (this.sort) {
                    params.sort = this.sort;
                }
                if (this.filters.status && this.filters.status !== '') {
                    params['search_eq_status'] = this.filters.status;
                }
                if (this.filters.name && this.filters.name !== '') {
                    params['search_like_name'] = this.filters.name.trim();
                }
                self.isLoading = true;
                http.get("api/sysDept/query", {params: params}).then(response => {
                    console.log("dataInfo="+JSON.stringify(response))
                    this.isLoading = false;
                    this.dataInfo = response;
                    window.console.log("=======================all this.pagination: " + JSON.stringify(this.pagination));
                });
            },
            searchPagination() {
                const self = this;
                this.data={};
                window.console.log("this.pagination.page:" + self.pagination.page);
                let params = {
                    page: this.pagination.page === 0 ? 1 : this.pagination.page,
                    size: this.size === 0 ? 20 : this.size
                };
                if (this.sort) {
                    params.sort = this.sort;
                }
                if (this.filters.status && this.filters.status !== '') {
                    params['search_eq_status'] = this.filters.status;
                }
                if (this.filters.name && this.filters.name !== '') {
                    params['search_like_name'] = this.filters.name.trim();
                }
                self.isLoading = true;
                http.get("api/sysDept", {params: params}).then(response => {
                    this.isLoading = false;
                    this.pagination = response;
                    window.console.log("=======================this.pagination: " + JSON.stringify(this.pagination));
                });
            },
            handleSearch(ref) {
                this.$refs[ref].validate((valid) => {
                    if (valid) {
                        this.searchPagination();
                    }
                });
            },
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.size = val;
                this.searchPagination();
            },
            handleCurrentChange(val) {
                //正在加载数据
                if(this.isLoading){
                    return;
                }
                window.console.log('this.pagination.page:' + val);
                this.pagination.page = val;
                window.console.log('排序动作:' + this.sort);
                this.searchPagination();
            },
            //部门编辑
            handleEdit(index,row){
                this.formObj = {
                    title: '部门-编辑',
                    formVisible: true,
                    formModel: Object.assign({}, row),
                };
            },
            //部门新增
            handleAdd() {
                console.log(JSON.stringify(this.data));
                if(this.data.id==undefined||this.data.id==''){
                    this.$alert("请选择一个菜单");
                    return;
                }
                this.formObj = {
                    title: '部门-创建',
                    formVisible: true,
                    formModel: {
                        level:this.data.level+1,
                        status:0,
                        parentName:this.data.name
                    },
                };
            },
            //启用禁用
            changeStatus(index, row) {
                if (row.status === 0) {
                    status = 1;
                } else {
                    status = 0;
                }
                let url = "api/sysDept/" + row.deptId + "/" + status;
                http.put(url).then(response => {
                    if(response.code==='00000'){
                        this.searchPagination();
                    }else{
                        this.$message.error(respons.message);
                    }
                }).catch(function (error) {
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },
            handleSubmit(ref) {//提交表单
                console.log("提交表单");
                this.$refs[ref].validate((valid) => {
                    console.log("valid : " + valid);
                    let url = "api/sysDept";
                    let method = "post";
                    console.log(JSON.stringify(this.formObj.formModel));
                    console.log(this.formObj.formModel.deptId);
                    if (this.formObj.formModel.deptId) {
                        method = "put";

                    }else{
                        this.formObj.formModel.parentId=this.data.id;
                    }
                    if (valid) {
                        this.isLoading = true;
                        http.postOrPut(url, method, this.formObj.formModel).then(response => {
                            if(response.code=='00000'){
                                this.$alert("修改成功");
                                this.isLoading = false;
                                this.formObj.formVisible = false;
                                this.filters.name=this.formObj.formModel.name;
                                this.searchPagination();
                                this.loadPagination();
                            }else{
                                this.$alert(response.msg);
                            }
                            this.$refs[ref].resetFields();

                        }).catch(function (error) {
                            this.isLoading = true;
                            console.log(JSON.stringify(error));
                            this.$message.error(error['data']);
                        });
                    } else {
                        this.isLoading = false;
                    }
                });
            },
            handleClose(ref) {//关闭表单
                this.formObj.formVisible = false;
                this.$refs[ref].resetFields();
                //this.handleReset(ref);
            },
            initData(){
                // 计算表格高度
                let self = this;
                // 2.0版本特殊处理
                setTimeout(function () {
                    self.heightNum = self.utils.calcTableHeight();
                    self.containerHeightNum = (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - 45;
                },10);
                // 响应窗口大小改变
                window.onresize = function () {
                    self.heightNum = self.utils.calcTableHeight();
                    self.containerHeightNum = (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - 45;
                };
            }
        },
        mounted() {
            this.initData();
            this.loadPagination();
        }
    }
</script>

<style scoped>
    .el-tree{
        border: 0;
    }
    .prd-menu-container {
        width: 100%; overflow-x: auto;
    }
    .prd-menu-container-left{
        width: 20%; height: 100%; float: left; overflow-y: auto; overflow-x: hidden; border-right: 3px solid #d1dbe5;
    }
    .prd-menu-container-right{
        width: 78%; height: 100%; float: left; overflow-y: hidden; overflow-x: hidden; margin-left: 20px;
    }
    @media screen and (max-width: 1350px) {
        .prd-menu-container{ width: 1170px; overflow-x: auto; overflow-y: hidden;}
        .prd-menu-container-left{width: 240px;}
        .prd-menu-container-right{width: 900px;}
    }
</style>
