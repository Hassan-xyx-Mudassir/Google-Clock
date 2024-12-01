package com.fast.lhr.nu.edu.pk.googleclock.models;

import java.util.HashSet;
import java.util.Set;

public class Alarm {
    private int id;
    private String time;
    private String day; // This will store the initial alarm day when it's created.
    private boolean enabled;
    private Set<String> activeDays; // Set to store all active days for the alarm

    public Alarm(int id, String time, String day, boolean enabled) {
        this.id = id;
        this.time = time;
        this.day = day;
        this.enabled = enabled;
        this.activeDays = new HashSet<>(); // Initialize the set
        this.activeDays.add(day); // Add the initial day
    }

    // Getters and setters
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
