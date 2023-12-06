package com.db.database.daoobject;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserInfoDO {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    private String username;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 生日
     */
    private Long birthDate;

    private Integer height;

    private Double weight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "UserInfoConfigDO{" +
                "username='" + username + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", height=" + height +
                ", weight=" + weight +
                '}';
    }
}
