package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fast.lhr.nu.edu.pk.googleclock.CircularProgressView;
import com.fast.lhr.nu.edu.pk.googleclock.R;

public class TimerPopupFragment extends Fragment {

    private TextView timerDisplay, timer_title;
    private ImageButton playPauseButton, stopButton;
    private Button addTimeButton;
    private ImageView restartButton, closeButton;
    private CountDownTimer countDownTimer;
    private long timeInMilliseconds;
    private boolean isPaused = false; // Flag to track play/pause state
    private MediaPlayer mediaPlayer;
     private View cardView;
    private CircularProgressView circularProgressView;

    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer_popup, container, false);

        // Initialize views
        timerDisplay = rootView.findViewById(R.id.timer_text);
        timer_title = rootView.findViewById(R.id.timer_title);
        playPauseButton = rootView.findViewById(R.id.pause_resume_button);
        addTimeButton = rootView.findViewById(R.id.add_time_button);
        restartButton = rootView.findViewById(R.id.restart_icon);
        closeButton = rootView.findViewById(R.id.close_button);
        cardView=rootView.findViewById(R.id.card_view);
        circularProgressView=rootView.findViewById(R.id.circular_progress);
        // Stop button initialization (hidden by default)
        stopButton = rootView.findViewById(R.id.stop_button);
        stopButton.setVisibility(View.GONE);




        // Close button functionality
        closeButton.setOnClickListener(v -> {
            // Stop the timer and release resources
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Replace the current fragment with a fresh instance of TimerFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TimerFragment())
                    .commit();
        });

        // Retrieve the passed timeInMilliseconds value from the arguments
        if (getArguments() != null) {
            timeInMilliseconds = getArguments().getLong("timeInMilliseconds", 0);
        }

        // Initialize MediaPlayer for the alarm sound
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm); // Replace with your sound file in `res/raw`

        // Display the timer values
        updateTimerDisplay();
        updateTitleDisplay();
        circularProgressView.startProgressAnimationFromFull(timeInMilliseconds);
        // Start the countdown
        startTimer();

        // Set up play/pause button functionality
        playPauseButton.setOnClickListener(v -> {
            if (isPaused) {
                restartButton.setVisibility(View.VISIBLE);
                addTimeButton.setVisibility(View.VISIBLE);
                circularProgressView.resumeAnimation();
                // Resume the timer
                startTimer();
                playPauseButton.setImageResource(R.drawable.ic_pause1); // Set the image to "Pause"
            } else {
                // Pause the timer
                pauseTimer();
                circularProgressView.pauseAnimation();

                playPauseButton.setImageResource(R.drawable.ic_play1); // Set the image to "Play"
            }
            isPaused = !isPaused;
        });


        // Set up add time button functionality
        addTimeButton.setOnClickListener(v -> {
            // Add 1 minute (60,000 milliseconds) to the current time
            Log.d("TimerFragment", "Time added " );
            if (timeInMilliseconds <= 0) {
                timerDisplay.setTextColor(Color.parseColor("#FFFFFF"));
                cardView.setBackgroundColor(Color.parseColor("#1c1c1c"));
                timer_title.setTextColor(Color.parseColor("#FFFFFF"));
                closeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#6ba5ff")));
                stopButton.setVisibility(View.GONE);
                restartButton.setVisibility(View.VISIBLE);
                playPauseButton.setVisibility(View.VISIBLE);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop(); // Stop the sound
                    mediaPlayer.release(); // Release the MediaPlayer resources
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm); // Reinitialize MediaPlayer for future use
                }
            }
            timeInMilliseconds += 60000; // Add 1 minute
            updateTimerDisplay(); // Update the timer display
            circularProgressView.startProgressAnimationFromFull(timeInMilliseconds);

            // If the timer is running, restart with updated time
            if (!isPaused) {
                restartTimerWithUpdatedTime();
            }
        });

        // Set up restart button functionality
        restartButton.setOnClickListener(v -> {
            restartTimer();
            circularProgressView.startProgressAnimationFromFull(timeInMilliseconds);
        });
        stopButton.setOnClickListener(v -> {
            resetUIAfterTimerEnds();
        });

        return rootView;
    }

    private void updateTitleDisplay() {
        long absTime = Math.abs(timeInMilliseconds);
        int title_hours = (int) (absTime / 3600000);
        int title_minutes = (int) ((absTime % 3600000) / 60000);
        int title_seconds = (int) ((absTime % 60000) / 1000);
        boolean isNegative = timeInMilliseconds < 0;
        String timeString = isNegative ? "-" : "";
        if (title_hours > 0) {
            timer_title.setText(String.format("%s%02dh:%02dm:%02ds Timer", timeString, title_hours, title_minutes, title_seconds));
        } else if (title_minutes > 0) {
            timer_title.setText(String.format("%s%02dm:%02ds Timer", timeString, title_minutes, title_seconds));
        } else {
            timer_title.setText(String.format("%s%02ds Timer", timeString, title_seconds));
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTimerDisplay() {
        boolean isNegative = timeInMilliseconds < 0;
        long absTime = Math.abs(timeInMilliseconds);

        int hours = (int) (absTime / 3600000);
        int minutes = (int) ((absTime % 3600000) / 60000);
        int seconds = (int) ((absTime % 60000) / 1000);

        String timeString = isNegative ? "-" : "";

        if (hours > 0) {
            timerDisplay.setText(String.format("%s%02d:%02d:%02d", timeString, hours, minutes, seconds));
        } else if (minutes > 0) {
            timerDisplay.setText(String.format("%s%02d:%02d", timeString, minutes, seconds));
        } else {
            timerDisplay.setText(String.format("%s%02d", timeString, seconds));
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeInMilliseconds -= 1000; // Decrease the time by 1 second
                updateTimerDisplay();

                // Animate the progress (only when it's near the end, e.g., last 10 seconds)

                // Animate progress from current value to zero


                // When the timer reaches zero
                if (timeInMilliseconds <= 0) {
                    onTimerEnds();
                }
            }

            @Override
            public void onFinish() {
                // This won't be called because we're using Long.MAX_VALUE
            }
        }.start();
    }


    private void onTimerEnds() {

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Play alarm sound
        }

        // Change UI for timer end
        playPauseButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.GONE);
        // Change the frame layout and text color
        cardView.setBackgroundColor(Color.parseColor("#5292f6"));
        timerDisplay.setTextColor(Color.parseColor("#062d6e"));
        timer_title.setTextColor(Color.parseColor("#062d6e"));
        closeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#13557e")));

    }

    private void resetUIAfterTimerEnds() {
        // Reset the UI to the paused state

        timerDisplay.setTextColor(Color.parseColor("#FFFFFF"));
        cardView.setBackgroundColor(Color.parseColor("#1c1c1c"));
        timer_title.setTextColor(Color.parseColor("#FFFFFF"));
        closeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#6ba5ff")));

        // Replace stop button with play/pause button
        addTimeButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        playPauseButton.setVisibility(View.VISIBLE);

        // Set the play button image
        playPauseButton.setImageResource(R.drawable.ic_play1);

        // Stop the alarm sound
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Stop the sound
            mediaPlayer.release(); // Release the MediaPlayer resources
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm); // Reinitialize MediaPlayer for future use
        }
        restartTimer();
        // Pause the timer
        pauseTimer();
        circularProgressView.startProgressAnimationFromFull(timeInMilliseconds);
        circularProgressView.pauseAnimation();
        playPauseButton.setImageResource(R.drawable.ic_play1);
        // Update the flag to indicate the timer is paused
        isPaused = true;
    }



    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void restartTimer() {
        pauseTimer();
        timeInMilliseconds = getArguments() != null ? getArguments().getLong("timeInMilliseconds", 0) : 0;
        updateTimerDisplay();
        startTimer();
        playPauseButton.setImageResource(R.drawable.ic_pause1);
        isPaused = false;
    }

    private void restartTimerWithUpdatedTime() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
