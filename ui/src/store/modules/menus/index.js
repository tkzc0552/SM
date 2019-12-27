import * as actions from './actions';
import * as getters from './getters';
import mutations from './mutations';
import * as constants from './constants';


const state = {
    menus: JSON.parse(window.localStorage.getItem(constants.STORAGE_KEY_MENUS) || '[]'),
    buttons: JSON.parse(window.localStorage.getItem(constants.STORAGE_KEY_BUTTONS) || '{}')
};

export default {
    state,
    actions,
    getters,
    mutations
}
