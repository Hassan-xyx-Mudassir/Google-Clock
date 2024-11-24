package com.fast.lhr.nu.edu.pk.googleclock.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fast.lhr.nu.edu.pk.googleclock.database.dao.ClockDao;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.ClockEntity;

@Database(entities = {ClockEntity.class}, version = 1, exportSchema = false)
public abstract class ClockDatabase extends RoomDatabase {

    private static ClockDatabase instance;

    public abstract ClockDao clockDao();

    public static synchronized ClockDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ClockDatabase.class, "clock_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

