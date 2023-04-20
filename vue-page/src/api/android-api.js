import androidVue from "./android-vue";

import config from '../fetch/config'
import storage from '../components/development/storage'

export default {

  // 扫描二维码
  scanQrCode() {
    request('post', "system/scan-qr-code");
  },

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
   * 停止蓝牙扫描
   * @returns 
   */
  stopScan(params) {
    if (config.developmentMode) {
      return;
    }
    request("post", "ble/stopScan", params);
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

  /**
   * 连接设备
   */
  connectDevice(params) {
    if (config.developmentMode) {
      return newPromise(true);
    }
    return request("post", "ble/connect", params);
  },

  /**
   * 获取连接状态
   * @param {address} params 
   * @returns 状态
   */
  getConnectingStatus(params) {
    if (config.developmentMode) {
      return newPromise(2);
    }
    return request("get", "ble/connect/status", params);
  },


  write(params) {
    return request("post", "ble/write", params);
  },

  startNotify(params) {
    return request("post", "ble/startNotify", params);
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
