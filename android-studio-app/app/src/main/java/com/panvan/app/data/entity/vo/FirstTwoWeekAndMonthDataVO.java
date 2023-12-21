package com.panvan.app.data.entity.vo;

public class FirstTwoWeekAndMonthDataVO {
    private Integer currWeek;
    private Integer lastWeek;

    private Integer currMonth;
    private Integer lastMonth;

    public Integer getCurrWeek() {
        return currWeek;
    }

    public void setCurrWeek(Integer currWeek) {
        this.currWeek = currWeek;
    }

    public Integer getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(Integer lastWeek) {
        this.lastWeek = lastWeek;
    }

    public Integer getCurrMonth() {
        return currMonth;
    }

    public void setCurrMonth(Integer currMonth) {
        this.currMonth = currMonth;
    }

    public Integer getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(Integer lastMonth) {
        this.lastMonth = lastMonth;
    }
}
