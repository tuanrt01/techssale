const state = {
  url: ""
}

const mutations = {
  setLinkImage(state, obj) {
    state.url = obj
  }
}

const actions = {
  updateLinkImage({commit}, obj) {
    commit('setLinkImage', obj)
  }
}

const getters = {
  getLinkImage(state) {
    return state.url
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
};