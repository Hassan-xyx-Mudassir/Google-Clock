package com.fast.lhr.nu.edu.pk.googleclock.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarm_channel";
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the alarm time from the Intent (if needed)
        String time = intent.getStringExtra("time");

        // Check if receiver is working
        Log.d("AlarmReceiver", "Alarm triggered at: " + time);

        // Show a Toast to indicate the alarm went off
        Toast.makeText(context, "Alarm triggered at " + time, Toast.LENGTH_SHORT).show();

//        // Play an alarm sound
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ether); // Add a sound file to res/raw
//        mediaPlayer.start();

        // Play system alarm sound
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            // Use MediaPlayer to play the sound
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, alarmUri);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ALARM).build());
            mediaPlayer.setLooping(true); // Alarm should repeat until dismissed
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Log successful alarm trigger
            Log.d("AlarmReceiver", "Alarm sound started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AlarmReceiver", "Failed to play alarm sound: " + e.getMessage());
        }

        // Create the stop action intent
        Intent stopIntent = new Intent(context, StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create notification channel
        createNotificationChannel(context);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Temporary default icon
                .setContentTitle("Alarm Triggered")
                .setContentText("Your alarm for " + time + " is ringing.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent);

        // Show notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                int notificationId = time != null ? time.hashCode() : 0;
                notificationManager.notify(notificationId, builder.build());
                Log.d("AlarmReceiver", "Notification displayed for: " + time);
            } else {
                Log.e("AlarmReceiver", "NotificationManager is null - cannot show notification");
            }
        } catch (SecurityException se) {
            Log.e("AlarmReceiver", "Security exception when showing notification", se);
        } catch (Exception e) {
            Log.e("AlarmReceiver", "Error showing notification", e);
        }
    }

    private void createNotificationChannel(Context context) {
        // Check Android version before creating channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for Alarm notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                try {
                    notificationManager.createNotificationChannel(channel);
                    Log.d("AlarmReceiver", "Notification channel created successfully");
                } catch (Exception e) {
                    Log.e("AlarmReceiver", "Failed to create notification channel", e);
                }
            } else {
                Log.e("AlarmReceiver", "NotificationManager is null - cannot create channel");
            }
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void clearMediaPlayer() {
        mediaPlayer = null;
    }
}
