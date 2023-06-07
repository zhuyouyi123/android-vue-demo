import instruct from "@/fetch/instruct";
import deviceDetailHelper from "../hepler/deviceDetailHelper";
import { Overlay, Circle, Dialog, Notify, Toast } from "vant";
export default {
  name: "deviceDetail",
  components: {
    navBar: () => import("@/components/navigation/navBar.vue"),
    [Overlay.name]: Overlay,
    [Circle.name]: Circle,
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 功能loading
      functionLoading: false,
      address: this.$route.query.address,
      // 蓝牙连接状态 0未连接 1 连接中 2连接成功
      bluetoothConnectStatus: 1,
      currentRate: 0,
      rate: 0,
      text: "",
      notificationSwitch: false,
      // 设备信息
      deviceInfo: {
        address: "",
        agreementInfoList: [],
        connectState: false,
        connectable: false,
        factoryVersionInfo: {
          firmwareVersion: "",
          hardwareVersion: "",
          softwareVersion: "",
          mac: "",
          manufactor: "",
          sdkVersion: "",
          model: "",
        },
        featureInfo: {
          channelNum: 6,
          supportAgreement:
            "iBeacon、UID、URL、TLM、Coreaiot、Quuppa、ACC、DeviceInfo、Line",
          supportPower:
            "-19.5dbm、-13.5dbm、-10dbm、-7dbm、-5dbm、-3.5dbm、-2dbm、-1dbm、0dbm、1dbm、1.5dbm、2.5dbm",
        },
      },

      // 修改防篡改秘钥弹窗
      secretKeyDialog: false,

      // 是否需要秘钥
      needSecretKey: false,

      secretKey: "",

      secretKeyCache: "",

      secretKeyOperationType: "update",
      // 触发响应时间
      keyTiggeredResponseDialog: false,
      keyTiggeredResponseTime: 5,
      // 可连接开关
      canConncetSwitch: true,

      i18nInfo: this.$i18n.t("device.detail"),

      // 成功
      RESULT_SUCCESS: "SUCCESS",
      RESULT_ERROR: "ERROR",
    };
  },

  mounted() {
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      window.addEventListener("popstate", this.goBack, false); //false阻止默认事件
    }
    window.addEventListener("commonAndroidEvent", this.callJs);

    this.canConncetSwitch = true;
    this.$storage.canConncetSwitch = true;
    this.getConnectDetailOrConnect();
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.goBack, false);
    window.removeEventListener("commonAndroidEvent", this.callJs);

    if (this.loading) {
      this.loading = false;
    }
  },

  methods: {
    callJs(e) {
      let result = e.data;
      let content = result.data;
      console.log(JSON.stringify(result));
      switch (result.eventName) {
        // 连接状态改变
        case "CONNECT_STATUS_CHANGE":
          if (
            content.data == 0 ||
            (content.data != "" &&
              content.data != "undefine" &&
              content.data != null)
          ) {
            this.handleConnectState(content.data);
          }
          break;
        case "START_NOTIFY_RESULT":
          if (content.status == this.RESULT_SUCCESS) {
            if (content.data) {
              this.notificationSwitch = true;
              // 获取是否需要秘钥
              this.rate = 35;
              this.getNeedSecretKey();
            } else {
              this.exit();
            }
          }
          break;
        // 通知状态改变
        case "NOTIFY_STATUS_CHANGE":
          // 开启通知成功
          if (content.type) {
            this.notifyChangeHandle(content);
          }
          break;
        default:
          break;
      }
    },
    goBack() {
      this.exit();
    },
    jumpToPage(page) {
      if (!this.checkConnectStatus()) {
        return;
      }
      this.$router.replace({
        path: "/home/deviceDetail/" + page,
        query: {
          address: this.address,
        },
      });
    },

    /**
     * 获取连接信息
     */
    getConnectDetailOrConnect() {
      deviceDetailHelper
        .getConnectDetail(this.address)
        .then((res) => {
          console.log(JSON.stringify(res));
          // 这边没有信息 证明没有连接过
          if (!res) {
            // 设置加载 进行连接
            this.loading = true;
            deviceDetailHelper
              .connectDevice(this.address)
              .then(() => {
                this.text = this.i18nInfo.tips.connecting;
                setTimeout(() => {
                  this.rate = 16;
                }, 100);
                // 获取秘钥
                this.$androidApi.getConnectSecretKey().then((res) => {
                  if (res) {
                    this.secretKeyCache = res;
                  }
                });
              })
              .catch(() => {
                this.exit();
              });
          } else {
            // 无需加载 直接读取信息到当前页面
            this.deviceInfo.address = res.address;
            this.deviceInfo.agreementInfoList = res.agreementInfoList;
            this.deviceInfo.connectState = res.connectState;
            this.bluetoothConnectStatus = res.connectState ? 2 : 0;
            this.deviceInfo.connectable = res.connectable;
            this.deviceInfo.factoryVersionInfo = {
              firmwareVersion: res.factoryVersionInfo.firmwareVersion,
              hardwareVersion: res.factoryVersionInfo.hardwareVersion,
              softwareVersion: res.factoryVersionInfo.sdkVersion,
              model: res.factoryVersionInfo.model,
            };
            this.needSecretKey = this.$storage.needSecretKey;
          }
        })
        .catch(() => {});
    },

    /**
     * 处理连接状态/
     */
    handleConnectState(status) {
      // 设置蓝牙连接状态
      this.bluetoothConnectStatus = status;
      if (status == 1 || status == "1") {
        this.rate == 20;
        this.text = this.i18nInfo.tips.connecting;
      } else if (status == 2 || status == "2") {
        this.checkNotificationSwitch();
        // 开启通知
        if (this.rate != 30) {
          this.rate == 30;
          this.text = this.i18nInfo.tips.connectionSucceeded;
          deviceDetailHelper.startNotify(this.address);
        }
      } else {
        if (this.notificationSwitch && !this.checkConnectStatus()) {
          this.bluetoothConnectStatus = 2;
        }
        this.loading = false;
        this.exit();
      }
    },

    // 写入数据
    write(key) {
      let params = {
        address: this.address,
        key: key,
      };
      this.$androidApi.write(params);
    },

    /**
     * 获取是否需要秘钥
     */
    getNeedSecretKey() {
      this.text = this.i18nInfo.tips.readSecretKey;
      this.rate = 50;
      if (!this.checkConnectStatus()) {
        return;
      }
      setTimeout(() => {
        deviceDetailHelper.writeData(
          this.address,
          instruct.NEED_SECRET_CONNECT_REQUEST
        );
      }, 500);
    },

    /**
     * 开启或关闭可连接
     */
    canConncetConfig() {
      // 开启弹窗
      Dialog.confirm({
        title: this.i18nInfo.title.configureConnectable,
        message: this.i18nInfo.tips.canConnectableTips,
        className: "warnDialogClass",
        cancelButtonText: this.$i18n.t("baseButton.cancel"),
        confirmButtonText: this.$i18n.t("baseButton.sure"),
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          if (!this.checkConnectStatus()) {
            return;
          }
          // 确认关闭
          deviceDetailHelper.writeData(
            this.address,
            instruct.NOT_CONNECTABLE_CONFIG_REQUEST,
            this.canConncetSwitch ? "0" : "1"
          );
        })
        .catch(() => {
          // 取消关闭
        });
    },

    /**
     * 回复出厂设置
     */
    restoreFactorySettings() {
      // 开启弹窗
      Dialog.confirm({
        title: this.i18nInfo.title.restoreFactorySettings,
        message: this.i18nInfo.tips.resetTips,
        className: "warnDialogClass",
        cancelButtonText: this.$i18n.t("baseButton.cancel"),
        confirmButtonText: this.$i18n.t("baseButton.sure"),
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          if (!this.checkConnectStatus()) {
            return;
          }
          // 确认回复
          deviceDetailHelper.writeData(
            this.address,
            instruct.RESTORE_FACTORY_SETTINGS_REQUEST
          );
        })
        .catch(() => {
          // 取消回复
        });
    },

    /**
     * 关机
     */
    shoudown() {
      // 开启弹窗
      Dialog.confirm({
        title: this.i18nInfo.button.shutdown,
        message: this.i18nInfo.tips.shutdownTips,
        className: "warnDialogClass",
        cancelButtonText: this.$i18n.t("baseButton.cancel"),
        confirmButtonText: this.$i18n.t("baseButton.sure"),
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          if (!this.checkConnectStatus()) {
            return;
          }
          this.functionLoading = true;
          // 确认关机
          deviceDetailHelper.writeData(this.address, instruct.SHUTDOWN_REQUEST);
          setTimeout(() => {
            if (this.functionLoading) {
              this.functionLoading = false;
              this.operationResultTip(false);
            }
          }, 5000);
        })
        .catch(() => {
          // 取消关机
        });
    },

    /**
     * 移除秘钥
     */
    removeSecretKey() {
      // 开启弹窗
      Dialog.confirm({
        title: this.i18nInfo.button.removeSecretKey,
        message: this.i18nInfo.tips.removeSecretKeyTips,
        className: "warnDialogClass",
        cancelButtonText: this.$i18n.t("baseButton.cancel"),
        confirmButtonText: this.$i18n.t("baseButton.sure"),
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          if (!this.checkConnectStatus()) {
            return;
          }
          // 确认移除
          deviceDetailHelper.writeData(
            this.address,
            instruct.REMOVE_SECRET_KEY_REQUEST
          );
        })
        .catch(() => {
          // 取消移除
        });
    },

    /**
     * 打开操作秘钥弹窗
     */
    openSecretKeyDialog() {
      this.secretKeyDialog = true;
      setTimeout(() => {
        if (this.secretKeyCache) {
          this.secretKey = this.secretKeyCache;
        } else {
          this.secretKey = "";
        }
        this.secretKeyOperationType = "update";
      }, 50);
    },

    /**
     * 打开弹窗 进行秘钥验证
     */
    checkSecretKeyDialog() {
      this.secretKeyDialog = true;
      setTimeout(() => {
        if (this.secretKeyCache) {
          this.secretKey = this.secretKeyCache;
        } else {
          this.secretKey = "";
        }
        this.secretKeyOperationType = "check";
      }, 50);
    },

    /**
     * 修改防篡改秘钥
     */
    secretKeyDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (!this.secretKey || this.secretKey.length != 6) {
          Notify({
            type: "warning",
            message: this.$i18n.t("notifyMessage.base.paramsError"),
          });
          done(false);
          return;
        }
        // 秘钥校验
        if (this.secretKeyOperationType == "check") {
          if (!this.checkConnectStatus()) {
            return;
          }
          deviceDetailHelper.writeData(
            this.address,
            instruct.CHECK_SECRET_REQUEST,
            this.secretKey
          );
        } else {
          if (!this.checkConnectStatus()) {
            return;
          }
          deviceDetailHelper.writeData(
            this.address,
            instruct.UPDATE_SECRET_KEY_REQUEST,
            this.secretKey
          );
        }
      } else {
        if (this.secretKeyOperationType == "check") {
          this.bluetoothConnectStatus = 2;
          this.exit();
        }
      }
      done();
    },

    tiggeredResponseDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (
          this.keyTiggeredResponseTime <= 0 ||
          this.keyTiggeredResponseTime > 10
        ) {
          Notify({ type: "warning", message: "Response Time 1~10" });
          done(false);
          return;
        }
        if (!this.checkConnectStatus()) {
          return;
        }

        deviceDetailHelper.writeData(
          this.address,
          instruct.TRIGGER_RESPONSE_TIME_CONFIG_REQUEST,
          this.keyTiggeredResponseTime
        );
      } else {
        // 取消操作
        this.keyTiggeredResponseTime =
          this.deviceInfo.factoryVersionInfo.keyTiggeredResponseTime;
      }
      done();
    },

    keyTiggeredResponseTimeInput() {
      if (this.keyTiggeredResponseTime > 60) {
        this.keyTiggeredResponseTime = 60;
      }
    },

    /**
     * 读取通道信息
     */
    readFactoryInfo() {
      this.rate = 55;
      let interval = setInterval(() => {
        this.rate = this.rate + 5;
      }, 500);
      // 虚假百分比 自动停止
      setTimeout(() => {
        clearInterval(interval);
      }, 1500);
      if (!this.checkConnectStatus()) {
        return;
      }
      deviceDetailHelper.writeData(
        this.address,
        instruct.READ_FACTORY_VERSION_INFO_REQUEST
      );
    },

    /**
     * 处理出厂信息
     */
    handleFactoryInfo(res) {
      console.log("准备处理出厂信息");
      return new Promise((resolve, reject) => {
        if (!res || res.code == 1) {
          // 出厂信息读取失败
          console.log("出厂信息读取失败");
        } else {
          // 赋值出厂信息
          console.log(res.data);
        }
        resolve();
      });
    },

    /**
     * 读取特征信息
     */
    readFeatureInfo() {
      this.text = this.i18nInfo.tips.readCharacteristicInformation;
      let interval = setInterval(() => {
        this.rate = this.rate + 5;
      }, 300);
      // 虚假百分比 自动停止
      setTimeout(() => {
        clearInterval(interval);
      }, 900);
      if (!this.checkConnectStatus()) {
        return;
      }
      deviceDetailHelper.writeData(
        this.address,
        instruct.READ_FEATURE_INFO_REQUEST
      );
    },

    /**
     * 读取通道信息
     */
    readConfigAgreementInfo() {
      this.text = this.i18nInfo.tips.readChannelInformation;
      let num = 0;
      if (!this.checkConnectStatus()) {
        return;
      }
      let interval = setInterval(() => {
        if (num < 6) {
          deviceDetailHelper.writeData(
            this.address,
            instruct.QUERY_CONFIG_AGREEMENT_REQUEST,
            num.toString()
          );
          num++;
          if (this.rate >= 90) {
            this.rate = 90;
          } else {
            this.rate = this.rate + 3;
          }
        } else {
          clearInterval(interval);
          interval = null;
        }
      }, 100);
    },

    /**
     * 更新协议信息
     * @param {结果} result
     */
    updateAgreementInfo(result) {
      if (result.code == 0) {
        this.deviceInfo.address = result.data.address;
        this.deviceInfo.agreementInfoList = result.data.agreementInfoList;
        this.deviceInfo.connectState = result.data.connectState;
        this.deviceInfo.connectable = result.data.connectable;

        this.$storage.supportACC = result.data.supportACC;

        if (result.data.factoryVersionInfo) {
          this.deviceInfo.factoryVersionInfo = {
            firmwareVersion: result.data.factoryVersionInfo.firmwareVersion,
            hardwareVersion: result.data.factoryVersionInfo.hardwareVersion,
            mac: result.data.factoryVersionInfo.mac,
            manufactor: result.data.factoryVersionInfo.manufactor,
            softwareVersion: result.data.factoryVersionInfo.sdkVersion,
            model: result.data.factoryVersionInfo.model,
            keyTiggeredResponseTime: result.data.factoryVersionInfo.checkTime,
          };
          this.keyTiggeredResponseTime =
            result.data.factoryVersionInfo.checkTime;
        }
      } else {
        this.exit();
      }
    },

    /**
     * 通知改变处理
     */
    notifyChangeHandle(data) {
      console.log("通知改变处理");
      let result = data.data;
      switch (data.type) {
        case instruct.NEED_SECRET_CONNECT_RESULT:
          if (result && result.data) {
            this.$storage.needSecretKey = true;
            this.needSecretKey = true;
            // 需要秘钥 进行弹窗输入秘钥
            this.checkSecretKeyDialog();
          } else {
            this.$storage.needSecretKey = false;
            this.needSecretKey = false;
            // 读取出厂信息
            this.readFactoryInfo();
          }
          break;
        case instruct.CHECK_SECRET_RESULT:
          // 秘钥校验结果
          if (result.code == 0) {
            this.secretKeyDialog = false;
            // 读取出厂信息
            this.readFactoryInfo();
          } else {
            Notify({
              type: "warning",
              message: this.i18nInfo.tips.secretCheckError,
            });
            setTimeout(() => {
              this.secretKeyDialog = true;
            }, 500);
          }
          break;
        case instruct.RESTORE_FACTORY_SETTINGS_RESULT:
          if (result.code == 0) {
            this.operationResultTip(true);
            this.exit();
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.READ_FACTORY_VERSION_INFO_RESPONSE:
          console.log("我得到了出厂信息的RESPONSE");
          break;
        case instruct.READ_FACTORY_VERSION_INFO_RESULT:
          // 处理出厂信息
          this.handleFactoryInfo(result)
            .then(() => {
              this.readConfigAgreementInfo();
            })
            .catch(() => {});
          break;
        case instruct.SHUTDOWN_RESPONSE:
          console.log("收到关机响应回复");
          break;
        case instruct.SHUTDOWN_RESULT:
          console.log("收到关机回复");
          if (this.functionLoading) {
            this.functionLoading = false;
          }
          if (result.code == 0) {
            this.operationResultTip(true);
            this.exit();
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.REMOVE_SECRET_KEY_RESULT:
          // 收到秘钥响应
          if (result.code == 0) {
            this.needSecretKey = false;
            this.$storage.needSecretKey = false;
            this.operationResultTip(true);
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.UPDATE_SECRET_KEY_RESULT:
          // 收到秘钥响应
          if (result.code == 0) {
            this.needSecretKey = true;
            this.$storage.needSecretKey = true;
            // 获取秘钥
            this.$androidApi.getConnectSecretKey().then((res) => {
              if (res) {
                this.secretKeyCache = res;
              }
            });
            this.operationResultTip(true);
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.QUERY_CONFIG_AGREEMENT_RESULT:
          this.loading = false;
          this.updateAgreementInfo(result);
          break;
        case instruct.NOT_CONNECTABLE_CONFIG_RESULT:
          if (result.code == 0) {
            this.canConncetSwitch = !this.canConncetSwitch;
            this.$storage.canConncetSwitch = this.canConncetSwitch;
            this.operationResultTip(true);
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.TRIGGER_RESPONSE_TIME_CONFIG_RESULT:
          if (result.code == 0) {
            this.deviceInfo.factoryVersionInfo.keyTiggeredResponseTime =
              this.keyTiggeredResponseTime;
            this.operationResultTip(true);
          } else {
            this.operationResultTip(false);
          }
          break;
        case instruct.RESTART_BEACON_RESULT:
          console.log("收到重启信标回复：" + result.code);
          this.exit();
          break;
        default:
          break;
      }
    },

    /**
     * operationResultTip
     * @param {结果} result
     * @param {消息} message
     */
    operationResultTip(result, message) {
      if (result) {
        Notify({
          type: "success",
          message: message
            ? message
            : this.$i18n.t("notifyMessage.base.operationSuccess"),
        });
      } else {
        Notify({
          type: "warning",
          message: message
            ? message
            : this.$i18n.t("notifyMessage.base.operationError"),
        });
      }
    },

    checkNotificationSwitch() {
      // 3秒后校验通知开关
      setTimeout(() => {
        if (!this.notificationSwitch) {
          console.log("通知校验失败，退出");
          // 连接失败
          this.exit();
        }
      }, 5000);
    },

    /**
     *
     * @returns 校验蓝牙
     */
    checkConnectStatus() {
      if (this.bluetoothConnectStatus != 2) {
        Notify({
          type: "warning",
          message: this.$i18n.t("notifyMessage.base.bluetoothDisconnected"),
        });
        return false;
      }
      return true;
    },

    exit() {
      if (this.bluetoothConnectStatus == 2) {
        this.restartDevice();
      }
      // 取消连接设备
      setTimeout(() => {
        deviceDetailHelper.cancelConnectDevice(this.address);
      }, 10);

      setTimeout(() => {
        this.$router.replace("/home");
      }, 15);
    },

    restartDevice() {
      deviceDetailHelper.writeData(
        this.address,
        instruct.RESTART_BEACON_REQUEST
      );
    },
  },
};
