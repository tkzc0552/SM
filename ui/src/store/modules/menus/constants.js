const contextPath = window.location.pathname.substr(1, location.pathname.substr(1).indexOf("/"));
export const STORAGE_KEY_MENUS = 'sd' + contextPath + '_menus';
export const STORAGE_KEY_MENUS_LAST_LOAD_TIME = 'sd' + contextPath + '_menus_last_load_time';
export const STORAGE_KEY_BUTTONS = 'sd' + contextPath + '_buttons';
export const FETCH_MENUS_URL = 'api/interface/permission';
export const FETCH_MENUS_URL_DEBUG = 'static/menus.json';
