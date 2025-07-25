<template>
  <div>
    <Modal :show="isOpen" size="modal-xl" @hidden="closeModal">
      <div class="flex relative">
        <ModalHeader class="flex flex-col border-none w-full p-2">
          <h2 class="intro-y text-lg font-medium">{{ title }}</h2>
        </ModalHeader>
        <button class="absolute top-2 right-3 flex items-center justify-center h-10 w-10 z-20" @click="closeModal">
          <i class="fa-solid fa-times"></i>
        </button>
      </div>
      <img class="w-[95%] h-[2px] flex m-auto" src="@/assets/images/separate.png" alt="">

      <div class="p-5">
        <div class="intro-y flex justify-between items-center mb-5">
          <h3 class="text-lg font-medium">{{ subtitle }}</h3>
          <div class="text-primary">{{ $t('lang.EXPENSE_MODAL.EM6') }}: {{ formatCurrency(totalAmount) }} {{ $t('lang.DASHBOARD.DB16') }}</div>
        </div>

        <div class="table-container">
          <table class="table table-report main-table">
            <colgroup>
              <col width="60">
              <col width="20%">
              <col width="35%">
              <col width="15%">
              <col width="15%">
            </colgroup>
            <thead>
              <tr class="header-row">
                <th class="text-center">{{ $t('lang.EXPENSE_MODAL.EM8') }}</th>
                <th>{{ $t('lang.EXPENSE_MODAL.EM9') }}</th>
                <th>{{ $t('lang.EXPENSE_MODAL.EM10') }}</th>
                <th class="text-right">{{ $t('lang.EXPENSE_MODAL.EM11') }}</th>
                <th>{{ $t('lang.EXPENSE_MODAL.EM12') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in items" :key="index" class="intro-x">
                <td class="text-center">{{ index + 1 }}</td>
                <td class="truncate">{{ item.user }}</td>
                <td class="truncate">{{ item.product }}</td>
                <td class="text-right">{{ formatCurrency(item.amount) }}</td>
                <td>{{ formatDate(item.date) }}</td>
              </tr>
              <tr v-if="items.length === 0">
                <td colspan="5" class="text-center py-4">{{ $t('lang.EXPENSE_MODAL.EM7') }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script>
import { Modal, ModalHeader } from "@/global-components/modal";

export default {
  name: "ExpenseDetailModal",
  components: {
    Modal,
    ModalHeader
  },
  props: {
    isOpen: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ""
    },
    subtitle: {
      type: String,
      default: ""
    },
    items: {
      type: Array,
      default: () => []
    }
  },

  watch: {
    isOpen: {
      immediate: true,
      handler(newVal) {
      }
    },
    items: {
      immediate: true,
      deep: true,
      handler(newVal) {
        if (newVal && newVal.length > 0) {

        }
      }
    }
  },
  computed: {
    totalAmount() {
      return this.items.reduce((total, item) => total + (Number(item.amount) || 0), 0);
    }
  },
  methods: {
    closeModal() {
      this.$emit("close");
    },
    formatCurrency(value) {
      if (value == null) {
        return 0;
      }
      const formatter = new Intl.NumberFormat('vi-VN');
      return formatter.format(value);
    },
    formatDate(dateString) {
      if (!dateString) return "";
      
      try {
        // Tạo đối tượng Date từ chuỗi
        const date = new Date(dateString);
        
        // Kiểm tra nếu ngày không hợp lệ
        if (isNaN(date.getTime())) {
          console.warn("Invalid date detected:", dateString);
          return dateString; // Trả về chuỗi nguyên bản nếu không parse được
        }
        
        // Format ngày theo định dạng dd/MM/yyyy
        return date.toLocaleDateString('vi-VN', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        });
      } catch (error) {
        console.error("Error formatting date:", error);
        return dateString; // Trả về chuỗi nguyên bản nếu có lỗi
      }
    }
  }
};
</script>

<style scoped>
.header-row {
  background-color: #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 50;
}

.table-container {
  position: relative;
  overflow: auto;
  max-height: 60vh;
  margin-right: 5px;
  border-radius: 0.375rem;
  scrollbar-width: thin;
}

.main-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: separate;
  border-spacing: 0;
}

.main-table thead {
  position: sticky;
  top: 0;
  z-index: 50;
}

.main-table th {
  position: sticky;
  top: 0;
  z-index: 50;
  background-color: #e5e7eb;
  font-weight: 600;
  color: #1e293b;
  padding: 0.75rem;
  border-bottom: 2px solid #cbd5e1;
  box-shadow: 0 3px 6px rgba(0,0,0,0.1);
}

.main-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
  background-color: #ffffff;
}

.main-table .text-center {
  text-align: center;
}

.main-table .text-right {
  text-align: right;
}

.main-table tbody tr:hover {
  background-color: #f8fafc;
}

.main-table tbody tr:hover td {
  background-color: #f8fafc;
}

.main-table tbody tr:nth-child(even) td {
  background-color: #f9fafb;
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

/* Định dạng thanh cuộn cho Chrome, Edge, Safari */
.table-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.table-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
  background: #bbb;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: #999;
}

/* Đảm bảo các nút và điều khiển hiển thị đúng */
.modal-xl {
  position: relative;
}

:deep(.modal) {
  overflow: visible;
}

:deep(.modal-content) {
  overflow: visible;
}
</style> 