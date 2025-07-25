import AuthApi from '@/api/AuthApi'
import {AuthUtils} from "@/utils/localStorageUtils";
import { useJwt } from '@vueuse/integrations/useJwt'
import {ref} from "vue";
import Cookies from 'js-cookie';
const state = {
    token: AuthUtils.getToken(),
    tokenFormCookies: AuthUtils.getTokenFromCookies(),
    user: null
}
const mutations = {
    SET_TOKEN: (state, token) => {
        if (AuthUtils.getTokenFromCookies()) {
            state.token = AuthUtils.getTokenFromCookies()
        } else {
            state.token = token
        }
    },
    SET_TOKEN_COOKIE: (state, tokenCookie) => {
        state.tokenFormCookies = tokenCookie
    },
    SET_USERINFO: (state, user) => {
        state.user = user
    },
    CLEAR_USERINFO: (state) => {
        state.token = null
        state.tokenFormCookies = null
        state.user = {}
    },
    SET_USER_ROLE: (status, role) => {
        state.userRole = role
    }
}

const actions = {
    login: async ({ commit }, data) => {
        try {
            const res  = await AuthApi.login(data)
            if(res.data.jwt && res.data.file){
                const imageData = `data:image/png;base64,${res.data.file}`;
                localStorage.setItem('userImage', imageData);
            }
            
            commit("SET_TOKEN", res.data.jwt)
            commit("SET_TOKEN_COOKIE", res.data.jwt)

            AuthUtils.setToken(res.data.jwt)
            AuthUtils.setTokenInCookies(res.data.jwt)
            return true
        }
        catch (e) {
            return false
        }
    },
    setTokenInCookies: ({commit}, token) => {
        commit('SET_TOKEN_COOKIE', token)
    },
    logout: async ({ commit, dispatch }, data) => {
        try {
            const res = await AuthApi.logout()
            dispatch("clearUser")
            AuthUtils.removeToken()
            AuthUtils.removeTokenFromCookie()
        } catch (e) {
            return false
        }
    },
    clearUser: ({ commit }) => {
        commit('CLEAR_USERINFO')
        AuthUtils.removeToken()
        AuthUtils.removeTokenFromCookie()
        localStorage.removeItem("userImage");
    },
    async getAuthInfo({commit, dispatch}) {
        try {
            const encodedJwt = ref(AuthUtils.getTokenFromCookies())
            const { header, payload } = useJwt(encodedJwt)
            const base64ImageData = localStorage.getItem("userImage");
            if(base64ImageData){
                payload.value.avtUrl=base64ImageData;
            }
            commit('SET_USERINFO', payload.value)
            commit('SET_USER_ROLE', payload.value.role)

        } catch (e) {
            console.log(e);
            dispatch("clearUser")
        }
    }
}

const getters = {
    getUserInfo: (state) => state.user
}

export default {
    namespaced: true,
    state,
    actions,
    mutations,
    getters
}

