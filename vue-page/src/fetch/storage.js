import i18n from "@/components/i18n";

export default {
  /**
   * 通道信息 通道列表里面赋值
   */
  agreementInfoList: null,

  language: "简体中文",

  // 触发条件列表
  triggerActions: [
    { name: i18n.t("baseButton.doubleClick"), code: 1 },
    { name: i18n.t("baseButton.tripleClick"), code: 2 },
    { name: i18n.t("baseButton.acceleration"), code: 3 },
  ],
  // 通道号列表
  channelNumberActions: [
    { name: 0 },
    { name: 1 },
    { name: 2 },
    { name: 3 },
    { name: 4 },
    { name: 5 },
  ],

  // 帧类型列表
  frameTypes: [
    {
      code: 1,
      name: "iBeacon",
      disabled: false,
    },
    {
      code: 2,
      name: "UID",
      disabled: false,
    },
    {
      code: 3,
      name: "URL",
      disabled: false,
    },
    {
      code: 4,
      name: "TLM",
      disabled: false,
    },
    {
      code: 5,
      name: "ACC",
      disabled: false,
    },
    {
      code: 6,
      name: "DeviceInfo",
      disabled: false,
    },
    {
      code: 7,
      name: "LINE",
      disabled: false,
    },
    {
      code: 8,
      name: "Coreaiot",
      disabled: false,
    },
    {
      code: 9,
      name: "Quuppa",
      disabled: false,
    },
  ],

  // 连接是否需要秘钥 连接成功存储
  needSecretKey: false,
  // 可连接开关
  canConncetSwitch: true,
  // 是否支持加速度
  supportACC: true,

  // 批量配置模块
  toBeConfiguredList: [],
  // 批量配置通道 配置通道列表
  toBeConfiguredChannelList: [],
  // 批量配置信息
  batchConfigChannelInfo: {
    batchConfigChannelFlag: false,
    secretKey: "",
  },

  /**
   * 添加配置通道
   */
  addConfigurableChannel(item) {
    if (!item) {
      return;
    }
    if (!this.toBeConfiguredChannelList) {
      this.toBeConfiguredChannelList = [];
    }

    this.toBeConfiguredChannelList.push(item);
  },

  /**
   * 根据通道号获取协议信息
   */
  getAgreementInfoByNumber(channelNum) {
    if (
      !channelNum ||
      !this.agreementInfoList ||
      this.agreementInfoList.length == 0
    ) {
      return null;
    }

    let channel = null;

    for (let i = 0; i < this.agreementInfoList.length; i++) {
      const agreementInfo = this.agreementInfoList[i];
      if (agreementInfo.channelNumber == channelNum) {
        channel = agreementInfo;
        break;
      }
    }
    return channel;
  },
};
