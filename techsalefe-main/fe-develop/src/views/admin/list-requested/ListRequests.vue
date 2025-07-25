<template>
  <DetailRequest/>
  <CompletePurchase/>
  <RejectPurchase/>
  <ConfirmRequest/>
  <CancelRequest/>
  <ConfirmOrder/>
  <Loading :show-loading="loadingAction" />

  <div class="mt-6">
    <div><h2 class="text-lg font-medium">{{$t('lang.TITLE.TT4')}}</h2></div>
    <div class="filter-wrapper">
      <!-- BEGIN: HTML Table Data -->
      <div class="intro-y box p-3 mt-3">
        <div class="flex flex-col">
          <form @keyup.enter="handleSearch" @submit.prevent="handleSearch" id="tabulator-html-filter-form class">
            <div class="flex justify-evenly">
              <div>
                <div class="flex mb-2 search__width">
                  <label class="label__width flex items-center font-medium mr-8">{{$t('lang.SEARCH.SR1')}}</label>
                  <input
                      id="tabulator-html-filter-value"
                      type="text"
                      class="form-control"
                      :placeholder="$t('lang.SEARCH.SR5')"
                      v-model.trim="name"
                  />
                </div>
                <div class="flex search__width">
                  <label class="label__width flex items-center font-medium mr-8 ">{{$t('lang.DETAIL.DT7')}}</label>
                  <select
                      v-model="selectedReason"
                      id="tabulator-html-filter-field"
                      class="form-select input__width"
                  >
                    <option value="">{{$t('lang.DETAIL.DT18')}}</option>
                    <option
                        v-for="(displayName, reasonValue) in RequestReason"
                        :key="reasonValue"
                        :value="reasonValue">{{ $t(displayName) }}
                    </option>
                  </select>
                </div>
                <div class="flex search__width">
                  <label class="label__width flex items-center font-medium mr-6">{{$t('lang.DETAIL.DT1')}}</label>
                  <div v-for="(displayName, statusValue) in RequestStatus"
                       :key="statusValue"
                       :value="statusValue"
                       @click="filterByStatus(statusValue)"
                       :class="{
                            'shadow shadow-gray-400 hover:shadow-black-500/60 cursor-pointer': statusValue === 'WAITING' && selectedStatus !== 'WAITING',
                            'bg-yellow-400 text-white cursor-pointer': statusValue === 'WAITING' && selectedStatus === 'WAITING',
                            'shadow shadow-gray-400 hover:shadow-black-500/70 cursor-pointer': statusValue === 'PROCESSING' && selectedStatus !== 'PROCESSING',
                            'bg-sky-300 text-white cursor-pointer': statusValue === 'PROCESSING' && selectedStatus === 'PROCESSING',
                            'shadow shadow-gray-400 hover:shadow-black-500/40 cursor-pointer': statusValue === 'ORDERING' && selectedStatus !== 'ORDERING',
                            'bg-gray-400 text-white cursor-pointer': statusValue === 'ORDERING' && selectedStatus === 'ORDERING',
                            'shadow shadow-gray-400 hover:shadow-black-500/80 cursor-pointer': statusValue === 'SUCCESS' && selectedStatus !== 'SUCCESS',
                            'bg-green-400 text-white cursor-pointer': statusValue === 'SUCCESS' && selectedStatus === 'SUCCESS',
                            'shadow shadow-gray-400 hover:shadow-black-500/50 cursor-pointer': statusValue === 'REJECTED' && selectedStatus !== 'REJECTED',
                            'bg-red-300 text-white cursor-pointer': statusValue === 'REJECTED' && selectedStatus === 'REJECTED',
                           }"
                       class="flex justify-center items-center h-8 p-2 m-2 rounded-xl">
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
              <div>
                <div class="mt-2 xl:mt-0">
                  <button type="submit" id="tabulator-html-filter-go"
                          class="btn btn-primary w-full sm:w-16">
                    {{$t('lang.SEARCH.SR2')}}
                  </button>
                  <button
                      id="tabulator-html-filter-reset"
                      type="button"
                      class="btn btn-secondary w-full sm:w-16 mt-2 sm:mt-0 sm:ml-1"
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
    <div>
      <div class="scroll-list" :class="{'scroll-view' : theResult.length >6}">
        <table class="table table-report">
          <thead class="text-white sticky top-0 z-20">
          <tr class="bg-teal-600">
            <th class="whitespace-nowrap border border-slate-300 text-center request-id">STT</th>
            <th class="whitespace-nowrap border border-slate-300 request-name"
                @click="sortTable('product_name') ">{{$t('lang.DETAIL.DT33')}}
              <span v-if="sort === 'PRODUCT_NAME' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 request-date-deadline"
                @click="sortTable('expect_receive_date')">{{$t('lang.DETAIL.DT2')}}
              <span v-if="sort === 'EXPECT_RECEIVE_DATE' && sortDirection === 'ASC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 request-reason"
                @click="sortTable('request_reason')">{{$t('lang.DETAIL.DT7')}}
              <span v-if="sort === 'REQUEST_REASON' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 text-center request-status"
                @click="sortTable('status')">{{$t('lang.DETAIL.DT1')}}
              <span v-if="sort === 'STATUS' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 text-center request-user "
                @click="sortTable('user_requested_name')">{{$t('lang.DETAIL.DT19')}}
              <span v-if="sort === 'USER_REQUESTED_NAME' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 text-center request-aprover"
                @click="sortTable('user_assigned_name')">{{$t('lang.DETAIL.DT6')}}
              <span v-if="sort === 'USER_ASSIGNED_NAME' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
            <th class="whitespace-nowrap border border-slate-300 request-date-created"
                @click="sortTable('createAt')">{{$t('lang.DETAIL.DT3')}}
              <span v-if="sort === 'CREATE_AT' && sortDirection === 'DESC'"
                    class="fa fa-caret-up ml-3"></span>
              <span v-else class="fa fa-caret-down ml-3"></span>
            </th>
          </tr>
          </thead>
          <tbody class="font-medium">
          <tr v-for="(item, index) in theResult" :key="index" class="cursor-pointer">
            <td class="text-center" @click="openDetailModal(item)">{{ stt + index }}</td>
            <td @click="openDetailModal(item)">
              <Tippy
                  :content="item?.productName"
                  :key="item?.id"
                  class="tooltip font-medium whitespace-nowrap"
                  tag="div"
              ><span class="truncate">{{ truncateText(item.productName) }}</span>
              </Tippy>
            </td>
            <td class="font-bold"
                 :class="{
                       'text-black-600': item.status !== 'PROCESSING',
                       'font-bold text-red-600': dateResultFormat(item.expectReceiveDate) <= isToday && (item.status === 'WAITING' || item.status === 'PROCESSING' || item.status === 'ORDERING')
                      }"
                 @click="openDetailModal(item)">
              {{ formatDate(item.expectReceiveDate) }}
            </td>
            <td @click="openDetailModal(item)">
              <Tippy
                :content="findReasonText(item.requestReason)"
                :key="`reason-${item.id}`"
                class="tooltip font-medium whitespace-nowrap"
                tag="div"
              >
                <span class="truncate">{{ truncateText(findReasonText(item.requestReason)) }}</span>
              </Tippy>
            </td>
            <td class="text-center" @click="openDetailModal(item)">
              <div
                :class="{
                  'bg-yellow-400': item.status ==='WAITING',
                  'bg-sky-300': item.status ==='PROCESSING',
                  'bg-gray-400': item.status ==='ORDERING',
                  'bg-green-400': item.status ==='SUCCESS',
                  'bg-red-300': item.status ==='REJECTED'
                }"
                class="text-white w-32 p-1.5 rounded-xl mx-auto">
                {{ $t(`${statusDisplayName[item.status]}`) }}
              </div>
            </td>
            <td @click="openDetailModal(item)">
              <div class="text-center">
                <AdminTagForRequest :admin="item.userRequest" class="h-9"/>
              </div>
            </td>
            <td @click="openDetailModal(item)">
              <div class="text-center">
                <AdminTagForRequest :admin="item.userAssign" class="h-9"/>
              </div>
            </td>
            <td @click="openDetailModal(item)">{{ formatDate(item.createdAt) }}</td>
          </tr>
          </tbody>
        </table>
        <div v-if="theResult.length <= 0 "
             class="text-sky-700 flex flex-col items-center justify-center p-24 font-medium text-2xl">
          <img class="w-16 h-16 mb-4" src="/src/assets/images/icons8-empty-box-64.png" alt="">
          <span>{{$t('lang.DETAIL.DT16')}}</span>
        </div>
      </div>
    </div>
    <div class="intro-y col-span-12 flex flex-wrap sm:flex-row sm:flex-nowrap justify-between mt-3"
         v-if="theResult.length > 0">
      <pagination
          :totalPages="totalPages"
          :perPage="perPage"
          :currentPage="currentPage + 1 "
          @pagechanged="onPageChange"
      />
      <select v-model="perPage" class="lg:col-span-4 w-20 form-select box mt-3 sm:mt-0"
              @change="updateStatusFilter">
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="15">15</option>
        <option value="20">20</option>
        <option value="100">100</option>
      </select>
    </div>
  </div>
</template>

<script>
import VueDatePicker from "@vuepic/vue-datepicker";
import '@vuepic/vue-datepicker/dist/main.css'
import AdminTagForRequest from '@/components/avatar-bubble/Main.vue'
import DetailRequest from "@/views/admin/list-requested/DetailRequest.vue";
import CompletePurchase from "@/views/admin/list-requested/CompletePurchase.vue";
import RejectPurchase from "@/views/admin/list-requested/RejectPurchase.vue";
import moment from "moment";
import VTooltip from 'v-tooltip';
import Pagination from "@/components/pagination/pagination.vue";
import {RequestStatus} from '@/common/StatusEnum';
import {RequestReason} from '@/common/RequestReasonEnum';
import {mapGetters} from "vuex";
import ConfirmRequest from "@/views/admin/list-requested/ConfirmRequest.vue";
import CancelRequest from "@/views/admin/list-requested/CancelRequest.vue";
import ConfirmOrder from "@/views/admin/list-requested/ConfirmOrder.vue";
import dayjs from "dayjs";
import Loading from "@/components/loading/Loading.vue";
import ReasonApi from "@/api/ReasonApi.js";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi.js";

export default {
  name: "ListRequests",
  directives: {
    tooltip: VTooltip,
  },
  components: {
    Loading,
    CancelRequest,
    ConfirmRequest,
    Pagination,
    DetailRequest,
    CompletePurchase,
    RejectPurchase,
    VueDatePicker,
    AdminTagForRequest,
    ConfirmOrder
  },
  data() {
    return {
      filterByCreatedDateFrom: "",
      filterByCreatedDateTo: "",
      filterByExpectedDateFrom: "",
      filterByExpectedDateTo: "",
      selectedRequest: null,
      currentPage: 0,
      perPage: 5,
      dataPage: {},
      totalPages: 0,
      totalReviewGroup: 0,
      productName: "",
      status: '',
      filteredData: [],
      userRequestName: '',
      userAssignName: '',
      userAssignedId: '',
      isAdmin: true,
      userRequest: '',
      sort: '',
      sortDirection: '',
      stt: 0,
      selectedReason: '',
      selectedStatus: '',
      name: '',
      reasonList: [],
      originalStatuses: {} // To store original statuses for reverting if needed
    }
  },
  created() {
    this.$store.dispatch("users/fetchListAssigners")
    this.list()
    this.fetchListReason()
  },
  computed: {
    isToday() {
      return dayjs(new Date()).format("YYYY-MM-DD")
    },
    RequestReason() {
      return RequestReason
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
    requestReasonDisplay() {
      return {
        '': 'Tất cả',
        ...RequestReason
      }
    },
    ...mapGetters({
      isCompleteModalOpen: "requestList/getCompleteModalState",
      isRejectModalOpen: "requestList/getRejectModalState",
      listRequests: "requestList/getListRequestAdmin",
      user: 'users/getUserAssign',
      theResult: 'requestList/getResultsDisplay',
      loadingAction: "general/getLoadingStatus"
    })
  },
  watch: {
    "perPage": function () {
      this.dataPage = this.paginate(this.theResult, this.perPage, this.currentPage);
    }
  },
  methods: {
    findReasonText(reasonId) {
      if (!reasonId || !this.reasonList || this.reasonList.length === 0) return '';
      const directReason = this.reasonList.find(reason => reason.id == reasonId);
      if (directReason) {
        return directReason.reason;
      }
      
      for (const parentReason of this.reasonList) {
        if (parentReason.children && Array.isArray(parentReason.children)) {
          const childReason = parentReason.children.find(child => child.id == reasonId);
          if (childReason) {
            return childReason.reason;
          }
        }
      }
      
      return '';
    },
    dateResultFormat(date) {
      return dayjs(new Date(date)).format("YYYY-MM-DD")
    },
    async handleSearch() {
      this.list();
    },
    resetFilters() {
      this.filterByCreatedDateFrom = "";
      this.filterByCreatedDateTo = "";
      this.filterByExpectedDateFrom = "";
      this.filterByExpectedDateTo = "";
      this.selectedReason = "";
      this.selectedStatus = "";
      this.name = "";
      this.list();
    },
    sortTable(column) {
      if (column === 'status') {
        this.sort = 'STATUS';
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'expect_receive_date') {
        this.sort = 'EXPECT_RECEIVE_DATE'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'product_name') {
        this.sort = 'PRODUCT_NAME'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'user_requested_name') {
        this.sort = 'USER_REQUESTED_NAME'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'user_assigned_name') {
        this.sort = 'USER_ASSIGNED_NAME'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'createAt') {
        this.sort = 'CREATE_AT'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      } else if (column === 'request_reason') {
        this.sort = 'REQUEST_REASON'
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
        this.list();
      }
    },
    openDetailModal(item) {
      this.$store.dispatch("requestList/setId", item.id)
      this.$store.dispatch("requestList/fetchDetails")
      this.$store.dispatch("requestList/openDetailModal")
    },
    closeDetailModal() {
      this.isDetailModalOpen = false;
      this.selectedRequest = null
    },
    onPageChange(page) {
      this.currentPage = page - 1;
      this.list(page - 1)
      this.$store.dispatch("requestList/fetchListRequestAdmin")
    },
    paginate(array, page_size, page_number) {
      return array.slice((page_number - 1) * page_size, (page_number) * page_size)
    },
    formatDate(date) {
      if (!date) {
        return '';
      }
      return moment(date).format("DD-MM-YYYY")
    },
    formatDateForSendingRequest(date) {
      if (!date) {
        return '';
      }
      return moment(date).format("YYYY-MM-DD")
    },

    onClickFirstPage() {
      this.$emit('pagechanged', 1);
    },

    async list(pageNumber = 0) {
      this.currentPage = Math.max(pageNumber)

      let params = {
        "size": this.perPage,
        "sort": this.sort,
        "requestReason": this.selectedReason,
        "page": this.currentPage,
        "direction": this.sortDirection,
        "createdAtBegin": this.formatDateForSendingRequest(this.filterByCreatedDateFrom),
        "createdAtEnd": this.formatDateForSendingRequest(this.filterByCreatedDateTo),
        "expectReceiveDateBegin": this.formatDateForSendingRequest(this.filterByExpectedDateFrom),
        "expectReceiveDateEnd": this.formatDateForSendingRequest(this.filterByExpectedDateTo),
        "userAssignedId": this.userAssignedId,
        "name": this.name,
        "statusList": this.selectedStatus,
        "isAdmin": true
      }
      this.$store.dispatch("general/activeLoading")
      this.$store.dispatch("requestList/setParamsForAdmin", params)
      await this.$store.dispatch("requestList/fetchListRequestAdmin")
      this.$store.dispatch("requestList/setResultDisplay", this.listRequests.content)
      const size = this.listRequests.size
      this.stt = size * this.currentPage + 1
      this.totalPages = this.listRequests.totalPages
      this.totalReviewGroup = this.theResult.length
      this.dataPage = this.paginate(this.theResult, this.perPage, 1)
      this.$store.dispatch("general/deactivateLoading")
      
      // Store original statuses
      this.theResult.forEach(item => {
        this.originalStatuses[item.id] = item.status;
      });
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
      if (text?.length > 25) {
        return text?.substring(0, 25) + '...'
      }
      return text
    },
    async fetchListReason(){
      let params = ""
      const res = await ReasonApi.getReasonAssign(params)
      this.reasonList = res.data
    },
    
    // Handle status change from dropdown
    async changeStatus(item) {
      try {
        this.$store.dispatch("general/activeLoading");
        
        if (!item || !item.id) {
          return;
        }
        
        const itemId = item.id;
        const newStatus = item.status;
        const originalStatus = this.originalStatuses[item.id];
        
        // If no change, just return
        if (newStatus === originalStatus) {
          this.$store.dispatch("general/deactivateLoading");
          return;
        }
        
        // Store the current ID for potential modal opening
        this.$store.dispatch("requestList/setId", itemId);
        
        // Call appropriate API based on status change
        let updateAt = new Date().toISOString();
        
        if (newStatus === 'REJECTED') {
          // For rejection, open the rejection modal
          // Reset status back to original
          item.status = originalStatus;
          this.$store.dispatch("requestList/openCancelModal");
          this.$store.dispatch("general/deactivateLoading");
          return;
        } 
        
        if (newStatus === 'SUCCESS') {
          // For purchase completion, open the completion modal
          // Reset status back to original
          item.status = originalStatus;
          this.$store.dispatch("requestList/openCompleteModal");
          this.$store.dispatch("general/deactivateLoading");
          return;
        }
        
        // Direct status updates (PROCESSING, ORDERING)
        let apiCall;
        const updateRequest = {
          id: itemId,
          status: newStatus,
          updateAt: updateAt
        };
        
        if (newStatus === 'PROCESSING') {
          // Approver accepting the request
          apiCall = ApproveAndRejectApi.confirmRequestByAssigner(itemId, updateRequest);
        } else if (newStatus === 'ORDERING') {
          // For buyer order
          apiCall = ApproveAndRejectApi.buyerOrder(itemId, updateAt);
        }
        
        if (apiCall) {
          await apiCall;
          
          // Explicitly update the status in the backend to ensure consistency
          await ApproveAndRejectApi.updateStatus(updateRequest);
          
          // Update the original status
          this.originalStatuses[itemId] = newStatus;
          
          // Force refresh detail view if open
          await this.$store.dispatch("requestList/fetchDetails");
          
          // Refresh the list
          await this.list(this.currentPage);
          
          this.$toast.success(this.$t('lang.TOAST.TS3'));
        }
      } catch (error) {
        console.error('Error updating status:', error);
        this.$toast.error(this.$t('lang.TOAST.TS2'));
        // Reset to original status
        if (item && item.id && this.originalStatuses[item.id]) {
          item.status = this.originalStatuses[item.id];
        }
        // Reload to get fresh data
        await this.list(this.currentPage);
      } finally {
        this.$store.dispatch("general/deactivateLoading");
      }
    }
  }
}
</script>

<style scoped>
.request-id {
  width: 2%;
}

.request-user {
  width: 8%;
}

.request-name {
  width: 15%;

}

.request-status {
  width: 8%;
}

.request-aprover {
  width: 8%;
}

.request-date-created {
  width: 8%;
}

.request-date-deadline {
  width: 8%;
}

.request-detail {
  width: 2%;
}

.request-reason {
  width: 12%;
}

select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 16px;
  padding-right: 28px !important;
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

.overflow-y-scroll {
  overflow-y: auto;
}

.scroll-view {
  height: 400px;
  overflow-y: scroll;
}

.input__width {
  width: 68.5%;
}

.search__width {
  width: 725px;
}

.label__width {
  width: 100px;
}

.form-control {
  width: 68.5%;
}
</style>
<style>
.content {
  min-height: 700px !important;
}
</style>