package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.adapters.AlarmAdapter;
import com.fast.lhr.nu.edu.pk.googleclock.database.ClockDatabase;
import com.fast.lhr.nu.edu.pk.googleclock.database.dao.AlarmDao;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;
import com.fast.lhr.nu.edu.pk.googleclock.interfaces.AlarmActionListener;
import com.fast.lhr.nu.edu.pk.googleclock.receivers.AlarmReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment implements AlarmActionListener {

    private RecyclerView alarmRecycler;
    private FloatingActionButton fabAddAlarm;
    private List<AlarmEntity> alarmList;
    private AlarmAdapter alarmAdapter;

    private ClockDatabase db;
    private AlarmDao alarmDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // Initialize RecyclerView
        alarmRecycler = view.findViewById(R.id.alarm_recycler);
        alarmRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList, requireContext(), this);
        alarmRecycler.setAdapter(alarmAdapter);

        // Initialize FAB
        fabAddAlarm = view.findViewById(R.id.fab_add_alarm);
        fabAddAlarm.setOnClickListener(v -> showTimePicker());

        // Initialize DAO
        db = ClockDatabase.getInstance(requireContext());
        alarmDao = db.alarmDao();

        return view;
    }

    // Show a TimePicker dialog to add an alarm
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
            AlarmEntity newAlarm = new AlarmEntity(formattedTime, "Today", true); // Directly use AlarmEntity

            // Save to the list and notify the adapter
            alarmList.add(newAlarm);
            alarmAdapter.notifyItemInserted(alarmList.size() - 1);

            // Save to the database in a background thread
            new Thread(() -> alarmDao.insertAlarm(newAlarm)).start();
        }, currentHour, currentMinute, false).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(() -> {
            // Query all saved alarms from the database
            List<AlarmEntity> alarmEntities = alarmDao.getAllAlarms();

            // Update the adapter on the UI thread
            requireActivity().runOnUiThread(() -> {
                alarmList.clear(); // Clear existing alarms
                alarmList.addAll(alarmEntities); // Add the newly loaded alarms
                alarmAdapter.notifyDataSetChanged(); // Notify the adapter
            });
        }).start();
    }

    @Override
    public void setAlarm(AlarmEntity alarm) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        requireContext().getPackageManager().setComponentEnabledSetting(
                new ComponentName(requireContext(), AlarmReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        String[] timeParts = alarm.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("time", alarm.getTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                alarm.getId(), // Use unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public void cancelAlarm(AlarmEntity alarm) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                alarm.getId(), // Use unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
