package com.fast.lhr.nu.edu.pk.googleclock.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CityClock {
    private int id; // New field
    private String cityName;
    private int gmtOffset; // in seconds

    public CityClock(String cityName, int gmtOffset) {
        this.cityName = cityName;
        this.gmtOffset = gmtOffset;
    }

    public CityClock(int id, String cityName, int gmtOffset) {
        this.id = id;
        this.cityName = cityName;
        this.gmtOffset = gmtOffset;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getFormattedTime() {
        // Convert GMT offset to the current time in that city
        long currentTimeMillis = System.currentTimeMillis();
        long offsetMillis = gmtOffset * 1000L;
        long localTimeMillis = currentTimeMillis + offsetMillis;

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date(localTimeMillis));
    }

    public String getOffset() {
        int hours = gmtOffset / 3600;
        int minutes = (gmtOffset % 3600) / 60;
        return String.format("GMT %+d:%02d", hours, Math.abs(minutes));
    }
}
