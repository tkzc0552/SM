<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" ref="filterForm">
                    <el-form-item label="表名" prop="tableName">
                        <el-input v-model="filters.tableName" placeholder="支持模糊搜索"></el-input>
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
                        <el-button @click="addCode()" type="primary">批量代码生成</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <!--列表开始-->
        <el-table :data="pagination.content" highlight-current-row v-loading="isLoading" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column type="index" label="NO" width="80"></el-table-column>
            <el-table-column prop="tableName" label="表名" min-width="100"></el-table-column>
            <el-table-column prop="engine" label="engine" min-width="100"></el-table-column>
            <el-table-column show-overflow-tooltip prop="tableComment" label="表备注" min-width="120"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
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
        name: 'sysGenerator',
        data() {
            return {
                heightNum: 0,
                filters: {
                    tableName:''
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
                    type:'1',//1新增，2编辑
                    action: '',
                    formModel: {
                        status:0,
                        type:0
                    },
                    formVisible: false//编辑界面是否显示
                },

                handleSelectionRowIndex: 0,
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
                if (this.filters.tableName && this.filters.tableName !== '') {//状态
                    params['search_like_tableName'] = this.filters.tableName.trim();
                }

                self.isLoading = true;
                http.get("api/sysGenerator", {params: params}).then(response => {
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
            // 表格多选
            handleSelectionChange(val) {
                console.log(JSON.stringify(val));
                this.selecteds = val;
            },
            addCode(){
                if(this.selecteds.length>0){
                    let tableNames='';
                    this.selecteds.forEach(item=>{
                        if(tableNames!=''){
                            tableNames=tableNames+","+item.tableName;
                        }else{
                            tableNames=item.tableName;
                        }

                    })
                    console.log(JSON.stringify(tableNames));
                    location.href = "api/sysGenerator/code/" + tableNames;
                }else{
                    this.$alert('请选择一条数据！', '提示', {
                        confirmButtonText: '确定',
                    });
                    return;
                }

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
