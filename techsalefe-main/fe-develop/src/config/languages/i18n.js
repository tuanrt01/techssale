import { createI18n } from 'vue-i18n'
import vnLang from '../languages/vi.js'
import enLang from '../languages/en.js'
import jaLang from '../languages/ja.js'
const lang = localStorage.getItem("language") || "vi";
const i18n = createI18n({
  locale: lang,
  messages: {
    vi: {
      lang: vnLang
    },
    // en: {
    //   lang: enLang
    // },
    ja: {
      lang: jaLang
    }
  }
})
export default i18n