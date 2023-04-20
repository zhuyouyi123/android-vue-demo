export default {
  // 页面标题
  pageTitle: {
    deviceDetail: "设备详情",
    configureChannel: "配置通道",
    addChannel: "添加通道",
  },

  notifyMessage: {
    searchPlaceholder: "请输入搜索关键词",
    base: {
      dataEmpty: "暂无数据",
    },
    connect: {
      connectSuccess: "连接成功",
      connectError: "连接失败"
    },
    scan: {
      startSuccess: "扫描开启成功",
    },
  },

  baseButton: {
    add: "添加",
    remove: "删除",
    update: "修改",
    save: "保存",
    cancel: "取消",
  },

  home: {
    button: {
      sorted: "排序",
      filter: "过滤",
      language: "语言",
      batch: "批量",
    },
    rssiRise: "Rssi升序",
    rssiFall: "Rssi降序",
    macRise: "Mac降序",
    macFall: "Mac升序",
    batteryRise: "电量降序",
    batteryFall: "电量升序",
    sorted: "排序",
    scannedCount: "扫描到设备数量：",
    individua: "个",
  },

  // 设备
  device: {
    // 设备详情
    detail: {
      title: {
        // 基础信息
        basicInformation: "基础信息",
        // 特件信息
        specialInformation: "特件信息",
      },
      // 标签
      lable: {
        model: "型号",
        softwareVersion: "软件版本",
        hardwareVersion: "硬件版本",
        firmwareVersion: "固件版本",

        numberOfChannels: "通道数量",
        supportPower: "支持功率",
        supportData: "支持数据",
      },
      // 按钮
      button: {
        // 可连接
        canConncet: "可连接",
        // 恢复出厂设置
        reset: "恢复出厂设置",
        // 关机
        shutdown: "关机",
        // 配置通道
        configureChannel: "配置通道",
        // 移除秘钥
        removeSecretKey: "移除秘钥",
        // 修改秘钥
        updateSecretKey: "修改秘钥",
        // 修改设备名称
        modifyDeviceName: "修改设备名称"
      }
    },
    // 通道
    channel: {
      channel: "通道",
      // 帧类型
      frameType: "帧类型",
      // 广播类型
      broadcastType: "广播类型",

      common: {
        calibrationDistance: "校准距离",
        broadcastPower: "广播功率",
        broadcastContent: "广播内容",
        broadcastInterval: "广播间隔",
        basicParams: "基础参数",
      },
      // 配置通道
      configChannel: {
        channelConfigured: "已配置通道：",
      },
      addChannel: {
        alwaysBroadcast: "始终广播",
        uuidContent: "长度32位,包含0-9/a-f/A-F",
        trigger: "触发器",
        triggerConditions: "触发条件",
        broadcastTime: "广播时间",

      },
    }
  }
}
