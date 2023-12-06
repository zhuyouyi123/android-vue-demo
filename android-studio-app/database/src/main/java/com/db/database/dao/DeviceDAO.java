package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.DeviceDO;
import com.db.database.daoobject.UserInfoDO;

@Dao
public interface DeviceDAO {

    @Insert
    void insert(DeviceDO... deviceArr);

    @Update
    void update(DeviceDO... deviceArr);

    @Query("SELECT * FROM device WHERE in_use = 1")
    DeviceDO queryInUse();

    @Query("SELECT * FROM device WHERE address=:address")
    DeviceDO queryByMac(String address);
}
