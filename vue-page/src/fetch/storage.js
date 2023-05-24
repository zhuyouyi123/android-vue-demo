export default {
  /**
   * 通道信息 通道列表里面赋值
   */
  agreementInfoList: null,

  language: "简体中文",

  // 触发条件列表
  triggerActions: [
    { name: "双击按钮", code: 1 },
    { name: "三击按钮", code: 2 },
    { name: "加速度", code: 3 },
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
    },
    {
      code: 2,
      name: "UID",
    },
    {
      code: 3,
      name: "URL",
    },
    {
      code: 4,
      name: "TLM",
    },
    {
      code: 5,
      name: "ACC",
    },
    {
      code: 6,
      name: "DeviceInfo",
    },
    {
      code: 7,
      name: "LINE",
    },
    {
      code: 8,
      name: "Coreaiot",
    },
    {
      code: 9,
      name: "Quuppa",
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
