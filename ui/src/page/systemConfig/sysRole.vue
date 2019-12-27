<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" ref="filterForm">
                    <el-form-item label="角色代码" prop="roleCode">
                        <el-input v-model="filters.roleCode" placeholder="支持模糊搜索"></el-input>
                    </el-form-item>
                    <el-form-item label="角色名称" prop="roleName">
                        <el-input v-model="filters.roleName" placeholder="支持模糊搜索"></el-input>
                    </el-form-item>
                    <el-form-item label="角色状态" prop="status">
                        <sd-param-radio-group v-model="filters.status" type-code="limitStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
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
            <el-table-column prop="roleCode" label="角色编号" min-width="120"></el-table-column>
            <el-table-column prop="roleName" label="角色名称" min-width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
                <template slot-scope="scope">
                    <p v-if="scope.row.status===0">启用</p>
                    <p v-else-if="scope.row.status===1">禁用</p>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column  label="操作" width="350" fixed="right">
                <template slot-scope="scope">
                    <el-button @click="handleView(scope.$index, scope.row)" type="primary">详情</el-button>
                    <el-button @click="handleEdit(scope.$index, scope.row)" type="primary">编辑</el-button>
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
                <el-form :inline="true" :model="formObj.formModel" :rules="sysRoleRules" label-width="80px" ref="sysRoleForm">
                   <!-- <el-form-item label="角色编号" prop="roleCode">
                        <el-input v-model="formObj.formModel.roleCode"></el-input>
                    </el-form-item>-->
                    <el-form-item label="角色名称" prop="roleName">
                        <el-input v-model="formObj.formModel.roleName"></el-input>
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="formObj.formModel.status">
                            <el-radio class="radio" :label="0">启用</el-radio>
                            <el-radio class="radio" :label="1">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="角色备注" prop="comment">
                        <el-input v-model="formObj.formModel.comment"></el-input>
                    </el-form-item>
                </el-form>
                <template>
                    <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="菜单" name="menu">
                            <el-tree
                                :data="formObj.formModel.menuList"
                                show-checkbox
                                default-expand-all
                                node-key="id"
                                ref="tree"
                                :default-checked-keys="resourceCheckedKey"
                                highlight-current
                                :props="defaultProps">
                            </el-tree>
                        </el-tab-pane>
                        <el-tab-pane label="数据权限" name="data">
                            <el-tree
                                :data="formObj.formModel.dataList"
                                show-checkbox
                                default-expand-all
                                node-key="id"
                                ref="dataTree"
                                :default-checked-keys="dataCheckedKey"
                                highlight-current
                                :props="defaultDatas">
                            </el-tree>
                        </el-tab-pane>
                    </el-tabs>
                </template>


                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('sysRoleForm')">取消</el-button>
                    <el-button type="primary"  @click="handleSubmit('sysRoleForm')">提交</el-button>
                </div>
            </el-dialog>
            <!--详情-->
            <el-dialog v-if="formObj.type=='0'"  :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                <el-form :inline="true" :model="formObj.formModel" label-width="110px" ref="reasonForm">
                    <table class="productListTable productListTable-col4">
                        <tr>
                            <td>
                                <el-form-item label="角色编号" prop="roleCode">
                                    {{formObj.formModel.roleCode}}
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="角色名称" prop="roleName">
                                    {{formObj.formModel.roleName}}
                                </el-form-item>
                            </td>
                        </tr>

                        <tr>
                            <el-form-item label="状态" prop="status">
                                <el-radio-group v-model="formObj.formModel.status">
                                    <el-radio class="radio" :label="0">启用</el-radio>
                                    <el-radio class="radio" :label="1">禁用</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </tr>

                        <tr>
                            <td>
                                <el-form-item label="角色备注" prop="comment">
                                    {{formObj.formModel.comment}}
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
                    </table>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('reasonForm')">取消</el-button>
                </div>
            </el-dialog>
        </section>



    </div>
</template>
<script>
    import http from '../../axios/http';

    export default {
        name: 'sysRole',
        data() {
            return {
                heightNum: 0,
                activeName: 'menu',
                filters: {
                    roleCode:'',
                    status:'',
                    roleName:''
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                resourceCheckedKey:[],
                dataCheckedKey:[],
                size: 20,
                isLoading: false,
                selecteds: [],//列表选中列
                //新增和编辑Obj
                formObj: {
                    title: '',
                    type:'1',//1新增，2编辑
                    action: '',
                    formModel: {
                        id:0,
                        roleName:'',
                        roleCode:'',
                        status:'0',
                        menuList:[],
                        dataList:[]
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

                defaultProps: {
                    children: 'children',
                    label: 'menuName'
                },
                defaultDatas: {
                    children: 'children',
                    label: 'name'
                },
                handleSelectionRowIndex: 0,
                sysRoleRules: {
                    roleName: [
                        {required: true, message: '登录名不能为空', trigger: 'blur'},
                        {max: 40, message:"输入长度不能超过40个字符", trigger: 'blur, change'}

                    ],
                    roleCode:[
                        {required: true, message: '请输入手机号', trigger: 'blur'},
                        {pattern: /^(0|[1-9][0-9]*)?$/, message: "只能输入数字", trigger: 'blur, change'},
                        {max: 11, message: "输入长度不能超过11个字符", trigger: 'blur, change'}
                    ]
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
                if (this.filters.status && this.filters.status !== '') {//状态
                    params['search_eq_status'] = this.filters.status;
                }
                if (this.filters.roleCode && this.filters.roleCode !== '') {//状态
                    params['search_like_roleCode'] = this.filters.roleCode.trim();
                }
                if (this.filters.roleName && this.filters.roleName !== '') {//状态
                    params['search_like_roleName'] = this.filters.roleName.trim();
                }

                self.isLoading = true;
                http.get("api/sysRole", {params: params}).then(response => {
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
                http.get("api/sysMenu/queryData").then(response => {
                    console.log("查询菜单："+response);
                    if(response!=null){
                        this.formObj = {
                            type:1,
                            title: '系统角色-创建',
                            formVisible: true,
                            formModel: {
                                roleName:'',
                                roleCode:'',
                                status:0,
                                menuList:response.data.menus,
                                dataList:response.data.depts,
                            },
                        };
                    }else{
                        this.$message("没有查到菜单");
                    }
                });
            },
            //编辑页面
            handleEdit(index,row){
                http.get("api/sysRole/checkMenu/"+row.id).then(response => {
                    console.log("編輯是菜单："+JSON.stringify(response));
                    if(response.code='00000'){
                        this.formObj = {
                            type:2,
                            title: '系统角色-編輯',
                            formVisible: true,
                            formModel: {
                                id:row.id,
                                roleName:row.roleName,
                                roleCode:row.roleCode,
                                status:row.status,
                                comment:row.comment,
                                menuList:response.data.menuList,
                                dataList:response.data.deptList
                            },
                        };
                        this.resourceCheckedKey=response.data.checks;
                        this.dataCheckedKey=response.data.checksDept;
                    }else{
                        this.$message("没有查到菜单");
                    }
                });
            },

            //显示详情
            handleView(index,row){
                this.formObj = {
                    title: '系统角色-详情',
                    type:'0',//0，详情，1新增，2编辑
                    formVisible: true,
                    formModel: Object.assign({}, row),
                };
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

            //保存与修改
            handleSubmit(ref){
                console.log("nodes===>"+this.$refs.tree.getCheckedNodes());
                console.log("keys===>"+this.$refs.tree.getCheckedKeys());
                this.$refs[ref].validate((valid) => {
                    console.log("valid : " + valid);
                    let url = "api/sysRole";
                    let method = "post";
                    if (valid){
                        this.formObj.formModel.menuList=this.$refs.tree.getCheckedKeys();
                        this.formObj.formModel.dataList=this.$refs.dataTree.getCheckedKeys();
                        http.postOrPut(url, method, this.formObj.formModel).then(response => {
                            if(response.code==='00000'){
                                this.formObj.formVisible = false;
                                this.$refs[ref].resetFields();
                                this.resourceCheckedKey=[];
                                this.dataCheckedKey=[];
                                this.loadPagination();
                            }else{
                                this.$message.error(response.message);
                            }

                        }).catch(function (error) {
                            console.log(JSON.stringify(error));
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
    .takManagementInfo{
        line-height: 36px;
    }

</style>
