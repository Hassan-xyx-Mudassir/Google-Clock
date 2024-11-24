package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.adapters.CityClockAdapter;
import com.fast.lhr.nu.edu.pk.googleclock.database.ClockDatabase;
import com.fast.lhr.nu.edu.pk.googleclock.database.entities.ClockEntity;
import com.fast.lhr.nu.edu.pk.googleclock.models.CityClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClockFragment extends Fragment {
    private TextView currentTimeTextView;
    private TextView currentDateTextView;
    private FloatingActionButton fabAddCity;
    private RecyclerView cityClockRecycler;
    private CityClockAdapter cityClockAdapter;
    private List<CityClock> cityClocks = new ArrayList<>();
    private Handler handler;
    private Runnable timeUpdater;
    private ClockDatabase clockDatabase;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cityClocks", new ArrayList<>(cityClocks));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            cityClocks = (List<CityClock>) savedInstanceState.getSerializable("cityClocks");
        }

        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        currentTimeTextView = view.findViewById(R.id.currentTime);
        currentDateTextView = view.findViewById(R.id.currentDate);
        fabAddCity = view.findViewById(R.id.fab_add_city);
        cityClockRecycler = view.findViewById(R.id.city_clock_recycler);

        // Setup RecyclerView
        cityClockRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        cityClockAdapter = new CityClockAdapter(cityClocks);
        cityClockRecycler.setAdapter(cityClockAdapter);

        // Add swipe-to-delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CityClock clock = cityClocks.get(position);
                cityClocks.remove(position);
                cityClockAdapter.notifyItemRemoved(position);
                deleteClockFromDatabase(clock);
            }
        }).attachToRecyclerView(cityClockRecycler);

        // Floating Action Button
        fabAddCity.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment(), "SearchFragment").addToBackStack("ClockFragment").commit();
        });

        updateDateTime();

        handler = new Handler(Looper.getMainLooper());
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(timeUpdater);

        // Initialize database
        clockDatabase = ClockDatabase.getInstance(requireContext());

        // Load saved clocks from database
        loadSavedClocks();

        return view;
    }

    private void updateDateTime() {
        Calendar calendar = Calendar.getInstance();

        String currentTime = String.format(Locale.getDefault(), "%tI:%tM %tp", calendar, calendar, calendar);
        currentTimeTextView.setText(currentTime);

        String currentDate = String.format(Locale.getDefault(), "%tA, %tB %te", calendar, calendar, calendar);
        currentDateTextView.setText(currentDate);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addCity(CityClock city) {
        if (city != null) {
            cityClocks.add(city);
            cityClockAdapter.notifyDataSetChanged(); // Notify adapter
            cityClockRecycler.post(() -> cityClockRecycler.invalidate()); // Force UI refresh
            saveClock(city);
        } else {
            Log.e("ClockFragment", "City is null, cannot add!");
        }
    }

    // Add a clock to the list and save it in the database
    private void saveClock(CityClock cityClock) {
        if (cityClock != null) {
            // Save to database on a background thread
            new Thread(() -> {
                ClockEntity clockEntity = toClockEntity(cityClock);

                // Insert into database and fetch the generated ID
                long generatedId = clockDatabase.clockDao().insertClock(clockEntity);

                // Update CityClock with the generated ID
                cityClock.setId((int) generatedId);

                Log.d("ClockFragment", "Inserted clock with ID: " + generatedId);
            }).start();
        } else {
            Log.e("ClockFragment", "CityClock is null. Cannot save!");
        }
    }

    // Delete a clock from the database
    private void deleteClockFromDatabase(CityClock cityClock) {
        // Ensure the clock has a valid ID
        new Thread(() -> {
            // Convert CityClock to ClockEntity for deletion
            ClockEntity clockEntity = toClockEntity(cityClock);

            clockDatabase.clockDao().deleteClock(clockEntity);
            Log.d("ClockFragment", "Deleted clock with ID: " + cityClock.getId());
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadSavedClocks() {
        new Thread(() -> {
            List<ClockEntity> savedEntities = clockDatabase.clockDao().getAllClocks();
            if (savedEntities != null && !savedEntities.isEmpty()) {
                cityClocks.clear();

                // Convert each ClockEntity to CityClock
                for (ClockEntity entity : savedEntities) {
                    cityClocks.add(toCityClock(entity));
                }

                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    cityClockAdapter.notifyDataSetChanged();
                    Log.d("ClockFragment", "Loaded clocks from database: " + cityClocks.size());
                });
            }
        }).start();
    }

    private ClockEntity toClockEntity(CityClock cityClock) {
        ClockEntity clockEntity = new ClockEntity(
                cityClock.getCityName(),
                cityClock.getOffset(),
                cityClock.getFormattedTime()
        );

        if (cityClock.getId() != 0) {
            clockEntity.setId(cityClock.getId()); // Preserve the ID for deletion
        }

        return clockEntity;
    }

    private CityClock toCityClock(ClockEntity clockEntity) {
        String[] parts = clockEntity.getOffset().replace("GMT ", "").split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = (parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
        int gmtOffsetInSeconds = (hours * 3600) + (minutes * 60);

        return new CityClock(clockEntity.getId(), clockEntity.getCityName(), gmtOffsetInSeconds);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && timeUpdater != null) {
            handler.removeCallbacks(timeUpdater);
        }
    }
}
