package com.ble.blescansdk.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ble.blescansdk.db.dataobject.SecretKeyDO;

@Dao
public interface SecretKeyDAO {

    @Insert
    void insert(SecretKeyDO... secretKeyDOArray);

    @Update
    void update(SecretKeyDO... secretKeyDOArray);

    @Query("SELECT * FROM SECRET_KEY WHERE address=:address LIMIT 1")
    SecretKeyDO queryFirst(String address);
}
