  <template>
  <nav class="w-full sm:w-auto">
    <ul class="pagination">
      <li class="page-item">
        <button class="page-link" @click="onClickPreviousPage" :disabled="isInFirstPage">
          <ChevronLeftIcon class="w-4 h-4" />
        </button>
      </li>
      <li class="page-item" v-for="page in pages" :key="page.name" :class="{ 'active': page.isDisabled }">
        <button class="page-link" type="button" @click="onClickPage(page.name)" :disabled="page.name === '...'">
          {{ page.name }}
        </button>
      </li>
      <li class="page-item">
        <button class="page-link" type="button" @click="onClickNextPage" :disabled="isInLastPage">
          <ChevronRightIcon class="w-4 h-4" />
        </button>
      </li>
    </ul>
  </nav>
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
      required: true,
    },
    currentPage: {
      type: Number,
      required: true,
    },
  },
  computed: {
    startPage() {
      if (this.currentPage === 1) {
        return 1;
      }
      return this.currentPage - 1;
    },
    endPage() {
      return Math.min(this.startPage + this.maxVisibleButtons - 1, this.totalPages);
    },
    pages() {
      const range = [];
      const maxVisibleButtons = this.maxVisibleButtons;
      const totalPages = this.totalPages;
      const currentPage = this.currentPage;

      if (totalPages <= maxVisibleButtons) {
        for (let i = 1; i <= totalPages; i++) {
          range.push({
            name: i,
            isDisabled: i === currentPage,
          });
        }
      } else {
        const halfVisibleButtons = Math.floor(maxVisibleButtons / 2);
        const startPage = Math.max(currentPage - halfVisibleButtons, 1);
        const endPage = Math.min(startPage + maxVisibleButtons - 1, totalPages);

        if (startPage > 1) {
          range.push({
            name: 1,
            isDisabled: false,
          });
          if (startPage > 2) {
            range.push({
              name: '...',
              isDisabled: false,
            });
          }
        }

        for (let i = startPage; i <= endPage; i++) {
          range.push({
            name: i,
            isDisabled: i === currentPage,
          });
        }

        if (endPage < totalPages) {
          if (endPage < totalPages - 1) {
            range.push({
              name: '...',
              isDisabled: false,
            });
          }
          range.push({
            name: totalPages,
            isDisabled: false,
          });
        }
      }
      return range;
    },
    isInFirstPage() {
      return this.currentPage === 1;
    },
    isInLastPage() {
      return this.currentPage === this.totalPages;
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

