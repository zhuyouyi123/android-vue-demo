package com.db.database.daoobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_target_configuration")
public class UserTargetConfigurationDO {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    /**
     * 类型
     */
    private String type;

    /**
     * 目标值
     */
    @ColumnInfo(name = "target_value")
    private Integer targetValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }
}
