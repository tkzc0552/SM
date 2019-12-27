<template>
    <el-dropdown trigger="click" @command="handleSetSize">
        <el-button size="mini" type="text">
            <i class="iconfont">&#xe600;</i>尺寸 <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
            <!--<el-dropdown-item style="min-width: 0;" command="medium" :disabled="size==='medium'"><span style="font-size: 18px;">Medium</span></el-dropdown-item>-->
            <el-dropdown-item style="min-width: 0;" command="small" :disabled="size==='small'"><span style="font-size: 16px;">Small</span></el-dropdown-item>
            <el-dropdown-item style="min-width: 0;" command="mini" :disabled="size==='mini'"><span style="font-size: 14px;">Mini</span></el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
</template>

<script>
    export default {
        computed: {
            size() {
                console.log("size : " + this.$store.getters.size);
                return this.$store.getters.size;
            }
        },
        methods: {
            //设置组件大小
            handleSetSize(size) {
                this.$confirm("更改页面组件尺寸将会刷新当前标签页, 是否继续?", "提示",  {
                    type: 'warning'
                }).then(() => {
                    //设置大小
                    this.$ELEMENT.size = size;
                    this.$store.dispatch('setSize', size);
                    this.$message.success("页面组件尺寸已修改为 " + size);
                    //刷新页面
                    this.refreshView();
                }).catch(() => {
                });
            },

            //刷新页面
            refreshView() {
                // In order to make the cached page re-rendered
                this.$store.dispatch('delAllCachedViews', this.$route);

                this.$nextTick(() => {
                    this.$router.replace({
                        path: '/redirect' + this.$route.path
                    })
                })
            }
        }
    }
</script>
