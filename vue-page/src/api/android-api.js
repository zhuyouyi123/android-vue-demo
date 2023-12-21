import { Toast } from "vant";
import androidVue from "../android-vue";

/**
 * tips
 * android 统一封装的返回结果 基本都是
 * {"errorCode":0,data:"","errorMsg":["success"]}
 */

export default {

  /**
   * 进入管理App页面
   * 用户需要开启App通知时 需要进行选择App通知 只有选择的App才会进行通知
   * 此方法就是打开选择App的页面
   * @param {无参数} params 
   * @returns 无
   */
  openManagerAppPage(params) {
    return request("post", "system/manager-app", params);
  },

  /**
   * 下载文件
   *
   * @param {
   * fileType: 文件类型 目前有四种类型 1.ANDROID_APP 2.IOS_APP 3.DFU_FIRMWARE 4.OTA_FIRMWARE
   * fileName: fileName 文件名称 通过接口获取
   * } params 
   * @returns 无
   */
  downloadFile(params) {
    return request("post", "system/download", params);
  },

  /**
   * 查询App版本
   * 用户检测App升级的
   * @returns app版本 Android版本返回的是 x.x.x
   */
  queryAppVersion() {
    return request("get", "system/app-version")
  },

  /**
   * 安装app
   * 指向安装app 通过接口下载的文件保存到手机本地 通过此方法安装
   * @returns 
   */
  installApp() {
    return request("post", "system/app-install")
  },


  /**
   * 获取方法 通过此方法可以获取到Android SharePreference中的内容
   * @param {key} params 例如 我的key "APP_KEY" this.$androidApi.shareGet('APP_KEY')
   * @returns value
   */
  shareGet(params) {
    return request('get', 'share/get', params)
  },

  /**
   * 请求扫描二维码 这边扫描使用的是华为统一扫码 有权限开启扫码 无权限 请求权限
   * @returns 扫码结果通过回调方法 Android直接调用vue返回 不通过结果返回
   */
  startScanQr() {
    return request("get", 'permission/camera')
  },

  /**
   * 请求获取相机权限
   * @returns 无
   */
  // 获取权限
  requestCameraPermission() {
    return request("post", 'permission/camera')
  },

  /**
   * 判断权限是否存在
   * 在权限不存在时 页面需要显示某些内容让用户知道
   * @param {权限类型} type 主要为：READ_CONTACTS：读取联系人 READ_PHONE_STATE：电话 READ_CALL_LOG：通话记录 NOTIFICATION 通知权限
   * @returns 是否存在权限
   */
  queryPermissionExist(type) {
    return request("get", 'permission/exist', type)
  },

  /**
   * 请求权限
   * @param {权限类型} type 
   * @returns 
   */
  requestPermission(type) {
    return request("post", 'permission', type)
  },

  /**
   * 连接扫码 废弃
   * 用户第一次添加设备时
   */
  // connectScanCode(params) {
  //   return request("post", "scan-code/start", params);
  // },

  /**
   * 在连接蓝牙并订阅通知成功之后 向蓝牙写入数据
   * 目前只使用了 固件升级时 writeCommand("B1")
   * @param {*} params 指令
   * @returns 
   */
  // 写入指令 
  writeCommand(params) {
    return request("post", "communication/write-command", params);
  },

  /**
   * 开始进行固件升级
   * @param {无} params 
   * @returns 无
   */
  startDfuUpgrade(params) {
    return request("post", "communication/dfu-upgrade", params);
  },


  /**
   * 首页初始化
   * 接收参数 reload 在蓝牙连接时 需要重新发送执行 向手表获取数据
   * 
   * 蓝牙未连接时 进行蓝牙连接 并且开启通知（蓝牙Notify）
   * @param {reload:是否需要重新加载} params 
   * @returns private Boolean bluetoothEnable; private Boolean locateEnable; 蓝牙开关 位置权限开关
   */
  // 首页初始化 
  init(params) {
    return request("post", "communication/init", params);
  },

  /**
   * 暂不使用
   * 加载设备信息 向蓝牙写入数据 获取电量 实时信息 设备信息等
   * @param {*} params 
   * @returns 
   */
  // 加载设备信息
  // loadingDeviceInfo(params) {
  //   return request("post", "communication/loading-device-info", params);
  // },

  /**
   * 获取设备信息
   * @param {*} params 
   * @returns 当天手表的一些信息 
   *  deviceInfo: {
   */
  getDeviceInfo(params) {
    return request("get", "communication/loading-device-info", params);
  },

  /**
   * 获取 本周 上周 步数数据统计
   * @returns   
   * private Integer currWeek;
   * private Integer lastWeek;
   * private Integer currMonth;
   * private Integer lastMonth;
   */
  queryWeekAndMonthStepData() {
    return request("get", "communication/week-and-month")
  },

  /**
   * 首页展示的血氧 血压 心率等几项最新数据 用于首页 绘制echarts等
   * private Integer bloodOxygen;
   * private Double temperature;
   * private Integer highPressure;
   * private Integer lowPressure;
   * private Integer heartRate;
  *  private List<Integer> heartRateList;
   * @returns 当天最新信息
   */
  queryCurrDayLastInfo() {
    return request("get", "communication/curr-day-last-info")
  },


  /**
   * type数据类型主要包括 04 09 0E 07 等
   * 时间类型 1 天 2 周 3 月
   * App手机安装时会记录首次使用时间 页面会通过van-swipe展示每天数据 通过currIndex知道我要获取的是哪天的数据
   * 例如我当天安装的 只能获取当天的数据  昨天安装的 我就可以通过van-swipe查看昨天的数据 
   * currIndex首次传值为-1 且为逆序 假设我有100天数据 最后一天 也就是今天的数据 理应传入100-1 或者（-1） 再往前一天 传入 100-2
   * van-swipe 默认展示数据最后一天 也就是当天数据
   * 
   * @param {数据类型：此数据类型和协议相关} type 
   * @param {日期类型} dateType 
   * @param {当前需要序号} currIndex 
   * @returns 
   */
  getHistoryData(type, dateType, currIndex) {
    // return new Promise((resolve, reject) => {
    //   resolve({"average":"0-0","chartSize":4,"data":{"dataList":[],"date":20231216},"dataIndex":2,"isMultiple":true,"max":"0-0","min":"0-0","needSoar":false});
    //   return
    // })
    return request("get", "communication/history/list", { type: type, dateType: dateType, currIndex: currIndex });
  },

  /**
   * 暂不使用
   * @param {*} type 
   * @returns 
   */
  startSport(type) {
    return request("post", "communication/history/list", type);
  },

  /**
   * 查询步数达标天数
   * @returns 达标天数 complianceDays continueComplianceDays：连续达标天数
   */
  // 查询达标次数
  queryComplianceDays() {
    return request("get", "communication/compliance/days");
  },

  /**
   * 恢复出厂设置
   * @returns 
   */
  reset() {
    return request("post", "communication/reset")
  },

  /**
   * 返回用户信息
   * @param {*} params 
   * @returns id username gender
   */
  // 用户表
  queryUserInfo(params) {
    return request("get", "user/user-info", params);
  },

  /**
   * 保存用户信息
   * @param { id: this.userInfo.id,username: this.userInfo.username, gender: this.userInfo.gender} params 
   * @returns   
   */
  saveUserInfo(params) {
    return request("post", "user/user-info", params);
  },

  /**
   * 查询绑定设备
   * @returns 设备mac地址 电量 固件版本等
   */
  // 设备表
  queryDevice() {
    return request('get', 'device')
  },

  /**
   * 解绑设备
   * @returns
   */
  unbindDevice() {
    return request('post', 'device/unbind')
  },

  // 配置表￥￥￥￥￥￥￥￥
  /**
   * 初始化配置表
   * 加载消息 和电话通知
   * @returns 
   */
  initConfig() {
    return request('get', 'configuration/init')
  },
  /**
   * 查询配置根据组
   * 根据组织查询配置列表 对应类型 HOME_CARD TARGET NOTIFICATION
   * @param {group} params 
   * @returns 
   */
  queryConfigurationByGroup(params) {
    return request("get", "configuration/list", params)
  },

  /**
   * 更新配置
   * @param {type,value,group} params 
   * @returns 
   */
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
      // console.log(`android-vue path:${path} res:${JSON.stringify(res)}`);
      resolve(res.data);
    } else {
      Toast.fail({ message: res.errorMsg[0], position: "top" })
      reject(res.errorMsg[0])
    }
  })
}
