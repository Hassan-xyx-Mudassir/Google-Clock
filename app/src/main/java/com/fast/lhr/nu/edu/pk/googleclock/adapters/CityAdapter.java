package com.fast.lhr.nu.edu.pk.googleclock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast.lhr.nu.edu.pk.googleclock.R;
import com.fast.lhr.nu.edu.pk.googleclock.models.CityClock;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private List<CityClock> cityClocks;
    private final OnCityClickListener listener;

    public CityAdapter(List<CityClock> cityClocks, OnCityClickListener listener) {
        this.cityClocks = cityClocks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        CityClock cityClock = cityClocks.get(position);

        holder.cityName.setText(cityClock.getCityName());
        holder.itemView.setOnClickListener(v -> listener.onCityClick(cityClock));
    }

    @Override
    public int getItemCount() {
        return cityClocks.size();
    }

    public void updateList(List<CityClock> updatedCities) {
        this.cityClocks = updatedCities;
        notifyDataSetChanged();
    }

    public interface OnCityClickListener {
        void onCityClick(CityClock cityClock);
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);
        }
    }
}
