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
import com.ble.blescansdk.ble.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StandardThoroughfareInfo {

    private final ConcurrentMap<String, Map<String, Object>> objectMap = new ConcurrentHashMap<>();

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
        this.battery = battery;
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
        return this;
    }


    public TLM getTlm() {
        return tlm;
    }

    public StandardThoroughfareInfo setTlm(TLM tlm) {
        this.tlm = tlm;
        return this;
    }

    public Acc getAcc() {
        return acc;
    }

    public StandardThoroughfareInfo setAcc(Acc acc) {
        this.acc = acc;
        return this;
    }

    public Line getLine() {
        return line;
    }

    public StandardThoroughfareInfo setLine(Line line) {
        this.line = line;
        return this;
    }


    public Info getInfo() {
        return info;
    }

    public StandardThoroughfareInfo setInfo(Info info) {
        this.info = info;
        return this;
    }

    public Quuppa getQuuppa() {
        return quuppa;
    }

    public StandardThoroughfareInfo setQuuppa(Quuppa quuppa) {
        this.quuppa = quuppa;
        return this;
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
