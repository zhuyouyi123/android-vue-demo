package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.RealTimeDataDO;


@Dao
public interface RealTimeDataDAO {
    @Insert
    void insert(RealTimeDataDO... realTimeDataArr);

    @Update
    void update(RealTimeDataDO... realTimeDataArr);

    @Query("DELETE FROM real_time_data")
    void deleteAll();

    @Query("SELECT * FROM real_time_data WHERE date_time=:dateTime LIMIT 1")
    RealTimeDataDO queryByDateTime(Integer dateTime);
}
