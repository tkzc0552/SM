import Cookies from 'js-cookie';
import i18n from "../../../lang";

const app = {
    state: {
        sidebar: {
            opened: !+Cookies.get('sidebarStatus')
        },
        theme: 'default',
        language: Cookies.get('language') || i18n.locale,
        size: Cookies.get('size') || 'small'
    },
    mutations: {
        TOGGLE_SIDEBAR: state => {
            if (state.sidebar.opened) {
                Cookies.set('sidebarStatus', 1);
            } else {
                Cookies.set('sidebarStatus', 0);
            }
            state.sidebar.opened = !state.sidebar.opened;
        },
        SET_LANGUAGE: (state, language) => {
            state.language = language
            Cookies.set('language', language)
        },
        SET_SIZE: (state, size) => {
            state.size = size
            Cookies.set('size', size)
        }
    },
    actions: {
        ToggleSideBar: ({commit}) => {
            commit('TOGGLE_SIDEBAR')
        },
        setTheme: ({commit}, theme) => {
            commit('SET_THEME', theme)
        },
        setLanguage({ commit }, language) {
            commit('SET_LANGUAGE', language)
        },
        setSize({ commit }, size) {
            commit('SET_SIZE', size)
        }
    }
};

export default app;
