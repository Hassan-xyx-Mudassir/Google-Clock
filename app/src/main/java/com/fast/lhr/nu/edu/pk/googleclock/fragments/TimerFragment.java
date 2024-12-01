package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fast.lhr.nu.edu.pk.googleclock.R;

public class TimerFragment extends Fragment {

    private TextView timerDisplay;
    private StringBuilder inputBuffer = new StringBuilder();
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeInMilliseconds = 0;
    private ImageButton startButton;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        // Initialize keypad buttons
        Button button1 = rootView.findViewById(R.id.button_1);
        Button button2 = rootView.findViewById(R.id.button_2);
        Button button3 = rootView.findViewById(R.id.button_3);
        Button button4 = rootView.findViewById(R.id.button_4);
        Button button5 = rootView.findViewById(R.id.button_5);
        Button button6 = rootView.findViewById(R.id.button_6);
        Button button7 = rootView.findViewById(R.id.button_7);
        Button button8 = rootView.findViewById(R.id.button_8);
        Button button9 = rootView.findViewById(R.id.button_9);
        Button button0 = rootView.findViewById(R.id.button_0);
        Button button00 = rootView.findViewById(R.id.button_00);
        Button buttonBackspace = rootView.findViewById(R.id.button_backspace);

        // Set background tint for the buttons
        button1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button8.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button9.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button0.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        button00.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1c1d1f")));
        buttonBackspace.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#014a77")));

        // Initialize views
        timerDisplay = rootView.findViewById(R.id.timer_display);
        GridLayout keypadLayout = rootView.findViewById(R.id.keypad_layout);
        startButton = rootView.findViewById(R.id.start_button);

        // Initially hide the start button
        startButton.setVisibility(View.INVISIBLE);

        // Set up keypad buttons
        for (int i = 0; i < keypadLayout.getChildCount(); i++) {
            View view = keypadLayout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;

                // Skip backspace button to prevent it from triggering handleKeypadInput
                if (button.getId() == R.id.button_backspace) {
                    continue; // Skip this iteration
                }

                // Add click listener for keypad input handling
                button.setOnClickListener(v -> handleKeypadInput(button.getText().toString()));

                // Add touch listener for dynamic corner radius adjustment
                button.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        GradientDrawable background = (GradientDrawable) button.getBackground();
                        background.setCornerRadius(50); // Set to square
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        GradientDrawable background = (GradientDrawable) button.getBackground();
                        background.setCornerRadius(800); // Reset to rounded
                    }
                    return false;
                });
            }
        }


        // Handle start button click
        startButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });

        // Add OnClickListener for backspace button
        buttonBackspace.setOnClickListener(v -> {
            try {
                Log.d("TimerFragment", "Backspace pressed. Current input buffer: " + inputBuffer);

                if (inputBuffer.length() > 0) {
                    inputBuffer.deleteCharAt(inputBuffer.length() - 1); // Remove the last character
                    Log.d("TimerFragment", "Updated input buffer: " + inputBuffer);
                    updateTimerDisplay();
                } else {
                    Log.d("TimerFragment", "Input buffer is empty, nothing to remove.");
                }

                // Hide the start button if the input buffer is empty
                if (inputBuffer.length() == 0) {
                    startButton.setVisibility(View.INVISIBLE);
                    timerDisplay.setText("00h 00m 00s"); // Clear the timer display
                }
            } catch (Exception e) {
                Log.e("TimerFragment", "Error while handling backspace: ", e);
            }

    });




        return rootView;
    }

    private void handleKeypadInput(String input) {
        // Add digit(s) to input buffer
        if (inputBuffer.length() < 6) { // Max 6 digits for HHMMSS
            inputBuffer.append(input);
            updateTimerDisplay();

            // Show the start button as soon as there is input
            if (inputBuffer.length() > 0) {
                startButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateTimerDisplay() {
        // Parse input buffer to HH:MM:SS format
        String paddedInput = String.format("%6s", inputBuffer).replace(' ', '0');
        String hours = paddedInput.substring(0, 2);
        String minutes = paddedInput.substring(2, 4);
        String seconds = paddedInput.substring(4, 6);
        timerDisplay.setText(String.format("%sh %sm %ss", hours, minutes, seconds));

        // Update time in milliseconds
        timeInMilliseconds = (Integer.parseInt(hours) * 3600L +
                Integer.parseInt(minutes) * 60L +
                Integer.parseInt(seconds)) * 1000L;
    }

    private void startTimer() {
        if (timeInMilliseconds > 0) {
            // Create an instance of TimerPopupFragment
            TimerPopupFragment popupFragment = new TimerPopupFragment();

            // Pass the timer values to the popup fragment
            Bundle bundle = new Bundle();
            bundle.putLong("timeInMilliseconds", timeInMilliseconds); // Pass the total time in milliseconds
            popupFragment.setArguments(bundle);

            // Transition to TimerPopupFragment
            requireActivity()

                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, popupFragment) // Replace the current fragment
                    .addToBackStack(null) // Optional: Add to back stack
                    .commit();

        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timerDisplay.setText("00h 00m 00s");
        inputBuffer.setLength(0); // Clear input buffer
        startButton.setVisibility(View.INVISIBLE); // Hide start button
    }
}
