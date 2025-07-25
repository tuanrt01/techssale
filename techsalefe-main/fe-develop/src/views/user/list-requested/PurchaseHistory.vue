<template>
  <Loading :show-loading="loadingAction" />
  <DetailMyRequest v-if="!standalone" :id="id" :isOpen="isDetailModalOpen" @close="closeDetailModal"/>
  <PurchaseDetailModal v-if="standalone" :show="showDetailModal" :data="detailData" @update:show="showDetailModal = $event" />
  <div class="mt-1">
    <div class="filter-wrapper">
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
                      :placeholder="$t('lang.SEARCH.SR4')"
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
                  <label class="w-20 flex items-center font-medium">{{$t('lang.DETAIL.DT2')}}</label>
                  <div class="flex gap-5">
                    <VueDatePicker v-model="filterByExpectDateFrom" auto-apply
                                   class="deadline w-40 text-center form-control"
                                   format="dd/MM/yyyy"
                                   :enable-time-picker="false"
                                   :teleport="true"
                                   :preview-format="'dd/MM/yyyy'"
                                   :month-name-format="'long'"
                                   :year-range="[2020, 2030]"
                                   locale="vi"
                                   position="bottom"
                                   text-input
                                   aria-label="Ngày nhận (từ)"
                                   placeholder="Từ ngày..."
                                   @update:model-value="handleChangeDateRangeByExpectDateFrom"
                    >
                      <template #trigger>
                        <div class="dp__custom_trigger">
                          <span>{{ filterByExpectDateFrom ? formatDate(filterByExpectDateFrom) : 'Từ ngày...' }}</span>
                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ml-2 text-gray-500">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                          </svg>
                        </div>
                      </template>
                    </VueDatePicker>
                    <div class="font-medium text-lg flex items-center">~</div>
                    <VueDatePicker v-model="filterByExpectDateTo" auto-apply
                                   class="deadline w-40 form-control text-center"
                                   format="dd/MM/yyyy"
                                   :enable-time-picker="false"
                                   :teleport="true"
                                   :preview-format="'dd/MM/yyyy'"
                                   :month-name-format="'long'"
                                   :year-range="[2020, 2030]"
                                   locale="vi"
                                   position="bottom"
                                   text-input
                                   aria-label="Ngày nhận (đến)"
                                   placeholder="Đến ngày..."
                                   @update:model-value="handleChangeDateRangeByExpectDateTo"
                    >
                      <template #trigger>
                        <div class="dp__custom_trigger">
                          <span>{{ filterByExpectDateTo ? formatDate(filterByExpectDateTo) : 'Đến ngày...' }}</span>
                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ml-2 text-gray-500">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                          </svg>
                        </div>
                      </template>
                    </VueDatePicker>
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
                        :enable-time-picker="false"
                        :teleport="true"
                        :preview-format="'dd/MM/yyyy'"
                        :month-name-format="'long'"
                        :year-range="[2020, 2030]"
                        locale="vi"
                        position="bottom"
                        text-input
                        aria-label="Ngày tạo (từ)"
                        placeholder="Từ ngày..."
                        @update:model-value="handleChangeDateRangeByCreatedDateFrom"
                    >
                      <template #trigger>
                        <div class="dp__custom_trigger">
                          <span>{{ filterByCreatedDateFrom ? formatDate(filterByCreatedDateFrom) : 'Từ ngày...' }}</span>
                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ml-2 text-gray-500">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                          </svg>
                        </div>
                      </template>
                    </VueDatePicker>
                    <div class="font-medium text-lg flex items-center">~</div>
                    <VueDatePicker
                        v-model="filterByCreatedDateTo"
                        auto-apply
                        class="w-40 text-center form-control"
                        format="dd/MM/yyyy"
                        :enable-time-picker="false"
                        :teleport="true"
                        :preview-format="'dd/MM/yyyy'"
                        :month-name-format="'long'"
                        :year-range="[2020, 2030]"
                        locale="vi"
                        position="bottom"
                        text-input
                        aria-label="Ngày tạo (đến)"
                        placeholder="Đến ngày..."
                        @update:model-value="handleChangeDateRangeByCreatedDateTo"
                    >
                      <template #trigger>
                        <div class="dp__custom_trigger">
                          <span>{{ filterByCreatedDateTo ? formatDate(filterByCreatedDateTo) : 'Đến ngày...' }}</span>
                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ml-2 text-gray-500">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                          </svg>
                        </div>
                      </template>
                    </VueDatePicker>
                  </div>
                </div>
              </div>
              <div>
                <div class="flex items-center gap-10 mb-2">
                  <label class="w-20 flex items-center font-medium">{{$t('lang.DETAIL.DT27')}}</label>
                  <div class="flex gap-5">
                    <input
                        type="number"
                        v-model="priceFrom"
                        class="form-control w-40 text-center"
                        placeholder="Từ"
                    />
                    <div class="font-medium text-lg flex items-center">~</div>
                    <input
                        type="number"
                        v-model="priceTo"
                        class="form-control w-40 text-center"
                        placeholder="Đến"
                    />
                  </div>
                </div>
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
    
    <!-- Purchase History Table -->
    <div class="mt-5">
      <div class="intro-y col-span-12 overflow-auto lg:overflow-visible">
        <table class="table table-report -mt-2">
          <thead>
            <tr>
              <th class="whitespace-nowrap">ID</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DT5')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DT1')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DT3')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DT4')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.ORDERING_DATE')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DELIVERY_DATE')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DT27')}}</th>
              <th class="whitespace-nowrap">{{$t('lang.DETAIL.DELIVERY_TIME')}}</th>
              <th class="text-center whitespace-nowrap">{{$t('lang.BUTTON.BT10')}}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in purchaseHistory" :key="index" class="intro-x">
              <td class="w-20">{{ item.id }}</td>
              <td class="w-64">
                <Tippy
                  :content="item?.productName"
                  :key="item?.id"
                  class="tooltip font-medium whitespace-nowrap"
                  tag="div"
                >
                  <span class="truncate">{{ truncateText(item.productName || '-', 30) }}</span>
                </Tippy>
              </td>
              <td>
                <div class="flex items-center">
                  <div class="text-center text-white py-1 px-2 rounded-xl mr-2" 
                      :class="{
                        'bg-yellow-400': item.status === 'WAITING',
                        'bg-sky-300': item.status === 'PROCESSING',
                        'bg-green-400': item.status === 'ORDERING',
                        'bg-green-300': item.status === 'SUCCESS',
                        'bg-red-300': item.status === 'REJECTED'
                      }">
                    {{ $t(statusDisplayName[item.status]) }}
                  </div>
                  <!-- Add status update dropdown -->
                  <select 
                    v-if="userInfo && userInfo.privileges && (userInfo.privileges.includes('approve') || userInfo.privileges.includes('buy'))"
                    v-model="item.status"
                    class="form-select w-32 ml-2"
                    @change="updateOrderStatus(item)"
                  >
                    <option v-for="(displayName, status) in availableStatusTransitions[item.status]" 
                            :key="status" 
                            :value="status">
                      {{ $t(statusDisplayName[status]) }}
                    </option>
                  </select>
                </div>
              </td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td>{{ formatDate(item.expectReceiveDate) }}</td>
              <td>{{ formatDate(item.orderingDate) }}</td>
              <td>{{ formatDate(item.deliveryDate) }}</td>
              <td>{{ formatPrice(item.status === 'SUCCESS' ? item.boughtPrice : item.estimatePrice) }}</td>
              <td>{{ calculateDeliveryTime(item) }}</td>
              <td class="table-report__action w-32">
                <div class="flex justify-center items-center">
                  <a class="flex items-center text-primary hover:underline mr-3" href="javascript:;" @click="viewDetails(item)">
                    <EyeIcon class="w-4 h-4 mr-1" /> {{$t('lang.BUTTON.BT10')}}
                  </a>
                </div>
              </td>
            </tr>
            <tr v-if="purchaseHistory.length === 0">
              <td colspan="7" class="text-center py-5">
                <div class="text-sky-700 flex flex-col items-center justify-center font-medium text-xl">
                  <img alt="" class="w-16 h-16 mb-4" src="/src/assets/images/icons8-empty-box-64.png"/>
                  <span>{{$t('lang.DETAIL.DT16')}}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <!-- Pagination -->
      <div class="intro-y flex flex-wrap sm:flex-row sm:flex-nowrap items-center mt-3 justify-between">
        <div class="flex items-center">
          <select v-model="pageSize" class="form-select w-20 mr-3" @change="handlePageSizeChange">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
          </select>
          <span class="text-gray-600">items per page</span>
        </div>
        <pagination 
          v-if="purchaseHistory.length > 0"
          :page-count="totalPages" 
          :click-handler="changePage" 
          :prev-text="'Prev'" 
          :next-text="'Next'"
          :container-class="'pagination justify-center'" 
          :page-class="'page-item'" 
          :page-link-class="'page-link'" 
          :prev-class="'page-item'" 
          :prev-link-class="'page-link'" 
          :next-class="'page-item'" 
          :next-link-class="'page-link'"
          :active-class="'active'">
        </pagination>
      </div>
    </div>
  </div>
</template>

<script>
import VueDatePicker from "@vuepic/vue-datepicker";
import '@vuepic/vue-datepicker/dist/main.css'
import moment from "moment";
import VTooltip from 'v-tooltip';
import { RequestStatus } from "@/common/StatusEnum";
import { mapGetters } from "vuex";
import Loading from "@/components/loading/Loading.vue";
import ReasonApi from "@/api/ReasonApi.js";
import DetailMyRequest from "@/views/user/list-requested/DetailMyRequest.vue";
import PurchaseDetailModal from "@/views/user/list-requested/PurchaseDetailModal.vue";
import Pagination from "@/components/pagination/pagination.vue";
import PublicRequestApi from "@/api/PublicRequestApi";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi.js";

export default {
  name: "PurchaseHistory",
  components: {
    Loading,
    VueDatePicker,
    DetailMyRequest,
    PurchaseDetailModal,
    Pagination
  },
  directives: {
    tooltip: VTooltip,
  },
  props: {
    standalone: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      id: "",
      filterByCreatedDateFrom: "",
      filterByCreatedDateTo: "",
      filterByExpectDateFrom: "",
      filterByExpectDateTo: "",
      name: "",
      selectedStatus: '',
      priceFrom: '',
      priceTo: '',
      reasonList: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 0,
      totalItems: 0,
      localPurchaseHistory: [], // Đổi tên thành localPurchaseHistory
      showDetailModal: false,
      detailData: {},
      availableStatusTransitions: {
        'WAITING': {
          'PROCESSING': 'lang.STATUS.STT2',
          'REJECTED': 'lang.STATUS.STT4'
        },
        'PROCESSING': {
          'ORDERING': 'lang.STATUS.STT5',
          'REJECTED': 'lang.STATUS.STT4'
        },
        'ORDERING': {
          'SUCCESS': 'lang.STATUS.STT3',
          'REJECTED': 'lang.STATUS.STT4'
        },
        'SUCCESS': {},
        'REJECTED': {}
      },
      debugMode: false, // Add debug mode toggle
    }
  },
  created() {
    this.fetchPurchaseHistory();
    this.fetchListReason();
    
    // Cấu hình mặc định cho date picker
    this.configureDatePicker();
  },
  computed: {
    RequestStatus() {
      return RequestStatus
    },
    statusDisplayName() {
      return {
        '': 'Tất cả',
        ...RequestStatus
      }
    },
    ...mapGetters({
      storeHistory: "requestList/getPurchaseHistory",
      purchaseHistoryPagination: "requestList/getPurchaseHistoryPagination",
      isDetailModalOpen: "requestList/getMyDetailModalState",
      loadingAction: "general/getLoadingStatus",
      userInfo: "user/getUserInfo"
    }),
    // Sử dụng dữ liệu từ store hoặc từ component tùy theo chế độ
    purchaseHistory() {
      const history = this.standalone ? this.localPurchaseHistory : this.storeHistory;
      // Log để debug
      console.log('Purchase History:', history);
      return history;
    }
  },
  watch: {
    purchaseHistoryPagination: {
      handler(newVal) {
        if (newVal) {
          this.totalPages = newVal.totalPages || 0;
          this.totalItems = newVal.totalElements || 0;
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    configureDatePicker() {
      // Đặt cấu hình toàn cục cho VueDatePicker nếu cần
      const style = document.createElement('style');
      style.innerHTML = `
        .dp__main {
          z-index: 50 !important;
        }
        .dp__overlay {
          z-index: 50 !important;
        }
        .dp__outer_menu {
          position: absolute !important;
          z-index: 50 !important;
        }
      `;
      document.head.appendChild(style);
    },
    changePage(pageNum) {
      this.currentPage = pageNum - 1;
      this.fetchPurchaseHistory();
    },
    closeDetailModal() {
      this.$store.dispatch("requestList/closeMyDetailModal");
    },
    formatCurrency(value) {
      if (!value) return '-';
      const formatter = new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(value || 0);
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    async fetchListReason(){
      let params = "";
      const res = await ReasonApi.getReasonAssign(params);
      this.reasonList = res.data;
    },
    async handleSearch() {
      this.currentPage = 0;
      this.fetchPurchaseHistory();
    },
    resetFilters() {
      this.filterByCreatedDateFrom = "";
      this.filterByCreatedDateTo = "";
      this.filterByExpectDateFrom = "";
      this.filterByExpectDateTo = "";
      this.selectedStatus = "";
      this.name = "";
      this.priceFrom = "";
      this.priceTo = "";
      this.currentPage = 0;
      this.fetchPurchaseHistory();
    },
    async viewDetails(item) {
      this.id = item.id;
      
      if (this.standalone) {
        try {
          const response = await PublicRequestApi.getPurchaseHistoryDetail(item.id);
          if (response && response.data) {
            this.detailData = response.data;
            this.showDetailModal = true;
          }
        } catch (error) {
          console.error("Error fetching purchase details:", error);
        }
      } else {
        // Sử dụng store khi chạy trong ứng dụng chính
        this.$store.dispatch("requestList/setId", item.id);
        this.$store.dispatch("requestList/fetchDetails");
        this.$store.dispatch("requestList/openMyDetailModal");
      }
    },
    formatDate(date) {
      if (!date) {
        return '-';
      }
      return moment(date).format("DD-MM-YYYY");
    },
    formatDateForSendingRequest(date) {
      if (!date) {
        return '';
      }
      return moment(date).format("YYYY-MM-DD");
    },
    async fetchPurchaseHistory() {
      let params = {
        "sort": '',
        "direction": '',
        "requestReason": "",
        "createdAtBegin": this.formatDateForSendingRequest(this.filterByCreatedDateFrom),
        "createdAtEnd": this.formatDateForSendingRequest(this.filterByCreatedDateTo),
        "expectReceiveDateBegin": this.formatDateForSendingRequest(this.filterByExpectDateFrom),
        "expectReceiveDateEnd": this.formatDateForSendingRequest(this.filterByExpectDateTo),
        "statusList": this.selectedStatus,
        "isAdmin": false,
        "name": this.name,
        "priceFrom": this.priceFrom || '',
        "priceTo": this.priceTo || '',
        "page": this.currentPage,
        "size": parseInt(this.pageSize)
      };
      
      this.$store.dispatch("general/activeLoading");
      
      try {
        let response;
        
        if (this.standalone) {
          response = await PublicRequestApi.getPurchaseHistory(params);
          // Log response để debug
          console.log('API Response:', response);
          if (response && response.data) {
            this.localPurchaseHistory = response.data.content || [];
            this.totalPages = response.data.totalPages || 0;
            this.totalItems = response.data.totalElements || 0;
            
            // Log dữ liệu để debug
            console.log('Purchase History Data:', this.localPurchaseHistory);
          }
        } else {
          this.$store.dispatch("requestList/setPurchaseHistoryParams", params);
          await this.$store.dispatch("requestList/fetchPurchaseHistory");
          
          // Log dữ liệu để debug
          console.log('Purchase History from Store:', this.storeHistory);
        }
      } catch (error) {
        console.error("Error fetching purchase history:", error);
        if (this.standalone) {
          this.localPurchaseHistory = [];
          this.totalPages = 0;
          this.totalItems = 0;
        }
      } finally {
        this.$store.dispatch("general/deactivateLoading");
      }
    },
    handleChangeDateRangeByCreatedDateFrom(newDate) {
      this.filterByCreatedDateFrom = newDate;
    },
    handleChangeDateRangeByCreatedDateTo(newDate) {
      this.filterByCreatedDateTo = newDate;
    },
    handleChangeDateRangeByExpectDateFrom(newDate) {
      this.filterByExpectDateFrom = newDate;
    },
    handleChangeDateRangeByExpectDateTo(newDate) {
      this.filterByExpectDateTo = newDate;
    },
    filterByStatus(statusValue) {
      if (this.selectedStatus === statusValue) {
        this.selectedStatus = '';
      } else {
        this.selectedStatus = statusValue;
      }
      // Automatically fetch new data when status changes
      this.handleSearch();
    },
    truncateText(text, length) {
      if (!text) return '-';
      if (text.length > length) {
        return text.substring(0, length) + '...';
      }
      return text;
    },
    async updateOrderStatus(item) {
      try {
        this.$store.dispatch("general/activeLoading");
        
        const itemId = item.id;
        const newStatus = item.status;
        const updateAt = new Date().toISOString();
        
        // Store the current ID for potential modal opening
        this.$store.dispatch("requestList/setId", itemId);
        
        if (newStatus === 'REJECTED') {
          // For rejection, open the rejection modal
          this.$store.dispatch("requestList/openCancelModal");
          return;
        }
        
        if (newStatus === 'SUCCESS') {
          // For completion, open the completion modal
          this.$store.dispatch("requestList/openCompleteModal");
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
          apiCall = ApproveAndRejectApi.confirmRequestByAssigner(itemId, updateRequest);
        } else if (newStatus === 'ORDERING') {
          apiCall = ApproveAndRejectApi.buyerOrder(itemId, updateAt);
        }
        
        if (apiCall) {
          await apiCall;
          await ApproveAndRejectApi.updateStatus(updateRequest);
          await this.fetchPurchaseHistory();
          this.$toast.success(this.$t('lang.TOAST.TS3'));
        }
      } catch (error) {
        console.error('Error updating status:', error);
        this.$toast.error(this.$t('lang.TOAST.TS2'));
        await this.fetchPurchaseHistory();
      } finally {
        this.$store.dispatch("general/deactivateLoading");
      }
    },
    handlePageSizeChange() {
      this.currentPage = 0; // Reset to first page when changing page size
      this.fetchPurchaseHistory();
    },
    formatPrice(price) {
      // Log để debug
      console.log('Formatting price:', price);
      
      // Kiểm tra giá trị null/undefined
      if (price === null || price === undefined) {
        return '-';
      }

      // Chuyển đổi sang số
      const numericPrice = Number(price);
      
      // Kiểm tra giá trị không hợp lệ
      if (isNaN(numericPrice)) {
        console.error('Invalid price value:', price);
        return '-';
      }

      try {
        const formatter = new Intl.NumberFormat("vi-VN", {
          style: "currency",
          currency: "VND",
          minimumFractionDigits: 0,
          maximumFractionDigits: 0
        });
        return formatter.format(numericPrice);
      } catch (error) {
        console.error('Error formatting price:', error);
        return '-';
      }
    },
    // Thêm hàm tính thời gian giao hàng
    calculateDeliveryTime(item) {
      // Chỉ tính thời gian cho các đơn đã hoàn thành (SUCCESS) hoặc đang giao (ORDERING)
      try {
        // Ưu tiên sử dụng các trường mới orderingDate và deliveryDate
        if (item.status === 'SUCCESS' && item.orderingDate && item.deliveryDate) {
          const orderDateTime = new Date(item.orderingDate);
          const deliveryDateTime = new Date(item.deliveryDate);
          
          if (this.debugMode) {
            console.log(`Item ${item.id} - Using specific dates - orderingDate: ${item.orderingDate}, deliveryDate: ${item.deliveryDate}`);
          }
          
          // Tính số ngày chênh lệch
          const diffTime = Math.abs(deliveryDateTime - orderDateTime);
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          
          if (diffDays === 0) {
            return this.$t('lang.DETAIL.SAME_DAY');
          } else {
            return `${diffDays} ${this.$t('lang.DETAIL.DAYS')}`;
          }
        } 
        // Nếu đơn đang trong quá trình đặt hàng
        else if (item.status === 'ORDERING' && item.orderingDate) {
          const orderDateTime = new Date(item.orderingDate);
          const today = new Date();
          
          if (this.debugMode) {
            console.log(`Item ${item.id} - Order in progress - orderingDate: ${item.orderingDate}`);
          }
          
          // Tính số ngày đã trôi qua kể từ khi đặt hàng
          const diffTime = Math.abs(today - orderDateTime);
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          
          if (diffDays === 0) {
            return this.$t('lang.DETAIL.TODAY');
          } else {
            return `${diffDays} ${this.$t('lang.DETAIL.DAYS_ONGOING')}`;
          }
        }
        
        // Fallback: Sử dụng thời gian tạo và cập nhật nếu không có thông tin cụ thể
        if (item && item.createdAt && item.updatedAt) {
          const createdDate = new Date(item.createdAt);
          const updatedDate = new Date(item.updatedAt);
          
          if (this.debugMode) {
            console.log(`Item ${item.id} - Fallback - createdAt: ${item.createdAt}, updatedAt: ${item.updatedAt}`);
          }
          
          if (createdDate && updatedDate) {
            const diffTime = Math.abs(updatedDate - createdDate);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            
            if (diffDays === 0) {
              return this.$t('lang.DETAIL.SAME_DAY');
            } else {
              return `${diffDays} ${this.$t('lang.DETAIL.DAYS')}`;
            }
          }
        }
      } catch (error) {
        console.error(`Error calculating delivery time for item ${item.id}:`, error);
      }
      
      return '-';
    }
  }
}
</script>

<style scoped>
.search__width {
  width: 725px;
}

.label__width {
  width: 100px;
}

.input-width {
  width: 68.5%;
}

.flex.justify-end {
  margin-top: 10px;
}

/* Nâng cấp style cho date range container */
.flex.gap-5 {
  position: relative;
}

/* Làm đẹp input date picker */
.form-control.deadline,
.form-control.w-40 {
  height: 38px;
  display: flex;
  align-items: center;
  background-color: white;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.form-control.deadline:hover,
.form-control.w-40:hover {
  border-color: #cbd5e1;
}

.form-control.deadline:focus,
.form-control.w-40:focus {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.25);
}

/* Style cho ký hiệu ~ giữa 2 input */
.font-medium.text-lg.flex.items-center {
  color: #64748b;
  font-weight: 600;
  margin: 0 8px;
}

/* Đảm bảo thẻ calendar đè lên các thành phần khác */
.dp__instance_calendar {
  z-index: 9999 !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15) !important;
  padding: 8px !important;
  margin-top: 5px !important;
  background: white !important;
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
}

/* Header "Jul 2023" */
.dp__month_year_wrap {
  background-color: #f8fafc !important;
  border-radius: 8px !important;
  margin-bottom: 8px !important;
  padding: 8px 4px !important;
}

/* Nút Tìm và Xoá */
.dp__action_buttons {
  margin-top: 10px !important;
  display: flex !important;
  justify-content: flex-end !important;
  gap: 8px !important;
}

/* Nút Tìm */
.btn.btn-primary {
  background-color: #f97316 !important;
  border-color: #f97316 !important;
  color: white !important;
  font-weight: 500 !important;
  padding: 0.5rem 1rem !important;
  border-radius: 0.375rem !important;
  transition: all 0.2s !important;
}

.btn.btn-primary:hover {
  background-color: #ea580c !important;
  border-color: #ea580c !important;
}

/* Nút Xoá */
.btn.btn-secondary {
  background-color: #e2e8f0 !important;
  border-color: #e2e8f0 !important;
  color: #334155 !important;
  font-weight: 500 !important;
  padding: 0.5rem 1rem !important;
  border-radius: 0.375rem !important;
  transition: all 0.2s !important;
}

.btn.btn-secondary:hover {
  background-color: #cbd5e1 !important;
  border-color: #cbd5e1 !important;
}

/* Custom trigger styles */
.dp__custom_trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding: 0 12px;
  cursor: pointer;
  background-color: white;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  color: #334155;
}

.dp__custom_trigger svg {
  flex-shrink: 0;
}

/* Style for the input container */
.deadline,
.w-40 {
  padding: 0 !important;
  overflow: hidden;
  transition: all 0.2s ease;
  width: 170px !important;
}

.deadline:hover,
.w-40:hover {
  border-color: #cbd5e1;
}

.deadline:focus-within,
.w-40:focus-within {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.25);
}

/* Month name styling */
.dp__month_year_select {
  font-weight: 600 !important;
  color: #1e293b !important;
}

/* Highlight selected date in orange */
.dp__today {
  border: 1px solid #f97316 !important;
}

.dp__active_date {
  background-color: #f97316 !important;
  color: white !important;
}

/* Better year/month row buttons */
.dp__month_year_row {
  padding: 8px !important;
  border-radius: 8px !important;
  background-color: #f8fafc !important;
  margin-bottom: 10px !important;
}

/* Button styling */
.dp__selection_preview {
  padding: 10px !important;
  background-color: #f8fafc !important;
  border-radius: 6px !important;
  margin-bottom: 8px !important;
  font-weight: 600 !important;
}

.form-select {
  padding: 0.375rem 1.75rem 0.375rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  border-radius: 0.375rem;
  border: 1px solid #e2e8f0;
  background-color: #fff;
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.25);
}

.pagination {
  display: flex;
  gap: 0.5rem;
}

.page-item {
  display: inline-flex;
}

.page-link {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  color: #4a5568;
  background-color: #fff;
  cursor: pointer;
}

.page-link:hover {
  background-color: #f7fafc;
}

.active .page-link {
  background-color: #f97316;
  border-color: #f97316;
  color: white;
}
</style>

<style>
.content {
  min-height: 700px !important;
  padding-bottom: 0;
}

/* Table styles */
.table th {
  padding: 0.75rem;
}

.table td {
  padding: 0.75rem;
  vertical-align: middle;
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

/* Pagination styling */
.pagination {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.page-item {
  margin: 0 2px;
}

.page-link {
  padding: 6px 12px;
  border: 1px solid #dee2e6;
  color: #4b5563;
  border-radius: 0.25rem;
  cursor: pointer;
}

.page-item.active .page-link {
  background-color: #f97316;
  color: white;
  border-color: #f97316;
}
</style> 