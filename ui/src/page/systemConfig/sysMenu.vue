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
                            <el-form-item label="菜单编号" prop="menuCode">
                                <el-input v-model="filters.menuCode" placeholder="支持模糊查询"></el-input>
                            </el-form-item>
                            <el-form-item label="菜单名称" prop="menuName">
                                <el-input v-model="filters.menuName" placeholder="支持模糊查询"></el-input>
                            </el-form-item>
                            <el-form-item label="菜单状态" prop="status">
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
                                <el-button type="primary" size="small" @click="handleAdd">添加菜單</el-button>
                            </el-form-item>
                        </el-form>
                    </el-col>
                </el-row>
                <el-table :data="pagination.content" :height="heightNum" highlight-current-row v-loading="isLoading" border>
                    <el-table-column type="index" label="NO" width="80"></el-table-column>
                    <el-table-column prop="menuCode" label="菜单编号" width="220"></el-table-column>
                    <el-table-column prop="menuName" label="菜单名称" width="300"></el-table-column>
                    <el-table-column prop="menuEname" label="菜单英文名称" width="300"></el-table-column>
                    <el-table-column prop="menuLevel" label="层级" width="100"></el-table-column>
                    <el-table-column prop="parentName" label="父菜单" width="120"></el-table-column>
                    <el-table-column prop="status" label="状态" width="120">
                        <template slot-scope="scope">
                            <p v-if="scope.row.status===0">启用</p>
                            <p v-else-if="scope.row.status===1">禁用</p>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" min-width="150" fixed="right">
                        <template slot-scope="scope">
                            <el-button type="primary" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
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
                                <el-form-item label="上级类型" prop="parentName">
                                    <el-input auto-complete="off" disabled="disabled" style="width: 400px;" v-model="formObj.formModel.parentName"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="层级" prop="menuLevel">
                                    <el-input  auto-complete="off" disabled="disabled" style="width: 400px;" v-model="formObj.formModel.menuLevel"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                      <tr>
                            <td v-if="formObj.formModel.menuLevel===1">
                                <el-form-item label="菜单类型" prop="menuType">
                                    <sd-param-group-for-test v-model="formObj.formModel.menuType" value="0" disabled="true" type-code="menuType" :hasAll="true" @change="changeMenuType()"></sd-param-group-for-test>
                                </el-form-item>
                            </td>
                          <td v-if="formObj.formModel.menuLevel!=1">
                              <el-form-item label="菜单类型" prop="menuType">
                                  <sd-param-group-for-test v-model="formObj.formModel.menuType" type-code="menuType" :hasAll="true" @change="changeMenuType()"></sd-param-group-for-test>
                              </el-form-item>
                          </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="菜单名称" prop="menuName">
                                    <el-input auto-complete="off" v-model="formObj.formModel.menuName"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="菜单英文名称" prop="menuEname">
                                    <el-input auto-complete="off" v-model="formObj.formModel.menuEname"></el-input>
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
                                <el-form-item label="菜单链接" prop="menuUrl">
                                    <el-input auto-complete="off" v-model="formObj.formModel.menuUrl"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="菜单顺序" prop="menuOrder">
                                    <el-input auto-complete="off" v-model="formObj.formModel.menuOrder"></el-input>
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
    import SdParamGroupForTest from "../../components/sdpmb/sdParamGroupForTest";

    export default {
        components: {SdParamGroupForTest},
        name: 'sysMenu',
        data() {
            return {
                heightNum: 0,
                containerHeightNum: 0,
                dataInfo: [],
                defaultProps: {
                    children: 'children',
                    label: 'menuName'
                },
                filters: {
                    menuName: '',
                    status:0,
                    menuCode: ''
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                size: 20,
                data:{
                    id:'',
                    menuLevel:'',
                    menuName:''
                },
                isLoading: false,
                selecteds: [],//列表选中列
                formObj: {
                    title: '',
                    action: '',
                    formModel: {
                        menuLevel:'',
                        menuCode:'',
                        menuUrl:'',
                        status:0,
                        menuOrder:0,
                        menuName:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                categoryRule: {
                    menuEname: [
                        {required: true, message: '请输入菜单英文名称', trigger: 'blur'},
                        {min:1,max: 100, message: "1到100个字符", trigger: 'blur, change'}
                    ],
                    menuName: [
                        {required: true, message: '请输入菜单名称', trigger: 'blur'},
                        {min:1,max: 100, message: "1到100个字符", trigger: 'blur, change'}
                    ],
                    menuType:[
                        {required: true, message: '请选择菜单类型', trigger: 'blur'}
                    ]
                }
            }
        },
        methods: {
            handleNodeClick(data) {
                console.log(data.id+","+data.menuName+","+data.level);
                this.data.id=data.id;
                this.data.menuName=data.menuName;
                this.data.menuLevel=data.menuLevel;
                let params = {
                    page: this.pagination.page === 0 ? 1 : this.pagination.page,
                    size: this.size === 0 ? 20 : this.size
                };
                if (this.sort) {
                    params.sort = this.sort;
                }
                params['search_eq_id'] = data.id;
                http.get("api/sysMenu/", {params: params}).then(response => {
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
                if (this.filters.menuCode && this.filters.menuCode !== '') {
                    params['search_like_menuCode'] = this.filters.menuCode.trim();
                }
                if (this.filters.menuName && this.filters.menuName !== '') {
                    params['search_like_menuName'] = this.filters.menuName.trim();
                }
                self.isLoading = true;
                http.get("api/sysMenu/query", {params: params}).then(response => {
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
                if (this.filters.menuName && this.filters.menuName !== '') {
                    params['search_like_menuName'] = this.filters.menuName.trim();
                }
                if (this.filters.menuCode && this.filters.menuCode !== '') {
                    params['search_like_menuCode'] = this.filters.menuCode.trim();
                }
                self.isLoading = true;
                http.get("api/sysMenu", {params: params}).then(response => {
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
            //启用禁用
            changeStatus(index, row) {
                if (row.status === 0) {
                    status = 1;
                } else {
                    status = 0;
                }
                let url = "api/sysMenu/" + row.id + "/" + status;
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
            //菜单编辑
            handleEdit(index,row){
                this.formObj = {
                    title: '菜单-编辑',
                    formVisible: true,
                    formModel: Object.assign({}, row),
                };
            },
            //菜单新增
            handleAdd() {
                console.log(JSON.stringify(this.data));
                if(this.data.id==undefined||this.data.id==''){
                    this.$alert("请选择一个菜单");
                    return;
                }
                this.formObj = {
                    title: '菜单-创建',
                    formVisible: true,
                    formModel: {
                        menuLevel:this.data.menuLevel+1,
                        parentName:this.data.menuName,
                        menuCode:'',
                        menuUrl:'',
                        status:0,
                        menuOrder:0,
                        menuType:0,
                        menuName:''
                    },
                };
            },
            changeMenuType(row,list){

            },
            handleSubmit(ref) {//提交表单
                this.$refs[ref].validate((valid) => {
                    console.log("valid : " + valid);
                    let url = "api/sysMenu";
                    let method = "post";
                    console.log(JSON.stringify(this.formObj.formModel));
                    console.log(this.formObj.formModel.id);
                    if (this.formObj.formModel.id) {
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
                                this.filters.menuName=this.formObj.formModel.menuName;
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
