import androidApi from "@/api/android-api";
import { Notify } from "vant";

import router from "@/components/router";

import config from "@/fetch/config";

export default {
  /**
   * 调用Android获取连接详情
   */
  getConnectDetail(address) {
    return callAndroidGetConnectDetail(address);
  },

  /**
   * 连接设备
   */
  connectDevice(address) {
    return callAndroidConnectDevice(address);
  },
  /**
   * 取消连接
   */
  cancelConnectDevice(address) {
    callAndroidCancelConnectDevice(address);
  },
  /**
   * 开启通知
   */
  startNotify(address) {
    callAndroidStartNotify(address);
  },

  writeData(address, key, data) {
    callAndroidWriteData(address, key, data);
  },

  /**
   * 读取通道信息
   */
  readChannelInfo() {
    return callAndroidReadChannelInfo();
  },

  /**
   * 获取通道类型下拉框
   */
  getFrameTypeDropDown(params) {
    return callAndroidGetFrameTypeDropDown(params);
  },
};

/**
 * 调用Android获取连接详情
 */
function callAndroidGetConnectDetail(address) {
  let params = {
    address: address,
  };

  // return new Promise((resolve, reject) => {
  //   resolve({
  //     agreementInfoList: [{"agreementData":["19181122334455667788991123334455","0002","0001","1","123456"],"agreementName":"iBeacon","agreementType":"1","alwaysBroadcast":false,"broadcastInterval":100,"broadcastPower":9,"broadcastPowerValue":0.0,"calibrationDistance":205,"channelNumber":0,"triggerBroadcastPowerValue":2.5,"triggerCondition":1,"triggerSwitch":true,"triggerTime":0,"txInterval":284,"txPower":12},{"agreementData":["http://www.","0",".com/"],"agreementName":"URL","agreementType":"3","alwaysBroadcast":false,"broadcastInterval":1000,"broadcastPower":9,"broadcastPowerValue":0.0,"calibrationDistance":205,"channelNumber":1,"triggerBroadcastPowerValue":-5.0,"triggerCondition":0,"triggerSwitch":false,"triggerTime":300,"txInterval":1200,"txPower":5},{"agreementData":["22434646466666666666666666666666","123","321","1","123345"],"agreementName":"iBeacon","agreementType":"1","alwaysBroadcast":false,"broadcastInterval":1000,"broadcastPower":9,"broadcastPowerValue":0.0,"calibrationDistance":205,"channelNumber":2,"triggerBroadcastPowerValue":0.0,"triggerCondition":3,"triggerSwitch":false,"triggerTime":0,"txInterval":1200,"txPower":9},{"agreementData":[],"agreementName":"TLM","agreementType":"4","alwaysBroadcast":false,"broadcastInterval":1600,"broadcastPower":5,"broadcastPowerValue":-5.0,"calibrationDistance":217,"channelNumber":3,"triggerBroadcastPowerValue":-5.0,"triggerCondition":3,"triggerSwitch":false,"triggerTime":700,"txInterval":1200,"txPower":5},{"agreementType":"0","channelNumber":4},{"agreementType":"0","channelNumber":5}],
  //   });
  // });

  return androidApi.getConnectDetail(params);
}

/**
 * 调用Android连接设备
 */
function callAndroidConnectDevice(address) {
  return new Promise((resolve, reject) => {
    let params = {
      address: address,
    };
    setTimeout(() => {
      androidApi
        .connectDevice(params)
        .then(() => {
          resolve();
        })
        .catch((error) => {
          Notify({
            type: "warning",
            message: errorMsg,
          });

          reject();
        });
    }, 200);
  });
}

function callAndroidCancelConnectDevice(address) {
  let params = {
    address: address,
  };
  androidApi.cancelConnectDevice(params);
}

/**
 * 调用Android开启通知
 */
function callAndroidStartNotify(address) {
  let params = {
    address: address,
  };
  setTimeout(() => {
    androidApi.startNotify(params);
  }, 1200);
}

/**
 * 调用Android写入数据
 */
function callAndroidWriteData(address, key, data) {
  let params = {
    address: address,
    key: key,
    data: "",
  };
  if (data) {
    params.data = data;
  }
  androidApi.write(params);
}
/**
 * 调用Android 读取通道信息
 */
function callAndroidReadChannelInfo() {}

// 通道相关

/**
 * 获取通道类型下拉框
 */
function callAndroidGetFrameTypeDropDown(params) {
  return androidApi.getFrameTypeDropDown(params);
}
