package com.example.sharetheride;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MyTripsFragment extends Fragment {

    private RecyclerView recyclerViewTrips;
    private TripsAdapter tripsAdapter;
    private FirebaseFirestore db;
    FirebaseUser user;

    private static final String TEXT_ID = "text_id";

    public static MyTripsFragment newInstance(@StringRes int textId) {

        MyTripsFragment frag = new MyTripsFragment();

        Bundle args = new Bundle();
        args.putInt(TEXT_ID, textId);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_trips, container, false);
        recyclerViewTrips = view.findViewById(R.id.recycler_view_trips);
        recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();

        user = ((MainActivity) getActivity()).getUser();  // Pass the logged-in user


        loadTrips();

        return view;
    }

    private void loadTrips() {
        // Replace "userId" with the actual user ID from authentication
        db.collection("trips")
                .whereEqualTo("organizer_id", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MyTrip> tripsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tripId = document.getString("trip_id");
                        String startLocation = document.getString("start_location");
                        String endLocation = document.getString("end_location");

                        String startLocText = startLocation != null ? startLocation.toString() + ", " + startLocation.toString() : "N/A";
                        String endLocText = endLocation != null ? endLocation.toString() + ", " + endLocation.toString() : "N/A";

                        tripsList.add(new MyTrip(tripId, startLocText, endLocText, document.getId()));
                    }
                    tripsAdapter = new TripsAdapter(tripsList);
                    recyclerViewTrips.setAdapter(tripsAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load trips", Toast.LENGTH_SHORT).show());
    }

    private void confirmDeleteTrip(String tripDocId) {
        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Trip")
                .setMessage("Do you confirm to delete the trip?")
                .setPositiveButton("Yes", (dialog, which) -> deleteTrip(tripDocId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteTrip(String tripDocId) {
        db.collection("trips").document(tripDocId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Trip deleted", Toast.LENGTH_SHORT).show();
                    loadTrips();  // Refresh the list
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting trip", Toast.LENGTH_SHORT).show());
    }

    // RecyclerView Adapter
    private class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {
        private final List<MyTrip> tripsList;

        TripsAdapter(List<MyTrip> tripsList) {
            this.tripsList = tripsList;
        }

        @NonNull
        @Override
        public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_trip, parent, false);
            return new TripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
            MyTrip trip = tripsList.get(position);
            holder.bind(trip);
        }

        @Override
        public int getItemCount() {
            return tripsList.size();
        }

        class TripViewHolder extends RecyclerView.ViewHolder {
            TextView textTripId, textTripLocations;
            ImageButton buttonDeleteTrip;

            TripViewHolder(View itemView) {
                super(itemView);
                textTripId = itemView.findViewById(R.id.text_trip_id);
                textTripLocations = itemView.findViewById(R.id.text_trip_locations);
                buttonDeleteTrip = itemView.findViewById(R.id.button_delete_trip);
            }

            void bind(MyTrip trip) {
                textTripId.setText(trip.getTripId());
                textTripLocations.setText(trip.getStartLocation() + " - " + trip.getEndLocation());

                //buttonDeleteTrip.setOnClickListener(v -> deleteTrip(trip.getDocId()));
                buttonDeleteTrip.setOnClickListener(v -> confirmDeleteTrip(trip.getDocId()));
            }
        }

    }
}
