<template>
  <Loading :show-loading="loadingAction" />
  <DetailMyRequest v-if="!standalone" :id="id" :isOpen="isDetailModalOpen" @close="closeDetailModal"/>
  <PurchaseDetailModal v-if="standalone" :show="showDetailModal" :data="detailData" @update:show="showDetailModal = $event" />
  <div class="mt-1 overflow-hidden">
    <div class="filter-wrapper">
      <div class="intro-y box p-5 mt-3">
        <div class="flex flex-col">
          <form id="tabulator-html-filter-form" @submit.prevent="handleSearch" @keyup.enter="handleSearch">
            <div class="grid grid-cols-1 xl:grid-cols-3 gap-8">
              <div class="space-y-4">
                <div class="flex items-center">
                  <label class="w-24 flex items-center font-medium mr-3">{{$t('lang.SEARCH.SR1')}}</label>
                  <input
                      id="tabulator-html-filter-value"
                      v-model.trim="name"
                      class="form-control flex-1"
                      :placeholder="$t('lang.SEARCH.SR4')"
                      type="text"
                  />
                </div>
                <div class="flex items-center">
                  <label class="w-24 flex items-center font-medium mr-3">{{$t('lang.DETAIL.DT1')}}</label>
                  <div class="flex gap-2">
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
                         class="flex h-8 justify-center items-center px-3 py-1 rounded-xl text-xs whitespace-nowrap"
                         @click="filterByStatus(statusValue)">
                      {{ $t(displayName) }}
                    </div>
                  </div>
                </div>
              </div>
              <div class="space-y-4">
                <div class="flex items-center">
                  <label class="w-24 flex items-center font-medium mr-3">{{$t('lang.DETAIL.DT2')}}</label>
                  <div class="flex gap-3">
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
                                   :max-date="maxExpectDate"
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
                      <template #day-content="{ day, isDisabled }">
                        <div 
                          :class="{ 'dp__disabled': isDisabled }"
                          class="dp__day_content"
                          @mouseenter="isDisabled && showTooltip($event, $t('lang.TOOLTIP.DATE_FUTURE_INVALID'))"
                          @mouseleave="isDisabled && hideTooltip()"
                        >
                          {{ day.day }}
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
                                   :max-date="maxExpectDate"
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
                      <template #day-content="{ day, isDisabled }">
                        <div 
                          :class="{ 'dp__disabled': isDisabled }"
                          class="dp__day_content"
                          @mouseenter="isDisabled && showTooltip($event, $t('lang.TOOLTIP.DATE_FUTURE_INVALID'))"
                          @mouseleave="isDisabled && hideTooltip()"
                        >
                          {{ day.day }}
                        </div>
                      </template>
                    </VueDatePicker>
                  </div>
                </div>
                <div class="flex items-center">
                  <label class="w-24 flex items-center font-medium mr-3">{{$t('lang.DETAIL.DT3')}}</label>
                  <div class="flex gap-3">
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
                        :max-date="maxCreatedDate"
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
                      <template #day-content="{ day, isDisabled }">
                        <div 
                          :class="{ 'dp__disabled': isDisabled }"
                          class="dp__day_content"
                          @mouseenter="isDisabled && showTooltip($event, $t('lang.TOOLTIP.DATE_FUTURE_INVALID'))"
                          @mouseleave="isDisabled && hideTooltip()"
                        >
                          {{ day.day }}
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
                        :max-date="maxCreatedDate"
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
                      <template #day-content="{ day, isDisabled }">
                        <div 
                          :class="{ 'dp__disabled': isDisabled }"
                          class="dp__day_content"
                          @mouseenter="isDisabled && showTooltip($event, $t('lang.TOOLTIP.DATE_FUTURE_INVALID'))"
                          @mouseleave="isDisabled && hideTooltip()"
                        >
                          {{ day.day }}
                        </div>
                      </template>
                    </VueDatePicker>
                  </div>
                </div>
              </div>
              <div class="space-y-4">
                <div class="flex items-center">
                  <label class="w-24 flex items-center font-medium mr-3">{{$t('lang.DETAIL.DT27')}}</label>
                  <div class="flex gap-3">
                    <div class="flex flex-col">
                      <input
                          type="number"
                          v-model="priceFrom"
                          class="form-control w-36 text-center"
                          :class="{ 'border-red-500': priceFromError }"
                          placeholder="Từ"
                          @input="validatePriceFrom"
                          @blur="validatePriceFrom"
                      />
                      <span v-if="priceFromError" class="text-red-500 text-xs mt-1">{{ priceFromError }}</span>
                    </div>
                    <div class="font-medium text-lg flex items-center">~</div>
                    <div class="flex flex-col">
                      <input
                          type="number"
                          v-model="priceTo"
                          class="form-control w-36 text-center"
                          :class="{ 'border-red-500': priceToError }"
                          placeholder="Đến"
                          @input="validatePriceTo"
                          @blur="validatePriceTo"
                      />
                      <span v-if="priceToError" class="text-red-500 text-xs mt-1">{{ priceToError }}</span>
                    </div>
                  </div>
                </div>
                <div class="flex gap-3">
                  <button id="tabulator-html-filter-go" class="btn btn-primary flex-1"
                          type="button" @click="handleSearch">
                    {{$t('lang.SEARCH.SR2')}}
                  </button>
                  <button
                      id="tabulator-html-filter-reset"
                      class="btn btn-secondary flex-1"
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
            <!-- Loading skeleton -->
            <template v-if="tableLoading">
              <tr v-for="n in 5" :key="`skeleton-${n}`" class="intro-x animate-pulse">
                <td class="w-20">
                  <div class="h-4 bg-gray-200 rounded w-12"></div>
                </td>
                <td class="w-64">
                  <div class="h-4 bg-gray-200 rounded w-32"></div>
                </td>
                <td>
                  <div class="h-6 bg-gray-200 rounded w-20"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-20"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-20"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-20"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-20"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-16"></div>
                </td>
                <td>
                  <div class="h-4 bg-gray-200 rounded w-12"></div>
                </td>
                <td class="text-center">
                  <div class="h-4 bg-gray-200 rounded w-8 mx-auto"></div>
                </td>
              </tr>
            </template>
            <!-- Actual data -->
            <template v-else>
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
            </template>
          </tbody>
        </table>
      </div>
      <!-- Pagination -->
      <div class="intro-y flex flex-wrap sm:flex-row sm:flex-nowrap items-center mt-5 justify-between bg-slate-50 p-4 rounded-lg">
        <div class="flex items-center">
          <select v-model="pageSize" class="form-select w-20 mr-3 rounded-md border-slate-300" @change="handlePageSizeChange">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
          </select>
          <span class="text-gray-600">{{ $t('lang.PAGINATION.items_per_page') }}</span>
        </div>
        <div class="flex items-center">
          <span class="mr-4 text-gray-600">{{ showingItemsText }}</span>
          <pagination 
            v-if="totalPages > 0"
            :total-pages="totalPages"
            :current-page="currentPage + 1"
            :per-page="pageSize"
            @pagechanged="changePage"
          />
        </div>
      </div>
    </div>
    
    <!-- Footer -->
    <footer class="mt-8 bg-gradient-to-r from-orange-500 to-orange-600 text-white w-full ">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="py-8">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
            <!-- Company Info -->
            <div class="col-span-1 md:col-span-2">
              <div class="flex items-center mb-4">
                                <div class="w-16 h-16 bg-white rounded-lg flex items-center justify-center mr-3 shadow-sm overflow-hidden">
                  <img 
                    src="@/assets/images/logotech.jpg" 
                    alt="TECHSALE Logo" 
                    class="w-full h-full object-cover"
                    @error="handleLogoError"
                  />
                </div>
                <div>
                  <h3 class="text-xl font-bold">TECHSALE</h3>
                  <p class="text-orange-100 text-sm">Mạch lạc quy trình, định hình đẳng cấp</p>
                </div>
              </div>
              <p class="text-orange-100 text-sm leading-relaxed mb-4">
                Hệ thống quản lý chuỗi cung ứng thông minh, giúp doanh nghiệp tối ưu hóa quy trình mua sắm và quản lý hàng tồn kho một cách hiệu quả.
              </p>
              <div class="flex space-x-4">
                <a href="#" class="text-orange-100 hover:text-white transition-colors">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/>
                  </svg>
                </a>
                <a href="#" class="text-orange-100 hover:text-white transition-colors">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M22.46 6c-.77.35-1.6.58-2.46.69.88-.53 1.56-1.37 1.88-2.38-.83.5-1.75.85-2.72 1.05C18.37 4.5 17.26 4 16 4c-2.35 0-4.27 1.92-4.27 4.29 0 .34.04.67.11.98C8.28 9.09 5.11 7.38 3 4.79c-.37.63-.58 1.37-.58 2.15 0 1.49.75 2.81 1.91 3.56-.71 0-1.37-.2-1.95-.5v.03c0 2.08 1.48 3.82 3.44 4.21a4.22 4.22 0 0 1-1.93.07 4.28 4.28 0 0 0 4 2.98 8.521 8.521 0 0 1-5.33 1.84c-.34 0-.68-.02-1.02-.06C3.44 20.29 5.7 21 8.12 21 16 21 20.33 14.46 20.33 8.79c0-.19 0-.37-.01-.56.84-.6 1.56-1.36 2.14-2.23z"/>
                  </svg>
                </a>
                <a href="#" class="text-orange-100 hover:text-white transition-colors">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                  </svg>
                </a>
              </div>
            </div>
            
            <!-- Quick Links -->
            <div>
              <h4 class="text-lg font-semibold mb-4">Liên kết nhanh</h4>
              <ul class="space-y-2">
                <li>
                  <a href="#" class="text-orange-100 hover:text-white transition-colors text-sm">
                    Trang chủ
                  </a>
                </li>
                <li>
                  <a href="#" class="text-orange-100 hover:text-white transition-colors text-sm">
                    Danh sách yêu cầu
                  </a>
                </li>
                <li>
                  <a href="#" class="text-orange-100 hover:text-white transition-colors text-sm">
                    Lịch sử mua hàng
                  </a>
                </li>
                <li>
                  <a href="#" class="text-orange-100 hover:text-white transition-colors text-sm">
                    Báo cáo thống kê
                  </a>
                </li>
              </ul>
            </div>
            
            <!-- Contact Info -->
            <div>
              <h4 class="text-lg font-semibold mb-4">Liên hệ</h4>
              <div class="space-y-3">
                <div class="flex items-center text-orange-100 text-sm">
                  <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                  </svg>
                  contact@techzen.vn
                </div>
                <div class="flex items-center text-orange-100 text-sm">
                  <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
                  </svg>
                  +84 93 550 56 40
                </div>
                <div class="flex items-center text-orange-100 text-sm">
                  <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                  </svg>
                  06 Trần Phú, Thạch Thang, Hải Châu, Da Nang, Vietnam
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Bottom Bar -->
        <div class="border-t border-orange-400 py-4">
          <div class="flex flex-col md:flex-row justify-between items-center">
            <div class="text-orange-100 text-sm mb-2 md:mb-0">
              © 2025 TECHSALE. Tất cả quyền được bảo lưu.
            </div>
            <div class="flex space-x-6 text-sm">
              <a href="#" class="text-orange-100 hover:text-white transition-colors">
                Chính sách bảo mật
              </a>
              <a href="#" class="text-orange-100 hover:text-white transition-colors">
                Điều khoản sử dụng
              </a>
              <a href="#" class="text-orange-100 hover:text-white transition-colors">
                Sơ đồ trang web
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
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
      currentTooltip: null, // For custom tooltip
      priceFromError: '', // Error message for priceFrom
      priceToError: '', // Error message for priceTo
      tableLoading: false, // Loading state for table only
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
    },
    showingItemsText() {
      const start = this.currentPage * this.pageSize + 1;
      const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalItems);
      return `${this.$t('lang.PAGINATION.showing')} ${start} - ${end} ${this.$t('lang.PAGINATION.of')} ${this.totalItems} ${this.$t('lang.PAGINATION.items')}`;
    },
    // Tính toán ngày giới hạn cho ngày nhận (20 ngày trong tương lai)
    maxExpectDate() {
      const today = new Date();
      const maxDate = new Date(today);
      maxDate.setDate(today.getDate() + 20);
      return maxDate;
    },
    // Tính toán ngày giới hạn cho ngày tạo (không thể chọn ngày trong tương lai)
    maxCreatedDate() {
      return new Date();
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
      if (pageNum < 1 || pageNum > this.totalPages) {
        return;
      }
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
      // Validate price inputs
      if (!this.validatePriceInputs()) {
        return;
      }
      
      this.currentPage = 0;
      this.fetchPurchaseHistory();
    },
    validatePriceInputs() {
      const minPrice = 10000; // 10.000 VND
      const maxPrice = 100000000; // 100.000.000 VND
      
      // Validate priceFrom
      if (this.priceFrom !== '' && this.priceFrom !== null) {
        const priceFromNum = parseFloat(this.priceFrom);
        
        if (isNaN(priceFromNum)) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_NEGATIVE'));
          return false;
        }
        
        if (priceFromNum < 0) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_NEGATIVE'));
          return false;
        }
        
        if (priceFromNum < minPrice) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_TOO_SMALL'));
          return false;
        }
        
        if (priceFromNum > maxPrice) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_TOO_LARGE'));
          return false;
        }
      }
      
      // Validate priceTo
      if (this.priceTo !== '' && this.priceTo !== null) {
        const priceToNum = parseFloat(this.priceTo);
        
        if (isNaN(priceToNum)) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_NEGATIVE'));
          return false;
        }
        
        if (priceToNum < 0) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_NEGATIVE'));
          return false;
        }
        
        if (priceToNum < minPrice) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_TOO_SMALL'));
          return false;
        }
        
        if (priceToNum > maxPrice) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_TOO_LARGE'));
          return false;
        }
      }
      
      // Validate price range (priceFrom <= priceTo)
      if (this.priceFrom !== '' && this.priceFrom !== null && 
          this.priceTo !== '' && this.priceTo !== null) {
        const priceFromNum = parseFloat(this.priceFrom);
        const priceToNum = parseFloat(this.priceTo);
        
        if (priceFromNum > priceToNum) {
          this.$toast.error(this.$t('lang.TOAST.PRICE_FROM_TO_GREATER'));
          return false;
        }
      }
      
      return true;
    },
    validatePriceFrom() {
      this.priceFromError = '';
      const minPrice = 10000;
      const maxPrice = 100000000;
      
      if (this.priceFrom === '' || this.priceFrom === null) {
        return;
      }
      
      const priceNum = parseFloat(this.priceFrom);
      
      if (isNaN(priceNum)) {
        this.priceFromError = this.$t('lang.TOAST.PRICE_NEGATIVE');
        return;
      }
      
      if (priceNum < 0) {
        this.priceFromError = this.$t('lang.TOAST.PRICE_NEGATIVE');
        return;
      }
      
      if (priceNum < minPrice) {
        this.priceFromError = this.$t('lang.TOAST.PRICE_TOO_SMALL');
        return;
      }
      
      if (priceNum > maxPrice) {
        this.priceFromError = this.$t('lang.TOAST.PRICE_TOO_LARGE');
        return;
      }
      
      // Validate range if priceTo is also set
      if (this.priceTo !== '' && this.priceTo !== null) {
        const priceToNum = parseFloat(this.priceTo);
        if (!isNaN(priceToNum) && priceNum > priceToNum) {
          this.priceFromError = this.$t('lang.TOAST.PRICE_FROM_TO_GREATER');
          return;
        }
      }
    },
    validatePriceTo() {
      this.priceToError = '';
      const minPrice = 10000;
      const maxPrice = 100000000;
      
      if (this.priceTo === '' || this.priceTo === null) {
        return;
      }
      
      const priceNum = parseFloat(this.priceTo);
      
      if (isNaN(priceNum)) {
        this.priceToError = this.$t('lang.TOAST.PRICE_NEGATIVE');
        return;
      }
      
      if (priceNum < 0) {
        this.priceToError = this.$t('lang.TOAST.PRICE_NEGATIVE');
        return;
      }
      
      if (priceNum < minPrice) {
        this.priceToError = this.$t('lang.TOAST.PRICE_TOO_SMALL');
        return;
      }
      
      if (priceNum > maxPrice) {
        this.priceToError = this.$t('lang.TOAST.PRICE_TOO_LARGE');
        return;
      }
      
      // Validate range if priceFrom is also set
      if (this.priceFrom !== '' && this.priceFrom !== null) {
        const priceFromNum = parseFloat(this.priceFrom);
        if (!isNaN(priceFromNum) && priceFromNum > priceNum) {
          this.priceToError = this.$t('lang.TOAST.PRICE_FROM_TO_GREATER');
          return;
        }
      }
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
      this.priceFromError = "";
      this.priceToError = "";
      this.currentPage = 0;
      this.tableLoading = false; // Reset table loading state
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
      
      // Set table loading state instead of full page loading
      this.tableLoading = true;
      
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
        this.tableLoading = false;
      }
    },
    handleChangeDateRangeByCreatedDateFrom(newDate) {
      if (newDate) {
        const selectedDate = new Date(newDate);
        const today = new Date();
        today.setHours(23, 59, 59, 999); // Đặt thời gian cuối ngày hôm nay
        
        if (selectedDate > today) {
          this.$toast.error(this.$t('lang.TOAST.DATE_CREATED_FUTURE'));
          this.filterByCreatedDateFrom = null;
          return;
        }
      }
      this.filterByCreatedDateFrom = newDate;
    },
    handleChangeDateRangeByCreatedDateTo(newDate) {
      if (newDate) {
        const selectedDate = new Date(newDate);
        const today = new Date();
        today.setHours(23, 59, 59, 999); // Đặt thời gian cuối ngày hôm nay
        
        if (selectedDate > today) {
          this.$toast.error(this.$t('lang.TOAST.DATE_CREATED_FUTURE'));
          this.filterByCreatedDateTo = null;
          return;
        }
        
        // Kiểm tra ngày đến phải lớn hơn hoặc bằng ngày từ
        if (this.filterByCreatedDateFrom && selectedDate < new Date(this.filterByCreatedDateFrom)) {
          this.$toast.error(this.$t('lang.TOAST.DATE_TO_GREATER'));
          this.filterByCreatedDateTo = null;
          return;
        }
      }
      this.filterByCreatedDateTo = newDate;
    },
    handleChangeDateRangeByExpectDateFrom(newDate) {
      if (newDate) {
        const selectedDate = new Date(newDate);
        const maxDate = new Date();
        maxDate.setDate(maxDate.getDate() + 20);
        maxDate.setHours(23, 59, 59, 999);
        
        if (selectedDate > maxDate) {
          this.$toast.error(this.$t('lang.TOAST.DATE_EXPECT_FUTURE'));
          this.filterByExpectDateFrom = null;
          return;
        }
      }
      this.filterByExpectDateFrom = newDate;
    },
    handleChangeDateRangeByExpectDateTo(newDate) {
      if (newDate) {
        const selectedDate = new Date(newDate);
        const maxDate = new Date();
        maxDate.setDate(maxDate.getDate() + 20);
        maxDate.setHours(23, 59, 59, 999);
        
        if (selectedDate > maxDate) {
          this.$toast.error(this.$t('lang.TOAST.DATE_EXPECT_FUTURE'));
          this.filterByExpectDateTo = null;
          return;
        }
        
        // Kiểm tra ngày đến phải lớn hơn hoặc bằng ngày từ
        if (this.filterByExpectDateFrom && selectedDate < new Date(this.filterByExpectDateFrom)) {
          this.$toast.error(this.$t('lang.TOAST.DATE_TO_GREATER'));
          this.filterByExpectDateTo = null;
          return;
        }
      }
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
    formatPrice(value) {
      if (!value) return '-';
      const formatter = new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(value || 0);
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    calculateDeliveryTime(item) {
      if (!item.orderingDate || !item.deliveryDate) {
        return '-';
      }
      
      const orderingDate = moment(item.orderingDate);
      const deliveryDate = moment(item.deliveryDate);
      
      if (!orderingDate.isValid() || !deliveryDate.isValid()) {
        return '-';
      }
      
      const days = deliveryDate.diff(orderingDate, 'days');
      return days + ' ' + this.$t('lang.DETAIL.DAYS');
    },
    showTooltip(event, message) {
      // Tạo tooltip element
      const tooltip = document.createElement('div');
      tooltip.className = 'custom-date-tooltip';
      tooltip.textContent = message;
      tooltip.style.cssText = `
        position: absolute;
        background-color: #1f2937;
        color: white;
        padding: 0.5rem;
        border-radius: 0.375rem;
        font-size: 0.75rem;
        z-index: 9999;
        pointer-events: none;
        white-space: nowrap;
        top: ${event.target.offsetTop - 40}px;
        left: ${event.target.offsetLeft + event.target.offsetWidth / 2}px;
        transform: translateX(-50%);
      `;
      
      // Thêm mũi tên
      const arrow = document.createElement('div');
      arrow.style.cssText = `
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 0.25rem solid transparent;
        border-top-color: #1f2937;
      `;
      tooltip.appendChild(arrow);
      
      // Thêm vào body
      document.body.appendChild(tooltip);
      this.currentTooltip = tooltip;
    },
    hideTooltip() {
      if (this.currentTooltip) {
        document.body.removeChild(this.currentTooltip);
        this.currentTooltip = null;
      }
    },
    handleLogoError(event) {
      // Fallback to text logo if image fails to load
      const imgElement = event.target;
      const parentDiv = imgElement.parentElement;
      
      // Remove the image
      imgElement.remove();
      
      // Add text fallback
      const textSpan = document.createElement('span');
      textSpan.className = 'text-orange-600 font-bold text-2xl';
      textSpan.textContent = 'T';
      parentDiv.appendChild(textSpan);
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

/* Footer styles */
footer {
  width: 100vw !important;
  position: relative;
  left: 50%;
  right: 50%;
  margin-left: -50vw;
  margin-right: -50vw;
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

/* Date picker disabled styles */
.dp__day_content {
  position: relative;
  cursor: pointer;
}

.dp__disabled {
  color: #cbd5e1 !important;
  background-color: #f1f5f9 !important;
  cursor: not-allowed !important;
}

.dp__disabled:hover {
  background-color: #e2e8f0 !important;
}

/* Loading animation */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

</style> 