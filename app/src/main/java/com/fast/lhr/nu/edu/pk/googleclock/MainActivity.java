package com.fast.lhr.nu.edu.pk.googleclock;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fast.lhr.nu.edu.pk.googleclock.fragments.AlarmFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.BedtimeFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.ClockFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.StopwatchFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.TimerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new AlarmFragment());
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_alarm) {
                fragment = new AlarmFragment();
            } else if (itemId == R.id.nav_clock) {
                fragment = new ClockFragment();
            } else if (itemId == R.id.nav_timer) {
                fragment = new TimerFragment();
            } else if (itemId == R.id.nav_stopwatch) {
                fragment = new StopwatchFragment();
            } else if (itemId == R.id.nav_bedtime) {
                fragment = new BedtimeFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
