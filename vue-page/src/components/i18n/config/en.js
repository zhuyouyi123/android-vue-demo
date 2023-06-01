export default {
  // 页面标题
  pageTitle: {
    deviceDetail: "Device details",
    configureChannel: "Configure Channels",
    batchConfigureChannel: "Batch configuration channels",
    addChannel: "Add Channel",
  },

  notifyMessage: {
    searchPlaceholder: "Please enter search keywords",
    base: {
      dataEmpty: "No data yet",
      paramsError: "Parameter error",
      lengthError: "Length error",
      operationSuccess: "Operation successful",
      operationError: "Operation failed",
      notResponding: "Not responding",
      bluetoothDisconnected: "Bluetooth disconnected",
      retry: "retry",
    },
    connect: {
      connectSuccess: "Successfully connected",
      connectError: "Connection failed",
    },
    scan: {
      startSuccess: "Scan enabled successfully",
    },
  },

  baseButton: {
    add: "Add",
    remove: "Delete",
    update: "Modify",
    save: "Save",
    sure: "Confirm",
    cancel: "Cancel",
    acceleration: "ACC",
    doubleClick: "Double click the button",
    tripleClick: "Three click button",
  },

  home: {
    button: {
      sorted: "Sort",
      filter: "Filtering",
      language: "Language",
      batch: "Bulk",
      batchConfigChannel: "Batch configuration channels",
      batchConfigSecretKey: "Batch configuration key",
      allDevice: "All devices",
      thisBrandDevice: "This brand of equipment",
      refresh: "Refresh",
      errorList: "Failure details",
      selectAll: "select all",
    },

    tips: {
      deviceCanNotConnect: "The current device is not connectable",
      newSecretDialogPlaceholder: "New key is 6 characters long",
      oldSecretDialogPlaceholder: "Old key is 6 characters long",
      secretDialogPlaceholder: "key is 6 characters long",
      configuration: "configuration",
      secretKeySameTips: "keys cannot be the same",
      success: "success：",
      failed: "fail：",
    },
    broadcastInterval: "Broadcast interval",
    calibrationDistance: "Calibration distance",
    battery: "Electricity",

    title: {
      batchConfigSecretKey: "Configure keys in batches",
      updateResult: "Modify Results",
      batchRecord: "Batch recording",
      secretKeySetting: "Fill in the secret key",
      configurationQuantity: "Configuration Quantity：",
    },

    sort: {
      rssiRise: "Rssi Ascending",
      rssiFall: "Rssi Descending",
      macRise: "Mac Ascending",
      macFall: "Mac Descending",
      batteryRise: "Power Ascending Order",
      batteryFall: "Power Descending Order",
      sorted: "Sort",
    },

    lable: {
      deviceFilter: "Equipment filtering",
      selectLanguage: "Select Language",
      errorReason: "Reason for failure ",
    },
    scannedCount: "Number of devices scanned：",
    individua: " individua",
    triggerConditions: "triggered：",
    // 批量模式操作
    batchModelNotAllowedClick: "Batch mode cannot be used",

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
        specialInformation: "Characteristic information",
        configureConnectable: "Configure connectable",
        // 恢复出厂设置
        restoreFactorySettings: "Restore factory settings",
      },
      // 标签
      lable: {
        model: "model",
        softwareVersion: "SDK version",
        hardwareVersion: "Hardware version",
        firmwareVersion: "Firmware version",

        numberOfChannels: "Number of channels",
        supportPower: "Support power",
        supportData: "supporting agreement",
      },
      // 按钮
      button: {
        // 可连接
        canConncet: "Connected·",
        open: "open",
        close: "close",
        // 恢复出厂设置
        restoreFactorySettings: "Restore factory settings",
        // 关机
        shutdown: "Shutdown",
        // 配置通道
        configureChannel: "Configure Channels",
        // 移除秘钥
        removeSecretKey: "Remove Key",
        // 修改秘钥
        updateSecretKey: "Modify Secret Key",
        // 添加秘钥
        addSecretKey: "Add Secret Key",
        // 修改设备名称
        modifyDeviceName: "Modify device name",
        // 触发响应时间
        triggerResponseTime: "Trigger response time",
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
        resetTips: "Restore the device to its factory settings? ",
        shutdownTips:
          "Please confirm that this device has a button and can only be turned on again after shutting down.",
        readCharacteristicInformation: "Read feature information...",
        readChannelInformation: "Read channel information...",
        connecting: "Connecting...",
        connectionSucceeded: "Successfully connected",
        readSecretKey: "Read Secret Key",
      },
    },
    // 通道
    channel: {
      channel: "channel",
      channelNumber: "Channel number",
      // 帧类型
      frameType: "Frame Type",
      // 广播类型
      broadcastType: "Broadcast Type",

      title: {
        deleteChannel: "Delete channel",
      },

      button: {
        retract: "retract",
        expand: "expand",
      },

      common: {
        calibrationDistance: "Calibration distance",
        broadcastPower: "Broadcast power",
        broadcastTime: "Broadcast time",
        triggerCondition: "Trigger method",
        broadcastContent: "Broadcast content",
        broadcastInterval: "Broadcast interval",
        basicParams: "Basic parameters",
        triggerParams: "Trigger parameters",
        alwaysBroadcast: "Always broadcast",
        triggerBroadcast: "Trigger broadcast",
      },
      // 配置通道
      configChannel: {
        channelConfigured: "Channel configured：",
      },
      addChannel: {
        alwaysBroadcast: "Always broadcast",
        uuidContent: "32 bits in length, including 0-9/A-F",
        namespaceContent: "20 bits in length, including0-9/A-F",
        instanceContent: "Length 12 bits, including 0-9/A-F",
        hwidContent: "10 digits in length, including 0-9/a-f/A-F",
        vendorKeyContent: "Length 8 bits, including 0-9/a-f/A-F",
        deviceNameContent: "Length 6 bits, including 0-9/a-f/A-F",
        lotKeyContent: "16 bits in length, including 0-9/a-f/A-F",
        urlContent: "Please enter the website address",
        trigger: "trigger",
        triggerConditions: "Trigger conditions",
        broadcastTime: "Broadcast time",
        broadcastChannel: "broadcast channel",
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
        expand: "open",
      },
      lable: {
        addChannel: "Add Channel",
        cancelConfig: "Cancel configuration",
        configCahnnel: "Configure Channels",
        configResult: "Configuration Results",
        triggerBroadcast: "Trigger broadcast",
        alwaysBroadcast: "Always broadcast",
        channelToBeConfigured: "Channel to be configured",
      },
      message: {
        configuration: "In configuration",
        addChannelFirst: "Please add a channel first",
        individua: "individual",
        notOnlyExistCoreaiot:
          "The device cannot only have the Coreaiot protocol",
        atLeastOneAlwaysBroadcast:
          "Always broadcast when at least one channel is open",
        secretKeySetting: "Fill in the secret key",
        secretDialogPlaceholder: "key is 6 characters long",
      },
    },
  },
};
