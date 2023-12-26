package com.panvan.app.data.entity.bo;

public class FunctionSwitchBO {

    private Integer bloodPressureInterval;
    private Boolean bloodPressure;

    private Integer bloodOxygenInterval;
    private Boolean bloodOxygen;

    public static FunctionSwitchBO getDefault() {
        FunctionSwitchBO functionSwitchBO = new FunctionSwitchBO();
        functionSwitchBO.setBloodPressure(false);
        functionSwitchBO.setBloodOxygen(false);
        functionSwitchBO.setBloodOxygenInterval(5);
        functionSwitchBO.setBloodPressureInterval(5);
        return functionSwitchBO;
    }

    public Boolean getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(Boolean bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Boolean getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Boolean bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public Integer getBloodPressureInterval() {
        return bloodPressureInterval;
    }

    public void setBloodPressureInterval(Integer bloodPressureInterval) {
        this.bloodPressureInterval = bloodPressureInterval;
    }

    public Integer getBloodOxygenInterval() {
        return bloodOxygenInterval;
    }

    public void setBloodOxygenInterval(Integer bloodOxygenInterval) {
        this.bloodOxygenInterval = bloodOxygenInterval;
    }
}
