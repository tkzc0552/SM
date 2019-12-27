import Vue from 'vue';
import App from './App.vue';
import store from './store';
import directive from './directive';
import router from './router/index';
import i18n from './lang' // Internationalization
import ElementUI from 'element-ui';
import vue_moment from "vue-moment";
import Cookies from "js-cookie";
// 引入自定义filter并注册到全局
import * as filters from './filter';
// 引入自定义指令并注册到全局
import hasRole from './directive/role';
import clipboard from './directive/clipboard/index';
// 封装公共参数和公共方法
import utils from './utils/index';
// ajax 支持
import http from './axios/http';
// components 支持
import components from './components/index';
// css相关
import 'element-ui/lib/theme-chalk/index.css' // upgrade to 2.0.0
import '../static/styles/index.scss';
import echarts from 'echarts'
//MD5加密
import md5 from 'js-md5';

// es6 语法兼容性处理
import "babel-polyfill";


Vue.use(ElementUI, { size: Cookies.get('size') || 'small' });
Vue.use(echarts);
Vue.prototype.$echarts=echarts
Vue.prototype.$md5 = md5;
Vue.use(vue_moment);

// register global utility filters.
Object.keys(filters).forEach(key => {
    Vue.filter(key, filters[key])
});

Vue.use(hasRole);
Vue.use(clipboard);

Vue.use(utils);

Vue.prototype.http = http;


// 初始化
new Vue({
    components,
    router,
    store,
    directive,
    i18n,
    Blob,
    render: h => h(App)
}).$mount('#app');
