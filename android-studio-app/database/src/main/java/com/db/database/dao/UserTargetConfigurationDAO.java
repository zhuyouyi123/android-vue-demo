package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.db.database.daoobject.UserInfoDO;
import com.db.database.daoobject.UserTargetConfigurationDO;

@Dao
public interface UserTargetConfigurationDAO {

    @Insert
    void insert(UserTargetConfigurationDO... userTargetConfigurationArr);

    @Update
    void update(UserTargetConfigurationDO... userTargetConfigurationArr);
}
