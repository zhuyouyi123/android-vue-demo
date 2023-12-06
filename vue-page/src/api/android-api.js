import androidVue from "../android-vue";

export default {

  // /**
  //  * 页面初始化
  //  * @param {参数} params 
  //  */
  // init(params) {
  //   return request("post", "system/pushMessage", params);
  // },

  shareGet(params) {
    return request('get', 'share/get', params)
  },

  startScanQr() {
    return request("get", 'permission/camera')
  },

  // 获取权限
  requestCameraPermission() {
    return request("post", 'permission/camera')
  },

  /**
   * 连接扫码
   */
  connectScanCode(params) {
    return request("post", "scan-code/start", params);
  },

  // 首页初始化 
  init(params) {
    return request("post", "communication/init", params);
  },

  // 加载设备信息
  loadingDeviceInfo(params) {
    return request("post", "communication/loading-device-info", params);
  },

  getDeviceInfo(params) {
    return request("get", "communication/loading-device-info", params);
  },


  getHistoryData(params) {
    return request("get", "communication/history/list", params);
  },

  startSport(params) {
    return request("post", "communication/history/list", params);
  },


  // 用户表
  queryUserInfo(params) {
    return request("get", "user/user-info", params);
  },

  saveUserInfo(params) {
    return request("post", "user/user-info", params);
  },

  // 设备表
  queryDevice() {
    return request('get', 'device')
  },

  // 配置表￥￥￥￥￥￥￥￥

  /**
   * 查询配置根据组
   * @param {group} params 
   * @returns 
   */
  queryConfigurationByGroup(params) {
    return request("get", "configuration/list", params)
  },

  updateConfigStatus(params) {
    return request("get", "configuration/save", params)
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
    if (!res) {
      resolve();
      return;
    }
    if (res.errorCode == 0) {
      console.log("androidVue 结果", JSON.stringify(res.data));
      resolve(res.data);
    } else {
      reject(res.errorMsg[0])
    }
  })
}
