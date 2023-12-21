package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.CommunicationDataDO;

import java.util.List;

@Dao
public interface CommunicationDataDAO {

    @Insert
    void insert(CommunicationDataDO... communicationDataArray);

    @Update
    void update(CommunicationDataDO... communicationDataArray);

    @Query("DELETE FROM communication_data WHERE data_date=:time AND type=:type")
    void deleteTodayByType(String time, String type);

    @Query("SELECT * FROM communication_data")
    List<CommunicationDataDO> queryAll();

    @Query("SELECT * FROM communication_data WHERE data_date=:date AND type=:type")
    CommunicationDataDO queryByDateAndType(Integer date, String type);

    /**
     * 根据时间删除数据 根据保留策略删除数据
     *
     * @param earliestDate 最早时间
     */
    @Query("DELETE FROM communication_data WHERE data_date < :earliestDate")
    void removeEarliestDate(int earliestDate);
}
