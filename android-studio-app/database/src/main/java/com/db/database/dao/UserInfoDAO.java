package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.db.database.daoobject.UserInfoDO;

@Dao
public interface UserInfoDAO {

    @Insert
    void insert(UserInfoDO... userInfoArr);

    @Update
    void update(UserInfoDO... userInfoArr);

    @Query("SELECT * FROM user")
    UserInfoDO query();
}
