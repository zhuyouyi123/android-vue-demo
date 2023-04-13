import Vue from 'vue'
// 使用插件
import VueI18n from 'vue-i18n'

Vue.use(VueI18n);

let langFileds = require.context('./config', false, /\.js$/)
let regExp = /\.\/([^\.\/]+)\.([^\.]+)$/ //正则用于匹配 ./en.js中的'en'

let messages = {} //声明一个数据模型，对应i18n中的message属性

langFileds.keys().forEach(key => {
  let prop = regExp.exec(key)[1] //正则匹配en|zh这样的值
  //messages[prop]相当于 messages['en'] = {table:{...}}
  messages[prop] = langFileds(key).default

})

let locale = localStorage.getItem('lang') || "zh" //从localstorag中获取

export default new VueI18n({
  locale, //指定语言字段
  messages //定义语言字段
})
