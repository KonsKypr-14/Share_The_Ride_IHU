package com.example.sharetheride;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapWithSearchFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private EditText locationSearch;
    private Button locationSearch, locationConfirm;
    private PlacesClient placesClient;

    private RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesAdapter placesAdapter;
    private List<AutocompletePrediction> predictions = new ArrayList<>();

    private SharedViewModel viewModel;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // or any unique integer

    String button_clicked;
    String latLng_to_send, loc_name_to_send;

    private static final String TEXT_ID = "text_id";

    public static MapWithSearchFragment newInstance(@StringRes int textId) {

        MapWithSearchFragment frag = new MapWithSearchFragment();

        Bundle args = new Bundle();
        args.putInt(TEXT_ID, textId);
        frag.setArguments(args);

        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_with_search, container, false);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize SharedViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // Initialize views
        locationSearch = rootView.findViewById(R.id.button_loc);
        locationConfirm = rootView.findViewById(R.id.button_confirm_loc);

        // Retrieve the button that was clicked on previous Fragment in order to change the texts on the button (to use only 1 layout and not 2)
        viewModel.getClickedButton().observe(getViewLifecycleOwner(), buttonValue -> {
            button_clicked = buttonValue;
            switch (button_clicked) {
                case "startLocButton":
                    locationSearch.setText(R.string.search_start);
                    locationConfirm.setText(R.string.confirm_start);
                    break;
                case "endLocButton":
                    locationSearch.setText(R.string.search_end);
                    locationConfirm.setText(R.string.confirm_end);
                    break;
            }
        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
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
            mapFragment.getMapAsync(this);
        }

        // Set up the search input
        Button searchInput = rootView.findViewById(R.id.button_loc);
        searchInput.setOnClickListener(v -> openAutocomplete());

        // Set search action
        locationSearch.setOnEditorActionListener((v, actionId, event) -> {
            String location = locationSearch.getText().toString();
            if (!TextUtils.isEmpty(location)) {
                searchLocation(location);
            } else {
                //Toast.makeText(getActivity(), "Please enter a location", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        locationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize SharedViewModel
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                // Setting data in ViewModel
                viewModel.setLocation(latLng_to_send);
                viewModel.setLocationName(loc_name_to_send);
                fm.popBackStack();
            }
        });

        return rootView;
    }

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

    private void openAutocomplete() {
        // Specify the fields to return
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Create the intent for the autocomplete
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getContext());

        // Launch the autocomplete activity
        autocompleteLauncher.launch(intent);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set default location and zoom level
        //LatLng defaultLocation = new LatLng(-34, 151);  // Example: Sydney
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
        getCurrentLocation(); // Get the current location and center the map
    }

    private void searchLocation(String location) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(location)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            predictions.clear(); // Clear previous predictions
            predictions.addAll(response.getAutocompletePredictions());
            placesAdapter.notifyDataSetChanged(); // Notify adapter to update the list
            recyclerView.setVisibility(predictions.isEmpty() ? View.GONE : View.VISIBLE); // Show/hide the recycler view
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
        /*
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            if (predictions.size() > 0) {
                AutocompletePrediction prediction = predictions.get(0);  // Choose first prediction
                getCoordinatesAndPlaceMarker(prediction.getPlaceId(), prediction.getPrimaryText(null).toString());
            } else {
                //Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

         */
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Move the camera to the current location
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            // Optionally, add a marker for the current location
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                        } else {
                            Log.e("MapWithSearchFragment", "Current location is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MapWithSearchFragment", "Failed to get location: " + e.getMessage());
                    });
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCoordinatesAndPlaceMarker(String placeId, String locationName) {
        // Use FetchPlaceRequest.newInstance to create the request
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, Arrays.asList(Place.Field.LAT_LNG));

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();

            if (latLng != null) {
                // Add a marker at the place location

                mMap.clear(); // Remove the markers
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                latLng_to_send = latLng.toString();
                loc_name_to_send = locationName;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }).addOnFailureListener((exception) -> {
            // Handle the error
            //Log.e("MapWithSearchFragment", "Place not found: " + exception.getMessage());
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(); // Permission granted, get the location
            } else {
                Log.e("MapWithSearchFragment", "Location permission denied");
            }
        }
    }


}
