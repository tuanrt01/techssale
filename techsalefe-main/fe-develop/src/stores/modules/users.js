import UserAssign from "@/api/UserAssign";
import { helper } from "@/utils/helper";

const state = {
  listUserAssigner: [],
  listUserBuyer: [],
  filteredUserBuyer: []
}

const mutations = {
  SET_LIST: (state, list) => {
    state.listUserAssigner = list
  },
  SET_BUYER_LIST: (state, list) => {
    state.listUserBuyer = list
  },
  SET_FILTERED_BUYER_LIST: (state, list) => {
    state.filteredUserBuyer = list
  }
}

const actions = {
  fetchListAssigners: async ({commit}) => {
    try {
      const res = await UserAssign.getUserAssign()
      commit('SET_LIST', res.data)
    } catch (e) {
    }
  },
  fetchListBuyers: async ({commit}) => {
    try {
      const res = await UserAssign.getUserBuyer()
      commit('SET_BUYER_LIST', res.data)
    } catch (e) {
    }
  },
  searchUserBuyer: ({commit, state}, { query }) => {
    if (!query || query.trim() === '') {
      commit('SET_FILTERED_BUYER_LIST', [])
      return
    }
    
    const normalizedQuery = helper.removeVietnameseTones(query.toLowerCase())
    
    const filteredList = state.listUserBuyer.filter(user => {
      const normalizedName = helper.removeVietnameseTones(user.name.toLowerCase())
      return normalizedName.includes(normalizedQuery)
    })
    
    commit('SET_FILTERED_BUYER_LIST', filteredList)
  }
}

const getters = {
  getUserAssign: (state) => state.listUserAssigner,
  getUserBuyer: (state) => state.filteredUserBuyer.length > 0 ? state.filteredUserBuyer : state.listUserBuyer
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
};
