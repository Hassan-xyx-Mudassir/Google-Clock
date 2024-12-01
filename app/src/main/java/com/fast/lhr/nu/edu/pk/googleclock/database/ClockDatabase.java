package com.fast.lhr.nu.edu.pk.googleclock.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fast.lhr.nu.edu.pk.googleclock.database.dao.AlarmDao;
import com.fast.lhr.nu.edu.pk.googleclock.database.dao.ClockDao;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.ClockEntity;
import com.fast.lhr.nu.edu.pk.googleclock.models.Alarm;
import com.fast.lhr.nu.edu.pk.googleclock.utils.ActiveDaysConverter;

@Database(entities = {ClockEntity.class, AlarmEntity.class}, version = 1, exportSchema = false)
@TypeConverters({ActiveDaysConverter.class})
public abstract class ClockDatabase extends RoomDatabase {

    private static ClockDatabase instance;

    public abstract ClockDao clockDao();
    public abstract AlarmDao alarmDao();

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

