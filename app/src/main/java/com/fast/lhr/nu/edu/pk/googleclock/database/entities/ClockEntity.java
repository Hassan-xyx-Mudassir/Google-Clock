package com.fast.lhr.nu.edu.pk.googleclock.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clocks")
public class ClockEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String cityName;
    private String offset;
    private String time;

    // Constructor
    public ClockEntity(String cityName, String offset, String time) {
        this.cityName = cityName;
        this.offset = offset;
        this.time = time;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

