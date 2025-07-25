<template>
  <Modal :show="show" @hidden="closeModal">
    <div class="p-5">
      <div class="flex justify-between items-center border-b pb-3">
        <h2 class="text-xl font-medium">{{ $t('lang.DETAIL.DT9') }}</h2>
        <button class="btn btn-outline-secondary" @click="closeModal">
          <XIcon class="w-4 h-4" />
        </button>
      </div>
      
      <div class="mt-5">
        <!-- Status Information -->
        <div class="mb-4">
          <div class="text-center text-white py-2 px-4 rounded-xl inline-block" 
              :class="{
                'bg-yellow-400': data.status === 'WAITING',
                'bg-sky-300': data.status === 'PROCESSING',
                'bg-green-400': data.status === 'ORDERING',
                'bg-green-300': data.status === 'SUCCESS',
                'bg-red-300': data.status === 'REJECTED'
              }">
            {{ $t(statusDisplayName[data.status]) }}
          </div>
        </div>
        
        <!-- Timeline Status -->
        <div class="relative pb-5 mt-5 mb-8" v-if="data.status !== 'WAITING'">
          <div class="flex items-center w-full">
            <div class="w-full bg-gray-200 h-1 flex items-center justify-between">
              <div v-for="(step, stepIndex) in filteredStatusSteps" :key="stepIndex"
                  class="relative flex flex-col items-center"
                  :class="{'opacity-50': !isStepPassed(step.status)}">
                <div :class="{
                    'bg-green-500': isStepPassed(step.status),
                    'bg-gray-300': !isStepPassed(step.status),
                    'w-6 h-6 rounded-full z-10 flex items-center justify-center': true
                  }">
                  <i v-if="isStepPassed(step.status)" class="fas fa-check text-white text-xs"></i>
                </div>
                <div class="absolute top-8 transform -translate-x-1/2 text-xs font-medium">
                  {{ $t(step.label) }}
                </div>
                <div v-if="getStatusDate(step.status)" class="absolute top-14 transform -translate-x-1/2 text-xs text-gray-500">
                  {{ formatDate(getStatusDate(step.status)) }}
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Request Information -->
        <div class="grid grid-cols-2 gap-4 mb-6">
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT5') }}</p>
            <p class="mt-1">{{ data.productName }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">ID</p>
            <p class="mt-1">{{ data.id }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT11') }}</p>
            <p class="mt-1">{{ data.amount }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT12') }}</p>
            <p class="mt-1">{{ formatCurrency(data.estimatePrice) }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT3') }}</p>
            <p class="mt-1">{{ formatDate(data.createdAt) }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT2') }}</p>
            <p class="mt-1">{{ formatDate(data.expectReceiveDate) }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT7') }}</p>
            <p class="mt-1">{{ data.reasonName || '-' }}</p>
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT19') }}</p>
            <p class="mt-1">{{ data.requestUsername || '-' }}</p>
          </div>
        </div>
        
        <!-- Product Link -->
        <div class="mb-6" v-if="data.productLink">
          <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT45') }}</p>
          <a :href="data.productLink" target="_blank" class="text-blue-500 hover:underline mt-1 block">
            {{ data.productLink }}
          </a>
        </div>
        
        <!-- Additional Info for SUCCESS Status -->
        <div v-if="data.status === 'SUCCESS'" class="mt-6">
          <h3 class="text-lg font-medium border-b pb-2 mb-4">{{ $t('lang.DETAIL.DT41') }}</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT33') }}</p>
              <p class="mt-1">{{ data.boughtProductName || '-' }}</p>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT38') }}</p>
              <p class="mt-1">{{ data.boughtAmount || '-' }}</p>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT40') }}</p>
              <p class="mt-1">{{ formatCurrency(data.boughtPrice) }}</p>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT39') }}</p>
              <p class="mt-1">{{ data.boughtPlace || '-' }}</p>
            </div>
          </div>
        </div>
        
        <!-- Rejection Reason -->
        <div v-if="data.status === 'REJECTED'" class="mt-6">
          <h3 class="text-lg font-medium border-b pb-2 mb-4">{{ $t('lang.DETAIL.DT26') }}</h3>
          <p>{{ data.rejectReason || '-' }}</p>
        </div>
      </div>
      
      <div class="mt-6 text-right">
        <button class="btn btn-primary" @click="closeModal">{{ $t('lang.BUTTON.BT1') }}</button>
      </div>
    </div>
  </Modal>
</template>

<script>
import { Modal } from "@/global-components/modal";
import { RequestStatus } from "@/common/StatusEnum";
import dayjs from "dayjs";

export default {
  name: "PurchaseDetailModal",
  components: { Modal },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    data: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      
    }
  },
  computed: {
    statusDisplayName() {
      return {
        '': 'Tất cả',
        ...RequestStatus
      }
    },
    filteredStatusSteps() {
      return [
        {
          status: 'WAITING',
          label: 'lang.STATUS.STT1',
          tooltip: 'lang.STATUSDESCRIPTION.STTD1'
        },
        {
          status: 'PROCESSING',
          label: 'lang.STATUS.STT2',
          tooltip: 'lang.STATUSDESCRIPTION.STTD2'
        },
        {
          status: 'ORDERING',
          label: 'lang.STATUS.STT5',
          tooltip: 'lang.STATUSDESCRIPTION.STTD5'
        },
        {
          status: 'SUCCESS',
          label: 'lang.STATUS.STT3',
          tooltip: 'lang.STATUSDESCRIPTION.STTD3'
        }
      ];
    }
  },
  methods: {
    closeModal() {
      this.$emit('update:show', false);
    },
    isStepPassed(status) {
      const statusOrder = {
        'WAITING': 0,
        'PROCESSING': 1,
        'ORDERING': 2,
        'SUCCESS': 3,
        'REJECTED': -1
      };
      
      if (this.data.status === 'REJECTED') {
        return false;
      }
      
      return statusOrder[status] <= statusOrder[this.data.status];
    },
    getStatusDate(status) {
      if (status === 'WAITING') {
        return this.data.createdAt;
      } else if (status === 'PROCESSING') {
        return this.data.updatedAt;
      } else if (status === 'ORDERING') {
        return this.data.updatedAt;
      } else if (status === 'SUCCESS') {
        return this.data.completedAt;
      }
      return null;
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      return dayjs(dateString).format('DD/MM/YYYY HH:mm');
    },
    formatCurrency(value) {
      if (!value) return '-';
      const formatter = new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(value || 0);
      return priceString.replace(/\D00(?=\D*$)/, "");
    }
  }
}
</script> 