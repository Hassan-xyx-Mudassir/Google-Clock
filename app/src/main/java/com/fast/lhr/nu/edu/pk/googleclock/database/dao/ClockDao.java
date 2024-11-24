package com.fast.lhr.nu.edu.pk.googleclock.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.fast.lhr.nu.edu.pk.googleclock.database.entities.ClockEntity;

import java.util.List;

@Dao
public interface ClockDao {
    @Insert
    long insertClock(ClockEntity clockEntity);

    @Delete
    void deleteClock(ClockEntity clockEntity);

    @Query("SELECT * FROM clocks")
    List<ClockEntity> getAllClocks();
}

