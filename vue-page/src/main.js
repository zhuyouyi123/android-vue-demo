import Vue from "vue";

import ElementUI from "element-ui";
import Vant from "vant";


import "vant/lib/index.css";
import "element-ui/lib/theme-chalk/index.css";

import "../static/css/vant.scss";
import "../static/css/element.scss";

import App from "./App";
import store from "./components/store";
import router from "./components/router";

// import '@/permission' // permission control
import "@/assets/base.css";
import "@/assets/font.css";

Vue.use(Vant);
Vue.use(ElementUI);

import defaultImport from "./defaultImportComp.js";
Vue.use(defaultImport);

router.afterEach((to, from, next) => {
  window.scrollTo(0, 0);
});

import Rem from "./utils/rem";
Vue.use(Rem);

// import vConsole from 'vconsole';
// //打印1000次后自动清除
// var con = new vConsole({ maxLogNumber: 1000 });

import i18n from "./components/i18n";

import androidApi from "./api/android-api.js";
Vue.prototype.$androidApi = androidApi;

import developmentStorage from "./components/development/storage";
Vue.prototype.$developmentStorage = developmentStorage;

import storage from "./fetch/storage";
Vue.prototype.$storage = storage;

// 引入config
import config from "@/fetch/config";
Vue.prototype.$config = config;

let utterMsg = null;
try {
  utterMsg = new SpeechSynthesisUtterance(" ");
  speechSynthesis.speak(utterMsg);
} catch (e) {
  // ElementUI.Message.error("当前系统不支持语音播放！");
}

Vue.prototype.$playVoice = function (text) {
  if (!utterMsg || !speechSynthesis) {
    return;
  }
  utterMsg.text = text;
  speechSynthesis.speak(utterMsg); //播放
};

Vue.config.productionTip = false;
// Vue.prototype.goBack = function (isBackReload) {
//   try {
//     window.history.go(-1);

//     if (isBackReload) {
//       let reloadId = setTimeout(() => {
//         clearTimeout(reloadId);
//         window.location.reload();
//       }, 200);
//     }

//   } catch (e) {
//     this.$message.alter("已经到最顶层了，不能在后退了！");
//   }
// }

window.addEventListener(
  "popstate",
  function (e) {
    router.isBack = true;
  },
  false
);

new Vue({
  el: "#app",
  router,
  store,
  i18n,
  render: (h) => h(App),
});
