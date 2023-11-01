import androidVue from "../android-vue";

export default {

  /**
   * 页面初始化
   * @param {参数} params 
   */
  init(params) {
    return request("post", "system/pushMessage", params);
  }


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
