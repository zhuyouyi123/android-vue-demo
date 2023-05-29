export default {
  // 页面标题
  pageTitle: {
    deviceDetail: "设备详情",
    configureChannel: "配置通道",
    batchConfigureChannel: "批量配置通道",
    addChannel: "添加通道",
  },

  notifyMessage: {
    searchPlaceholder: "请输入搜索关键词",
    base: {
      dataEmpty: "暂无数据",
      paramsError: "参数错误",
      lengthError: "长度错误",
      operationSuccess: "操作成功",
      operationError: "操作失败",
      notResponding: "未响应",
      bluetoothDisconnected: "蓝牙已断开",
    },
    connect: {
      connectSuccess: "连接成功",
      connectError: "连接失败",
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
    sure: "确认",
    cancel: "取消",
    acceleration: "加速度",
    doubleClick: "双击按钮",
    tripleClick: "三击按钮",
  },

  home: {
    button: {
      sorted: "排序",
      filter: "过滤",
      language: "语言",
      batch: "批量",
      batchConfigChannel: "批量配置通道",
      batchConfigSecretKey: "批量配置秘钥",
      allDevice: "全部设备",
      thisBrandDevice: "此品牌设备",
      refresh: "刷新",
      errorList: "失败明细",
    },

    tips: {
      deviceCanNotConnect: "当前设备不可连接",
      newSecretDialogPlaceholder: "新密钥为6个字符",
      oldSecretDialogPlaceholder: "旧密钥为6个字符",
      secretDialogPlaceholder: "密钥为6个字符",
      configuration: "配置中",
      secretKeySameTips: "旧秘钥不能与新秘钥相同",
    },
    broadcastInterval: "广播间隔",
    calibrationDistance: "校准距离",
    battery: "电量",

    title: {
      batchConfigSecretKey: "批量配置秘钥",
      updateResult: "修改结果",
      batchRecord: "批量记录",
      secretKeySetting: "填写秘钥",
    },

    sort: {
      rssiRise: "Rssi升序",
      rssiFall: "Rssi降序",
      macRise: "Mac降序",
      macFall: "Mac升序",
      batteryRise: "电量降序",
      batteryFall: "电量升序",
      sorted: "排序",
    },

    lable: {
      deviceFilter: "设备过滤",
      selectLanguage: "选择语言",
      errorReason: "失败原因",
    },
    scannedCount: "扫描到设备数量：",
    individua: "个",
    triggerConditions: "触发条件：",
    // 批量模式操作
    batchModelNotAllowedClick: "批量模式，无法使用",

    language: "中文",
  },

  // 设备
  device: {
    // 设备详情
    detail: {
      title: {
        // 基础信息
        basicInformation: "基础信息",
        // 特件信息
        specialInformation: "特性信息",
        configureConnectable: "配置可连接",
        // 恢复出厂设置
        restoreFactorySettings: "恢复出厂设置",
      },
      // 标签
      lable: {
        model: "型号",
        softwareVersion: "SDK版本",
        hardwareVersion: "硬件版本",
        firmwareVersion: "固件版本",

        numberOfChannels: "通道数量",
        supportPower: "支持功率",
        supportData: "支持数据",
      },
      // 按钮
      button: {
        // 可连接
        canConncet: "可连接·",
        open: "开",
        close: "关",
        // 恢复出厂设置
        restoreFactorySettings: "恢复出厂设置",
        // 关机
        shutdown: "关机",
        // 配置通道
        configureChannel: "配置通道",
        // 移除秘钥
        removeSecretKey: "移除秘钥",
        // 修改秘钥
        updateSecretKey: "修改秘钥",
        // 添加秘钥
        addSecretKey: "添加秘钥",
        // 修改设备名称
        modifyDeviceName: "修改设备名称",
        // 触发响应时间
        triggerResponseTime: "触发响应时间",
      },
      function: {
        inputSecretKey: "输入防篡改密钥",
        secretDialogPlaceholder: "密钥为6个字符",
        keyTiggeredResponseTime: "按键触发响应时间",
      },
      tips: {
        secretCheckError: "秘钥校验失败",
        removeSecretKeyTips: "移除密钥后，连接时将不在需要验证防篡改密钥。",
        canConnectableTips:
          "可连接开关关闭后，下次连接时需要通过按键激活方可连接。",
        resetTips: "将该设备恢复出厂设置？",
        shutdownTips: "请确认此设备有按键，关机后需要通过按键才能再次开机。",
        readCharacteristicInformation: "读取特征信息...",
        readChannelInformation: "读取通道信息...",
        connecting: "连接中...",
        connectionSucceeded: "连接成功",
        readSecretKey: "读取秘钥",
      },
    },
    // 通道
    channel: {
      channel: "通道",
      channelNumber: "通道号",
      // 帧类型
      frameType: "帧类型",
      // 广播类型
      broadcastType: "广播类型",

      title: {
        deleteChannel: "删除通道",
      },

      button: {
        retract: "收起",
        expand: "展开",
      },

      common: {
        calibrationDistance: "校准距离",
        broadcastPower: "广播功率",
        broadcastTime: "广播时间",
        triggerCondition: "触发方式",
        broadcastContent: "广播内容",
        broadcastInterval: "广播间隔",
        basicParams: "基础参数",
        triggerParams: "触发器参数",
        alwaysBroadcast: "始终广播",
        triggerBroadcast: "触发广播",
      },
      // 配置通道
      configChannel: {
        channelConfigured: "已配置通道：",
      },
      addChannel: {
        alwaysBroadcast: "始终广播",
        uuidContent: "长度32位,包含0-9/a-f/A-F",
        namespaceContent: "长度20位,包含0-9/a-f/A-F",
        instanceContent: "长度12位,包含0-9/a-f/A-F",
        hwidContent: "长度10位,包含0-9/a-f/A-F",
        vendorKeyContent: "长度8位,包含0-9/a-f/A-F",
        lotKeyContent: "长度16位,包含0-9/a-f/A-F",
        urlContent: "请输入网址",
        trigger: "触发器",
        triggerConditions: "触发条件",
        broadcastTime: "广播时间",
      },
      tips: {
        uuidFormatError: "UUID格式错误",
        majorFormatError: "Major格式错误",
        minorFormatError: "Minor格式错误",
        deviceNameFormatError: "DeviceName格式错误",
        namespaceFormatError: "NamespaceID格式错误",
        instanceFormatError: "InstanceID格式错误",
        urlFormatError: "URL格式错误",
        hwidFormatError: "Hwid格式错误",
        vendorKeyFormatError: "Vendor_Key格式错误",
        lotKeyFormatError: "Lot_Key格式错误",
        quuppaTagFormatError: "Quuppa Tag格式错误",
        pleaseSelect: "请选择",
        deleteChannelTips: "通道删除后不可恢复，确认删除吗？",
      },
    },

    // 批量配置通道
    batchConfigChannel: {
      button: {
        errorList: "失败明细",
        retract: "收起",
        expand: "展开",
      },
      lable: {
        addChannel: "添加通道",
        cancelConfig: "取消配置",
        configCahnnel: "配置通道",
        configResult: "配置结果",
        triggerBroadcast: "触发广播",
        alwaysBroadcast: "始终广播",
        channelToBeConfigured: "待配置通道",
      },
      message: {
        configuration: "配置中",
        addChannelFirst: "请先添加通道",
        individua: "个",
        notOnlyExistCoreaiot: "设备不能只存在Coreaiot协议",
        atLeastOneAlwaysBroadcast: "至少有一条通道开启始终广播",
        secretKeySetting: "填写秘钥",
        secretDialogPlaceholder: "密钥为6个字符",
      },
    },
  },
};
