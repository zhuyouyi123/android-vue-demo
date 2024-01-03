package com.panvan.app.data.holder;

import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.dfuupgrade.MyBleManager;
import com.panvan.app.data.holder.statistics.StatisticsInfo;

import java.util.Objects;

public class DeviceHolder {

    public static final int CONNECT_FAILED = -1;

    public static final int NOT_CONNECTED = 0;

    public static final int CONNECTING = 1;

    public static final int CONNECTED = 2;

    public static BraceletDevice DEVICE = null;

    private static DeviceHolder INSTANCE = null;

    private MyBleManager bleManager;

    private DeviceInfo info;

    /**
     * 部署统计数据
     */
    private StatisticsInfo stepStatisticsInfo;

    private StatisticsInfo calorieStatisticsInfo;


    public static DeviceHolder getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DeviceHolder();
        }
        return INSTANCE;
    }


    public StatisticsInfo getStepStatisticsInfo() {
        if (Objects.isNull(stepStatisticsInfo)) {
            this.stepStatisticsInfo = new StatisticsInfo();
        }
        return stepStatisticsInfo;
    }

    public void setStepStatisticsInfo(StatisticsInfo stepStatisticsInfo) {
        this.stepStatisticsInfo = stepStatisticsInfo;
    }

    public StatisticsInfo getCalorieStatisticsInfo() {
        if (Objects.isNull(calorieStatisticsInfo)) {
            this.calorieStatisticsInfo = new StatisticsInfo();
        }
        return calorieStatisticsInfo;
    }

    public void setCalorieStatisticsInfo(StatisticsInfo calorieStatisticsInfo) {
        this.calorieStatisticsInfo = calorieStatisticsInfo;
    }

    public MyBleManager getBleManager() {
        return bleManager;
    }

    public void setBleManager(MyBleManager bleManager) {
        this.bleManager = bleManager;
    }

    public static class DeviceInfo {
        private long time;

        private int battery = 0;
        /**
         * 心率
         */
        private int heartRate = 60;
        /**
         * 步数信息
         */
        private StepInfo stepInfo = new StepInfo();
        /**
         * 体表温度
         */
        private double bodySurfaceTemperature = 36.5;

        /**
         * 佩戴状态
         */
        private boolean wearingStatus = false;

        /**
         * 血液相关
         */
        private BloodPressureInfo bloodPressureInfo = new BloodPressureInfo();

        private String model;

        private String firmwareVersion;

        private String otaAddress;

        private String otaFirmwareVersion;

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }

        public StepInfo getStepInfo() {
            if (Objects.isNull(stepInfo)) {
                stepInfo = new StepInfo();
            }
            return stepInfo;
        }

        public void setStepInfo(StepInfo stepInfo) {
            this.stepInfo = stepInfo;
        }

        public double getBodySurfaceTemperature() {
            return bodySurfaceTemperature;
        }

        public void setBodySurfaceTemperature(double bodySurfaceTemperature) {
            this.bodySurfaceTemperature = bodySurfaceTemperature;
        }

        public boolean isWearingStatus() {
            return wearingStatus;
        }

        public void setWearingStatus(boolean wearingStatus) {
            this.wearingStatus = wearingStatus;
        }

        public BloodPressureInfo getBloodPressureInfo() {
            if (Objects.isNull(bloodPressureInfo)) {
                bloodPressureInfo = new BloodPressureInfo();
            }
            return bloodPressureInfo;
        }

        public void setBloodPressureInfo(BloodPressureInfo bloodPressureInfo) {
            this.bloodPressureInfo = bloodPressureInfo;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public String getOtaAddress() {
            return otaAddress;
        }

        public void setOtaAddress(String otaAddress) {
            this.otaAddress = otaAddress;
        }

        public String getOtaFirmwareVersion() {
            return otaFirmwareVersion;
        }

        public void setOtaFirmwareVersion(String otaFirmwareVersion) {
            this.otaFirmwareVersion = otaFirmwareVersion;
        }
    }

    public static class StepInfo {
        private int stepNumber = 0;
        private int mileage = 0;
        private int calories = 0;
        private int useTime = 0;

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public int getMileage() {
            return mileage;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public int getUseTime() {
            return useTime;
        }

        public void setUseTime(int useTime) {
            this.useTime = useTime;
        }
    }

    public static class BloodPressureInfo {

        /**
         * 血氧
         */
        private int bloodOxygen = 100;

        /**
         * 收缩压
         */
        private int systolicPressure = 0;

        /**
         * 舒张压
         */
        private int diastolicPressure = 0;

        /**
         * 血液粘稠度
         */
        private int bloodViscosity = 0;

        public int getBloodOxygen() {
            return bloodOxygen;
        }

        public void setBloodOxygen(int bloodOxygen) {
            this.bloodOxygen = bloodOxygen;
        }

        public int getSystolicPressure() {
            return systolicPressure;
        }

        public void setSystolicPressure(int systolicPressure) {
            this.systolicPressure = systolicPressure;
        }

        public int getDiastolicPressure() {
            return diastolicPressure;
        }

        public void setDiastolicPressure(int diastolicPressure) {
            this.diastolicPressure = diastolicPressure;
        }

        public int getBloodViscosity() {
            return bloodViscosity;
        }

        public void setBloodViscosity(int bloodViscosity) {
            this.bloodViscosity = bloodViscosity;
        }
    }

    public DeviceInfo getInfo() {
        if (Objects.isNull(info)) {
            info = new DeviceInfo();
        }
        info.setTime(System.currentTimeMillis());
        return info;
    }

    public void setInfo(DeviceInfo info) {
        this.info = info;
    }
}
