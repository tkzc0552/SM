import Vue from 'vue';
import Vuex from 'vuex';
import app from './modules/app';
import menus from './modules/menus';
import tags from './modules/tags';
import getters from './getters';

Vue.use(Vuex);

export default new Vuex.Store({
    modules: {
        app,
        menus,
        tags
    },
    getters,
})
