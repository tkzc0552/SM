<template>
    <div class="app-container">
        <!--工具条-->
        <el-row>
            <el-col :span="24" class="toolbar">
                <el-form :inline="true" :model="filters" ref="filterForm">
                    <el-form-item label="公告标题" prop="title">
                        <el-input v-model="filters.title" placeholder="支持模糊搜索"></el-input>
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <sd-param-radio-group v-model="filters.status" type-code="limitStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                    </el-form-item>
                    <el-form-item label="类型" prop="type">
                        <sd-param-radio-group v-model="filters.type" type-code="announceType" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-radio-group>
                    </el-form-item>
                    <el-form-item label="公告起止时间" prop="time">
                        <el-date-picker v-model="filters.time" type="datetimerange" value-format="yyyy-MM-dd hh:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" @input="changeTime"></el-date-picker>
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
            <el-table-column prop="title" label="公告标题" min-width="100"></el-table-column>
            <el-table-column prop="title" label="公告起止时间" min-width="100">
                <template slot-scope="scope">
                    {{scope.row.beginTime}} -- {{scope.row.endTime}}
                </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" width="120">
                <template slot-scope="scope">
                    {{scope.row.type|paramCode2ParamCname("announceType")}}
                   <!-- <p v-if="scope.row.type===1">公告</p>
                    <p v-else-if="scope.row.type===2">通知</p>-->
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
                <template slot-scope="scope">
                    {{scope.row.status|paramCode2ParamCname("limitStatus")}}
                </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="comment" label="备注" min-width="120"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column  label="操作" width="350" fixed="right">
                <template slot-scope="scope">
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
            <el-dialog :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                <el-form :inline="true" :model="formObj.formModel" :rules="announceRule" label-width="110px" ref="announceForm">
                    <table class="productListTable productListTable-col4">
                        <tr>
                            <td>
                                <el-form-item label="标题" prop="title">
                                    <el-input auto-complete="off" style="width: 400px;" v-model="formObj.formModel.title"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="类型" prop="type">
                                    <sd-param-group-for-test v-model="formObj.formModel.type" type-code="announceType" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-group-for-test>

                                    <!--<el-radio-group v-model="formObj.formModel.type">
                                        <el-radio class="radio" :label="1">公告</el-radio>
                                        <el-radio class="radio" :label="2">通知</el-radio>
                                    </el-radio-group>-->
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="内容" prop="content">
                                    <el-input type="textarea" auto-complete="off" v-model="formObj.formModel.content"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="状态">
                                    <sd-param-group-for-test v-model="formObj.formModel.status" type-code="limitStatus" :hasAll="true" @change="handleSearch('filterForm')"></sd-param-group-for-test >

                                    <!-- <el-radio-group v-model="formObj.formModel.status">
                                         <el-radio class="radio" :label="0">正常</el-radio>
                                         <el-radio class="radio" :label="1">关闭</el-radio>
                                     </el-radio-group>-->
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="公告起止时间" prop="deadTime">
                                    <el-date-picker v-model="formObj.formModel.deadTime" type="datetimerange" value-format="yyyy-MM-dd hh:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" @input="changeTime"></el-date-picker>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <el-form-item label="备注" prop="comment">
                                    <el-input auto-complete="off" v-model="formObj.formModel.comment"></el-input>
                                </el-form-item>
                            </td>
                        </tr>
                    </table>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="handleClose('announceForm')">取消</el-button>
                    <el-button type="primary"  @click="handleSubmit('announceForm')">提交</el-button>
                </div>
            </el-dialog>
        </section>

    </div>
</template>
<script>
    import http from '../../axios/http';
    export default {
        name: 'sysUser',
        data() {
            return {
                heightNum: 0,
                filters: {
                    status:'',
                    type:'',
                    title:'',
                    time:[]
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
                announceRule: {
                    title: [
                        {required: true, message: '公告标题不能为空', trigger: 'blur'},
                        {max: 40, message:"输入长度不能超过40个字符", trigger: 'blur, change'}

                    ],
                    content: [
                        {required: true, message: '公告内容不能为空', trigger: 'blur'},
                        {max: 255, message: "输入长度不能超过255个字符", trigger: 'blur, change'}
                    ],
                    type:[
                        {required: true, message: '强选择一个类型', trigger: 'blur'}
                    ],
                    status:[
                        {required: true, message: '强选择一个状态', trigger: 'blur'}
                    ],
                    deadTime:[
                        {required: true, message: '请输入公告起止时间', trigger: 'blur'}
                    ]
                }
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
                if (this.filters.title && this.filters.title !== '') {//状态
                    params['search_like_title'] = this.filters.title.trim();
                }
                if (this.filters.status && this.filters.status !== '') {//状态
                    params['search_eq_status'] = this.filters.status;
                }
                if (this.filters.type && this.filters.type !== '') {
                    params['search_eq_type'] = this.filters.type;
                }
                if (this.filters.time && this.filters.time !== '') {
                    params['search_gte_beginTime'] =this.filters.time[0];
                    params['search_lte_endTime'] =this.filters.time[1];
                }

                self.isLoading = true;
                http.get("api/sysAnnounce", {params: params}).then(response => {
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
                this.formObj = {
                    title: '公告-创建',
                    formVisible: true,
                    type:'1',//1新增，2编辑
                    formModel: {
                        roleIds:[],
                        sex:0
                    },
                };
            },
            //编辑页面
            handleEdit(index,row){
                console.log("时间："+JSON.stringify(row))
                this.formObj = {
                    title: '公告-编辑',
                    type:'2',//0，详情，1新增，2编辑
                    formVisible: true,
                    formModel: Object.assign({}, row),
                };
                this.$set( this.formObj.formModel, "deadTime", [
                    row.beginTime,row.endTime
                ]);
            },

            changeTime(e) {
                this.$nextTick(() => {
                    this.$set(this.formObj.formModel, "deadTime", [e[0], e[1]]);
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
            //保存与修改
            handleSubmit(ref){
                this.$refs[ref].validate((valid) => {
                    console.log("valid : " + valid);
                    let url = "api/sysAnnounce";
                    let method = "post";
                    if(this.formObj.formModel.id){
                        method="put";
                        url="api/sysAnnounce";
                    }
                    this.formObj.formModel.beginTime=this.formObj.formModel.deadTime[0];
                    this.formObj.formModel.endTime=this.formObj.formModel.deadTime[1];
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
    .takManagementInfo{
        line-height: 36px;
    }

</style>
