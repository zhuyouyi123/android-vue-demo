package com.db.database.daoobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "all_day_data", indices = {@Index(value = "date_time")})
public class AllDayDataDO {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "date_time")
    public Integer dateTime;

    @ColumnInfo(name = "step_number")
    public Integer step;

    @ColumnInfo(name = "calorie_number")
    public Integer calorie;

    @ColumnInfo(name = "mileage_number")
    public Integer mileage;

    @ColumnInfo(name = "active_time")
    public Integer activeTime;

    @ColumnInfo(name = "compliance")
    public Boolean compliance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDateTime() {
        return dateTime;
    }

    public void setDateTime(Integer dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getCalorie() {
        return calorie;
    }

    public void setCalorie(Integer calorie) {
        this.calorie = calorie;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Integer activeTime) {
        this.activeTime = activeTime;
    }

    public Boolean getCompliance() {
        return compliance;
    }

    public void setCompliance(Boolean compliance) {
        this.compliance = compliance;
    }
}
