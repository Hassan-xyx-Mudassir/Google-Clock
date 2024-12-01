package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import com.fast.lhr.nu.edu.pk.googleclock.CircularProgressView;
import com.fast.lhr.nu.edu.pk.googleclock.R;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import androidx.fragment.app.Fragment;
import android.animation.AnimatorSet;


public class StopwatchFragment extends Fragment {
    // Additional variables to track laps
    private int lapNumber = 0;
    private int lastLapTimeInMilliseconds = 0; // Time in milliseconds for the last lap
    private ImageButton playButton, resetButton, lapButton;
    private TextView timeDisplayTop, timeDisplayBottom;
    private LinearLayout lapList; // To display laps
    private FrameLayout frameLayout;
    private ImageView circle_border;
    private boolean isRunning = false;
    private boolean isPaused = false; // To check if the stopwatch is paused
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private int milliseconds = 0;
    private boolean hasSlidUp = false;
    // Flag to track if the views have already slid up
    private CircularProgressView circularProgressView;


    private int originalButtonWidth;
    private int originalButtonHeight;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliseconds++;

            if (milliseconds == 100) {
                milliseconds = 0;
                seconds++;
            }

            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }

            if (minutes == 60) {
                minutes = 0;
                hours++;
            }

            updateTimeDisplay();

            handler.postDelayed(this, 10); // Update every 10ms
        }
    };

    private void updateTimeDisplay() {
        // Update the display based on the elapsed time
        if (hours > 0) {
            timeDisplayTop.setText(String.format("%02d", hours));
            timeDisplayBottom.setText(String.format("%02d:%02d", minutes, seconds));
        } else if (minutes > 0) {
            timeDisplayTop.setText(String.format("%02d:%02d", minutes, seconds));
            timeDisplayBottom.setText(String.format("%02d", milliseconds));
        } else {
            timeDisplayTop.setText(String.format("%02d", seconds));
            timeDisplayBottom.setText(String.format("%02d", milliseconds));
        }
    }
    private void slideUpStopwatchAndLapList() {
        // Slide the time display (top and bottom), circle border, and lap list up by 60px
        ObjectAnimator slideTop = ObjectAnimator.ofFloat(timeDisplayTop, "translationY", 0f, -25f);
        ObjectAnimator slideBottom = ObjectAnimator.ofFloat(timeDisplayBottom, "translationY", 0f, -25f);
        ObjectAnimator slideCircle = ObjectAnimator.ofFloat(circularProgressView, "translationY", 0f, -25f);
        ObjectAnimator slideLapList = ObjectAnimator.ofFloat(lapList, "translationY", 0f, -25f);
        ObjectAnimator slideFrameLayout = ObjectAnimator.ofFloat(frameLayout, "translationY", 0f, -25f);

        // Adjust padding dynamically (ensure non-negative values)
        int newPaddingTop = Math.max(lapList.getPaddingTop() + 5, 0);
        lapList.setPadding(
                lapList.getPaddingLeft(),
                newPaddingTop,
                lapList.getPaddingRight(),
                lapList.getPaddingBottom()
        );
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin-100, params.rightMargin, params.bottomMargin);
        frameLayout.setLayoutParams(params);
        // Combine all animations into an AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideTop, slideBottom, slideCircle, slideLapList, slideFrameLayout);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    private void slideDownStopwatchAndLapList() {
        // Slide the time display (top and bottom), circle border, and lap list back to the original position
        ObjectAnimator slideTop = ObjectAnimator.ofFloat(timeDisplayTop, "translationY", -25f, 0f);
        ObjectAnimator slideBottom = ObjectAnimator.ofFloat(timeDisplayBottom, "translationY", -25f, 0f);
        ObjectAnimator slideCircle = ObjectAnimator.ofFloat(circularProgressView, "translationY", -25f, 0f);
        ObjectAnimator slideLapList = ObjectAnimator.ofFloat(lapList, "translationY", -25f, 0f);
        ObjectAnimator slideFrameLayout = ObjectAnimator.ofFloat(frameLayout, "translationY", -25f, 0f);

        // Adjust padding dynamically (ensure non-negative values)
        int newPaddingTop = Math.max(lapList.getPaddingTop() - 5, 0);
        lapList.setPadding(
                lapList.getPaddingLeft(),
                newPaddingTop,
                lapList.getPaddingRight(),
                lapList.getPaddingBottom()
        );
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin+100, params.rightMargin, params.bottomMargin);
        frameLayout.setLayoutParams(params);

        // Combine all animations into an AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideTop, slideBottom, slideCircle, slideLapList, slideFrameLayout);
        animatorSet.setDuration(300);
        animatorSet.start();
    }



    private void startBlinking() {
        // Create a blinking animation
        AlphaAnimation blinkAnimation = new AlphaAnimation(1.0f, 0.0f);
        blinkAnimation.setDuration(500); // Blink duration (500ms for fade in/out)
        blinkAnimation.setRepeatMode(AlphaAnimation.REVERSE); // Reverse animation
        blinkAnimation.setRepeatCount(AlphaAnimation.INFINITE); // Infinite blinking
        timeDisplayTop.startAnimation(blinkAnimation);
        timeDisplayBottom.startAnimation(blinkAnimation);
    }

    private void stopBlinking() {
        // Stop the blinking animation
        timeDisplayTop.clearAnimation();
        timeDisplayBottom.clearAnimation();
    }

    private void animateButton(View v, boolean enlarge) {
        // Get the current button layout params
        LayoutParams layoutParams = v.getLayoutParams();

        // If this is the first time, store the original button size
        if (originalButtonWidth == 0 && originalButtonHeight == 0) {
            originalButtonWidth = layoutParams.width;
            originalButtonHeight = layoutParams.height;
        }

        if (enlarge) {
            // Enlarge the button size by 50%
            layoutParams.width = (int) (originalButtonWidth * 1.5);
            v.setLayoutParams(layoutParams);

            // Change the button background
            v.setBackgroundResource(R.drawable.button_rounded); // Enlarge and set rounded background
        } else {
            // Reset the button size back to the original
            layoutParams.width = originalButtonWidth;
            layoutParams.height = originalButtonHeight;
            v.setLayoutParams(layoutParams);

            // Reset background to the original background
            v.setBackgroundResource(R.drawable.circular_button_background); // Reset to original background
        }
    }

    private void slideResetButtonLeft() {
        // Slide the reset button to the left using ObjectAnimator
        ObjectAnimator slideLeft = ObjectAnimator.ofFloat(resetButton, "translationX", 0f, -50f); // Move 50px to the left
        slideLeft.setDuration(300); // Set the duration for the slide effect
        slideLeft.start();
    }

    private void slideResetButtonRight() {
        // Slide the reset button back to its original position
        ObjectAnimator slideRight = ObjectAnimator.ofFloat(resetButton, "translationX", -50f, 0f); // Move back to the original position
        slideRight.setDuration(300); // Set the duration for the slide effect
        slideRight.start();
    }

    private void slideResetButtonRightFromLeft() {
        // Slide the reset button from the left (for when stopwatch starts)
        ObjectAnimator slideFromLeft = ObjectAnimator.ofFloat(resetButton, "translationX", -200f, 0f); // Move 200px from left
        slideFromLeft.setDuration(300);
        slideFromLeft.start();
    }

    // Add Lap to the list
    private void addLap() {
        lapNumber++;

        // Calculate total time in milliseconds for the current lap
        int currentLapTimeInMilliseconds = (hours * 3600000) + (minutes * 60000) + (seconds * 1000) + (milliseconds * 10);

        // Calculate the difference from the last lap
        int difference = currentLapTimeInMilliseconds - lastLapTimeInMilliseconds;

        // Update last lap time
        lastLapTimeInMilliseconds = currentLapTimeInMilliseconds;

        // Convert difference to hh:mm:ss:ms
        int diffHours = difference / 3600000;
        int diffMinutes = (difference % 3600000) / 60000;
        int diffSeconds = (difference % 60000) / 1000;
        int diffMilliseconds = (difference % 1000) / 10;

        // Create the display text
        String lapTimeText = String.format(
                "  # %d  %02d:%02d:%02d:%02d  %02d:%02d:%02d:%02d", lapNumber,
                diffHours, diffMinutes, diffSeconds, diffMilliseconds,
                hours, minutes, seconds, milliseconds
        );

        // Add the lap to the list
        TextView lapTime = new TextView(getContext());
        lapTime.setText(lapTimeText);
        lapTime.setTextSize(18); // Adjust text size
        lapTime.setPadding(32, 8, 32, 8); // Padding for better layout
        lapList.addView(lapTime, 0); // Add at the top

        // Force layout update after adding a lap
        lapList.requestLayout();
    }

    private void slideLapButtonRight() {
        // Slide the lap button 50px to the right
        ObjectAnimator slideLapButton = ObjectAnimator.ofFloat(lapButton, "translationX", 0f, 50f); // Move 50px to the right
        slideLapButton.setDuration(300); // Set the duration for the slide effect
        slideLapButton.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        playButton = rootView.findViewById(R.id.playButton);
        resetButton = rootView.findViewById(R.id.resetButton);
        lapButton = rootView.findViewById(R.id.lapButton); // Lap button
        timeDisplayTop = rootView.findViewById(R.id.timeDisplayTop);
        timeDisplayBottom = rootView.findViewById(R.id.timeDisplayBottom);
        lapList = rootView.findViewById(R.id.lapList); // Lap list
        // circle_border=rootView.findViewById(R.id.circle_border)  ;
        circularProgressView = rootView.findViewById(R.id.circularProgressView);
        frameLayout=rootView.findViewById(R.id.frameLayout);
        // Initially hide the lap button
        lapButton.setVisibility(View.INVISIBLE);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    handler.removeCallbacks(runnable);
                    playButton.setImageResource(R.drawable.ic_play); // Change to play icon
                    animateButton(v, false); // Reset button size and background
                    //stopBlinking(); // Stop blinking
                    resetButton.setVisibility(View.VISIBLE); // Show reset button when paused
                    slideResetButtonRight(); // Slide reset button back to the right
                    isPaused = true;
                    startBlinking();
                    // Hide the Lap button when the stopwatch is paused
                    lapButton.setVisibility(View.INVISIBLE);
                } else {
                    handler.postDelayed(runnable, 10); // Start the stopwatch
                    playButton.setImageResource(R.drawable.ic_pause); // Change to pause icon
                    animateButton(v, true); // Enlarge button and change background
                    resetButton.setVisibility(View.VISIBLE); // Show reset button when running
                    slideResetButtonLeft(); // Slide reset button to the left
                    stopBlinking(); // Stop blinking
                    isPaused = false; // Mark as running

                    // Show the Lap button and slide it 50px to the right when the stopwatch starts
                    lapButton.setVisibility(View.VISIBLE);
                    slideLapButtonRight();
                }
                isRunning = !isRunning; // Toggle running state
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                seconds = 0;
                minutes = 0;
                hours = 0;
                milliseconds = 0;
                lastLapTimeInMilliseconds=0;
                lapNumber=0;

                // Update the display to reset values
                updateTimeDisplay();

                // Hide the reset button
                resetButton.setVisibility(View.GONE);

                // Reset the play button to play icon and stop the stopwatch
                playButton.setImageResource(R.drawable.ic_play);
                isRunning = false;
                isPaused = false;

                // Reset play button to original size and background
                animateButton(playButton, false);
                hasSlidUp = false;
                // Clear lap list
                lapList.removeAllViews();
                slideDownStopwatchAndLapList();

                circularProgressView.resetProgress();
                // Hide the Lap button when stopwatch is reset
                lapButton.setVisibility(View.INVISIBLE);
            }
        });


        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    // Slide views up only once
                    if (!hasSlidUp) {
                        slideUpStopwatchAndLapList();
                        hasSlidUp = true;
                    }
                    // Add a new lap to the list
                    addLap();

                    // Reset and restart the circular progress animation

                    circularProgressView.startNewLap();
                    circularProgressView.startProgressAnimation(1000); // 10 seconds for a full circle

                } else {
                    Toast.makeText(getContext(), "Start the stopwatch to record laps.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRunning) {
            handler.removeCallbacks(runnable);
        }
        // Ensure the Lap button is hidden when the fragment stops
        lapButton.setVisibility(View.INVISIBLE);
    }
}
