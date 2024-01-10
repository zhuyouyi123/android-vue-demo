import Vue from 'vue'


import ElementUI from 'element-ui'
import Vant from 'vant';
// import VCharts from 'v-charts'

import 'vant/lib/index.css';
import 'element-ui/lib/theme-chalk/index.css'
import './assets/scss/app.scss'

import App from './App'
import store from './store'
import router from './router'

// import echarts from 'echarts'
// Vue.prototype.$echarts = echarts

// import '@/permission' // permission control
import '@/assets/base.css'
import '@/assets/font.css'


Vue.use(Vant);
// Vue.use(VCharts);

Vue.use(ElementUI);
// // set ElementUI lang to EN
// Vue.use(ElementUI, { locale })

router.afterEach((to, from, next) => {
  window.scrollTo(0, 0);
});

import VConsole from 'vconsole';
const vconsole = new VConsole()
Vue.use(vconsole)

import androidVue from '@/android-vue.js';
Vue.use(androidVue);

import androidApi from './api/android-api.js'
Vue.prototype.$androidApi = androidApi

import dateUtil from './utils/dateUtil.js'
Vue.prototype.$dateUtil = dateUtil

import chartUtil from './utils/chartUtil.js'
Vue.prototype.$chartUtil = chartUtil


import testCharts from './store/testCharts.js'
Vue.prototype.$testCharts = testCharts

import deviceHolder from './store/deviceHolder.js'
Vue.prototype.$deviceHolder = deviceHolder



let utterMsg = null;
try {
  utterMsg = new SpeechSynthesisUtterance(' ');
  speechSynthesis.speak(utterMsg);
} catch (e) {
  // ElementUI.Message.error("当前系统不支持语音播放！");
}

Vue.prototype.$playVoice = function (text) {
  if (!utterMsg || !speechSynthesis) {
    return;
  }
  utterMsg.text = text;
  speechSynthesis.speak(utterMsg);//播放
}

Vue.config.productionTip = false
Vue.prototype.goBack = function (isBackReload) {
  try {
    window.history.go(-1);

    if (isBackReload) {
      let reloadId = setTimeout(() => {
        clearTimeout(reloadId);
        window.location.reload();
      }, 200);
    }

  } catch (e) {
    this.$message.alter("已经到最顶层了，不能在后退了！");
  }
}


window.addEventListener('popstate', function (e) {
  router.isBack = true
}, false)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
