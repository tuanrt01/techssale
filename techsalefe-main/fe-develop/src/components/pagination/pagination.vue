<template>
  <div class="pagination-container">
    <nav class="w-full sm:w-auto">
      <ul class="pagination">
        <li class="page-item">
          <button class="page-link rounded-l-md" @click="onClickPreviousPage" :disabled="isInFirstPage" :class="{ 'disabled': isInFirstPage }">
            <LucideChevronLeftIcon class="w-4 h-4" />
          </button>
        </li>
        <li class="page-item" v-for="page in pages" :key="page.name" :class="{ 'active': page.isCurrent }">
          <button class="page-link" type="button" @click="onClickPage(page.name)" :disabled="page.name === '...'" :class="{ 'current': page.isCurrent, 'disabled': page.name === '...' }">
            {{ page.name }}
          </button>
        </li>
        <li class="page-item">
          <button class="page-link rounded-r-md" type="button" @click="onClickNextPage" :disabled="isInLastPage" :class="{ 'disabled': isInLastPage }">
            <LucideChevronRightIcon class="w-4 h-4" />
          </button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
export default {
  props: {
    maxVisibleButtons: {
      type: Number,
      required: false,
      default: 3,
    },
    totalPages: {
      type: Number,
      required: true,
    },
    perPage: {
      type: Number,
      required: false,
      default: 10,
    },
    currentPage: {
      type: Number,
      required: false,
      default: 1,
    },
  },
  computed: {
    pages() {
      const range = [];
      const maxVisibleButtons = this.maxVisibleButtons;
      const totalPages = this.totalPages;
      const currentPage = this.currentPage;



      if (totalPages <= maxVisibleButtons) {
        // Nếu tổng số trang ít hơn hoặc bằng maxVisibleButtons, hiển thị tất cả
        for (let i = 1; i <= totalPages; i++) {
          range.push({
            name: i,
            isCurrent: i === currentPage,
          });
        }
      } else {
        // Logic mới: Luôn hiển thị trang hiện tại ở giữa nếu có thể
        const halfVisibleButtons = Math.floor(maxVisibleButtons / 2);
        
        // Tính toán vùng hiển thị với trang hiện tại ở giữa
        let startPage = currentPage - halfVisibleButtons;
        let endPage = currentPage + halfVisibleButtons;
        
        // Điều chỉnh nếu vượt quá giới hạn
        if (startPage < 1) {
          startPage = 1;
          endPage = Math.min(maxVisibleButtons, totalPages);
        }
        
        if (endPage > totalPages) {
          endPage = totalPages;
          startPage = Math.max(1, totalPages - maxVisibleButtons + 1);
        }
        
        // Đảm bảo trang hiện tại luôn nằm trong vùng hiển thị
        if (currentPage < startPage || currentPage > endPage) {
          startPage = Math.max(1, currentPage - halfVisibleButtons);
          endPage = Math.min(totalPages, startPage + maxVisibleButtons - 1);
          
          // Điều chỉnh lại nếu endPage vượt quá totalPages
          if (endPage === totalPages) {
            startPage = Math.max(1, totalPages - maxVisibleButtons + 1);
          }
        }

        // Thêm trang đầu tiên nếu không nằm trong vùng hiển thị
        if (startPage > 1) {
          range.push({
            name: 1,
            isCurrent: false,
          });
          if (startPage > 2) {
            range.push({
              name: '...',
              isCurrent: false,
            });
          }
        }

        // Thêm các trang trong vùng hiển thị
        for (let i = startPage; i <= endPage; i++) {
          range.push({
            name: i,
            isCurrent: i === currentPage,
          });
        }

        // Thêm trang cuối nếu không nằm trong vùng hiển thị
        if (endPage < totalPages) {
          if (endPage < totalPages - 1) {
            range.push({
              name: '...',
              isCurrent: false,
            });
          }
          range.push({
            name: totalPages,
            isCurrent: false,
          });
        }
      }

      return range;
    },
    isInFirstPage() {
      return this.currentPage === 1;
    },
    isInLastPage() {
      return this.currentPage === this.totalPages || this.totalPages === 0;
    },
  },
  methods: {
    onClickFirstPage() {
      this.$emit('pagechanged', 1);
    },
    onClickPreviousPage() {
      this.$emit('pagechanged', this.currentPage - 1);
    },
    onClickPage(page) {
      this.$emit('pagechanged', page);
    },
    onClickNextPage() {
      this.$emit('pagechanged', this.currentPage + 1);
    },
    onClickLastPage() {
      this.$emit('pagechanged', this.totalPages);
    },
  },
};
</script>

<style scoped>
.pagination-container {
  display: inline-flex;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background-color: white;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.pagination {
  display: flex;
  align-items: center;
}

.page-item {
  display: flex;
  align-items: center;
}

.page-item:not(:first-child) {
  margin-left: 2px;
}

.page-link {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 2.25rem;
  height: 2.25rem;
  padding: 0 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #1e40af;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.15s ease;
}

.page-link:hover:not(.disabled):not(.current) {
  background-color: #f3f4f6;
  color: #1e40af;
  border-color: #cbd5e1;
  z-index: 1;
}

.page-link.current {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
  z-index: 2;
}

.page-link.disabled {
  color: #94a3b8;
  cursor: not-allowed;
  background-color: #f8fafc;
}

.active .page-link {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
</style>

