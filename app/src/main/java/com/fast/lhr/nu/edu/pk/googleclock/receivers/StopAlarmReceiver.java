package com.fast.lhr.nu.edu.pk.googleclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the alarm sound (if applicable)
        Log.d("StopAlarmReceiver", "Stop alarm action triggered");

        MediaPlayer mediaPlayer = AlarmReceiver.getMediaPlayer();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            AlarmReceiver.clearMediaPlayer();
            Log.d("StopAlarmReceiver", "Alarm cleared");
        }
    }
}
