import androidVue from "../android-vue";

export default {

  // /**
  //  * 页面初始化
  //  * @param {参数} params 
  //  */
  openManagerAppPage(params) {
    return request("post", "system/manager-app", params);
  },

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

  queryPermissionExist(type) {
    return request("get", 'permission/exist', type)
  },

  requestPermission(type) {
    return request("post", 'permission', type)
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


  getHistoryData(type, dateType, currIndex) {
    // return new Promise((resolve, reject) => {
    //   resolve({"average":"97","chartSize":314,"dataIndex":0,"list":[{"date":20231215,"hourlyData":["98","96","98","98","99","97","98","0","94","98","99","98","98","0","0","0","0","0","0","0","0","0","0","0"]}],"max":"99","min":"94"});
    //   return
    // })
    return request("get", "communication/history/list", { type: type, dateType: dateType, currIndex: currIndex });
  },

  startSport(type) {
    return request("post", "communication/history/list", type);
  },

  // 查询达标次数
  queryComplianceDays() {
    return request("get", "communication/compliance/days");
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

  initConfig() {
    return request('get', 'configuration/init')
  },
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
