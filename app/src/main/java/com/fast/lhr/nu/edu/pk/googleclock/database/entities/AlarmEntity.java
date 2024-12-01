package com.fast.lhr.nu.edu.pk.googleclock.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fast.lhr.nu.edu.pk.googleclock.utils.ActiveDaysConverter;

import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "alarms")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private String day;
    private boolean enabled;

    @TypeConverters(ActiveDaysConverter.class) // Handle the Set<String> type
    private Set<String> activeDays;

    public AlarmEntity(String time, String day, boolean enabled) {
        this.time = time;
        this.day = day;
        this.enabled = enabled;
        this.activeDays = new HashSet<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Set<String> activeDays) {
        this.activeDays = activeDays;
    }
}
