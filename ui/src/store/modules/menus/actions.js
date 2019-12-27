import * as constants from './constants';
import http from '../../../axios/http';
import {isEmpty, isNotEmpty} from "../../../filter";

const doFetch = commit => {
    // console.debug("actions.doFetch");
    clean(commit);
    const isDebug = process.env.NODE_ENV === 'development';
    // console.debug("isDebug = " + isDebug);
    let menu_url;
    if (isDebug) {
        menu_url = constants.FETCH_MENUS_URL;
    } else {
        menu_url = constants.FETCH_MENUS_URL;
    }
    // console.debug("menu_url : " + menu_url);
    return http.get(menu_url).then(response => {
        // console.debug("request menu response : " + JSON.stringify(response));
        let code = response['code'];
        if (code === '00000') {
            let root = response['data'];
            let menus = [];
            if(root.constructor === Array) {
                // console.debug("root.constructor === Array");
                if(root.length > 0) {
                    // console.debug("root.length > 0, menus = root[0]['children']");
                    menus = root[0]['children'];
                } else {
                    // console.debug("root.length == 0, menus = []");
                    menus = [];
                }
            } else if(root.constructor === Object) {
                // console.debug("root.constructor === Object, menus = root['children']");
                menus = root['children'];
            }
            // console.debug("return menus : " + JSON.stringify(menus));
            if(menus != null && menus.constructor === Array && menus.length > 0) {
                commit('SAVE_MENUS', menus);
                window.sessionStorage.setItem(constants.STORAGE_KEY_MENUS, JSON.stringify(menus));
                window.sessionStorage.setItem(constants.STORAGE_KEY_MENUS_LAST_LOAD_TIME, new Date().getTime().toString());
                saveButtons(commit, menus);
            }
            return menus;
        } else {
            // console.debug("response['code'] = " + code + ", return menus : null");
            return null;
        }
    }).catch(function (error) {
        // console.warn("error:" + error + ", return menus : null");
        return null;
    });
};
// 获取menus列表后，初始化存储所有有权限的按钮
const saveButtons = (commit, menus) => {
    // console.debug("actions.saveButtons");
    // 所有按钮code列表
    let menuButtons = {};
    setButtons(commit, menus, menuButtons);
    // console.debug("------- SAVE_BUTTONS -------");
    if(menuButtons.constructor === Object && Object.getOwnPropertyNames(menuButtons).length > 0) {
        // console.debug("menuButtons is not empty, save in sessionStorage");
        window.sessionStorage.setItem(constants.STORAGE_KEY_BUTTONS, JSON.stringify(menuButtons));
    }
    commit('SAVE_BUTTONS', menuButtons);
    return menuButtons;
};

const setButtons = (commit, menus, menuButtons) => {
    // console.debug("actions.setButtons,   menuButtons = " + JSON.stringify(menuButtons));
    if(menus == null || menus.length === 0) {
        // console.debug("menus is empty, return...");
        return;
    }
    for (let i = 0; i < menus.length; i++) {
        let menuItem = menus[i];
        if(menuItem == null) {
            // console.debug("menus[" + i + "] == null, continue");
            continue;
        }
        let menuLink = menuItem['menuUrl'];
        // console.debug("menuLink = " + menuLink);
        if(isNotEmpty(menuLink)) {
            // console.debug("menuLink = " + menuLink);
            let buttons = menuButtons[menuLink];
            if(buttons == null) {
                buttons  = [];
            }
            let opts = menuItem.opts;
            if (opts != null && opts.constructor === Array && opts.length > 0) {
                for (let j = 0; j < opts.length; j++) {
                    let menuEname = opts[j]['menuEname'];
                    // console.debug("optEname = " + optEname);
                    if(isNotEmpty(menuEname)) {
                        if (buttons.indexOf(menuEname) === -1) {
                            buttons.push(menuEname);
                        }
                    }
                }
            }
            if(buttons.length > 0) {
                // console.debug("buttons.length > 0, buttons : " + JSON.stringify(buttons));
                menuButtons[menuLink] = buttons;
            }
        }
        if(menuItem.children != null && menuItem.children.constructor === Array && menuItem.children.length > 0) {
            setButtons(commit, menuItem.children, menuButtons);
        }
    }
};

const clean = commit => {
    // console.debug("actions.clean");
    window.sessionStorage.removeItem(constants.STORAGE_KEY_MENUS);
    window.sessionStorage.removeItem(constants.STORAGE_KEY_BUTTONS);
    window.sessionStorage.removeItem(constants.STORAGE_KEY_MENUS_LAST_LOAD_TIME);
    commit('CLEAN_MENUS');
};


const fetchFromStorage = commit => {
    // console.debug("actions.fetchFromStorage");
    let menusJson = window.sessionStorage.getItem(constants.STORAGE_KEY_MENUS);
    let menus = JSON.parse(menusJson);
    // console.debug("cached menus: " + JSON.stringify(menus));
    // console.debug("------- SAVE_MENUS -------");
    commit('SAVE_MENUS', menus);
    let buttonsJson = window.sessionStorage.getItem(constants.STORAGE_KEY_BUTTONS);
    // console.debug("cached buttons: " + buttonsJson);
    let needResetButtons = false;
    let buttons;
    if(isEmpty(buttonsJson)) {
        needResetButtons = true;
    } else {
        buttons = JSON.parse(buttonsJson);
        if(buttons != null) {
            needResetButtons = true;
        } else if(buttons.constructor === Object && Object.getOwnPropertyNames(buttons).length === 0) {
            needResetButtons = true;
        }
    }
    if(needResetButtons === true) {
        buttons = saveButtons(commit, menus);
    }
    // console.debug("final buttons: " + JSON.stringify(buttons));

    // console.debug("------- SAVE_BUTTONS -------");
    commit('SAVE_BUTTONS', buttons);
    return menus;
};

export const loadPermissions = ({commit}) => {
    // console.debug("actions.loadPermissions");
    let lastLoadTimeStr = window.sessionStorage.getItem(constants.STORAGE_KEY_MENUS_LAST_LOAD_TIME);
    // console.debug("lastLoadTime = " + lastLoadTimeStr);
    if (lastLoadTimeStr) {
        let lastLoadTime = parseInt(lastLoadTimeStr);
        if (isNaN(lastLoadTime)) {
            // console.debug("lastLoadTime isNaN");
            return doFetch(commit);
        }
        if ((new Date().getTime() - lastLoadTime) > 1800000) {
            // console.debug("lastLoadTime: " + lastLoadTime + " expired ... ");
            return doFetch(commit);
        } else {
            // console.debug("使用缓存的menus");
            return fetchFromStorage(commit);
        }
    } else {
        // console.debug("lastLoadTime == null, clean");
        return doFetch(commit);
    }
};
