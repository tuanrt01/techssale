<template>
  <div class="py-5 px-5">
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center">
        <img src="/mainLogo.png" alt="Logo" class="h-12 mr-4">
        <h1 class="text-2xl font-bold">{{ $t('lang.TITLE.TT_PURCHASE_HISTORY') }}</h1>
      </div>
      <div>
        <select v-model="selectedLanguage" @change="changeLanguage" class="form-select">
          <option value="vi">Tiếng Việt</option>
          <option value="en">English</option>
        </select>
      </div>
    </div>
    <div class="bg-white rounded-lg shadow-lg p-6">
      <PurchaseHistory :standalone="true" />
    </div>
  </div>
</template>

<script>
import PurchaseHistory from '@/views/user/list-requested/PurchaseHistory.vue'

export default {
  name: 'PurchaseHistoryApp',
  components: {
    PurchaseHistory
  },
  data() {
    return {
      selectedLanguage: this.$i18n.locale || 'vi'
    }
  },
  methods: {
    changeLanguage() {
      this.$i18n.locale = this.selectedLanguage;
      localStorage.setItem('locale', this.selectedLanguage);
    }
  },
  mounted() {
    const savedLanguage = localStorage.getItem('locale');
    if (savedLanguage) {
      this.selectedLanguage = savedLanguage;
      this.$i18n.locale = savedLanguage;
    }
  }
}
</script>

<style>
body {
  background-color: #f3f4f6;
  margin: 0;
  padding: 0;
  font-family: 'Roboto', sans-serif;
}

/* Make sure VueDatePicker container is positioned correctly */
.dp__main {
  position: relative !important;
}

/* Ensure VueDatePicker popups appear above other elements */
.dp__instance_calendar {
  position: fixed !important;
}

/* Fix date input width */
.deadline, .form-control {
  width: 100% !important;
}
</style> 