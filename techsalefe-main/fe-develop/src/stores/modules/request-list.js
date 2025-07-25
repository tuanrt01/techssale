import requestAdminApi from "@/api/RequestAdminApi";
import RequestApi from "@/api/RequestApi";

const state = {

  isDetailModalOpen: false,
  isCompleteModalOpen: false,
  isRejectModalOpen: false,
  isConfirmModalOpen: false,
  isCancelModalOpen: false,
  isConfirmOrderModalOpen: false,
  isConfirmModalDelete: false,
  isCreateModalOpen: false,
  isMyDetailModalOpen: false,
  id: "",
  details: {},
  listRequests: {},
  listRequestAdmin: {},
  dataForDup: {},
  requestAction: 'CREATE',
  purchaseHistory: [],
  purchaseHistoryPagination: {},
  purchaseHistoryParams: {
    "size": 10,
    "sort": "",
    "requestReason": "",
    "page": 0,
    "direction": "",
    "createdAtBegin": "",
    "createdAtEnd": "",
    "expectReceiveDateBegin": "",
    "expectReceiveDateEnd": "",
    "userAssignedId": "",
    "name": "",
    "statusList": "",
    "isAdmin": false,
    "priceFrom": "",
    "priceTo": ""
  },
  params: {
    "size": 50,
    "sort": "",
    "requestReason": "",
    "page": 0,
    "direction": "",
    "createdAtBegin": "",
    "createdAtEnd": "",
    "expectReceiveDateBegin": "",
    "expectReceiveDateEnd": "",
    "userAssignedId": "",
    "name": "",
    "statusList": "",
    "isAdmin": false
  },
  paramsForAdmin: {
    "size": 5,
    "sort":  "",
    "requestReason": "",
    "page": 0,
    "direction": "",
    "createdAtBegin": "",
    "createdAtEnd": "",
    "expectReceiveDateBegin": "",
    "expectReceiveDateEnd": "",
    "userAssignedId": "",
    "name": "",
    "status": "",
    "isAdmin": true
  },
  resultDataDisplay: [],
  buyerUserId: null
}

const mutations = {
  openDetailModal(state) {
    state.isDetailModalOpen = true
  },
  closeDetailModal(state) {
    state.isDetailModalOpen = false
  },
  openCompleteModal(state) {
    state.isCompleteModalOpen = true
  },
  closeCompleteModal(state) {
    state.isCompleteModalOpen = false
  },
  openRejectModal(state) {
    state.isRejectModalOpen = true
  },
  closeRejectModal(state) {
    state.isRejectModalOpen = false
  },
  openConfirmModal(state) {
    state.isConfirmModalOpen = true
  },
  closeConfirmModal(state) {
    state.isConfirmModalOpen = false
  },
  openDeleteModal(state){
    state.isConfirmModalDelete = true
  },
  closeDeleteModal(state){
    state.isConfirmModalDelete = false
  },
  openCancelModal(state) {
    state.isCancelModalOpen = true
  },
  closeCancelModal(state) {
    state.isCancelModalOpen = false
  },
  openConfirmOrderModal(state){
    state.isConfirmOrderModalOpen = true
  },
  closeConfirmOrderModal(state){
    state.isConfirmOrderModalOpen = false
  },
  openCreateModal(state) {
    state.isCreateModalOpen = true
  },
  closeCreateModal(state) {
    state.isCreateModalOpen = false
  },
  openMyDetailModal(state) {
    state.isMyDetailModalOpen = true
  },
  closeMyDetailModal(state) {
    state.isMyDetailModalOpen = false
  },
  SET_LIST(state, list) {
    state.listRequests = list
  },
  SET_LIST_ADMIN(state, list) {
    state.listRequestAdmin = list
  },
  SET_PURCHASE_HISTORY(state, data) {
    state.purchaseHistory = data.content || []
    state.purchaseHistoryPagination = {
      totalPages: data.totalPages || 0,
      totalElements: data.totalElements || 0,
      size: data.size || 10,
      number: data.number || 0
    }
  },
  SET_RESULT_DISPLAY(state, list) {
    state.resultDataDisplay = list
  },
  switchToDuplicate(state) {
    state.requestAction = "DUPLICATE"
  },
  switchToCreate(state) {
    state.requestAction = "CREATE"
  },
  SET_BUYER_USER_ID(state, id) {
    state.buyerUserId = id;
  }
}

const actions = {
  setId: ({state}, id) => {
    state.id = id
  },
  setParams: ({state}, params) => {
    state.params = params
  },
  setPurchaseHistoryParams: ({state}, params) => {
    state.purchaseHistoryParams = params
  },
  setParamsForAdmin: ({state}, params) => {
    state.paramsForAdmin = params
  },
  setDataForDup: ({state}, params) => {
    state.dataForDup = params
  },
  fetchDetails: async ({commit, state}) => {
    // Only fetch if we have an ID
    if (state.id) {
      try {
        // Force a fresh request from the server, bypass cache
        const res = await requestAdminApi.getDetailRequest(state.id);
        if (res.status === 200) {
          // Update the details with fresh data
          state.details = res.data;
        }
      } catch (error) {
        console.error('Error fetching request details:', error);
      }
    }
  },
  openDetailModal: async ({commit, state}) => {
    commit("openDetailModal")
  },
  closeDetailModal({commit}) {
    commit("closeDetailModal")
  },
  openCompleteModal({commit}) {
    commit("openCompleteModal")
  },
  closeCompleteModal({commit}) {
    commit("closeCompleteModal")
  },
  openRejectModal({commit}) {
    commit("openRejectModal")
  },
  closeRejectModal({commit}) {
    commit("closeRejectModal")
  },
  openConfirmModal({commit}, payload) {
    commit("openConfirmModal");
    commit("SET_BUYER_USER_ID", payload ? payload.buyerUserId : null);
  },
  closeConfirmModal({commit}) {
    commit("closeConfirmModal")
  },
  openCancelModal({commit}) {
    commit("openCancelModal")
  },
  closeCancelModal({commit}) {
    commit("closeCancelModal")
  },
  openDeleteModal({commit}) {
    commit("openDeleteModal")
  },
  closeDeleteModal({commit}){
    commit("closeDeleteModal")
  },
  openConfirmOrderModal({commit}) {
    commit("openConfirmOrderModal")
  },
  closeConfirmOrderModal({commit}){
    commit("closeConfirmOrderModal")
  },
  openCreateModal({commit}) {
    commit("openCreateModal")
  },
  closeCreateModal({commit}) {
    commit("closeCreateModal")
  },
  openMyDetailModal({commit}) {
    commit("openMyDetailModal")
  },
  closeMyDetailModal({commit}) {
    commit("closeMyDetailModal")
  },
  async fetchListRequest({commit, state}) {
    try {
      const res = await RequestApi.getAllRequest(state.params)
      commit("SET_LIST", res.data.content)
    } catch (e) {

    }
  },
  async fetchPurchaseHistory({commit, state}) {
    try {
      const res = await RequestApi.getAllRequest(state.purchaseHistoryParams)
      commit("SET_PURCHASE_HISTORY", res.data)
    } catch (e) {
      console.error("Error fetching purchase history:", e)
      commit("SET_PURCHASE_HISTORY", { content: [] })
    }
  },
  async fetchListRequestAdmin({commit, state, dispatch}) {
    try {
      const res = await RequestApi.getAllRequest(state.paramsForAdmin)
      commit("SET_LIST_ADMIN", res.data)
      dispatch("setResultDisplay");
    } catch (e) {

    }
  },
  setResultDisplay ({commit, state}) {
    commit("SET_RESULT_DISPLAY", state.listRequestAdmin.content)
  },
  switchToCreate({commit}) {
    commit("switchToCreate")
  },
  switchToDuplicate({commit}) {
    commit("switchToDuplicate")
  }
}

const getters = {
  getDetailModalState: (state) => state.isDetailModalOpen,
  getCompleteModalState: (state) => state.isCompleteModalOpen,
  getRejectModalState: (state) => state.isRejectModalOpen,
  getConfirmModalState: (state) => state.isConfirmModalOpen,
  getCancelModalState: (state) => state.isCancelModalOpen,
  getCreateModalState: (state) => state.isCreateModalOpen,
  getMyDetailModalState: (state) => state.isMyDetailModalOpen,
  getDetails: (state) => state.details,
  getListRequest: (state) => state.listRequests,
  getListRequestAdmin: (state) => state.listRequestAdmin,
  getPurchaseHistory: (state) => state.purchaseHistory,
  getPurchaseHistoryPagination: (state) => state.purchaseHistoryPagination,
  getConfirmDelete: (state) =>state.isConfirmModalDelete,
  getConfirmOrder: (state) =>state.isConfirmOrderModalOpen,
  getResultsDisplay: (state) => state.resultDataDisplay,
  getRequestActionState: (state) => state.requestAction,
  getDataForDup: (state) =>  state.dataForDup,
  getBuyerUserId: (state) => state.buyerUserId,
  getId: (state) => state.id
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}