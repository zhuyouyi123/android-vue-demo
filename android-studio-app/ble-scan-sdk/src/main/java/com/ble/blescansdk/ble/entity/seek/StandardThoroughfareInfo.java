package com.ble.blescansdk.ble.entity.seek;

import com.ble.blescansdk.ble.entity.seek.thoroughfare.Acc;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.IBeacon;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Info;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Line;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Quuppa;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.TLM;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.UID;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.URL;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StandardThoroughfareInfo {

    private final ConcurrentMap<String, Map<String, Object>> objectMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ConcurrentMap<String, Long>> selfCheckMap = new ConcurrentHashMap<>();

    private int battery;

    private String deviceName;

    private List<Object> beacons;

    private List<Object> uids;

    private List<Object> urls;

    private TLM tlm;

    private Acc acc;

    private Line line;

    private Info info;

    private Quuppa quuppa;

    public int getBattery() {
        return battery;
    }

    public List<Object> getBeacons() {
        return beacons;
    }

    public StandardThoroughfareInfo setBattery(int battery) {
        if (battery > 100) {
            this.battery = 100;
        } else if (battery == 0) {
            this.battery = 1;
        } else {
            this.battery = battery;
        }
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public StandardThoroughfareInfo setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public void setBeacons() {
        Map<String, Object> stringObjectMap = objectMap.get(ThoroughfareTypeEnum.I_BEACON.getValue());
        if (null == stringObjectMap || stringObjectMap.isEmpty()) {
            this.beacons = Collections.emptyList();
        } else {
            this.beacons = new ArrayList<>(stringObjectMap.values());
        }
    }

    public StandardThoroughfareInfo addBeacon(IBeacon beacon) {
        String key = beacon.getUuid() + ProtocolUtil.byteArrToHexStr(ProtocolUtil.intToByteArrayTwo(beacon.getMajor())) + ProtocolUtil.byteArrToHexStr(ProtocolUtil.intToByteArrayTwo(beacon.getMinor()));
        String type = beacon.getType();
        Map<String, Object> stringObjectMap = objectMap.get(type);
        if (null == stringObjectMap) {
            stringObjectMap = new HashMap<>();
        }
        stringObjectMap.put(key, beacon);
        objectMap.put(type, stringObjectMap);
        addCheckSelfMap(beacon.getType(), key);
        return this;
    }


    public void setUids() {
        Map<String, Object> stringObjectMap = objectMap.get(ThoroughfareTypeEnum.EDDYSTONE_UID.getValue());
        if (null == stringObjectMap || stringObjectMap.isEmpty()) {
            this.uids = Collections.emptyList();
        } else {
            this.uids = new ArrayList<>(stringObjectMap.values());
        }
    }

    public StandardThoroughfareInfo addUid(UID uid) {
        String key = uid.getNamespaceId() + uid.getInstanceId();
        String type = uid.getType();
        Map<String, Object> stringObjectMap = objectMap.get(type);
        if (null == stringObjectMap) {
            stringObjectMap = new HashMap<>();
        }
        stringObjectMap.put(key, uid);
        objectMap.put(type, stringObjectMap);
        addCheckSelfMap(uid.getType(), key);
        return this;
    }


    public void setUrls() {
        Map<String, Object> stringObjectMap = objectMap.get(ThoroughfareTypeEnum.EDDYSTONE_URL.getValue());
        if (null == stringObjectMap || stringObjectMap.isEmpty()) {
            this.urls = Collections.emptyList();
        } else {
            this.urls = new ArrayList<>(stringObjectMap.values());
        }

    }


    public StandardThoroughfareInfo addUrl(URL url) {
        String key = url.getLink();
        String type = url.getType();
        Map<String, Object> stringObjectMap = objectMap.get(type);
        if (null == stringObjectMap) {
            stringObjectMap = new HashMap<>();
        }
        stringObjectMap.put(key, url);
        objectMap.put(type, stringObjectMap);
        addCheckSelfMap(url.getType(), key);
        return this;
    }


    public TLM getTlm() {
        return tlm;
    }

    public StandardThoroughfareInfo setTlm(TLM tlm) {
        this.tlm = tlm;
        addCheckSelfMap(tlm.getType(), tlm.getType());
        return this;
    }

    public Acc getAcc() {
        return acc;
    }

    public StandardThoroughfareInfo setAcc(Acc acc) {
        this.acc = acc;
        addCheckSelfMap(acc.getType(), acc.getType());
        return this;
    }

    public Line getLine() {
        return line;
    }

    public StandardThoroughfareInfo setLine(Line line) {
        this.line = line;
        addCheckSelfMap(line.getType(), line.getType());
        return this;
    }


    public Info getInfo() {
        return info;
    }

    public StandardThoroughfareInfo setInfo(Info info) {
        this.info = info;
        addCheckSelfMap(info.getType(), info.getType());
        return this;
    }

    public Quuppa getQuuppa() {
        return quuppa;
    }

    public StandardThoroughfareInfo setQuuppa(Quuppa quuppa) {
        this.quuppa = quuppa;
        addCheckSelfMap(quuppa.getType(), quuppa.getType());
        return this;
    }

    private void addCheckSelfMap(String type, String key) {
        ConcurrentMap<String, Long> stringLongMap = selfCheckMap.get(type);
        if (null == stringLongMap) {
            stringLongMap = new ConcurrentHashMap<>();
        }
        stringLongMap.put(key, System.currentTimeMillis());
        selfCheckMap.put(type, stringLongMap);
    }

    public void checkChannel(String address) {
        if (!address.startsWith("19:18")) {
            return;
        }
        // 开始校验
        final long currentTimeMillis = System.currentTimeMillis();
        for (Map.Entry<String, ConcurrentMap<String, Long>> stringMapEntry : selfCheckMap.entrySet()) {
            final ConcurrentMap<String, Long> value = stringMapEntry.getValue();
            final String key = stringMapEntry.getKey();
            if (null == value) {
                continue;
            }

            boolean isMultiple = ThoroughfareTypeEnum.I_BEACON.getValue().equals(key) ||
                    ThoroughfareTypeEnum.EDDYSTONE_URL.getValue().equals(key) ||
                    ThoroughfareTypeEnum.EDDYSTONE_UID.getValue().equals(key);


            for (Map.Entry<String, Long> stringLongEntry : value.entrySet()) {
                final Long scanTime = stringLongEntry.getValue();
                final String longEntryKey = stringLongEntry.getKey();
                if (null == scanTime || currentTimeMillis - scanTime > 10_000) {
                    if (isMultiple) {
                        final Map<String, Object> stringObjectMap = objectMap.get(key);
                        if (null != stringObjectMap && !stringObjectMap.isEmpty()) {
                            stringObjectMap.remove(longEntryKey);
                            objectMap.put(key, stringObjectMap);
                        }
                    } else {
                        if (Objects.equals(key, ThoroughfareTypeEnum.EDDYSTONE_TLM.getValue())) {
                            this.tlm = null;
                        } else if (Objects.equals(key, ThoroughfareTypeEnum.ACC.getValue())) {
                            this.acc = null;
                        } else if (Objects.equals(key, ThoroughfareTypeEnum.INFO.getValue())) {
                            this.info = null;
                        } else if (Objects.equals(key, ThoroughfareTypeEnum.LINE.getValue())) {
                            this.line = null;
                        } else if (Objects.equals(key, ThoroughfareTypeEnum.QUUPPA_AOA.getValue())) {
                            this.quuppa = null;
                        }
                    }

                    value.remove(longEntryKey);
                    selfCheckMap.put(key, value);
                }

            }
        }

    }

    @Override
    public String toString() {
        return "StandardThoroughfareInfo{" +
                "objectMap=" + objectMap +
                ", battery=" + battery +
                ", beacons=" + beacons +
                ", uids=" + uids +
                ", urls=" + urls +
                ", tlm=" + tlm +
                ", acc=" + acc +
                '}';
    }
}
