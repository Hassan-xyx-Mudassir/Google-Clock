package com.fast.lhr.nu.edu.pk.googleclock.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.models.CityClock;

import java.util.List;

public class CityClockAdapter extends RecyclerView.Adapter<CityClockAdapter.CityClockViewHolder> {

    private List<CityClock> cityClocks;

    public CityClockAdapter(List<CityClock> cityClocks) {
        this.cityClocks = cityClocks;
        Log.d("CityClockAdapter", "Adapter initialized with cities: " + cityClocks.size());
    }

    @NonNull
    @Override
    public CityClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_clock, parent, false);
        return new CityClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityClockViewHolder holder, int position) {
        CityClock clock = cityClocks.get(position);

        holder.cityName.setText(clock.getCityName());
        holder.cityTime.setText(clock.getFormattedTime());
        holder.timeOffset.setText(clock.getOffset());

        Log.d("CityClockAdapter", "Binding city: " + clock.getCityName()); // Debug log
    }

    @Override
    public int getItemCount() {
        return cityClocks.size();
    }

    public void updateList(List<CityClock> updatedList) {
        this.cityClocks = updatedList;
        notifyDataSetChanged();
    }

    static class CityClockViewHolder extends RecyclerView.ViewHolder {
        TextView cityName, cityTime, timeOffset;

        public CityClockViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);
            cityTime = itemView.findViewById(R.id.city_time);
            timeOffset = itemView.findViewById(R.id.time_offset);
        }
    }
}
