import Vue from 'vue'


import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n
import Vant from 'vant';

import 'vant/lib/index.css';
import 'element-ui/lib/theme-chalk/index.css'

import App from './App'
import store from './store'
import router from './router'

// import '@/permission' // permission control
import '@/assets/base.css'
import '@/assets/font.css'


Vue.use(Vant);
Vue.use(ElementUI);
// // set ElementUI lang to EN
// Vue.use(ElementUI, { locale })

import defaultImport from './defaultImportComp.js';
Vue.use(defaultImport);


router.afterEach((to, from, next) => {
  window.scrollTo(0, 0);
});

import androidVue from '@/android-vue.js';
Vue.use(androidVue);

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
