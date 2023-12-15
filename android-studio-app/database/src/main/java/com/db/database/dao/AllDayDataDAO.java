package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.AllDayDataDO;

import java.util.List;

@Dao
public interface AllDayDataDAO {

    @Query("SELECT * FROM all_day_data WHERE date_time=:dateTime LIMIT 1")
    AllDayDataDO queryByDate(Integer dateTime);

    @Insert
    void insert(AllDayDataDO allDayDataDO);

    @Update
    void update(AllDayDataDO allDayDataDO);

    @Query("SELECT COUNT(*) FROM all_day_data WHERE compliance = 1")
    Integer queryComplianceDays();

    @Query("SELECT compliance FROM all_day_data")
    List<Boolean> queryAllCompliance();
}
