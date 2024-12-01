package com.fast.lhr.nu.edu.pk.googleclock.utils;

import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;
import com.fast.lhr.nu.edu.pk.googleclock.models.Alarm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AlarmMapper {

    // Convert AlarmEntity to Alarm (for UI)
    public static Alarm toModel(AlarmEntity entity) {
        Alarm alarm = new Alarm(entity.getId(), entity.getTime(), entity.getDay(), entity.isEnabled());
        alarm.setActiveDays(entity.getActiveDays() != null ? entity.getActiveDays() : new HashSet<>());
        return alarm;
    }

    // Convert Alarm to AlarmEntity (for Room database)
    public static AlarmEntity toEntity(Alarm alarm) {
        AlarmEntity entity = new AlarmEntity(alarm.getTime(), alarm.getDay(), alarm.isEnabled());
        entity.setId(alarm.getId());
        entity.setActiveDays(alarm.getActiveDays() != null ? alarm.getActiveDays() : new HashSet<>());
        return entity;
    }

    // Convert List of AlarmEntity to List of Alarm
    public static List<Alarm> toModelList(List<AlarmEntity> entities) {
        List<Alarm> alarms = new ArrayList<>();
        for (AlarmEntity entity : entities) {
            alarms.add(toModel(entity));
        }
        return alarms;
    }

    // Convert List of Alarm to List of AlarmEntity
    public static List<AlarmEntity> toEntityList(List<Alarm> alarms) {
        List<AlarmEntity> entities = new ArrayList<>();
        for (Alarm alarm : alarms) {
            entities.add(toEntity(alarm));
        }
        return entities;
    }
}
