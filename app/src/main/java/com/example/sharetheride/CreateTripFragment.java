package com.example.sharetheride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTripFragment extends Fragment {

    private EditText tripIdInput, organizerIdInput, organizerNameInput, vehiclePlateInput, carModelInput;
    private EditText startLocationLatInput, startLocationLngInput, endLocationLatInput, endLocationLngInput;
    private EditText maxPassengersInput, currentPassengersInput, tripStatusInput, pricePerSeatInput, ratingInput;
    private Button createTripButton;

    private FirebaseFirestore db;
    FirebaseUser user;

    public CreateTripFragment() {
        // Required empty public constructor
    }

    private static final String TEXT_ID = "text_id";

    public static CreateTripFragment newInstance(@StringRes int textId) {

        CreateTripFragment frag = new CreateTripFragment();

        Bundle args = new Bundle();
        args.putInt(TEXT_ID, textId);
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        user = ((MainActivity) getActivity()).getUser();  // Pass the logged-in user

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        tripIdInput = view.findViewById(R.id.trip_id_input);
        organizerIdInput = view.findViewById(R.id.organizer_id_input);
        organizerNameInput = view.findViewById(R.id.organizer_name_input);
        vehiclePlateInput = view.findViewById(R.id.vehicle_plate_input);
        carModelInput = view.findViewById(R.id.car_model_input);
        startLocationLatInput = view.findViewById(R.id.start_location_input);
        //startLocationLngInput = view.findViewById(R.id.start_location_lng_input);
        endLocationLatInput = view.findViewById(R.id.end_location_input);
        //endLocationLngInput = view.findViewById(R.id.end_location_lng_input);
        maxPassengersInput = view.findViewById(R.id.max_passengers_input);
        currentPassengersInput = view.findViewById(R.id.current_passengers_input);
        tripStatusInput = view.findViewById(R.id.trip_status_input);
        pricePerSeatInput = view.findViewById(R.id.price_per_seat_input);
        ratingInput = view.findViewById(R.id.rating_input);
        createTripButton = view.findViewById(R.id.create_trip_button);

        // Set button click listener
        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTrip();
            }
        });

        return view;
    }

    private void createTrip() {
        String tripId = tripIdInput.getText().toString().trim();
        String organizerId = organizerIdInput.getText().toString().trim();
        String organizerName = organizerNameInput.getText().toString().trim();
        String vehiclePlate = vehiclePlateInput.getText().toString().trim();
        String carModel = carModelInput.getText().toString().trim();

        String startLat = startLocationLatInput.getText().toString().trim();
        //String startLng = startLocationLngInput.getText().toString().trim();
        String startLocation = startLat;

        String endLat = endLocationLatInput.getText().toString().trim();
        //String endLng = endLocationLngInput.getText().toString().trim();
        String endLocation = endLat;

        String maxPassengers = maxPassengersInput.getText().toString().trim();
        String currentPassengers = currentPassengersInput.getText().toString().trim();
        String tripStatus = tripStatusInput.getText().toString().trim();
        String pricePerSeat = pricePerSeatInput.getText().toString().trim();
        String rating = ratingInput.getText().toString().trim();

        // Create trip map
        Map<String, Object> mapTrip = new HashMap<>();
        mapTrip.put("trip_id", tripId);
        mapTrip.put("organizer_id", user.getUid().toString());
        mapTrip.put("organizer_name", organizerName);
        mapTrip.put("vehicle_plate", vehiclePlate);
        mapTrip.put("car_model", carModel);
        mapTrip.put("start_location", startLocation);
        mapTrip.put("end_location", endLocation);
        mapTrip.put("pickup_points", new ArrayList<>()); // Add pickup points if needed
        mapTrip.put("start_time", new Timestamp(new Date())); // Set current time
        mapTrip.put("end_time", new Timestamp(new Date())); // Set current time (change as needed)
        mapTrip.put("max_passengers", maxPassengers);
        mapTrip.put("current_passengers", currentPassengers);
        mapTrip.put("passengers", new ArrayList<>()); // Add passengers if needed
        mapTrip.put("trip_status", tripStatus);
        mapTrip.put("price_per_seat", pricePerSeat);
        mapTrip.put("rating", rating);
        mapTrip.put("created_at", new Date());
        mapTrip.put("updated_at", new Date());

        // Save to Firestore
        db.collection("trips").document(tripId) // Use tripId as the document ID
                .set(mapTrip)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Trip created successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally, clear input fields or navigate back
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error creating trip: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
