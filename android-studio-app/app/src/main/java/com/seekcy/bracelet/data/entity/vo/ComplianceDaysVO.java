package com.seekcy.bracelet.data.entity.vo;

public class ComplianceDaysVO {

    private Integer complianceDays;

    /**
     * 连续达标天数
     */
    private Integer continueComplianceDays;

    public Integer getComplianceDays() {
        return complianceDays;
    }

    public void setComplianceDays(Integer complianceDays) {
        this.complianceDays = complianceDays;
    }

    public Integer getContinueComplianceDays() {
        return continueComplianceDays;
    }

    public void setContinueComplianceDays(Integer continueComplianceDays) {
        this.continueComplianceDays = continueComplianceDays;
    }
}
