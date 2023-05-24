export default {
  // 页面标题
  pageTitle: {
    deviceDetail: "Detail",
    configureChannel: "Configure Channel",
    batchConfigureChannel: "Batch Config Channel",
    addChannel: "Add Channel",
  },

  notifyMessage: {
    searchPlaceholder: "Please enter search keywords",
    base: {
      dataEmpty: "No Data",
      paramsError: "parameter error",
      lengthError: "Length Error",
      operationSuccess: "Operation successful",
      operationError: "Operation failed",
      notResponding: "Not responding",
      bluetoothDisconnected: "Bluetooth disconnected",
    },
    connect: {
      connectSuccess: "Successfully connected",
      connectError: "connect failed",
    },
    scan: {
      startSuccess: "Scan enabled successfully",
    },
  },

  baseButton: {
    add: "add",
    remove: "remove",
    update: "update",
    save: "save",
    sure: "sure",
    cancel: "cancel",
  },

  home: {
    button: {
      sorted: "sort",
      filter: "filterate",
      language: "language",
      batch: "batch",
      batchConfigChannel: "configuration channels",
      batchConfigSecretKey: "configuration keys",
      allDevice: "All devices",
      thisBrandDevice: "This brand of equipment",
      refresh: "refresh",
    },

    tips: {
      deviceCanNotConnect: "The current device cannot be connected",
      newSecretDialogPlaceholder: "New key is 6 characters long",
      oldSecretDialogPlaceholder: "Old key is 6 characters long",
    },
    broadcastInterval: "broadcast interval",
    calibrationDistance: "Calibration distance",
    battery: "battery",

    title: {
      batchConfigSecretKey: "Configure keys in batches",
    },

    sort: {
      rssiRise: "Rssi rise",
      rssiFall: "Rssi fall",
      macRise: "Mac rise",
      macFall: "Mac fall",
      batteryRise: "battery rise",
      batteryFall: "battery fall",
      sorted: "sort",
    },

    lable: {
      deviceFilter: "Equipment filtering",
      selectLanguage: "Select Language",
    },
    scannedCount: "number of devices scanned：",
    individua: " individua",
    triggerConditions: "Trigger conditions：",
    // 批量模式操作
    batchModelNotAllowedClick: "Batch mode, cannot be used",

    language: "Chinese",
  },

  // 设备
  device: {
    // 设备详情
    detail: {
      title: {
        // 基础信息
        basicInformation: "Basic information",
        // 特件信息
        specialInformation: "property information",
        configureConnectable: "Configure connectable",
        // 恢复出厂设置
        restoreFactorySettings: "reset",
      },
      // 标签
      lable: {
        model: "model",
        softwareVersion: "SDK version",
        hardwareVersion: "Hardware version",
        firmwareVersion: "Firmware version",

        numberOfChannels: "channel number",
        supportPower: "Support power",
        supportData: "supporting data",
      },
      // 按钮
      button: {
        // 可连接
        canConncet: "Connected·",
        open: "open",
        close: "close",
        // 恢复出厂设置
        restoreFactorySettings: "reset",
        // 关机
        shutdown: "shutdown",
        // 配置通道
        configureChannel: "configure channel",
        // 移除秘钥
        removeSecretKey: "remove key",
        // 修改秘钥
        updateSecretKey: "update key",
        // 添加秘钥
        addSecretKey: "add key",
        // 修改设备名称
        modifyDeviceName: "modify name",
        // 触发响应时间
        triggerResponseTime: "trigger response time",
      },
      function: {
        inputSecretKey: "Enter tamper proof key",
        secretDialogPlaceholder: "The key is 6 characters long",
        keyTiggeredResponseTime: "Key triggered response time",
      },
      tips: {
        secretCheckError: "Key verification failed",
        removeSecretKeyTips:
          "After removing the key, there will be no need to verify the tamper proof key during connection.",
        canConnectableTips:
          "After the connectable switch is turned off, the next connection needs to be activated by pressing the button to connect.",
        resetTips: "Reset the device to factory settings？",
        shutdownTips:
          "Please make sure that the device has a button. After it is turned off, it needs to be pressed to turn it on again.",
        readCharacteristicInformation: "characteristic information...",
        readChannelInformation: "channel information...",
        connecting: "connecting...",
        connectionSucceeded: "connection succeeded",
        readSecretKey: "read key",
      },
    },
    // 通道
    channel: {
      channel: "channel",
      channelNumber: "Channel Number",
      // 帧类型
      frameType: "Frame Type",
      // 广播类型
      broadcastType: "Broadcast Type",

      title: {
        deleteChannel: "delete channel",
      },

      button: {
        retract: "retract",
        expand: "expand",
      },

      common: {
        calibrationDistance: "Calibration distance",
        broadcastPower: "Broadcast power",
        broadcastTime: "Broadcast time",
        triggerCondition: "Trigger Mode",
        broadcastContent: "Broadcast content",
        broadcastInterval: "broadcast interval",
        basicParams: "basic parameters",
        triggerParams: "Trigger parameters",
        alwaysBroadcast: "Always broadcast",
        triggerBroadcast: "Trigger broadcast",
      },
      // 配置通道
      configChannel: {
        channelConfigured: "Configured channel：",
      },
      addChannel: {
        alwaysBroadcast: "Always broadcast",
        uuidContent: "32 bits in length, including0-9/a-f/A-F",
        namespaceContent: "20 bits in length, including0-9/a-f/A-F",
        instanceContent: "12 bits in length, including0-9/a-f/A-F",
        hwidContent: "10 bits in length, including0-9/a-f/A-F",
        vendorKeyContent: "8 bits in length, including0-9/a-f/A-F",
        lotKeyContent: "16 bits in length, including0-9/a-f/A-F",
        urlContent: "Please enter the website address",
        trigger: "trigger",
        triggerConditions: "Trigger conditions",
        broadcastTime: "Broadcast time",
      },
      tips: {
        uuidFormatError: "UUID format error",
        majorFormatError: "Major format error",
        minorFormatError: "Minor format error",
        deviceNameFormatError: "DeviceName format error",
        namespaceFormatError: "NamespaceID format error",
        instanceFormatError: "InstanceID format error",
        urlFormatError: "URL format error",
        hwidFormatError: "Hwid format error",
        vendorKeyFormatError: "Vendor_Key format error",
        lotKeyFormatError: "Lot_Key format error",
        quuppaTagFormatError: "Quuppa Tag format error",
        pleaseSelect: "Please select",
        deleteChannelTips:
          "Channels cannot be recovered after deletion. Are you sure to delete?",
      },
    },

    // 批量配置通道
    batchConfigChannel: {
      button: {
        errorList: "Failure Details",
        retract: "retract",
        expand: "expand",
      },
      lable: {
        addChannel: "Add Channel",
        cancelConfig: "Cancel",
        configCahnnel: "Configure",
        configResult: "Configuration Results",
        triggerBroadcast: "Trigger broadcast",
        alwaysBroadcast: "Always broadcast",
        channelToBeConfigured: "Channel to be configured",
      },
      message: {
        configuration: "In configuration",
        addChannelFirst: "Please add a channel first",
        individua: "individual",
      },
    },
  },
};
