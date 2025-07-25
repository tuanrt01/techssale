<template>
  <Loading :show-loading="loadingAction" />
  <DetailMyRequest :id="id" :isOpen="isDetailModalOpen" @close="closeDetailModal"/>
  <CreateRequest />
  <div class="mt-1">
    <div class="filter-wrapper">
      <!-- BEGIN: HTML Table Data -->
      <div class="intro-y box p-5 mt-3">
        <div class="flex flex-col">
          <form id="tabulator-html-filter-form" @submit.prevent="handleSearch" @keyup.enter="handleSearch">
            <div class="flex justify-evenly">
              <div>
                <div class="flex mb-2 search__width">
                  <label class="label__width flex items-center font-medium mr-4">{{$t('lang.SEARCH.SR1')}}</label>
                  <input
                      id="tabulator-html-filter-value"
                      v-model.trim="name"
                      class="form-control input-width"
                      :placeholder= "$t('lang.SEARCH.SR4')"
                      type="text"
                  />
                </div>
                <div class="flex search__width">
                  <label class="label__width flex items-center font-medium mr-2">{{$t('lang.DETAIL.DT1')}}</label>
                  <div v-for="(displayName, statusValue) in RequestStatus"
                       :key="statusValue"
                       :class="{
                            'shadow shadow-gray-400 hover:shadow-black-500/60 cursor-pointer': statusValue === 'WAITING' && selectedStatus !== 'WAITING',
                            'bg-yellow-400 text-white cursor-pointer': statusValue === 'WAITING' && selectedStatus === 'WAITING',
                            'shadow shadow-gray-400 hover:shadow-black-500/70 cursor-pointer': statusValue === 'PROCESSING' && selectedStatus !== 'PROCESSING',
                            'bg-sky-300 text-white cursor-pointer': statusValue === 'PROCESSING' && selectedStatus === 'PROCESSING',
                            'shadow shadow-gray-400 hover:shadow-black-500/90 cursor-pointer': statusValue === 'ORDERING' && selectedStatus !== 'ORDERING',
                            'bg-green-400 text-white cursor-pointer': statusValue === 'ORDERING' && selectedStatus === 'ORDERING',
                            'shadow shadow-gray-400 hover:shadow-black-500/80 cursor-pointer': statusValue === 'SUCCESS' && selectedStatus !== 'SUCCESS',
                            'bg-green-300 text-white cursor-pointer': statusValue === 'SUCCESS' && selectedStatus === 'SUCCESS',
                            'shadow shadow-gray-400 hover:shadow-black-500/50 cursor-pointer': statusValue === 'REJECTED' && selectedStatus !== 'REJECTED',
                            'bg-red-300 text-white cursor-pointer': statusValue === 'REJECTED' && selectedStatus === 'REJECTED',
                           }"
                       :value="statusValue"
                       class="flex h-8 justify-center items-center p-2 m-2 rounded-xl test"
                       @click="filterByStatus(statusValue)">
                    {{ $t(displayName) }}
                  </div>
                </div>
              </div>
              <div>
                <div class="flex items-center gap-10 mb-2">
                  <label class="w-20 flex items-center font-medium"
                  >{{$t('lang.DETAIL.DT2')}}</label
                  >
                  <div class="flex gap-5">
                    <VueDatePicker v-model="filterByExpectedDateFrom" auto-apply
                                   class="deadline w-40 text-center form-control"
                                   format="dd/MM/yyyy"
                                   @update:model-value="handleChangeDateRangeByEndDateFrom"
                    />
                    <div class="font-medium text-lg flex items-center">~</div>
                    <VueDatePicker v-model="filterByExpectedDateTo" auto-apply
                                   class="deadline w-40 form-control text-center"
                                   format="dd/MM/yyyy"
                                   @update:model-value="handleChangeDateRangeByEndDateTo"
                    />
                  </div>
                </div>
                <div class="flex items-center gap-10">
                  <label class="w-20 flex items-center font-medium">{{$t('lang.DETAIL.DT3')}}</label>
                  <div class="flex gap-5">
                    <VueDatePicker
                        v-model="filterByCreatedDateFrom"
                        auto-apply
                        class="w-40 text-center form-control"
                        format="dd/MM/yyyy"
                        @update:model-value="handleChangeDateRangeByCreatedDateFrom"
                    />
                    <div class="font-medium text-lg flex items-center">~</div>
                    <VueDatePicker
                        v-model="filterByCreatedDateTo"
                        auto-apply
                        class="w-40 text-center form-control"
                        format="dd/MM/yyyy"
                        @update:model-value="handleChangeDateRangeByCreatedDateTo"
                    />
                  </div>
                </div>
              </div>
              <div class="">
                <div class="mt-2 xl:mt-0">
                  <button id="tabulator-html-filter-go" class="btn btn-primary w-full sm:w-16 mr-3"
                          type="button" @click="handleSearch">
                    {{$t('lang.SEARCH.SR2')}}
                  </button>
                  <button
                      id="tabulator-html-filter-reset"
                      class="btn btn-secondary w-full sm:w-16 mt-2 sm:mt-0 sm:ml-1"
                      type="button"
                      @click="resetFilters"
                  >
                    {{$t('lang.SEARCH.SR3')}}
                  </button>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="mt-5">
      <div :class="{'scroll-view' : result.length > 6}" class="scroll-list h-2 mt-3">
        <div class="grid grid-cols-4 gap-8 ml-24 mr-24 justify-center items-center leading-7">
          <div v-for="(item, index) in result" :key="index"
               class="bg-white p-4 shadow-md rounded-2xl cursor-pointer" @click="openDetailModal(item)">
            <div class="flex justify-end -ml-6 ">
              <button
                  class="relative inset-0 flex items-center justify-center text-black bg-gray-200 w-10 rounded-2xl h-4 -mb-10 -mt-3 -mr-1"
                  @click="openDetailModal(item)">
                <MoreHorizontalIcon class="w-5 h-5"/>
              </button>
            </div>
            <div class="flex">
              <div>
                <div class="mr-3 font-bold">{{$t('lang.DETAIL.DT4')}} 2:</div>
                <div class="font-bold mr-16">{{$t('lang.DETAIL.DT5')}}:</div>
                <div class="font-bold mr-14">{{$t('lang.DETAIL.DT1')}}:</div>
                <div v-if="item.status === 'WAITING' || item.status === 'REJECTED'" class= "mr-14 font-bold my-3">{{$t('lang.DETAIL.DT6')}}:</div>
                <div v-else class="mr-14 font-bold my-3">{{$t('lang.DETAIL.DT8')}}:</div>
                <div class="mr-2 font-bold">{{$t('lang.DETAIL.DT7')}}:</div>
                <div class="font-bold mr-14">{{$t('lang.DETAIL.DT3')}}:</div>
              </div>
              <div>
                <div class="font-bold"
                     :class="{
                       'text-green-600': item.status !== 'REJECTED',
                       'text-black-600': item.status === 'REJECTED',
                       'font-bold text-red-600': dateResultFormat(item.expectReceiveDate) <= isToday && (item.status === 'WAITING' || item.status === 'PROCESSING')
                      }">
                  {{ formatDate(item.expectReceiveDate) }}
                </div>
                <div>
                  <Tippy
                      :content="item?.productName"
                      :key="item?.id"
                      class="tooltip font-medium whitespace-nowrap"
                      tag="div"
                  ><span class="truncate">{{ truncateText(item.productName) }}</span>
                  </Tippy>
                </div>
                <div class="text-center text-white w-28 rounded-xl mb-2" :class="{
                    'bg-yellow-400': item.status ==='WAITING',
                    'bg-sky-300': item.status ==='PROCESSING',
                    'bg-green-400': item.status ==='ORDERING',
                    'bg-green-300': item.status ==='SUCCESS',
                    'bg-red-300': item.status ==='REJECTED'
                  }">{{ $t(statusDisplayName[item.status]) }}
                </div>
                <div>
                  <AdminTagForRequest
                      :admin="item.status === 'WAITING' || item.status === 'REJECTED' ? item.userAssign : item.buyer"
                      class="h-9"
                  />
                </div>
                
                <div>
                  <Tippy
                    :content="findReasonText(item.requestReason)"
                    :key="`reason-${item.id}`"
                    class="tooltip font-medium whitespace-nowrap"
                    tag="div"
                  >
                    <span class="truncate">{{ truncateText(findReasonText(item.requestReason)) }}</span>
                  </Tippy>
                </div>
                <div>{{ formatDate(item.createdAt) }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="flex justify-center fixed-button-container">
          <button
              class="bg-orange-500 hover:bg-orange-700 text-white-800 font-bold p-3 transition-all ease-in-out delay-150 inline-flex items-center text-white rounded-3xl shadow-2xl fixed-button-content"
              @click="openCreateModal">
            <PlusIcon class="h-7 mr-2 w-7 font-bold"/>
            <span class="text-md">{{$t('lang.DETAIL.DT15')}}</span>
          </button>
        </div>
        <div v-if="result.length <= 0"
             class="text-sky-700 flex flex-col items-center justify-center p-40 font-medium text-2xl">
          <img alt="" class="w-16 h-16 mb-4" src="/src/assets/images/icons8-empty-box-64.png"/>
          <span>{{$t('lang.DETAIL.DT16')}}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import VueDatePicker from "@vuepic/vue-datepicker";
import '@vuepic/vue-datepicker/dist/main.css'
import AdminTagForRequest from '@/components/avatar-bubble/Main.vue'
import DetailMyRequest from "@/views/user/list-requested/DetailMyRequest.vue";
import moment from "moment";
import VTooltip from 'v-tooltip';
import {RequestStatus} from "@/common/StatusEnum";
import {RequestReason} from "@/common/RequestReasonEnum";
import CreateRequest from "@/views/user/list-requested/CreateRequest.vue";
import {mapGetters} from "vuex";
import Loading from "@/components/loading/Loading.vue";
import ReasonApi from "@/api/ReasonApi.js"

export default {
  name: "ListRequests",
  components: {
    Loading,
    CreateRequest,
    DetailMyRequest,
    VueDatePicker,
    AdminTagForRequest
  },
  directives: {
    tooltip: VTooltip,
  },
  data() {
    return {
      filterByCreatedDateFrom: "",
      filterByCreatedDateTo: "",
      filterByExpectedDateFrom: "",
      filterByExpectedDateTo: "",
      name: "",
      status: '',
      filteredData: [],
      userRequestName: '',
      isClicked: false,
      sort: '',
      sortDirection: '',
      selectedStatus: '',
      requestReasonDetail: '',
      buyerUsername:'',
      reasonList : [],
    }
  },
  created() {
    this.$store.dispatch("requestList/fetchListRequest")
    this.fetchListReason()
  },
  computed: {
    isToday() {
      return new Date()
    },
    RequestStatus() {
      return RequestStatus
    },
    statusDisplayName() {
      return {
        '': 'Tất cả',
        ...RequestStatus
      }
    },
    reasonDisplayName() {
      return {
        ...RequestReason
      }
    },
    ...mapGetters({
      result: "requestList/getListRequest",
      isDetailModalOpen: "requestList/getMyDetailModalState",
      loadingAction: "general/getLoadingStatus"
    })
  },
  watch: {
    "perPage": function () {
      this.dataPage = this.paginate(this.result, this.perPage, this.currentPage);
    }
  },
  methods: {
    findReasonText(reasonId) {
      if (!reasonId || !this.reasonList || this.reasonList.length === 0) return '';
            const directReason = this.reasonList.find(reason => reason.id == reasonId);
      if (directReason) {
        return this.$t(directReason.reason);
      }
      for (const parentReason of this.reasonList) {
        if (parentReason.children && Array.isArray(parentReason.children)) {
          const childReason = parentReason.children.find(child => child.id == reasonId);
          if (childReason) {
            return `${this.$t(childReason.reason)}`;
          }
        }
      }
      return '';
    },
    dateResultFormat(date) {
      return new Date(date)
    },
    async handleSearch() {
      this.list();
    },
    resetFilters() {
      this.filterByCreatedDateFrom = "";
      this.filterByCreatedDateTo = "";
      this.filterByExpectedDateFrom = "";
      this.filterByExpectedDateTo = "";
      this.selectedStatus = "";
      this.name = "";
      this.list();
    },
    openDetailModal(request) {
      this.id = request.id
      this.$store.dispatch("requestList/setId", request.id)
      this.$store.dispatch("requestList/fetchDetails")
      this.$store.dispatch("requestList/openMyDetailModal")
    },

    closeDetailModal() {
      this.$store.dispatch("requestList/closeMyDetailModal")
    },
    openCreateModal() {
      this.$store.dispatch("requestList/openCreateModal")
      this.$store.dispatch("requestList/switchToCreate")
    },
    formatDate(date) {
      if (!date) {
        return '';
      }
      return moment(date).format("DD-MM-YYYY");
    },

    formatDateForSendingRequest(date) {
      if (!date) {
        return '';
      }
      return moment(date).format("YYYY-MM-DD");
    },

    async list() {
      let params = {
        "sort": '',
        "direction": '',
        "requestReason": "",
        "createdAtBegin": this.formatDateForSendingRequest(this.filterByCreatedDateFrom),
        "createdAtEnd": this.formatDateForSendingRequest(this.filterByCreatedDateTo),
        "expectReceiveDateBegin": this.formatDateForSendingRequest(this.filterByExpectedDateFrom),
        "expectReceiveDateEnd": this.formatDateForSendingRequest(this.filterByExpectedDateTo),
        "userAssignedId": "",
        "statusList": this.selectedStatus,
        "isAdmin": false,
        "requestReasonDetail": this.requestReasonDetail,
        "name": this.name
      }
      this.$store.dispatch("general/activeLoading")
      this.$store.dispatch("requestList/setParams", params)
      await this.$store.dispatch("requestList/fetchListRequest")
      this.$store.dispatch("general/deactivateLoading")
    },

    updateStatusFilter() {
      this.list()
    },
    updateSearch() {
      this.list()
    },
    handleChangeDateRangeByCreatedDateFrom(newDate) {
      this.filterByCreatedDateFrom = newDate
    },
    handleChangeDateRangeByCreatedDateTo(newDate) {
      this.filterByCreatedDateTo = newDate
    },
    handleChangeDateRangeByEndDateFrom(newDate) {
      this.filterByExpectedDateFrom = newDate
    },
    handleChangeDateRangeByEndDateTo(newDate) {
      this.filterByExpectedDateTo = newDate
    },
    filterByStatus(statusValue) {
      if (this.selectedStatus === statusValue) {
        this.selectedStatus = '';
      } else {
        this.selectedStatus = statusValue;
      }
      // Automatically fetch new data when status changes
      this.list();
    },
    truncateText(text) {
      if (text.length > 15) {
        return text.substring(0, 15) + '...'
      }
      return text
    },
    async fetchListReason(){
      let params = ""
      const res = await ReasonApi.getReasonAssign(params)
      this.reasonList = res.data
    },
  }

}
</script>

<style scoped>
.grid {
  display: grid;
}

.table th {
  padding: 0.75rem
}

.table td {
  padding: 0.25rem 0.75rem
}

.table th:first-child {
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
}

.table th:last-child {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

.table thead tr {
  border-radius: 5px;
  font-size: 15px;
}

.scroll-view {
  height: 500px;
  overflow-y: scroll;
}

.active {
  border-color: #EDD0C4;
  outline: none;
  box-shadow: 2px 2px 2px 2px #FCE1D3,
  2px -2px 2px 2px #FCE1D3,
  -2px 2px 2px 2px #FCE1D3,
  -2px -2px 2px 2px #FCE1D3;
}

.input__width {
  width: 160px;
}

.input-width {
  width: 68.5%;
}

.search__width {
  width: 725px;
}

.label__width {
  width: 100px;
}

.flex.justify-end {
  margin-top: 10px;
}

.fixed-button-container {
  position: fixed;
  bottom: 35px;
  right: 30px;
  z-index: 999;

}

.fixed-button-content {
  box-shadow: rgba(0, 0, 0, 0.16) 0px 10px 36px 0px, rgba(0, 0, 0, 0.06) 0px 0px 0px 1px;
  transition: all 0.3s ease;
}

.fixed-button-content:hover {
  transform: scale(1.1);
  box-shadow: rgba(0, 0, 0, 0.25) 0px 12px 35px 0px, rgba(0, 0, 0, 0.12) 0px 1px 10px 0px;
}
</style>
<style>
.content {
  min-height: 700px !important;
  padding-bottom: 0;
}

.dp__input:focus {
  border-color: #EDD0C4;
  outline: none;
  box-shadow: 2px 2px 2px 2px #FCE1D3,
  2px -2px 2px 2px #FCE1D3,
  -2px 2px 2px 2px #FCE1D3,
  -2px -2px 2px 2px #FCE1D3;
}
</style>
