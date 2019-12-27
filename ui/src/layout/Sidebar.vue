<template>
    <div>
        <div class="scroll-box" ref="scrollContainer" @wheel.prevent="handleScroll">
            <div class="scroll-wrapper" ref="scrollWrapper" :style="{left: left + 'px'}">
                <el-menu mode="horizontal" :default-active="$route.name" class="menu-bar"
                         background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" @select="handleSelect">

                    <el-popover placement="bottom-start" trigger="click" popper-class="matrix-pop">
                        <!--<div class="matrix-btn"><i class="iconfont">&#xe715;</i></div>-->
                        <img class="matrix-btn" slot="reference" src="../../static/img/logo.png"/>

                        <div class="matrix-box">
                            <div class="matrix-item" v-for="item in matrixList" :style="{backgroundColor: item.bgColor}" @click="handleJump(item.menuUrl)">
                                <div class="matrix-icon" :style="{backgroundPosition: item.pos}"></div>
                                <!-- 暂时不使用多语言，此处注释 -->
                                <!--<div class="matrix-title">{{$i18n.locale === 'en' ? item.menuEname : item.menuCname}}</div>-->
                                <div class="matrix-title">{{item.menuName}}</div>
                            </div>
                        </div>
                    </el-popover>


                    <sidebar-item style="margin-left: 40px;" :menus='menus'></sidebar-item>
                </el-menu>
            </div>
        </div>
    </div>

</template>

<script>
    import {mapGetters} from 'vuex'
    import SidebarItem from './SidebarItem'
    import ScrollBar from '../components/ScrollBar'
    import http from '../axios/http';

    const padding = 0; // tag's padding

    export default {
        components: {SidebarItem, ScrollBar},
        name: 'Sidebar',
        data() {
            return {
                menus: [],
                left: 0,
                matrixList: [],

                matrixBGC: ['#0078d7', '#5133AB', '#D24726', '#094AB2', '#2B579A', '#217346', '#B7472A', '#7719AA', '#008272', '#00AFF0', '#D83B01', '#0C8484', '#0077FF', '#34A853', '#4285F4'],
                matrixICO: [4, 4], //matrix-icon.png 图标4*4排列
            }
        },
        computed: {
            ...mapGetters([
                'sidebar'
            ]),
        },
        methods: {
            handleScroll(e) {
                // console.log(e)
                const eventDelta = e.wheelDelta || -e.deltaY * 3;
                const $container = this.$refs.scrollContainer;
                const $containerWidth = $container.offsetWidth;
                const $wrapper = this.$refs.scrollWrapper;
                const $wrapperWidth = $wrapper.offsetWidth;

                if (eventDelta > 0) {
                    this.left = Math.min(0, this.left + eventDelta)
                } else {
                    if ($containerWidth - padding < $wrapperWidth) {
                        if (this.left < -($wrapperWidth - $containerWidth + padding)) {
                        } else {
                            this.left = Math.max(this.left + eventDelta, $containerWidth - $wrapperWidth - padding);
                        }
                    } else {
                        this.left = 0
                    }
                }
            },

            initMenus() {
                Promise.resolve(this.$store.dispatch('loadPermissions')).then(() => {
                    this.menus = this.$store.getters.menus;
                    // console.debug("initMenus.this.menus:" + JSON.stringify(this.menus));
                });
            },

            initMatrix() {
               http.get("api/interface/menu").then(response => {
                    if(response.code == '00000'){
                        response.data.forEach(item => {
                            item.bgColor = this.matrixBGC[Math.ceil(Math.random() * this.matrixBGC.length)-1];
                            item.pos = (Math.ceil(Math.random() * this.matrixICO[0])-1)*40 + "px " + (Math.ceil(Math.random() * this.matrixICO[1])-1)*40 + "px";
                        })
                        this.matrixList = response.data;
                        console.log(this.matrixList)
                    }else{
                        this.$router.push({ name: 'login' , params: { userId:null}});
                    }
                });
            },
            handleJump(url){
                window.open(url)
            },

            handleSelect(index, indexPath) {
                // console.log("handleSelect");
                // console.log(index);
                // console.log(indexPath);
                //
                // console.log(this.$route)
                this.$router.push({name: index})
            }
        },
        created() {
            this.initMenus();
            this.initMatrix();
        }
    }
</script>

<style>
    .scroll-box {
        width: 100%;
        height: 40px;
        overflow: hidden;
        position: relative;
        white-space: nowrap;
        background-color: #304156;
    }
    .scroll-wrapper {
        /*width: 100%;*/
        position: absolute;
    }

    .menu-bar {
        width: auto;
        height: 40px;
        min-height: 40px;
        border-radius: 0 !important;
        white-space: nowrap;
    }

    /*.menu-bar .logo {
        width: 150px;
        height: 26px;
        margin: 6px 20px 0 0;

        position: absolute;
        left: 40px;
        top: 0;
        cursor:pointer;
    }*/

    .menu-bar .el-menu-item, .menu-bar .el-submenu, .menu-bar .el-submenu__title {
        height: 40px;
        line-height: 40px;
        display: inline-block;
        text-align: center;
    }

    .menu-bar .el-submenu__title {
        border-bottom: 3px #304156 solid;
    }
    .menu-bar .el-menu-item.is-active, .menu-bar .el-submenu__title.is-active {
        border-bottom: 3px #409EFF solid;
    }

    .menu-bar .el-submenu__title .el-submenu__icon-arrow {
        position: initial;
        margin-top: 0;
        margin-left: 5px;
    }

    .el-submenu.is-active .el-submenu__title .el-submenu__icon-arrow {
        color: #409EFF;
    }

    .el-submenu.is-opened .el-menu--horizontal {
        margin-top: -5px;
    }
</style>
