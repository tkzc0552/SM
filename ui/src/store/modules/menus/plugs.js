import * as types from './mutation-type'
import * as constants from './constants'

export default function localStoragePlugin() {
    return store => {
        store.subscribe((mutation, state) => {
            // console.log("store.subscribe");
            if (mutation.type === types.CLEAN_MENUS) {
                // console.log("store.subscribe CLEAN_MENUS");
                window.localStorage.removeItem(constants.STORAGE_KEY_MENUS);
                window.localStorage.removeItem(constants.STORAGE_KEY_MENUS_LAST_LOAD_TIME);
            } else if (mutation.type === types.SAVE_MENUS) {
                // console.log("store.subscribe SAVE_MENUS");
                window.localStorage.setItem(constants.STORAGE_KEY_MENUS, state.menus);
                window.localStorage.setItem(constants.STORAGE_KEY_MENUS_LAST_LOAD_TIME, new Date().getTime().toString());
            }
        })
    }
}
