package com.ble.blescansdk.ble.analysis;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.entity.seek.StandardThoroughfareInfo;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AnalysisSeekStandardBeaconHandle extends AbstractDeviceAnalysis<SeekStandardDevice> {

    private static AnalysisSeekStandardBeaconHandle instance = null;

    private static final ConcurrentMap<String, Map<String, StandardThoroughfareInfo>> thoroughfareMap = new ConcurrentHashMap<>();

    public static AnalysisSeekStandardBeaconHandle getInstance() {
        if (instance == null) {
            return instance = new AnalysisSeekStandardBeaconHandle();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public SeekStandardDevice analysis(SeekStandardDevice seekStandardDevice, byte[] scanBytes, BluetoothDevice device, boolean isConnectable, int rssi) {

        seekStandardDevice = preHandle(seekStandardDevice, device, isConnectable, rssi);

        return handle(scanBytes, seekStandardDevice, isConnectable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected SeekStandardDevice handle(byte[] scanBytes, SeekStandardDevice seekStandardDevice, boolean isConnectable) {

        ThoroughfareTypeEnum thoroughfareTypeEnum = getThoroughfareType(scanBytes, isConnectable);

        if (Objects.isNull(thoroughfareTypeEnum)) {
            return null;
        }

        StandardThoroughfareInfo thoroughfareInfo = seekStandardDevice.getStandardThoroughfareInfo();
        try {
            thoroughfareInfo = thoroughfareTypeEnum.analysis(scanBytes, thoroughfareInfo, isConnectable);
        } catch (Exception e) {
            BleLogUtil.e(seekStandardDevice.getAddress() + ":" + thoroughfareTypeEnum.getValue() + ": 协议解析失败:" + ProtocolUtil.byteArrToHexStr(scanBytes));
        }

        if (null == thoroughfareInfo) {
            return null;
        }

        final int nextInt = new Random().nextInt(5);

        if (nextInt == 3) {
            thoroughfareInfo.checkChannel(seekStandardDevice.getAddress());
        }

        thoroughfareInfo.setBeacons();
        thoroughfareInfo.setUids();
        thoroughfareInfo.setUrls();

        seekStandardDevice.setStandardThoroughfareInfo(thoroughfareInfo);

        seekStandardDevice.setBattery(thoroughfareInfo.getBattery());

        return seekStandardDevice;
    }

    /**
     * 获取通道类型
     *
     * @param scanBytes 扫描数据
     * @return 类型
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ThoroughfareTypeEnum getThoroughfareType(byte[] scanBytes, boolean isConnectable) {
        int startByte = isConnectable ? 5 : 2;
        // 通道类型
        String type = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 2);

        String childIde = ProtocolUtil.byteToHexStr(scanBytes[isConnectable ? 11 : 8]);

        return ThoroughfareTypeEnum.getByType(type, childIde);

    }


}
