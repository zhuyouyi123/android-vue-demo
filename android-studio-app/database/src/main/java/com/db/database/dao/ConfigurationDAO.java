package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.ConfigurationDO;

import java.util.List;

@Dao
public interface ConfigurationDAO {

    @Insert
    void insert(ConfigurationDO... configurationArr);

    @Update
    void update(ConfigurationDO... configurationArr);

    @Query("SELECT * FROM configuration WHERE config_group=:group")
    List<ConfigurationDO> queryByGroup(String group);

    @Query("SELECT * FROM configuration WHERE config_group=:group AND type=:type")
    ConfigurationDO queryByGroupAndType(String group,String type);
}
