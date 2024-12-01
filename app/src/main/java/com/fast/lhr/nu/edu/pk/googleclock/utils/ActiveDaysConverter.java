package com.fast.lhr.nu.edu.pk.googleclock.utils;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActiveDaysConverter {

    @TypeConverter
    public static String fromSet(Set<String> activeDays) {
        return String.join(",", activeDays);
    }

    @TypeConverter
    public static Set<String> toSet(String activeDaysString) {
        return new HashSet<>(Arrays.asList(activeDaysString.split(",")));
    }
}
