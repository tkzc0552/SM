<template>
    <div>
        <div>
            <el-container>
                <el-header style="height: 10%">
                    <span class="international">
                        <el-button type="text" @click="goHome">
                            <i class="iconfont">&#xe607;</i>安全退出
                        </el-button>
                    </span>
                </el-header>
                <div style="text-align: center;font-size: 29px;">stay hungry stay foolish</div>
                <el-main style="height: 80%">
                    <el-row :gutter="20">
                        <el-col :span="4" v-for="(item,index) in menuList" :key="index">
                            <div class="grid-content bg-purple">
                                <el-button style="text-align: center;height: 200px;width: 200px;font-size: 25px;"  @click="skiUrl(item)" round>
                                    {{item.menuName}}</el-button>
                            </div>
                        </el-col>
                    </el-row>
                </el-main>
            </el-container>
            <el-footer style="position:absolute;bottom:0;display: flex;justify-content: center">
                <div class="grid-content bg-purple" style="text-align: center;color:#000;">
                    © 2019 SM 京ICP备16016817号
                </div>
            </el-footer>
        </div>
    </div>
</template>

<script>
    import http from '../../axios/http';
    import { Loading } from 'element-ui';
    import ElImage from "../../../node_modules/element-ui/packages/image/src/main";
    import ElPopover from "../../../node_modules/element-ui/packages/popover/src/main";

    export default {
        components: {
            ElPopover,
            ElImage},
        name: 'view',
        data(){
            return{
                menuList:[

                ],
                visible: false,
                userInfo:{}
            }
        },
        methods: {
            skiUrl(row){
                console.log(JSON.stringify(row));
                window.location.href=row.menuUrl;
            },
            goHome(){
                let url="api/interface/exitLogin";
                let exitLoading = Loading.service({ fullscreen: true , text: '退出中。。。。。'});
                http.get(url).then(response => {
                    exitLoading.close();
                    if(response.code==='00000'){
                        window.location.href='';
                    }else{
                        window.location.href='';
                    }

                }).catch(function (error) {
                    this.isLoading = true;
                    console.log(JSON.stringify(error));
                });
            },
            queryMenu(){
                let url="api/interface/menu";
                let loading = Loading.service({ fullscreen: true , text: '页面加载中。。。。。'});
                http.get(url).then(response => {
                  loading.close();
                  if(response.code==='00000'){
                     this.menuList=response.data;
                  }else{
                      this.$router.push({ name: 'login' , params: { userId:null}});

                      this.$message(response.message);
                  }

              }).catch(function (error) {
                  this.isLoading = true;
                  console.log(JSON.stringify(error));
              });
            },
        },
        mounted() {
            this.queryMenu();
        }
    }
</script>

<style>
    .international{
        vertical-align: top;
        margin: 0 15px;
        float:right;
    }

</style>
