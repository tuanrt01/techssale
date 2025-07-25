import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import globalComponents from './global-components'
import utils from './utils'
import './assets/css/app.css'
import './assets/css/components/date-picker-fix.css'
import store from './stores'
import '@fortawesome/fontawesome-free/css/all.css'
import i18n from '@/config/languages/i18n'
import Cookies from 'js-cookie';
import FloatingVue from 'floating-vue'
import 'floating-vue/dist/style.css'
const app = createApp(App).use(router).use(store).use(i18n).use(Cookies).use(createPinia())
globalComponents(app)
utils(app)
app.use(FloatingVue)
app.mount('#app')

export  default  app
