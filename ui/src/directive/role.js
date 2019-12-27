import Vue from 'vue';
import store from '../store';

export default Vue.directive('hasRole', {
    bind: function (el, binding) {
        console.debug("------------------- hasRole.bind -------------------");
        visibilityWithRole(el, binding);
    },
    inserted: function () {
        // console.debug("------------------- hasRole.inserted -------------------");
    },
    componentUpdated: function () {
        // console.debug("------------------- hasRole.componentUpdated -------------------");
    },
    unbind: function () {
        // console.debug("------------------- hasRole.unbind -------------------");
    },
    update: function (el, binding) {
        console.debug("------------------- hasRole.update -------------------");
        visibilityWithRole(el, binding);
    }
});


const visibilityWithRole = (el, binding) => {
    /*console.group("directive - hasRole")
    console.debug("el ↓ ");
    console.debug(el);
    console.debug("el.id:" + el.id);
    console.debug("el.attributes:" + el.getAttribute("aria-labelledby"));
    console.debug("el.type:" + el.type);
    console.debug("el.click:" + el.methods);
    console.debug("binding ↓ ");
    console.debug(binding);
    console.debug(binding.oldValue);
    console.debug(binding.value);
    console.groupEnd();*/

    // 获取当前登录用户有权限的按钮集合
    // console.debug("store.state.menus.buttons : " + JSON.stringify(store.state.menus.buttons));
    let menuButtons = store.state.menus.buttons;
    if(menuButtons == null) {
        return;
    }
    //获取当前页面路径对应的按钮权限
    let currentPath = window.location.hash.replace("#/", "/");
    let buttons = menuButtons[currentPath];
    console.debug("buttons : " + JSON.stringify(buttons));
    let bindingValue = binding.value;
    console.debug("binding.value : " + bindingValue);

    //绑定项是否有权限
    let hasRole = false;
    if (buttons != null && buttons.constructor === Array && buttons.length > 0) {
        if (buttons.indexOf(bindingValue.toString()) > -1) {
            hasRole = true;
        }
    }
    console.debug("hasRole(" + bindingValue + ") : " + hasRole);

    //绑定元素 隐藏/显示
    if(hasRole === false) {
        // 隐藏元素
        el.style.visibility = "hidden";
    } else {
        // 显示元素
        el.style.visibility = null;
    }

    //element-ui组件特殊处理
    //1.tab组件，还需要隐藏页签标题
    if(el.className.indexOf("el-tab-pane") > -1 && el.getAttribute("aria-labelledby")){
        console.debug("** Element-ui -> Tab 组件 **");
        // console.debug(document.getElementById("tab-merchant"));
        let tabDom = document.getElementById(el.getAttribute("aria-labelledby"));
        if(tabDom){
            if(hasRole === false) {
                // 隐藏元素
                tabDom.style.display = "none";
            } else {
                // 显示元素
                tabDom.style.display = "inline-block";
            }
        }
    }
};
