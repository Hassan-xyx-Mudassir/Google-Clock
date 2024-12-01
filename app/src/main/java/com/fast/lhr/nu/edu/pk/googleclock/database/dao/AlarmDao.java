package com.fast.lhr.nu.edu.pk.googleclock.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;

import java.util.List;

@Dao
public interface AlarmDao {

    @Insert
    void insertAlarm(AlarmEntity alarm);

    @Query("DELETE FROM alarms WHERE id = :id")
    int deleteAlarmById(int id);

    @Query("SELECT * FROM alarms")
    List<AlarmEntity> getAllAlarms();
}
