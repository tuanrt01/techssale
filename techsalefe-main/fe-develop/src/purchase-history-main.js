import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PurchaseHistoryApp from './PurchaseHistoryApp.vue'
import './assets/css/app.css'
import './assets/css/components/date-picker-fix.css'
import store from './stores'
import '@fortawesome/fontawesome-free/css/all.css'
import i18n from '@/config/languages/i18n'
import Cookies from 'js-cookie';
import FloatingVue from 'floating-vue'
import 'floating-vue/dist/style.css'

const app = createApp(PurchaseHistoryApp)
  .use(store)
  .use(i18n)
  .use(Cookies)
  .use(createPinia())
  .use(FloatingVue)

// Add global components that are needed
import globalComponents from './purchase-history-components'
globalComponents(app)

app.mount('#purchase-history-app')

export default app 