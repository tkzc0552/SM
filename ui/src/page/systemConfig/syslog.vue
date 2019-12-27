<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" ref="filterForm">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="filters.username"></el-input>
                    </el-form-item>
                    <el-form-item label="用户操作" prop="operation">
                        <el-input v-model="filters.operation"></el-input>
                    </el-form-item>
                    <el-form-item label="请求方法" prop="method">
                        <el-input
                            v-model="filters.method"></el-input>
                    </el-form-item>
                    <el-form-item label="请求参数" prop="params">
                        <el-input v-model="filters.params"></el-input>
                    </el-form-item>
                    <el-form-item label="执行时长(毫秒)" prop="time">
                        <el-input v-model="filters.time"></el-input>
                    </el-form-item>
                    <el-form-item label="IP地址" prop="ip">
                        <el-input v-model="filters.ip"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch('filterForm')">查询</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <!-- <el-row>
             <el-col :span="24" class="toolbar">
                 <el-form :inline="true">
                     <el-form-item>
                         <el-button  type="primary" @click="handleAdd">创建</el-button>
                         <el-button  type="primary" @click="handleBatch">批量操作</el-button>
                     </el-form-item>
                 </el-form>
             </el-col>
         </el-row>-->
        <!--列表开始-->
        <el-table :data="pagination.content" highlight-current-row v-loading="isLoading" border
                  @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column type="index" label="NO" width="80"></el-table-column>
            <el-table-column show-overflow-tooltip prop="username" label="用户名" width="150"></el-table-column>
            <el-table-column show-overflow-tooltip prop="operation" label="用户操作" width="350"></el-table-column>
            <el-table-column show-overflow-tooltip prop="method" label="请求方法" width="250"></el-table-column>
            <el-table-column show-overflow-tooltip prop="params" label="请求参数" width="350"></el-table-column>
            <el-table-column show-overflow-tooltip prop="time" label="执行时长(毫秒)" width="150"></el-table-column>
            <el-table-column show-overflow-tooltip prop="ip" label="IP地址" width="250"></el-table-column>
            <el-table-column show-overflow-tooltip prop="createTime" label="创建时间" width="250"></el-table-column>
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
    </div>
</template>
<script>
    import http from '../../axios/http';
    export default {
        name: 'syslog',
        data() {
            return {
                heightNum: 0,
                filters: {
                    username: '',
                    operation: '',
                    method: '',
                    params: '',
                    time: '',
                    ip: '',
                },
                pagination: {
                    totalElements: 0,
                    page: 1,
                    content: []
                },
                size: 20,
                isLoading: false,
                selecteds: [],//列表选中列
                //新增和编辑Obj
                formObj: {
                    title: '',
                    type: '1',//1新增，2编辑
                    action: '',
                    formModel: {},
                    formVisible: false//编辑界面是否显示
                },

                handleSelectionRowIndex: 0,
                syslogRule: {}
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
                //  用户名
                if (this.filters.username && this.filters.username !== '') {
                    params['search_eq_username '] = this.filters.username.trim();
                }
                //  用户操作
                if (this.filters.operation && this.filters.operation !== '') {
                    params['search_eq_operation '] = this.filters.operation.trim();
                }
                //  请求方法
                if (this.filters.method && this.filters.method !== '') {
                    params['search_eq_method '] = this.filters.method.trim();
                }
                //  请求参数
                if (this.filters.params && this.filters.params !== '') {
                    params['search_eq_params '] = this.filters.params.trim();
                }
                //  执行时长(毫秒)
                if (this.filters.time && this.filters.time !== '') {
                    params['search_eq_time '] = this.filters.time.trim();
                }
                //  IP地址
                if (this.filters.ip && this.filters.ip !== '') {
                    params['search_eq_ip '] = this.filters.ip.trim();
                }
                self.isLoading = true;
                http.get("api/syslog", {params: params}).then(response => {
                    console.log(JSON.stringify(response));
                    response.page = response.number + 1;
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

            },
            //新建页面
            handleAdd() {
                this.formObj = {
                    title: '系统日志-创建',
                    formVisible: true,
                    type: '1',//1新增，2编辑
                    formModel: {},
                };
            },
            handleSelectionChange(val){
                this.selecteds = val;
                console.log("handleSelectionChange");
                console.log("批量勾选的值：" + JSON.stringify(val));
            },
            //批量操作
            handleBatch(){
                if (this.selecteds.length == 0) {
                    this.$alert('请重新选择一条数据！', '提示', {
                        confirmButtonText: '确定',
                    });
                    return;
                } else {
                    this.$alert(JSON.stringify(this.selecteds));
                }
            },
            //编辑页面
            handleEdit(index, row){
                console.log("时间：" + JSON.stringify(row))
                this.formObj = {
                    title: '系统日志-编辑',
                    type: '2',//0，详情，1新增，2编辑
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
                if (this.isLoading) {
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
                this.$refs[ref].validate((valid) => {
                    let url = "api/syslog";
                    let method = "post";
                    if (this.formObj.formModel.id) {
                        method = "put";
                        url = "api/syslog";
                    }
                    console.log(JSON.stringify(this.formObj.formModel));
                    if (valid) {
                        http.postOrPut(url, method, this.formObj.formModel).then(response => {
                            if (response.code === '00000') {
                                this.formObj.formVisible = false;
                                this.$refs[ref].resetFields();
                                this.loadPagination();
                            } else {
                                console.log(JSON.stringify(response.message))
                            }
                        }).catch(function (error) {
                            console.log(JSON.stringify(error));
                        });
                    } else {
                        this.$refs[ref].resetFields();
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
                }, 10);
                // 响应窗口大小改变
                window.onresize = function () {
                    self.heightNum = self.utils.calcTableHeight();
                };
            }
        },
        filters: {
            auditFilter: function (row) {
                if (!row) return ''
                let list = row.piclist;
                let num = 0;
                for (let i = 0; i < list.length; i++) {
                    let info = list[i];
                    if (info.auditStatus != 1) {
                        num = num + 1;
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
    .takManagementInfo {
        line-height: 36px;
    }

</style>
