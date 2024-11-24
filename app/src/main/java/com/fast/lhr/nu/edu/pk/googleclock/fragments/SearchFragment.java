package com.fast.lhr.nu.edu.pk.googleclock.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.adapters.CityAdapter;
import com.fast.lhr.nu.edu.pk.googleclock.models.CityClock;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CityAdapter adapter;
    private List<CityClock> cityClocks = new ArrayList<>(); // List of city clocks

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI elements
        recyclerView = view.findViewById(R.id.city_recycler);
        searchView = view.findViewById(R.id.search_view);

        // Hardcoded list of cities with time differences
        populateCityData();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CityAdapter(new ArrayList<>(), city -> {
            // Pass selected city to ClockFragment
            ClockFragment clockFragment = (ClockFragment) requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag("ClockFragment");

            if (clockFragment != null) {
                clockFragment.addCity(city); // Add the city to the ClockFragment
                Log.d("SearchFragment", "City passed to ClockFragment: " + city.getCityName()); // Debug log
            } else {
                Log.e("SearchFragment", "ClockFragment not found!"); // Error log
                Toast.makeText(getContext(), "Unable to add city.", Toast.LENGTH_SHORT).show();
            }


            // Return to ClockFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        recyclerView.setAdapter(adapter);

        // Initially hide RecyclerView
        recyclerView.setVisibility(View.GONE);

        // Setup SearchView listener
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateList(cityClocks); // Show all cities initially
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCities(query); // Filter cities when the user submits a query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCities(newText); // Filter cities as the user types
                return true;
            }
        });

        return view;
    }

    private void populateCityData() {
        cityClocks.clear();
        cityClocks.add(new CityClock("New York", -5 * 60 * 60)); // UTC -5 hours
        cityClocks.add(new CityClock("London", 0));              // UTC
        cityClocks.add(new CityClock("Tokyo", 9 * 60 * 60));     // UTC +9 hours
        cityClocks.add(new CityClock("Sydney", 11 * 60 * 60));   // UTC +11 hours
        cityClocks.add(new CityClock("Dubai", 4 * 60 * 60));     // UTC +4 hours
        cityClocks.add(new CityClock("Paris", 1 * 60 * 60));     // UTC +1 hour
        cityClocks.add(new CityClock("Los Angeles", -8 * 60 * 60)); // UTC -8 hours
        cityClocks.add(new CityClock("Berlin", 1 * 60 * 60));    // UTC +1 hour
    }

    private void filterCities(String query) {
        List<CityClock> filteredCities = new ArrayList<>();
        for (CityClock city : cityClocks) {
            if (city.getCityName().toLowerCase().contains(query.toLowerCase())) {
                filteredCities.add(city);
            }
        }
        adapter.updateList(filteredCities);
    }
}
