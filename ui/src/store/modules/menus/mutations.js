import * as types from './mutation-type'

const mutations = {
    [types.SAVE_MENUS](state, menus) {
        // console.log("mutations.SAVE_MENUS, menus:" + JSON.stringify(menus));
        state.menus = menus;
    },
    [types.SAVE_BUTTONS](state, buttons) {
        // console.log("mutations.SAVE_BUTTONS, buttons:" + JSON.stringify(buttons));
        state.buttons = buttons;
    },

    [types.CLEAN_MENUS](state) {
        // console.log("mutations.CLEAN_MENUS");
        state.menus = [];
    },
    [types.CLEAN_BUTTONS](state) {
        // console.log("mutations.CLEAN_BUTTONS");
        state.buttons = {};
    }
};

export default mutations
