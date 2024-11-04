package com.example.sharetheride;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateTripFragment extends Fragment  {

    private EditText tripIdInput, organizerIdInput, organizerNameInput, vehiclePlateInput, carModelInput;
    private EditText startLocationLatInput, startLocationLngInput, endLocationLatInput, endLocationLngInput;
    private EditText maxPassengersInput, currentPassengersInput, tripStatusInput, pricePerSeatInput, ratingInput, pickUpPointStart, pickUpPointEnd;
    RelativeLayout pickUpPointStart_out, pickUpPointEnd_out;
    private Button createTripButton, startLocButton, endLocButton;

    private Calendar calendar = Calendar.getInstance();

    private List<Trip> tripList;
    private TripAdapter tripAdapter;

    //TextInputLayout startTimeInput;
    //EditText startTimeInputText;
    EditText startTimeInput;

    private FirebaseFirestore db;
    FirebaseUser user;

    String button_clicked;
    private SharedViewModel viewModel;
    String latLng_retrieved, loc_name_retrived, latLng_retrieved_start, latLng_retrieved_end, loc_name_retrieved_start, loc_name_retrieved_end;

    //GoogleMap myMap;
    //SearchView mapSearchView;
    //private FusedLocationProviderClient fusedLocationClient;
    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // or any unique integer
    //private PlacesClient placesClient;

    public CreateTripFragment() {
        // Required empty public constructor

        // Initialize UI elements
        if (tripIdInput != null) {
            tripIdInput.setText("");
            organizerIdInput.setText("");
            organizerNameInput.setText("");
            //vehiclePlateInput = view.findViewById(R.id.vehicle_plate_input);
            //carModelInput = view.findViewById(R.id.car_model_input);
            startLocationLatInput.setText("");
            //startLocationLngInput = view.findViewById(R.id.start_location_lng_input);
            endLocationLatInput.setText("");
            //endLocationLngInput = view.findViewById(R.id.end_location_lng_input);
            maxPassengersInput.setText("");
            //currentPassengersInput = view.findViewById(R.id.current_passengers_input);
            //tripStatusInput = view.findViewById(R.id.trip_status_input);
            pricePerSeatInput.setText("");
        }
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

        pickUpPointStart = view.findViewById(R.id.pickup_point_input_start);
        pickUpPointEnd = view.findViewById(R.id.pickup_point_input_end);
        pickUpPointStart_out = view.findViewById(R.id.pickup_point_input_start_out);
        pickUpPointEnd_out = view.findViewById(R.id.pickup_point_input_end_out);

//        if (pickUpPointStart == null);
//        {
//            pickUpPointStart.setVisibility(View.GONE);
//        }
//        if (pickUpPointEnd == null);
//        {
//            pickUpPointEnd.setVisibility(View.GONE);
//        }

        // Initialize SharedViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Retrieve the button that was clicked on previous Fragment in order to change the texts on the button (to use only 1 layout and not 2)
//        viewModel.getLocation().observe(getViewLifecycleOwner(), locValue -> {
//            latLng_retrieved = locValue;
//            if (latLng_retrieved != null){
//                //pickUpPointStart.setVisibility(View.VISIBLE);
//                //pickUpPointStart.setText(latLng_retrieved);
//            }
//        });

        viewModel.getLocationStart().observe(getViewLifecycleOwner(), locValue -> {
            latLng_retrieved_start = locValue;
            if (latLng_retrieved_start != null) {
                //pickUpPointStart.setVisibility(View.VISIBLE);
                //pickUpPointStart.setText(latLng_retrieved);
            }
        });
        viewModel.getLocationEnd().observe(getViewLifecycleOwner(), locValue -> {
            latLng_retrieved_end = locValue;
            if (latLng_retrieved_end != null) {
                //pickUpPointStart.setVisibility(View.VISIBLE);
                //pickUpPointStart.setText(latLng_retrieved);
            }
        });

        if (latLng_retrieved_start == null) {
            pickUpPointStart.setVisibility(View.GONE);
            pickUpPointStart_out.setVisibility(View.GONE);
            pickUpPointStart.setEnabled(false);
        }
        if (latLng_retrieved_end == null) {
            pickUpPointEnd.setVisibility(View.GONE);
            pickUpPointEnd_out.setVisibility(View.GONE);
            pickUpPointStart.setEnabled(false);
        }

//        viewModel.getLocationNameStart().observe(getViewLifecycleOwner(), locNameValue -> {
//            loc_name_retrieved_start = locNameValue;
//
//            viewModel.getClickedButton().observe(getViewLifecycleOwner(), buttonValue -> {
//                button_clicked = buttonValue;
//                switch (button_clicked) {
//                    case "startLocButton":
//                        if (loc_name_retrieved_start != null && !loc_name_retrieved_start.isEmpty()) {
//                            pickUpPointStart.setVisibility(View.VISIBLE);
//                            pickUpPointStart_out.setVisibility(View.VISIBLE);
//                            pickUpPointStart.setText(loc_name_retrieved_start);
//                            pickUpPointStart.setEnabled(false);
//                        } else {
//                            pickUpPointStart.setVisibility(View.GONE);
//                            pickUpPointStart_out.setVisibility(View.GONE);
//                        }
//                        break;
//                    case "endLocButton":
//                        if (loc_name_retrieved_start != null && !loc_name_retrieved_start.isEmpty()) {
//                            pickUpPointEnd.setVisibility(View.VISIBLE);
//                            pickUpPointEnd_out.setVisibility(View.VISIBLE);
//                            pickUpPointEnd.setText(loc_name_retrieved_start);
//                            pickUpPointEnd.setEnabled(false);
//                        } else {
//                            pickUpPointEnd.setVisibility(View.GONE);
//                            pickUpPointEnd_out.setVisibility(View.GONE);
//                        }
//                        break;
//                }
//            });
//        });
//
//        viewModel.getLocationNameEnd().observe(getViewLifecycleOwner(), locNameValue -> {
//            loc_name_retrieved_end = locNameValue;
//
//            viewModel.getClickedButton().observe(getViewLifecycleOwner(), buttonValue -> {
//                button_clicked = buttonValue;
//                switch (button_clicked) {
//                    case "startLocButton":
//                        if (loc_name_retrieved_end != null && !loc_name_retrieved_end.isEmpty()) {
//                            pickUpPointStart.setVisibility(View.VISIBLE);
//                            pickUpPointStart_out.setVisibility(View.VISIBLE);
//                            pickUpPointStart.setText(loc_name_retrieved_end);
//                            pickUpPointStart.setEnabled(false);
//                        } else {
//                            pickUpPointStart.setVisibility(View.GONE);
//                            pickUpPointStart_out.setVisibility(View.GONE);
//                        }
//                        break;
//                    case "endLocButton":
//                        if (loc_name_retrieved_end != null && !loc_name_retrieved_end.isEmpty()) {
//                            pickUpPointEnd.setVisibility(View.VISIBLE);
//                            pickUpPointEnd_out.setVisibility(View.VISIBLE);
//                            pickUpPointEnd.setText(loc_name_retrieved_end);
//                            pickUpPointEnd.setEnabled(false);
//                        } else {
//                            pickUpPointEnd.setVisibility(View.GONE);
//                            pickUpPointEnd_out.setVisibility(View.GONE);
//                        }
//                        break;
//                }
//            });
//        });
// Observe location name for start
        viewModel.getLocationNameStart().observe(getViewLifecycleOwner(), locNameValue -> {
            loc_name_retrieved_start = locNameValue;
            updatePickUpPoint();
        });

// Observe location name for end
        viewModel.getLocationNameEnd().observe(getViewLifecycleOwner(), locNameValue -> {
            loc_name_retrieved_end = locNameValue;
            updatePickUpPoint();
        });

// Observe which button was clicked
        viewModel.getClickedButton().observe(getViewLifecycleOwner(), buttonValue -> {
            button_clicked = buttonValue;
            updatePickUpPoint();
        });

        ImageView pickupPointIconstart = view.findViewById(R.id.pickup_point_icon_start);
        ImageView pickupPointIconend = view.findViewById(R.id.pickup_point_icon_end);

        pickupPointIconstart.setOnClickListener(v -> {
            loc_name_retrieved_start = "";
            pickUpPointStart.setText("");
            pickUpPointStart_out.setVisibility(View.GONE);
            pickUpPointStart.setEnabled(false);
            viewModel.setLocationStart(latLng_retrieved_start);
            viewModel.setLocationNameStart(loc_name_retrieved_start);
            // Handle the icon click
            //Toast.makeText(getContext(), "Icon clicked!", Toast.LENGTH_SHORT).show();
        });

        pickupPointIconend.setOnClickListener(v -> {
            loc_name_retrieved_end = "";
            pickUpPointEnd.setText("");
            pickUpPointEnd_out.setVisibility(View.GONE);
            pickUpPointEnd.setEnabled(false);
            viewModel.setLocationEnd(latLng_retrieved_end);
            viewModel.setLocationNameEnd(loc_name_retrieved_end);
            // Handle the icon click
            //Toast.makeText(getContext(), "Icon clicked!", Toast.LENGTH_SHORT).show();
        });


//        viewModel.getLocationName().observe(getViewLifecycleOwner(), locNameValue -> {
//            loc_name_retrived = locNameValue;
//
//            viewModel.getClickedButton().observe(getViewLifecycleOwner(), buttonValue -> {
//                button_clicked = buttonValue;
//                switch (button_clicked) {
//                    case "startLocButton":
//                        if (loc_name_retrived != null && !loc_name_retrived.isEmpty()){
//                            pickUpPointStart.setVisibility(View.VISIBLE);
//                            pickUpPointStart.setText(loc_name_retrived);
//                            pickUpPointStart.setEnabled(false);
//                            viewModel.setLocationName("");
//                        }
//                        break;
//                    case "endLocButton":
//                        if (loc_name_retrived != null && !loc_name_retrived.isEmpty()){
//                            pickUpPointEnd.setVisibility(View.VISIBLE);
//                            pickUpPointEnd.setText(loc_name_retrived);
//                            pickUpPointEnd.setEnabled(false);
//                        }
//                        break;
//                }
//            });
//        });

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
        startTimeInput = view.findViewById(R.id.start_time_input);

        // Open Date and Time picker on click
        startTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
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
        //currentPassengersInput = view.findViewById(R.id.current_passengers_input);
        //tripStatusInput = view.findViewById(R.id.trip_status_input);
        pricePerSeatInput = view.findViewById(R.id.price_per_seat_input);
        //ratingInput = view.findViewById(R.id.rating_input);
        createTripButton = view.findViewById(R.id.create_trip_button);

        tripIdInput.setEnabled(false); //Disable the change of the tripId.
        organizerIdInput.setEnabled(false); //Disable the change of the Organizer ID.
        organizerNameInput.setEnabled(false); //Disable the change of the Organizer Name/Surname.

        String randomId = generateId(28); // 28 characters
        tripIdInput.setText(randomId);
        organizerIdInput.setText(user.getUid().toString());

        loadTripsFromDB();

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

    // Method to update the visibility and text based on LiveData values
    private void updatePickUpPoint() {
        if (button_clicked == null) return;

        switch (button_clicked) {
            case "startLocButton":
                if (loc_name_retrieved_start != null && !loc_name_retrieved_start.isEmpty()) {
                    pickUpPointStart.setVisibility(View.VISIBLE);
                    pickUpPointStart_out.setVisibility(View.VISIBLE);
                    pickUpPointStart.setText(loc_name_retrieved_start);
                    pickUpPointStart.setEnabled(false);
                } else {
                    pickUpPointStart.setVisibility(View.GONE);
                    pickUpPointStart_out.setVisibility(View.GONE);
                }
                if (loc_name_retrieved_end != null && !loc_name_retrieved_end.isEmpty()) {
                    pickUpPointEnd.setVisibility(View.VISIBLE);
                    pickUpPointEnd_out.setVisibility(View.VISIBLE);
                    pickUpPointEnd.setText(loc_name_retrieved_end);
                    pickUpPointEnd.setEnabled(false);
                } else {
                    pickUpPointEnd.setVisibility(View.GONE);
                    pickUpPointEnd_out.setVisibility(View.GONE);
                }
                break;
            case "endLocButton":
                if (loc_name_retrieved_end != null && !loc_name_retrieved_end.isEmpty()) {
                    pickUpPointEnd.setVisibility(View.VISIBLE);
                    pickUpPointEnd_out.setVisibility(View.VISIBLE);
                    pickUpPointEnd.setText(loc_name_retrieved_end);
                    pickUpPointEnd.setEnabled(false);
                } else {
                    pickUpPointEnd.setVisibility(View.GONE);
                    pickUpPointEnd_out.setVisibility(View.GONE);
                }
                if (loc_name_retrieved_start != null && !loc_name_retrieved_start.isEmpty()) {
                    pickUpPointStart.setVisibility(View.VISIBLE);
                    pickUpPointStart_out.setVisibility(View.VISIBLE);
                    pickUpPointStart.setText(loc_name_retrieved_start);
                    pickUpPointStart.setEnabled(false);
                } else {
                    pickUpPointStart.setVisibility(View.GONE);
                    pickUpPointStart_out.setVisibility(View.GONE);
                }
                break;
        }
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
        //String currentPassengers = currentPassengersInput.getText().toString().trim();
        //String tripStatus = tripStatusInput.getText().toString().trim();
        String pricePerSeat = pricePerSeatInput.getText().toString().trim();
        //String rating = ratingInput.getText().toString().trim();

        List<Map<String, Object>> passenger_list = new ArrayList<>();
//        Map<String, Object> passenger1 = new HashMap<>();
//        Map<String, Object> passenger2 = new HashMap<>();
//        Map<String, Object> passenger3 = new HashMap<>();
//        Map<String, Object> passenger4 = new HashMap<>();
//
//        passenger1.put("passenger_id", "");
//        passenger1.put("name", "");
//        //passenger1.put("pickup_confirmed", true);
//        passenger1.put("available", true);
//        passenger_list.add(passenger1);
//
//        passenger2.put("passenger_id", "");
//        passenger2.put("name", "");
//        //passenger2.put("pickup_confirmed", true);
//        passenger2.put("available", true);
//        passenger_list.add(passenger2);
//
//        passenger3.put("passenger_id", "");
//        passenger3.put("name", "");
//        //passenger3.put("pickup_confirmed", true);
//        passenger3.put("available", true);
//        passenger_list.add(passenger3);
//
//        passenger4.put("passenger_id", "");
//        passenger4.put("name", "");
//        //passenger4.put("pickup_confirmed", true);
//        passenger4.put("available", true);
//        passenger_list.add(passenger4);

        int numberOfPassengers = 4; // Change this to the number of passengers you want
        if (maxPassengers == null || maxPassengers.isEmpty()) {
            Toast.makeText(getContext(), "Add max number of passengers to create a trip.", Toast.LENGTH_LONG).show();
            return;
        }
        if (Integer.parseInt(maxPassengers) <= 0) {
            Toast.makeText(getContext(), "Add max number of passengers to create a trip.", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < Integer.parseInt(maxPassengers); i++) {
            Map<String, Object> passenger = new HashMap<>();
            passenger.put("passenger_id", "");
            passenger.put("name", "");
            // passenger.put("pickup_confirmed", true); // Uncomment if needed
            passenger.put("available", true);
            passenger_list.add(passenger);
        }

        // Create trip map
        Map<String, Object> mapTrip = new HashMap<>();
        mapTrip.put("trip_id", tripId);
        mapTrip.put("organizer_id", user.getUid().toString());
        mapTrip.put("organizer_name", organizerName);
        //mapTrip.put("vehicle_plate", vehiclePlate);
        mapTrip.put("car_model", carModel);
        //mapTrip.put("start_location", startLocation); //Example of initial runs
        if (latLng_retrieved_start == null || loc_name_retrieved_start == null || latLng_retrieved_end == null || loc_name_retrieved_end == null ||
                latLng_retrieved_start.isEmpty() || loc_name_retrieved_start.isEmpty() || latLng_retrieved_end.isEmpty() || loc_name_retrieved_end.isEmpty()) {
            Toast.makeText(getContext(), "You have no select a starting or ending location.", Toast.LENGTH_LONG).show();
            return;
        }

        if (startTimeInput.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Select which date/time the trip will start.", Toast.LENGTH_LONG).show();
            return;
        }
        mapTrip.put("start_location", latLng_retrieved_start);
        mapTrip.put("start_location_name", loc_name_retrieved_start);
        mapTrip.put("end_location", latLng_retrieved_end);
        mapTrip.put("end_location_name", loc_name_retrieved_end);
        //mapTrip.put("pickup_points", new ArrayList<>()); // Add pickup points if needed
        //mapTrip.put("start_time", new Timestamp(new Date())); // Set current time
        mapTrip.put("start_time", startTimeInput.getText().toString()); // Set the selected date from user
        //mapTrip.put("end_time", new Timestamp(new Date())); // Set current time (change as needed)
        mapTrip.put("max_passengers", maxPassengers);
        mapTrip.put("current_passengers", "0");
        //mapTrip.put("passengers", new ArrayList<>()); // Add passengers if needed
        mapTrip.put("passengers", passenger_list); // Array of passenger details
        //mapTrip.put("trip_status", tripStatus);
        mapTrip.put("price_per_seat", pricePerSeat);
        //mapTrip.put("rating", rating);
        mapTrip.put("created_at", new Date());
        mapTrip.put("updated_at", new Date());

        // Save to Firestore
        db.collection("trips").document(tripId) // Use tripId as the document ID
                .set(mapTrip)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Trip created successfully!", Toast.LENGTH_SHORT).show();

                    viewModel.setLocationStart("");
                    viewModel.setLocationNameStart("");
                    viewModel.setLocationEnd("");
                    viewModel.setLocationNameEnd("");

                    String randomId = generateId(28); // 28 characters
                    tripIdInput.setText(randomId);
                    carModelInput.setText("");
                    maxPassengersInput.setText("");
                    pricePerSeatInput.setText("");
                    startTimeInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error creating trip: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDateTimePicker() {
        // Step 1: Open Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            // Set the selected date in the calendar
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Step 2: Open Time Picker Dialog after date is selected
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                // Set the selected time in the calendar
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                Calendar currentDateTime = Calendar.getInstance();

                // Step 3: Format date and time as "Sunday, 03/11/2024 at 15:00"
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy 'at' HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

                // Step 4: Check if the selected date and time are in the past
                if (calendar.getTimeInMillis() <= currentDateTime.getTimeInMillis()) {
                    // Show error message and do not update the TextInputEditText
                    Toast.makeText(requireContext(), "Please select a future date and time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display formatted date and time in TextInputEditText
                startTimeInput.setText(formattedDate);

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(requireContext()));

            timePickerDialog.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void loadTripsFromDB() {

        List<FieldMapper> fieldMappers = new ArrayList<>();

        db.collection("users_collection").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    organizerNameInput.setText(documentSnapshot.getString("name") + " " + documentSnapshot.getString("surname"));
                }
            }
        });
    }

}
