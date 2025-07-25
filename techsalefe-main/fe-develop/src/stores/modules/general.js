const state  = {
  isLoading: false
}

const mutations = {
  activeLoading(state) {
    state.isLoading = true
  },
  deactivateLoading(state) {
    state.isLoading = false
  }
}

const actions = {
  activeLoading({commit}) {
    commit("activeLoading")
  },
  deactivateLoading({commit}) {
    commit("deactivateLoading")
  }
}

const getters = {
  getLoadingStatus: (state) => state.isLoading
}

const loadingAction = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default loadingAction