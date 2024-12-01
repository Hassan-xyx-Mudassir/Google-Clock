package com.fast.lhr.nu.edu.pk.googleclock.interfaces;

import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;

public interface AlarmActionListener {
    void setAlarm(AlarmEntity alarm);
    void cancelAlarm(AlarmEntity alarm);
}
