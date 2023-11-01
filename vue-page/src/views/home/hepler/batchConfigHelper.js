import androidApi from "@/api/android-api";
import i18n from "@/components/i18n";
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
  let resetRecordList = [];
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
          } else if (e.type == 6) {
            resetRecordList.push(e);
          }
        });
      }
      resolve([
        {
          list: channelRecordList,
          count: channelRecordList.length,
          type: i18n.t("home.title.channel"),
          key: "channel",
        },
        {
          list: secretKeyRecordList,
          count: secretKeyRecordList.length,
          type: i18n.t("home.title.secretKeyUpdate"),
          key: "secret",
        },
        {
          list: removeSecretKeyRecordList,
          count: removeSecretKeyRecordList.length,
          type: i18n.t("home.title.secretKeyRemove"),
          key: "remove_secret_key",
        },
        {
          list: connectableRecordList,
          count: connectableRecordList.length,
          type: i18n.t("home.title.connectable"),
          key: "connectable",
        },
        {
          list: triggerResponseTimeRecordList,
          count: triggerResponseTimeRecordList.length,
          type: i18n.t("home.title.triggerResponseTime"),
          key: "trigger_response_time",
        },
        {
          list: resetRecordList,
          count: resetRecordList.length,
          type: i18n.t("home.title.reset"),
          key: "reset",
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
      case "reset":
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
