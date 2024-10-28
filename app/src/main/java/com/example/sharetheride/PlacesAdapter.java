package com.example.sharetheride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private final List<AutocompletePrediction> predictions;
    private final OnPlaceClickListener listener;

    public PlacesAdapter(List<AutocompletePrediction> predictions, OnPlaceClickListener listener) {
        this.predictions = predictions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        AutocompletePrediction prediction = predictions.get(position);
        holder.bind(prediction, listener);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(AutocompletePrediction prediction, OnPlaceClickListener listener) {
            textView.setText(prediction.getPrimaryText(null));
            itemView.setOnClickListener(v -> listener.onPlaceClick(prediction));
        }
    }

    public interface OnPlaceClickListener {
        void onPlaceClick(AutocompletePrediction prediction);
    }
}
