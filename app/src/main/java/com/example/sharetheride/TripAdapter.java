package com.example.sharetheride;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;

    FirebaseUser user;

    private static Context context;
    private final OnTripDataUpdatedListener onTripDataUpdatedListener;

    // Define the interface for the callback
    public interface OnTripDataUpdatedListener {
        void onTripDataUpdated();
    }

    public TripAdapter(List<Trip> tripList, OnTripDataUpdatedListener listener) {
        this.tripList = tripList;
        this.onTripDataUpdatedListener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        this.context = parent.getContext();
        return new TripViewHolder(view);
    }


//    public static class TripViewHolder extends RecyclerView.ViewHolder {
    // UI elements for Trip item
//
//        public TripViewHolder(View itemView) {
//            super(itemView);
//            // Initialize UI elements
//        }
//
//        public void bind(Trip trip) {
//            // Bind trip data to the view
//        }
//    }


    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {

        user = ((MainActivity) context).getUser();  // Pass the logged-in user

        Trip trip = tripList.get(position);        // Bind trip data to the view
        holder.bind(trip);
        holder.bind_listener(this.onTripDataUpdatedListener);
        // Notify the fragment to reload data after successful update
        /*
        holder.destination.setText(trip.getDestination());
        holder.date.setText(trip.getDate());
        holder.description.setText(trip.getDescription());
         */


        // Bind trip data to the UI components
        for (int i = 0; i < trip.passengers.size(); i++) {
            if (trip.passengers.get(i).getpassenger_id().toString().equals(user.getUid())){
                holder.join_ride_button.setText("Already Joined");
                holder.join_ride_button.setClickable(false);
            }
        }

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

        OnTripDataUpdatedListener onTripDataUpdatedListener;

        public void bind_listener(OnTripDataUpdatedListener listener) {
            // Bind trip data to the view
            this.onTripDataUpdatedListener = listener;
        }

        public void bind(Trip trip) {
            // Bind trip data to the view
        }


        //TextView destination, date, description;
        TextView tripId;
        TextView organizerName;
        TextView organizer_name;
        TextView vehiclePlate;
        TextView carModel;
        TextView startLocation;
        TextView start_location_name;
        TextView endLocation;
        TextView end_location_name;
        TextView maxPassengers;
        TextView currentPassengers;
        TextView tripStatus;
        TextView pricePerSeat;
        TextView price_per_seat;
        TextView rating;
        TextView createdAt;
        String name, surname, organizer_id, current_passengers;
        Integer positionAvailable;
        FirebaseUser user;
        Button view_on_map_button, join_ride_button;
        Double latitude_start, longitude_start, latitude_end, longitude_end;
        String trip_id_text;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Trip> tripListTemp;

        List<Trip.Passenger> passengers;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            destination = itemView.findViewById(R.id.text_destination);
            date = itemView.findViewById(R.id.text_date);
            description = itemView.findViewById(R.id.text_description);
             */

            view_on_map_button = itemView.findViewById(R.id.view_on_map);
            join_ride_button = itemView.findViewById(R.id.join_ride);

            view_on_map_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String regex = "lat/lng:\\s*\\(([-+]?\\d*\\.\\d+),\\s*([-+]?\\d*\\.\\d+)\\)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher_start = pattern.matcher(startLocation.getText().toString());
                    Matcher matcher_end = pattern.matcher(endLocation.getText().toString());

                    // Check if the pattern matches
                    if (matcher_start.find()) {
                        // Parse latitude and longitude
                        latitude_start = Double.parseDouble(matcher_start.group(1));
                        longitude_start = Double.parseDouble(matcher_start.group(2));
                    }
                    if (matcher_end.find()) {
                        // Parse latitude and longitude
                        latitude_end = Double.parseDouble(matcher_end.group(1));
                        longitude_end = Double.parseDouble(matcher_end.group(2));
                    }
                    String uri = "http://maps.google.com/maps?saddr=" + latitude_start + "," + longitude_start + "&daddr=" + latitude_end + "," + longitude_end;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);
                }
            });

            tripListTemp = new ArrayList<>();

            db.collection("trips") // Change to the correct collection name
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Trip trip = document.toObject(Trip.class);
                            tripListTemp.add(trip);
                        }
                        // Update data in adapter
                        //tripAdapter.updateTrips(tripList);
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors here
                        Log.e("DisplayTipsFragment", "Error fetching data", e);
                    });

//            DocumentReference tripDocRef = db.collection("trips").document(tripDocId);
//            tripDocRef.update("passengers", passengers,
//                            "current_passengers", current_passengers)
//                    .addOnSuccessListener(aVoid -> {
//                        // Successfully updated
//                        onTripDataUpdatedListener.onTripDataUpdated(); //Reload all the Trips from DB in order to update the current Passengers
//                        Toast.makeText(context, "Passenger slot updated successfully!", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        // Error updating
//                        Toast.makeText(context, "Error updating passenger slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });

            join_ride_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trip_id_text = tripId.getText().toString().replace("Trip ID: ", "");
                    // Reference to the specific document in "trips" collection
                    DocumentReference tripDocRef = db.collection("trips").document(trip_id_text);
// Check if the document exists and retrieve the `passengers` array
                    tripDocRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                // Document exists, retrieve the passengers array
                                List<Map<String, Object>> passengers = (List<Map<String, Object>>) document.get("passengers");
                                if (passengers != null) {
                                    boolean hasAvailableSlot = false;
                                    // Iterate through the passengers list to check for availability
                                    positionAvailable = 0; //First position is the 0 of the array.
                                    for (Map<String, Object> passenger : passengers) {
                                        Boolean isAvailable = (Boolean) passenger.get("available");
                                        if (isAvailable != null && isAvailable) {
                                            hasAvailableSlot = true;
                                            break;
                                        }
                                        positionAvailable = positionAvailable + 1;
                                    }
                                    //confirmJoinTrip(trip.getDocId());
                                    confirmJoinTrip(trip_id_text, positionAvailable);
                                    if (hasAvailableSlot) {
                                        Toast.makeText(context, "There is an available slot. Position: " + positionAvailable, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "No available slot.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Passengers list is empty or not found.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Document does not exist
                                Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle any errors
                            Toast.makeText(context, "Failed to retrieve document: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            tripId = itemView.findViewById(R.id.trip_id);
            organizerName = itemView.findViewById(R.id.organizer_name);
            organizer_name = itemView.findViewById(R.id.organizer_name);
            vehiclePlate = itemView.findViewById(R.id.vehicle_plate);
            carModel = itemView.findViewById(R.id.car_model);
            startLocation = itemView.findViewById(R.id.start_location);
            start_location_name = itemView.findViewById(R.id.start_location_name);
            endLocation = itemView.findViewById(R.id.end_location);
            end_location_name = itemView.findViewById(R.id.end_location_name);
            maxPassengers = itemView.findViewById(R.id.max_passengers);
            currentPassengers = itemView.findViewById(R.id.current_passengers);
            tripStatus = itemView.findViewById(R.id.trip_status);
            pricePerSeat = itemView.findViewById(R.id.price_per_seat);
            price_per_seat = itemView.findViewById(R.id.price_per_seat);
            rating = itemView.findViewById(R.id.rating);
            createdAt = itemView.findViewById(R.id.created_at); // Add createdAt TextView in your layout
        }

        private void confirmJoinTrip(String tripDocId, Integer positionAvailable) {
            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Join the trip")
                    .setMessage("Would you like to join the trip?")
                    .setPositiveButton("Yes", (dialog, which) -> joinTrip(tripDocId, positionAvailable))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        private void joinTrip(String tripDocId, Integer positionAvailable) {
            //Update the database with the Passenger Name, Available False and Passenger ID.

//// Step 1: Retrieve the document
//            tripDocRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    if (document.exists()) {
//                        // Step 2: Get the `passengers` array
//                        List<Map<String, Object>> passengers = (List<Map<String, Object>>) document.get("passengers");
//
//                        if (passengers != null) {
//                            // Step 3: Modify the specific item in the array
//                            for (Map<String, Object> passenger : passengers) {
//                                if (passenger.get("passenger_id").equals("specific_passenger_id")) {  // Replace with the ID of the passenger you want to update
//                                    passenger.put("available", false);  // Update the value
//                                    break;
//                                }
//                            }
//
//                            // Step 4: Update the modified array in Firestore
//                            tripDocRef.update("passengers", passengers)
//                                    .addOnSuccessListener(aVoid -> {
//                                        // Successfully updated
//                                        Toast.makeText(context, "Passenger updated successfully!", Toast.LENGTH_SHORT).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Error updating
//                                        Toast.makeText(context, "Error updating passenger: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    });
//                        } else {
//                            Toast.makeText(context, "Passengers array not found.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(context, "Document does not exist.", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // Error retrieving document
//                    Toast.makeText(context, "Error fetching document: " + task.getException(), Toast.LENGTH_SHORT).show();
//                }
//            });

            DocumentReference tripDocRef = db.collection("trips").document(tripDocId);
            user = ((MainActivity) context).getUser();  // Pass the logged-in user
            // Assuming 'user' and 'context' are available in this scope
            db.collection("users_collection")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            name = documentSnapshot.getString("name");
                            surname = documentSnapshot.getString("surname");
                            tripDocRef.get().addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        organizer_id = document.get("organizer_id").toString();

                                        if (!organizer_id.contentEquals(user.getUid().toString())) {
                                            // Step 2: Get the `passengers` array
                                            List<Map<String, Object>> passengers = (List<Map<String, Object>>) document.get("passengers");
                                            current_passengers = document.getString("current_passengers");

                                            if (passengers != null && positionAvailable >= 0 && positionAvailable < passengers.size()) {
                                                // Step 3: Modify the specific item in the array at the given index
                                                Map<String, Object> passenger = passengers.get(positionAvailable);

                                                // Check if this spot is indeed available
                                                Boolean isAvailable = (Boolean) passenger.get("available");
                                                if (isAvailable != null && isAvailable) {
                                                    passenger.put("available", false); // Mark it as no longer available
                                                    passenger.put("passenger_id", user.getUid()); // Insert the new passenger's ID
                                                    passenger.put("name", name + ' ' + surname); // Insert the new passenger's ID
                                                    current_passengers = String.valueOf(Integer.parseInt(current_passengers) + 1);
                                                    // Step 4: Update the modified array in Firestore
                                                    tripDocRef.update("passengers", passengers,
                                                                    "current_passengers", current_passengers)
                                                            .addOnSuccessListener(aVoid -> {
                                                                // Successfully updated
                                                                onTripDataUpdatedListener.onTripDataUpdated(); //Reload all the Trips from DB in order to update the current Passengers
                                                                Toast.makeText(context, "Passenger slot updated successfully!", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                // Error updating
                                                                Toast.makeText(context, "Error updating passenger slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    Toast.makeText(context, "The specified slot is not available.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Passengers array not found or invalid index.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "You cannot join your own trips.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Document does not exist.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Error retrieving document
                                    Toast.makeText(context, "Error fetching document: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(context, "You cannot join your own trips", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, "Failed to load trips", Toast.LENGTH_SHORT).show());

        }
    }

    public void updateTrips(List<Trip> newTrips) {
        this.tripList.clear();
        this.tripList.addAll(newTrips);
        notifyDataSetChanged();
    }
}