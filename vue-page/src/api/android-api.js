import androidVue from "./android-vue";

import config from '../fetch/config'
import storage from '../components/development/storage'

export default {

  /**
   * 页面初始化
   * @param {参数} params 
   */
  init(params) {
    return request("get", "ble/init", params);
  },

  startScan(params) {
    if (config.developmentMode) {
      return newPromise();
    }
    return request("post", "ble/startScan", params);
  },

  /**
   * 设备列表
   */
  deviceList(params) {
    if (config.developmentMode) {
      return newPromise(storage.getHomeList());
    }
    return request("get", "ble/list", params);
  },


}

/**
 * 
 * @param {类型} type 
 * @param {地址} path 
 * @param {参数} params 
 * @returns 结果
 */
function request(type, path, params) {
  return new Promise((resolve, reject) => {
    let res;
    if (type == "get") {
      res = androidVue.getRequest(path, params);
    } else {
      res = androidVue.postRequest(path, params);
    }
    console.log(path);
    console.log(JSON.stringify(res));
    if (!res) {
      resolve();
      return;
    }
    if (res.errorCode == 0) {
      resolve(res.data);
    } else {
      reject(res.errorMsg[0])
    }
  })
}

function newPromise(data) {
  return new Promise((resolve, reject) => {
    resolve(data);
  })
}
