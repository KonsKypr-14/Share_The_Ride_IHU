package com.example.sharetheride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateTripFragment extends Fragment  {

    private EditText tripIdInput, organizerIdInput, organizerNameInput, vehiclePlateInput, carModelInput;
    private EditText startLocationLatInput, startLocationLngInput, endLocationLatInput, endLocationLngInput;
    private EditText maxPassengersInput, currentPassengersInput, tripStatusInput, pricePerSeatInput, ratingInput;
    private Button createTripButton, startLocButton, endLocButton;

    private FirebaseFirestore db;
    FirebaseUser user;

    private SharedViewModel viewModel;
    String latLng_retrieved, loc_name_retrived;

    //GoogleMap myMap;
    //SearchView mapSearchView;
    //private FusedLocationProviderClient fusedLocationClient;
    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // or any unique integer
    //private PlacesClient placesClient;

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
/*
    // Define an ActivityResultLauncher for Autocomplete
    private final ActivityResultLauncher<Intent> autocompleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    String placeId = place.getId();
                    String locationName = place.getName();
                    LatLng latLng = place.getLatLng();

                    // Call your method to handle this place
                    getCoordinatesAndPlaceMarker(placeId, locationName);
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    //Status status = Autocomplete.getStatus(result.getData());
                    //Log.e("MapWithSearchFragment", "Error: " + status.getStatusMessage());
                }
            }
    );
    private void getCoordinatesAndPlaceMarker(String placeId, String locationName) {
        // Use FetchPlaceRequest.newInstance to create the request
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, Arrays.asList(Place.Field.LAT_LNG));

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();

            if (latLng != null) {
                // Add a marker at the place location
                myMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }).addOnFailureListener((exception) -> {
            // Handle the error
            //Log.e("MapWithSearchFragment", "Place not found: " + exception.getMessage());
        });
    }

 */



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        // Initialize SharedViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Retrieve the button that was clicked on previous Fragment in order to change the texts on the button (to use only 1 layout and not 2)
        viewModel.getLocation().observe(getViewLifecycleOwner(), locValue -> {
            latLng_retrieved = locValue;
        });
        viewModel.getLocationName().observe(getViewLifecycleOwner(), locNameValue -> {
            loc_name_retrived = locNameValue;
        });

        /*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

// In your activity or fragment
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }


        // Initialize Places API
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyBShreWrfH1g9yIAmcn8DxgHkCUOv0ttCI");
        placesClient = Places.createClient(getActivity());


        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }

 */

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

        tripIdInput.setEnabled(false); //Disable the change of the tripId.
        organizerIdInput.setEnabled(false); //Disable the change of the Organizer ID.
        organizerNameInput.setEnabled(false); //Disable the change of the Organizer Name/Surname.

        String randomId = generateId(28); // 28 characters
        tripIdInput.setText(randomId);
        organizerIdInput.setText(user.getUid().toString());
        organizerNameInput.setText("Name-Surname");

        // Set button click listener
        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTrip();
            }
        });

        startLocButton = view.findViewById(R.id.button_loc);
        endLocButton = view.findViewById(R.id.button_end_loc);

        startLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize SharedViewModel
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                // Setting data in ViewModel
                viewModel.setClickedButton("startLocButton");

                Fragment mapFragment = MapWithSearchFragment.newInstance(R.string.email);
                // Get the FragmentManager to perform the transaction
                FragmentManager fragmentManager = getParentFragmentManager();
                // Start a FragmentTransaction to replace LoginFragment with RegisterFragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Optionally set animations for fragment transition
                fragmentTransaction.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit);
                // Replace the current fragment with RegisterFragment
                fragmentTransaction.replace(R.id.home_content, mapFragment);
                // Add the transaction to the back stack, so pressing "Back" returns to LoginFragment
                fragmentTransaction.addToBackStack(null);
                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        endLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize SharedViewModel
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                // Setting data in ViewModel
                viewModel.setClickedButton("endLocButton");

                Fragment mapFragment = MapWithSearchFragment.newInstance(R.string.email);
                // Get the FragmentManager to perform the transaction
                FragmentManager fragmentManager = getParentFragmentManager();
                // Start a FragmentTransaction to replace LoginFragment with RegisterFragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Optionally set animations for fragment transition
                fragmentTransaction.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit);
                // Replace the current fragment with RegisterFragment
                fragmentTransaction.replace(R.id.home_content, mapFragment);
                // Add the transaction to the back stack, so pressing "Back" returns to LoginFragment
                fragmentTransaction.addToBackStack(null);
                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        //View view_map = inflater.inflate(R.layout.activity_map, container, false);

        //mapSearchView = view_map.findViewById(R.id.mapSearch);

        return view;
    }

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateId(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }

        return sb.toString();
    }
/*
    private String getClickedButton(){
        return this.clicked_button;
    }

 */

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
