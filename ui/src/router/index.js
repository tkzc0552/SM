import Vue from 'vue';
import VueRouter from 'vue-router';
import Layout from '../layout/Layout';

import home from '../page/home/home';
import login from '../page/login/login.vue';
import register from '../page/login/register.vue';

import view from '../page/view/view.vue';
import paramSetting from "../page/setting/paramSetting";
import sysUser from '../page/systemConfig/sysUser';
import sysMenu from '../page/systemConfig/sysMenu';
import sysRole from '../page/systemConfig/sysRole';
import sysDept from '../page/systemConfig/sysDept';
import sysAnnounce from '../page/systemConfig/sysAnnounce';
import sysArea from '../page/systemConfig/sysArea';
import sysGenerator from '../page/systemConfig/sysGenerator';
import syslog from '../page/systemConfig/syslog';

Vue.use(VueRouter);

const router = new VueRouter({
    //mode: 'history',
    scrollBehavior: () => ({y: 0}),
    routes: [
        {
            name: '',
            path: '/',
            redirect: '/login',
            hidden: true
        },
        {
            name: '登录',
            path: '/login',
            component: login,
            redirect: '/login',
            meta: { title: '登录'},
            children: [
                {
                    name: 'login',
                    path: '/login',
                    component: home,
                    meta: { title: '登录'}
                }
            ]
        },
        {
            name: '注册',
            path: '/register',
            component: register,
            redirect: '/register',
            meta: { title: '注册'},
            children: [
                {
                    name: 'register',
                    path: '/register',
                    component: home,
                    meta: { title: '注册'}
                }
            ]
        },
        {
            name: 'view',
            path: '/view',
            component: view,
            redirect: '/view',
            meta: { title: 'view'},
            children: [
                {
                    name: 'view',
                    path: '/view',
                    component: view,
                    meta: { title: 'view'}
                }
            ]
        },
        {
            name: 'home',
            path: '/home',
            component: Layout,
            redirect: '/home',
            meta: { title: '首页'},
            children: [
                {
                    name: 'home',
                    path: '/home',
                    component: home,
                    meta: { title: '首页'}
                }
            ]
        },

        {
            path: '/redirect',
            component: Layout,
            hidden: true,
            children: [
                {
                    path: '/redirect/:path*',
                    component: () => import('../page/redirect/index')
                }
            ]
        },
        {
            name: '权限系统管理',
            path: '/systemModel',
            component: Layout,
            redirect: '/systemModel',
            meta: { title: '权限系统管理'},
            children: [
                {
                    name: 'sysUser',
                    path: '/sysUser',
                    component: sysUser,
                    meta: { title: '系统用户'}
                },
                {
                    name: 'sysMenu',
                    path: '/sysMenu',
                    component: sysMenu,
                    meta: { title: '系统菜单'}
                },
                {
                    name: 'sysRole',
                    path: '/sysRole',
                    component: sysRole,
                    meta: { title: '系统角色'}
                },
                {
                    name: 'sysDept',
                    path: '/sysDept',
                    component: sysDept,
                    meta: { title: '系统部门'}
                },
                {
                    name: 'sysGenerator',
                    path: '/sysGenerator',
                    component: sysGenerator,
                    meta: { title: '代码生成器'}
                }
            ]
        },
        {
            name: 'announce',
            path: '/announce',
            component: Layout,
            redirect: '/announce',
            meta: { title: '公告管理'},
            children: [
                {
                    name: 'sysAnnounce',
                    path: '/sysAnnounce',
                    component: sysAnnounce,
                    meta: { title: '公告列表'}
                },

            ]
        },
        {
            name: 'logManage',
            path: '/logManage',
            component: Layout,
            redirect: '/logManage',
            meta: { title: '日志管理'},
            children: [
                {
                    name: 'syslog',
                    path: '/syslog',
                    component: syslog,
                    meta: { title: '系统日志'}
                }
            ]
        },
        {
            name: 'param',
            path: '/param',
            component: Layout,
            redirect: '/paramSetting',
            meta: { title: '系统参数'},
            children: [
                {
                    name: 'paramSetting',
                    path: '/paramSetting',
                    component: paramSetting,
                    meta: { title: '系统参数'}
                },{
                    name: 'sysArea',
                    path: '/sysArea',
                    component: sysArea,
                    meta: { title: '省市县'}
                },

            ]
        }

    ]
});

export default router;
