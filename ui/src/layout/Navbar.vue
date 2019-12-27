<template>
    <el-menu class="navbar" mode="horizontal">
        <!--<Hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened"></Hamburger>-->
        <!--<levelbar></levelbar>-->

        <tags-view></tags-view>

        <div class="fr" style="line-height: 34px;">
            <size-select class="international"></size-select><span style="color: #B5BDB9;">|</span>
            <!-- 暂时不使用多语言，此处注释 -->
            <!--<lang-select class="international"></lang-select><span style="color: #B5BDB9;">|</span>-->
            <span class="international">
                <el-button size="mini" type="text" @click="goHome">
                    <i class="iconfont">&#xe607;</i>返回首页
                </el-button>
            </span>
        </div>
    </el-menu>
</template>

<script>
    import {mapGetters} from 'vuex'
    // import Hamburger from '../components/Hamburger';
    // import Levelbar from './Levelbar';
    // import LangSelect from '../components/LangSelect';
    import SizeSelect from '../components/SizeSelect';
    import TagsView from './TagsView';


    export default {
        components: {
            // Hamburger,
            // Levelbar,
            // LangSelect,
            SizeSelect,
            TagsView
        },
        data() {
            return {}
        },
        computed: {
            ...mapGetters([
                'sidebar',
                'name',
                'avatar'
            ])
        },
        methods: {
            toggleSideBar() {
                this.$store.dispatch('ToggleSideBar')
            },
            goHome(){
                window.location.href = "#/view";
            }
        }
    }
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
    .navbar {
        height: 35px;
        /*background-color: #eef1f6;*/
        background-color: #eef1f6;
        box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .15);

        /*line-height: 35px;*/
        border-radius: 0 !important;
        /*.hamburger-container {
            line-height: 35px;
            height: 35px;
            float: left;
            padding-top: 5px;
            padding-left: 5px;
        }*/
    }
    .international{
        vertical-align: top;
        margin: 0 15px;
    }
    .breadcrumb-container {
        float: left;
    }

</style>
