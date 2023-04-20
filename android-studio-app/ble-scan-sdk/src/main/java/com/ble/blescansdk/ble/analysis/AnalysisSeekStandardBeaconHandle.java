package com.ble.blescansdk.ble.analysis;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.entity.seek.StandardThoroughfareInfo;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.Md5Util;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AnalysisSeekStandardBeaconHandle extends AbstractDeviceAnalysis<SeekStandardDevice> {

    private static AnalysisSeekStandardBeaconHandle instance = null;

    private static ConcurrentMap<String, Map<String, StandardThoroughfareInfo>> thoroughfareMap = new ConcurrentHashMap<>();

    public static AnalysisSeekStandardBeaconHandle getInstance() {
        if (instance == null) {
            return instance = new AnalysisSeekStandardBeaconHandle();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public SeekStandardDevice analysis(byte[] scanBytes, BluetoothDevice device, int rssi) {
        SeekStandardDevice seekStandardDevice = preHandle(new SeekStandardDevice(), device, rssi);
        return handle(scanBytes, seekStandardDevice);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected SeekStandardDevice handle(byte[] scanBytes, SeekStandardDevice seekStandardDevice) {
        String address = seekStandardDevice.getAddress();
        Map<String, StandardThoroughfareInfo> stringStringMap = thoroughfareMap.get(address);
        if (null == stringStringMap) {
            stringStringMap = new HashMap<>();
        }

        String byteArrToHexStr = ProtocolUtil.byteArrToHexStr(scanBytes);

        BleLogUtil.i(byteArrToHexStr);

        String md5 = Md5Util.md5(byteArrToHexStr);
        if (!stringStringMap.containsKey(md5)) {
            ThoroughfareTypeEnum thoroughfareTypeEnum = getThoroughfareType(scanBytes);
            if (null != thoroughfareTypeEnum) {
                StandardThoroughfareInfo analysis = thoroughfareTypeEnum.analysis(scanBytes);
                if (Objects.nonNull(analysis)) {
                    stringStringMap.put(md5, analysis);
                }
            }
        }

        thoroughfareMap.put(address, stringStringMap);

        if (!stringStringMap.isEmpty()){
            seekStandardDevice.setThoroughfares(new ArrayList<>(stringStringMap.values()));
        }

        return seekStandardDevice;
    }

    /**
     * 获取通道类型
     *
     * @param scanBytes 扫描数据
     * @return 类型
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ThoroughfareTypeEnum getThoroughfareType(byte[] scanBytes) {
        int startByte = 5;
        // 通道类型
        String type = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 2);

        String childIde = ProtocolUtil.byteToHexStr(scanBytes[11]);

        return ThoroughfareTypeEnum.getByType(type, childIde);

    }


}
