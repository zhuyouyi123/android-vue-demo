import androidApi from "@/api/android-api";
import { Notify } from "vant";
export default {
  /**
   * 查询批量配置错误列表
   */
  queryBatchConfigFailureRecord() {
    return callAndroidQueryBatchConfigFailureRecord();
  },

  /**
   * 开始批量配置
   */
  startBatchConfig(type, params) {
    return callAndroidStartBatchConfig(type, params);
  },
};

/**
 * 调用Android查询错误记录
 */
function callAndroidQueryBatchConfigFailureRecord() {
  let channelRecordList = [];
  let secretKeyRecordList = [];
  let removeSecretKeyRecordList = [];
  let connectableRecordList = [];
  let triggerResponseTimeRecordList = [];
  return new Promise((resolve, reject) => {
    androidApi.queryBatchConfigFailureRecord().then((data) => {
      if (data && data.length > 0) {
        data.forEach((e) => {
          if (e.type == 0) {
            channelRecordList.push(e);
          } else if (e.type == 1) {
            secretKeyRecordList.push(e);
          } else if (e.type == 3) {
            removeSecretKeyRecordList.push(e);
          } else if (e.type == 4) {
            connectableRecordList.push(e);
          } else if (e.type == 5) {
            triggerResponseTimeRecordList.push(e);
          }
        });
      }
      resolve([
        {
          list: channelRecordList,
          count: channelRecordList.length,
          type: "Chanel",
          key: "channel",
        },
        {
          list: secretKeyRecordList,
          count: secretKeyRecordList.length,
          type: "SecretKey",
          key: "secret",
        },
        {
          list: removeSecretKeyRecordList,
          count: removeSecretKeyRecordList.length,
          type: "Remove SecretKey",
          key: "remove_secret_key",
        },
        {
          list: connectableRecordList,
          count: connectableRecordList.length,
          type: "Connectable",
          key: "connectable",
        },
        {
          list: triggerResponseTimeRecordList,
          count: triggerResponseTimeRecordList.length,
          type: "Resonse Time",
          key: "trigger_response_time",
        },
      ]);
    });
  });
}

/**
 * 调用Android开始批量配置
 */
function callAndroidStartBatchConfig(type, params) {
  return new Promise((resolve, reject) => {
    switch (type) {
      case "channel":
        androidApi
          .batchConfigChannel(params)
          .then(() => {
            resolve();
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
            reject();
          });
        break;
      case "secret":
        androidApi
          .batchConfigSecretKey(params)
          .then(() => {
            resolve();
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
            reject();
          });
        break;
      case "remove_secret_key":
      case "connectable":
      case "trigger_response_time":
        androidApi
          .batchConfig(params)
          .then(() => {
            resolve();
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
            reject();
          });
        break;
      default:
        break;
    }
  });
}
