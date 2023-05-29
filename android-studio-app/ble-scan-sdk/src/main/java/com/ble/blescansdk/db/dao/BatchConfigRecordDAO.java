package com.ble.blescansdk.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ble.blescansdk.db.dataobject.BatchConfigRecordDO;

import java.util.List;

@Dao
public interface BatchConfigRecordDAO {

    @Insert
    void insert(BatchConfigRecordDO... batchConfigRecordDOArray);

    @Update
    void update(BatchConfigRecordDO... batchConfigRecordDOArray);

    @Query("SELECT * FROM BATCH_CONFIG_RECORD")
    List<BatchConfigRecordDO> list();

    @Query("SELECT * FROM BATCH_CONFIG_RECORD WHERE result=:resultCode")
    List<BatchConfigRecordDO> listByResult(int resultCode);

    @Query("DELETE FROM BATCH_CONFIG_RECORD ")
    void deleteAll();

    @Query("DELETE FROM BATCH_CONFIG_RECORD WHERE type=:type")
    void deleteByType(int type);


}
