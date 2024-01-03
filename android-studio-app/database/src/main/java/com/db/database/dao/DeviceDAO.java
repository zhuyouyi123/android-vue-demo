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

    @Query("UPDATE device SET model=:model , firmware_version=:version WHERE in_use = 1")
    void updateVerAndFirmware(String model, String version);

    @Query("UPDATE device SET ota_address=:address,ota_firmware_version=:version WHERE in_use = 1")
    void updateOtaInfo(String address, String version);

    @Query("SELECT * FROM device WHERE in_use = 1")
    DeviceDO queryInUse();

    @Query("SELECT * FROM device WHERE address=:address")
    DeviceDO queryByMac(String address);
}
