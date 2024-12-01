package com.fast.lhr.nu.edu.pk.googleclock.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.database.ClockDatabase;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.AlarmEntity;
import com.fast.lhr.nu.edu.pk.googleclock.interfaces.AlarmActionListener;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private final List<AlarmEntity> alarmList;
    private final Context context;
    private final AlarmActionListener alarmActionListener;


    public AlarmAdapter(List<AlarmEntity> alarmList, Context context, AlarmActionListener alarmActionListener) {
        this.alarmList = alarmList;
        this.context = context;
        this.alarmActionListener = alarmActionListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmEntity alarm = alarmList.get(position);

        // Set alarm time and day
        holder.alarmTime.setText(alarm.getTime());
        holder.alarmDay.setText(alarm.getDay());

        // Handle alarm switch behavior
        holder.alarmSwitch.setChecked(alarm.isEnabled());
        holder.itemView.setAlpha(alarm.isEnabled() ? 1.0f : 0.5f); // Dim card if alarm is disabled

        // Setting alarm
        if (holder.alarmSwitch.isChecked() && isTimeInFuture(alarm.getTime())) {
            alarmActionListener.setAlarm(alarm);
            Log.d("AlarmAdapter", "Alarm set for had ID: " + alarm.getId());
        } else {
            Log.d("AlarmAdapter", "Alarm not set for had ID: " + alarm.getId());
        }

        holder.alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);
            holder.itemView.setAlpha(isChecked ? 1.0f : 0.5f);

            if (isChecked && isTimeInFuture(alarm.getTime())) {
                // Schedule the alarm
                alarmActionListener.setAlarm(alarm);
                Log.d("AlarmAdapter", "Alarm set for had ID: " + alarm.getId());
            } else {
                // Cancel the alarm
                alarmActionListener.cancelAlarm(alarm);
                Log.d("AlarmAdapter", "Alarm cancelled for had ID: " + alarm.getId());
            }
        });

        // Handle expandable section visibility
        holder.itemView.setOnClickListener(v -> {
            boolean isExpanded = holder.expandableSection.getVisibility() == View.VISIBLE;
            holder.expandableSection.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        });

        // Initialize the day buttons
        setupDayButton(holder.btnMonday, "Monday", alarm);
        setupDayButton(holder.btnTuesday, "Tuesday", alarm);
        setupDayButton(holder.btnWednesday, "Wednesday", alarm);
        setupDayButton(holder.btnThursday, "Thursday", alarm);
        setupDayButton(holder.btnFriday, "Friday", alarm);
        setupDayButton(holder.btnSaturday, "Saturday", alarm);
        setupDayButton(holder.btnSunday, "Sunday", alarm);

        // Handle delete option
        holder.deleteOption.setOnClickListener(v -> {
            int positionToRemove = holder.getAdapterPosition(); // Get the position of the alarm

            if (positionToRemove != RecyclerView.NO_POSITION) { // Check if position is valid
                AlarmEntity alarmToDelete = alarmList.get(positionToRemove); // Get the alarm to delete

                // Delete from database in a background thread
                new Thread(() -> {
                    try {
                        Log.d("AlarmAdapter", "Deleting alarm with ID: " + alarmToDelete.getId());

                        int wasDeleted = ClockDatabase.getInstance(context).alarmDao().deleteAlarmById(alarmToDelete.getId()); // Delete from database
                        Log.d("AlarmAdapter", "Was alarm deleted: " + wasDeleted);
                    } catch (Exception e) {
                        Log.e("AlarmAdapter", "Error deleting alarm: ", e);
                    }
                }).start();

                alarmList.remove(positionToRemove); // Remove from list
                notifyItemRemoved(positionToRemove); // Notify adapter
                notifyItemRangeChanged(positionToRemove, alarmList.size());
                Log.d("AlarmAdapter", "Size of alarmList: " + alarmList.size());
            }
        });
    }

    private boolean isTimeInFuture(String alarmTime) {
        String[] timeParts = alarmTime.split(":");
        int alarmHour = Integer.parseInt(timeParts[0]);
        int alarmMinute = Integer.parseInt(timeParts[1]);

        // Get the current time
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Compare alarm time with current time
        return (alarmHour > currentHour || (alarmHour == currentHour && alarmMinute > currentMinute));
    }

    private void setupDayButton(Button dayButton, String day, AlarmEntity alarm) {
        // Initialize the button's background based on its state
        updateDayButtonBackground(dayButton, alarm.getActiveDays().contains(day));

        // Handle button click to toggle the day's selection
        dayButton.setOnClickListener(v -> {
            Set<String> activeDays = alarm.getActiveDays();

            if (activeDays.contains(day)) {
                activeDays.remove(day); // Remove the day if it's already selected
            } else {
                activeDays.add(day); // Add the day if it's not selected
            }

            // Update the alarm's activeDays using the setter
            alarm.setActiveDays(activeDays);

            // Update the button background
            updateDayButtonBackground(dayButton, activeDays.contains(day));
        });
    }

    private void updateDayButtonBackground(Button dayButton, boolean isSelected) {
        // Update background based on selection state
        if (isSelected) {
            dayButton.setBackgroundResource(R.drawable.circle_blue); // Blue background
            dayButton.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blue_highlight))); // White text for contrast
        } else {
            dayButton.setBackgroundResource(R.drawable.circle_gray); // Gray background
            dayButton.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray))); // Gray text for contrast
        }
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime, alarmDay;
        Switch alarmSwitch;
        View expandableSection;
        Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday, btnSaturday, btnSunday;
        LinearLayout deleteOption;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_time);
            alarmDay = itemView.findViewById(R.id.alarm_day);
            alarmSwitch = itemView.findViewById(R.id.alarm_switch);
            expandableSection = itemView.findViewById(R.id.expandable_section);

            // Initialize day buttons
            btnMonday = itemView.findViewById(R.id.btn_monday);
            btnTuesday = itemView.findViewById(R.id.btn_tuesday);
            btnWednesday = itemView.findViewById(R.id.btn_wednesday);
            btnThursday = itemView.findViewById(R.id.btn_thursday);
            btnFriday = itemView.findViewById(R.id.btn_friday);
            btnSaturday = itemView.findViewById(R.id.btn_saturday);
            btnSunday = itemView.findViewById(R.id.btn_sunday);

            // Initialize delete option
            deleteOption = itemView.findViewById(R.id.delete_option);
        }
    }
}
