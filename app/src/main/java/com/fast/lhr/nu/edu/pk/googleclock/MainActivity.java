package com.fast.lhr.nu.edu.pk.googleclock;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fast.lhr.nu.edu.pk.googleclock.fragments.AlarmFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.BedtimeFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.ClockFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.StopwatchFragment;
import com.fast.lhr.nu.edu.pk.googleclock.fragments.TimerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new AlarmFragment(), "Alarm");
            bottomNavigationView.setSelectedItemId(R.id.nav_alarm);
        }


        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String title = "";

            int itemId = item.getItemId();

            if (itemId == R.id.nav_alarm) {
                fragment = new AlarmFragment();
                title = "Alarm";
            } else if (itemId == R.id.nav_clock) {
                fragment = new ClockFragment();
                title = "Clock";
            } else if (itemId == R.id.nav_timer) {
                fragment = new TimerFragment();
                title = "Timer";
            } else if (itemId == R.id.nav_stopwatch) {
                fragment = new StopwatchFragment();
                title = "Stopwatch";
            }

            if (fragment != null) {
                loadFragment(fragment, title);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        if (!Objects.equals(title, "Clock")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClockFragment(), "ClockFragment").commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menu_screensaver) {
            showToast("Screensaver selected");
        } else if (itemId == R.id.menu_settings) {
            showToast("Settings selected");
        } else if (itemId == R.id.menu_privacy_policy) {
            showToast("Privacypolicy selected");
        } else if (itemId == R.id.menu_send_feedback) {
            showToast("Send feedback selected");
        } else if (itemId == R.id.menu_help) {
            showToast("Help selected");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
