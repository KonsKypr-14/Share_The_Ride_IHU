package com.example.sharetheride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        /*
        holder.destination.setText(trip.getDestination());
        holder.date.setText(trip.getDate());
        holder.description.setText(trip.getDescription());
         */


        // Bind trip data to the UI components
        holder.tripId.setText("Trip ID: " + trip.gettrip_id());
        holder.organizerName.setText("Organizer name: " + trip.getOrganizerName());
        holder.organizer_name.setText("Organizer name: " + trip.getorganizer_name());
        holder.vehiclePlate.setText("Vehicle plate: " + trip.getvehicle_plate());
        holder.carModel.setText("Car model: " + trip.getcar_model());
        holder.startLocation.setText("Start: " + trip.getstart_location());
        holder.start_location_name .setText("Start: " + trip.getstart_location_name());
        holder.endLocation.setText("End: " + trip.getend_location());
        holder.end_location_name.setText("End: " + trip.getend_location_name());
        holder.maxPassengers.setText("Max Passengers: " + trip.getmax_passengers());
        holder.currentPassengers.setText("Current Passengers: " + trip.getcurrent_passengers());
        holder.tripStatus.setText("Status: " + trip.getTripStatus());
        if ( trip.getPricePerSeat() != null ){
            if (!trip.getPricePerSeat().toString().isEmpty()){
                holder.pricePerSeat.setText("Price/Seat: " + trip.getPricePerSeat());
            }else{
                holder.pricePerSeat.setVisibility(View.GONE);
            }
        }else{
            holder.pricePerSeat.setVisibility(View.GONE);
        }
        holder.price_per_seat.setText("Price/Seat: " + trip.getprice_per_seat());
        holder.rating.setText("Rating: " + trip.getRating());

        // If you have a date field, convert it to a string and set it
        if (trip.getcreated_at() != null) {
            holder.createdAt.setText("Created: " + trip.getcreated_at().toString());
        }

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        //TextView destination, date, description;
        TextView tripId;
        TextView organizerName;
        TextView organizer_name;
        TextView vehiclePlate;
        TextView carModel;
        TextView startLocation;
        TextView start_location_name ;
        TextView endLocation;
        TextView end_location_name;
        TextView maxPassengers;
        TextView currentPassengers;
        TextView tripStatus;
        TextView pricePerSeat;
        TextView price_per_seat;
        TextView rating;
        TextView createdAt;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            destination = itemView.findViewById(R.id.text_destination);
            date = itemView.findViewById(R.id.text_date);
            description = itemView.findViewById(R.id.text_description);
             */

            tripId = itemView.findViewById(R.id.trip_id);
            organizerName = itemView.findViewById(R.id.organizer_name);
            organizer_name = itemView.findViewById(R.id.organizer_name);
            vehiclePlate = itemView.findViewById(R.id.vehicle_plate);
            carModel = itemView.findViewById(R.id.car_model);
            startLocation = itemView.findViewById(R.id.start_location);
            start_location_name = itemView.findViewById(R.id.start_location);
            endLocation = itemView.findViewById(R.id.end_location);
            end_location_name = itemView.findViewById(R.id.end_location);
            maxPassengers = itemView.findViewById(R.id.max_passengers);
            currentPassengers = itemView.findViewById(R.id.current_passengers);
            tripStatus = itemView.findViewById(R.id.trip_status);
            pricePerSeat = itemView.findViewById(R.id.price_per_seat);
            price_per_seat = itemView.findViewById(R.id.price_per_seat);
            rating = itemView.findViewById(R.id.rating);
            createdAt = itemView.findViewById(R.id.created_at); // Add createdAt TextView in your layout
        }
    }
}
