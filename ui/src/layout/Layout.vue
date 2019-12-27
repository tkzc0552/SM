<template>
    <div class="app-wrapper">
        <sidebar></sidebar>
        <div class="main-container">
            <navbar></navbar>
            <app-main></app-main>
        </div>
    </div>
</template>

<script>
    import Navbar from './Navbar';
    import Sidebar from './Sidebar';
    // import TagsView from './TagsView';
    import AppMain from './AppMain';

    export default {
        name: 'layout',
        components: {
            Navbar,
            Sidebar,
            AppMain,
            // TagsView
        },
        computed: {
            sidebar() {
                return this.$store.state.app.sidebar
            }
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
    @import "../../static/styles/mixin";
    .app-wrapper {
        @include clearfix;
        position: relative;
        height: 100%;
        width: 100%;
    }

    // 主体区域
    .main-container {
        min-height: 100%;
        transition: margin-left .28s;
        //margin-left: 180px;
    }
</style>
