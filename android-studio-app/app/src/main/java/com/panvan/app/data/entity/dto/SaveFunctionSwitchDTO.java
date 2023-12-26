package com.panvan.app.data.entity.dto;

public class SaveFunctionSwitchDTO {

    private Integer bloodPressureInterval;
    private Boolean bloodPressure;

    private Integer bloodOxygenInterval;
    private Boolean bloodOxygen;


    public Integer getBloodPressureInterval() {
        return bloodPressureInterval;
    }

    public void setBloodPressureInterval(Integer bloodPressureInterval) {
        this.bloodPressureInterval = bloodPressureInterval;
    }

    public Boolean getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(Boolean bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getBloodOxygenInterval() {
        return bloodOxygenInterval;
    }

    public void setBloodOxygenInterval(Integer bloodOxygenInterval) {
        this.bloodOxygenInterval = bloodOxygenInterval;
    }

    public Boolean getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Boolean bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }
}
