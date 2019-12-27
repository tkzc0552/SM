<template>
    <div>
        <template v-for="menu in menus">
            <!-- 叶子节点 按钮 -->
            <template v-if="menu.leafFlag === 1">
                <!--<el-menu-item :index="(menu.menuLink != null && menu.menuLink.length > 0) ? menu.menuLink : menu.menuCode" :key="menu.menuCode">-->
                <el-menu-item :index="menu.menuEname" :key="menu.menuCode">
                    <!--<i v-if="menu.icon" :class="menu.icon"></i>-->
                    <!--<i v-else class="el-icon-star-on"></i>-->
                    <span>{{menu.menuCname}}</span>
                    <!--<span>{{generateTitle(menu.menuEname)}}</span>--><!-- 暂时不使用多语言，此处注释 -->
                </el-menu-item>
            </template>


            <!-- 按钮组 -->
            <!--<el-submenu v-else :index="menu.menuLink" :key="menu.menuCode">-->
            <el-submenu v-else :index="menu.menuEname" :key="menu.menuCode">
                <template slot="title">
                    <!--<i v-if="menu.icon" :class="menu.icon"></i>-->
                    <!--<i v-else class="el-icon-menu"></i>-->
                    <span>{{menu.menuCname}}</span>
                    <!--<span>{{generateTitle(menu.menuEname)}}</span>--><!-- 暂时不使用多语言，此处注释 -->
                </template>

                <template v-for="subMenu in menu.children">
                    <template v-if="subMenu.children && subMenu.children.length > 0">
                        <sidebar-item :menus='[subMenu]'></sidebar-item>
                    </template>
                    <template v-else>
                        <!--<el-menu-item :index="(subMenu.menuLink != null && subMenu.menuLink.length > 0) ? subMenu.menuLink : subMenu.menuCode" :key="subMenu.menuCode">-->
                        <el-menu-item :index="subMenu.menuEname" :key="subMenu.menuCode">
                            <!--<i v-if="subMenu.icon" :class="subMenu.icon"></i>-->
                            <!--<i v-else class="el-icon-setting"></i>-->
                            <span>{{subMenu.menuCname}}</span>
                            <!--<span>{{generateTitle(subMenu.menuEname)}}</span>--><!-- 暂时不使用多语言，此处注释 -->
                        </el-menu-item>
                    </template>
                </template>
            </el-submenu>
        </template>


    </div>
</template>

<script>
    // import {generateTitle} from '../utils/i18n'

    export default {
        name: 'SidebarItem',
        props:{
            menus: {
                type: Array
            }
        },
        methods: {
            // generateTitle
        },
    }
</script>

