import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import locale from 'element-ui/lib/locale'
import elementEnLocale from 'element-ui/lib/locale/lang/en' // element-ui lang
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN' // element-ui lang
// import enLocale from './en' // 自定义字典-中文
// import zhLocale from './zh' // 自定义字典-英文

Vue.use(VueI18n)

const messages = {
  en: {
    // ...enLocale,
    ...elementEnLocale
  },
  zh: {
    // ...zhLocale,
    ...elementZhLocale
  }
}

//加载多语言
const i18n = new VueI18n({
  locale: Cookies.get('language') || (navigator.language.indexOf('zh') >= 0 ? 'zh' : 'en'), // set locale
  messages // set locale messages
})
console.log("lang -> language: " + i18n.locale)

//多语言处理ElementUI组件
locale.i18n((key, value) => i18n.t(key, value))

export default i18n
