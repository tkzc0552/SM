<template>
    <div class="body">
        <div class="app-container">
            <div style="float: right;padding-right: 50px;color: #fff;padding-top: 20px;line-height: 12px;">
                客服电话：010-0123456789
                <el-button @click="goLogin()" round>登录</el-button>
                <el-button @click="goRegister()" round>注册</el-button>
            </div>

            <div style="width:400px; height: 80px;margin-left: 100px;margin-top:10px;color:#fff;position: absolute;">
                <div class="address">{{weather.city}} 的天气</div>
                <div class="temp">
                    <span class="tempNum">
                        {{weather.temp}}
                    </span>
                    <span class="tempDu">
                        ℃
                    </span>
                </div>
                <el-image style="width: 50px;height: 50px;margin-top: 20px;margin-left: 60px;"   :src="weather.pic1"></el-image>
                <div class="otherWeather">
                    <div class="weatherRow">
                        <span>{{weather.WD}}</span>
                        <span>{{weather.WS}}</span>
                    </div>
                    <div class="weatherRow">
                        <span>相对湿度</span>
                        <span>{{weather.SD}}</span>
                    </div>
                </div>
            </div>

            <div style="font-size: 40px;color: #ffffff;margin-left: 200px;margin-top: 20%;position: absolute;">
               让操作更简单，让生活更美好！
            </div>
            <div style="height: 400px;position: absolute;right: 9%;top: 7%;border-radius:10px;" class="demo-ruleForm login-container">
                <el-form :model="ruleForm2" :rules="rules2" ref="ruleForm2" label-position="left" label-width="0px">
                    <h3 class="title">系统登录</h3>
                    <el-form-item prop="account">
                        <el-input type="text" v-model="ruleForm2.account" auto-complete="off" placeholder="账号"></el-input>
                    </el-form-item>
                    <el-form-item prop="checkPass">
                        <el-input type="password" v-model="ruleForm2.checkPass" auto-complete="off" placeholder="密码"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <img alt="验证码" onclick = "this.src='api/defaultKaptcha?d='+new Date()*1" src="api/defaultKaptcha" />
                    </el-form-item>
                    <el-form-item prop="vrifyCode">
                        <el-input type="text" v-model="ruleForm2.vrifyCode" auto-complete="off" placeholder="验证码"></el-input>
                    </el-form-item>
                    <el-checkbox v-model="checked" checked class="remember">记住密码</el-checkbox>
                    <el-form-item style="width:100%;">
                        <el-button type="primary" style="width:100%;" @click="handleLogin('ruleForm2')" round>登录</el-button>
                        <br>
                        <el-button @click="handleRegister('ruleForm2')" round>修改密码</el-button>
                        <el-button @click="handlePassword('ruleForm2')" round>找回密码</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <!--修改密码-->
            <section>
                <el-dialog  :title="formObj.title" :visible.sync="formObj.formVisible" :close-on-click-modal="false">
                    <el-form :inline="true" :model="formObj.formModel"  label-width="110px" ref="reasonForm">
                        <table>
                            <tr>
                                <td>
                                    <el-form-item label="用户名" prop="userName">
                                        <span>{{formObj.formModel.userName}}</span>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="原密码" prop="passWord">
                                        <el-input type="password" auto-complete="off" style="width: 400px;" v-model="formObj.formModel.passWord"></el-input>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="新密码" prop="repassWord">
                                        <el-input type="password" auto-complete="off" v-model="formObj.formModel.repassWord"></el-input>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="确认密码" prop="conpassWord">
                                        <el-input type="password" auto-complete="off" v-model="formObj.formModel.conpassWord"></el-input>
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
            <!--找回密码之发送验证码-->
            <section>
                <el-dialog  :title="searchPassword.title" :visible.sync="searchPassword.formVisible" :close-on-click-modal="false">
                    <el-form :inline="true" :model="searchPassword.formModel" ref="reasonForm">
                        <table>
                            <tr>
                                <td>
                                    <el-form-item label="登陆名" prop="userName">
                                        <span>{{searchPassword.formModel.userName}}</span>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="邮箱" prop="email">
                                        <el-input  auto-complete="off" style="width: 400px;" v-model="searchPassword.formModel.email" ></el-input>
                                        <el-button @click.native.prevent="handleSendPassword()">
                                            {{this.sendName}}
                                        </el-button>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="验证码" prop="code">
                                        <el-input  auto-complete="off" style="width: 400px;" v-model="searchPassword.formModel.code" ></el-input>
                                    </el-form-item>
                                </td>
                            </tr>
                        </table>
                    </el-form>
                    <div slot="footer" class="dialog-footer">
                        <el-button @click="handlePassWordClose('reasonForm')">取消</el-button>
                        <el-button type="primary"  @click="handlePassWordSubmit('reasonForm')">提交</el-button>
                    </div>
                </el-dialog>
            </section>
            <!--找回密码之找回密码-->
            <section>
                <el-dialog  :title="newPassword.title" :visible.sync="newPassword.formVisible" :close-on-click-modal="false">
                    <el-form :inline="true" :model="newPassword.formModel" ref="newPasswordForm">
                        <table>
                            <tr>
                                <td>
                                    <el-form-item label="用户名" prop="userName">
                                        <span>{{newPassword.formModel.userName}}</span>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="新密码" prop="newpassword">
                                        <el-input type="password" auto-complete="off" style="width: 400px;" v-model="newPassword.formModel.newpassword" ></el-input>
                                    </el-form-item>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <el-form-item label="确认密码" prop="oldpassword">
                                        <el-input type="password" auto-complete="off" style="width: 400px;" v-model="newPassword.formModel.conpassword" ></el-input>
                                    </el-form-item>
                                </td>
                            </tr>
                        </table>
                    </el-form>
                    <div slot="footer" class="dialog-footer">
                        <el-button @click="handleNewPassWordClose('newPasswordForm')">取消</el-button>
                        <el-button type="primary"  @click="handleNewPassWordSubmit('newPasswordForm')">提交</el-button>
                    </div>
                </el-dialog>
            </section>
            <div class="footer">
                © 2019 SystemModel 京ICP备16016817号
            </div>

        </div>
    </div>
</template>

<script>
    import http from '../../axios/http';
    import { Loading } from 'element-ui';
    import ElImage from "../../../node_modules/element-ui/packages/image/src/main";

    export default {
        components: {ElImage},
        name: 'login',
        data() {
            return {
                logining: false,
                ruleForm2: {
                    account: '',
                    vrifyCode:'',
                    checkPass: ''
                },
                myTime:0,
                historyList:'',
                weather:'',
                sendName:"发送邮件",
                formObj: {
                    title: '',
                    action: '',
                    formModel: {
                        userName:'',
                        passWord:'',
                        repassWord:'',
                        conpassWord:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                searchPassword:{
                    title: '',
                    action: '',
                    formModel: {
                        userName:'',
                        email:'',
                        code:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                newPassword:{
                    title: '',
                    action: '',
                    formModel: {
                        userName:'',
                        code:'',
                        newpassword:'',
                        conpassword:''
                    },
                    formVisible: false//编辑界面是否显示
                },
                rules2: {
                    account: [
                        { required: true, message: '请输入账号', trigger: 'blur' },
                        //{ validator: validaePass }
                    ],
                    checkPass: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                        //{ validator: validaePass2 }
                    ],
                    vrifyCode:[
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                    ]
                },
                checked: true
            };
        },
        methods: {
            countDown(count) {
               console.log("倒计时开始",this.myTime);
                if(count == 0) {
                    this.myTime = 0;
                    this.sendName="发送邮件";
                    return;
                } else {
                    this.myTime=count;
                    this.myTime--;
                    this.sendName="发送邮件["+this.myTime+"s]";
                }
                var that=this;
                setTimeout(function() {
                    console.log("xx");
                    that.countDown(that.myTime);
                }, 1000);
            },
            //修改密码
            handleRegister(){
                if(this.ruleForm2.account==''){
                    this.$message("请输入登录名");
                    return;
                }
                console.log("修改密码："+JSON.stringify(this.ruleForm2));
                this.formObj = {
                    title: '修改密码',
                    formVisible: true,
                    formModel: {
                        userName:this.ruleForm2.account,
                        passWord:this.ruleForm2.passWord,
                        repassWord:'',
                        conpassWord:''
                    },
                };
            },
            //发送邮件的验证码
            handleSendPassword(){
                console.log("发送邮件信息："+JSON.stringify(this.searchPassword.formModel))
                if(this.searchPassword.formModel.email==''){
                    this.$message("请输入邮箱");
                    return;
                }
                if(this.myTime>=1){
                    return;
                }
               var sendParam = { userName: this.searchPassword.formModel.userName, toEmail: this.searchPassword.formModel.email };
                let url = "api/login/sendEmail";
                let self=this;
                let sendEmailloadingInstance = Loading.service({ fullscreen: true , text: '正在发送邮件中。。。。。'});
                http.post(url,sendParam).then(response => {
                    sendEmailloadingInstance.close();
                    if(response.code==='00000'){
                        console.log("验证码："+response.data);
                        if(this.myTime<=0){
                            this.countDown(180);
                        }
                        this.$message(response.message);
                    }else{
                        this.$message(response.message);
                    }
                }).catch(function (error) {
                    self.$message.error(error['data']);
                });
            },
            //找回密码
            handlePassword(){
                if(this.ruleForm2.account==''){
                    this.$message("请输入登录名");
                    return;
                }
                console.log("找回密码："+JSON.stringify(this.ruleForm2));
                this.searchPassword = {
                    title: '找回密码',
                    formVisible: true,
                    formModel: {
                        userName:this.ruleForm2.account,
                        email:'',
                        code:''
                    },
                };
            },
            handlePassWordClose(ref){
                this.myTime=0;
                this.searchPassword.formVisible = false;
                this.$refs[ref].resetFields();
            },
            handleClose(ref) {//关闭表单
                this.formObj.formVisible = false;
                this.$refs[ref].resetFields();
            },
            //找回密码之新密码页面关闭
            handleNewPassWordClose(ref){
                this.newPassword.formVisible = false;
                this.$refs[ref].resetFields();
            },
            //找回密码之新密码页面提交
            handleNewPassWordSubmit(){
                console.log("新密码");
                //跳转到找回密码信息框
                let url = "api/login/changePassword";
                let changePasswordloadingInstance = Loading.service({ fullscreen: true , text: '修改密码中。。。。。'});
                http.postOrPut(url, "post", this.newPassword.formModel).then(response => {
                    changePasswordloadingInstance.close();
                    if(response.code==='00000'){
                        this.newPassword.formVisible=false;
                        this.$message(response.message);
                    }else{
                        this.$message(response.message);
                    }

                }).catch(function (error) {
                    this.isLoading = true;
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },
            handlePassWordSubmit(){
                if(this.searchPassword.formModel.code==''){
                    this.$message("请获取验证码");
                    return;
                }
                if(this.myTime<=1){
                    this.$message("请重新获取验证码");
                    return;
                }
                console.log("找回密码");
                //跳转到找回密码信息框
                let url = "api/login/searchPassword";
                let checkloadingInstance = Loading.service({ fullscreen: true , text: '数据正在校验中。。。。。'});
                http.postOrPut(url, "post",this.searchPassword.formModel).then(response => {
                    checkloadingInstance.close();
                    if(response.code==='00000'){
                        this.myTime=0;
                        this.searchPassword.formVisible=false;
                        this.formObj.formVisible=false;
                        this.$message(response.message);
                        this.newPassword = {
                            title: '新密码',
                            formVisible: true,
                            formModel: {
                                userName:this.ruleForm2.account,
                                code:response.message,
                                newpassword:'',
                                conpassword:''
                            },
                        };
                    }else{
                        this.$message(response.message);
                    }

                }).catch(function (error) {
                    this.isLoading = true;
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },
            //修改密码页面
            handleSubmit(){
                console.log("修改密码"+JSON.stringify(this.formObj.formModel));
                let url = "api/login/update";
                let changeloadingInstance = Loading.service({ fullscreen: true , text: '修改密码中。。。。。'});
                http.postOrPut(url, "post", this.formObj.formModel).then(response => {
                    changeloadingInstance.close();
                    if(response.code==='00000'){
                        this.formObj.formVisible=false;
                        this.$message(response.message);
                    }else{
                        this.$message(response.message);
                    }

                }).catch(function (error) {
                    this.isLoading = true;
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },
            //数据初始化---今日天气
            initTodayWeather(){
                let url="api/interface/todayWeather";
                http.get(url).then(response => {
                    console.log(response.data)
                    if(response.code==='00000'){
                        this.weather=response.data;
                    }else{
                        this.$message(response.message);
                    }

                }).catch(function (error) {
                    this.isLoading = true;
                    console.log(JSON.stringify(error));
                    this.$message.error(error['data']);
                });
            },
            goLogin(){
                this.$router.push('login');
            },
            goRegister(){
                this.$router.push('register');
            },
            //登陆
            handleLogin() {
                this.$refs.ruleForm2.validate((valid) => {
                    if (valid) {
                        this.logining = true;
                        var loginParams = { username: this.ruleForm2.account, password: this.$md5(this.ruleForm2.checkPass),vrifyCode:this.ruleForm2.vrifyCode};
                        let url = "api/login";
                        let loginLoading = Loading.service({ fullscreen: true , text: '正在登录中。。。。。'});

                        http.postOrPut(url, "post", loginParams).then(response => {
                            loginLoading.close();
                            if(response.code==='00000'){
                                this.$router.push({ name: 'view' , params: { userId:response.data.userId}});
                               // this.$router.push({ path: '/view' });
                            }else{
                                this.$message(response.message);
                            }

                        }).catch(function (error) {
                            this.isLoading = true;
                            console.log(JSON.stringify(error));
                            this.$message.error(error['data']);
                        });
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
        },
        mounted() {
            this.initTodayWeather();
        }
    }

</script>

<style lang="scss" scoped>
    .body{
        background: url('http://test-reg.seedeer.com/static/images/h_register_bg.jpg')  no-repeat;
        width: 100%;
        height:100%;
    }
    .login-container {
        /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
        -webkit-border-radius: 5px;
        border-radius: 5px;
        -moz-border-radius: 5px;
        background-clip: padding-box;
        margin: 180px auto;
        width: 350px;
        padding:  0 3%;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
        .title {
            margin: 0px auto 40px auto;
            text-align: center;
            padding-top:20px;
            color: #505458;
        }
        .remember {
            margin: 0px 0px 15px 0px;
        }

    }
    .footer{
        position:absolute;
        left:0;
        bottom:0;
        width:100%;
        height:80px;
        text-align: center;
        color:#000;
        font-weight:bold;
        line-height: 20px;
    }
    .address{
        width: 100%;
        height: 40px;
        line-height: 40px;
        background: #043567;
    }
    .temp {
        width: 160px;
        float: left;
        margin-left: 0;
        text-align: center;
    }
    .temp .tempNum {
        font-size: 80px;
        color: #fff;
        float: none;
        height: 110px;
    }
    .temp span {
        font-size: 22px;
        color: #fff;
        float: none;
        cursor: pointer;
    }
    .temp .tempDu {
         position: relative;
         top: -84px;
         left: 100px;
     }
    .otherWeather {
        width: 109px;
        float: right;
        margin-top: 10px;
    }
    .weatherRow {
        width: 100%;
        text-align: left;
        font-size: 12px;
        height: 17px;
        display: none;
        cursor: pointer;
    }
</style>
