/* eslint global-require: 0 */
import Vuex from 'vuex';
import createPersistedState from "vuex-persistedstate";
import auth from "@/stores/modules/auth";
import requestList from "@/stores/modules/request-list";
import users from "@/stores/modules/users";
import general from "@/stores/modules/general";
const store = new Vuex.Store({
  modules: {
    auth,
    requestList,
    users,
    general
  },
  plugins: [createPersistedState({paths: ["auth"]})],

  mutations: {
    SET_LANG(state, payload) {
    }
  },

  actions: {
    setLang({commit}, payload) {
      commit('SET_LANG', payload)
    }
  }
});

export default store;