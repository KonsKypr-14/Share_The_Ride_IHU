package com.example.sharetheride;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTripsFragment extends Fragment {

    private RecyclerView recyclerViewTrips;
    private RecyclerView recyclerViewJointedTrips;
    private TripsAdapter tripsAdapter;
    private JointedTripAdapter jointedTripsAdapter;
    private FirebaseFirestore db;
    FirebaseUser user;
    String user_full_name;

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

        recyclerViewJointedTrips = view.findViewById(R.id.recycler_view_jointed_trips);
        recyclerViewJointedTrips.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();

        user = ((MainActivity) getActivity()).getUser();  // Pass the logged-in user


        loadTrips();

        return view;
    }

    private void loadTrips() {
        // Initialize lists for trips and jointed trips
        List<MyTrip> tripsList = new ArrayList<>();
        List<JointedTrip> jointedTripList = new ArrayList<>();

        db.collection("trips")
                .whereEqualTo("organizer_id", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tripId = document.getString("trip_id");
                        String startLocation = document.getString("start_location_name");
                        String endLocation = document.getString("end_location_name");

                        String startLocText = startLocation != null ? startLocation : "N/A";
                        String endLocText = endLocation != null ? endLocation : "N/A";

                        // Add to tripsList
                        tripsList.add(new MyTrip(tripId, startLocText, endLocText, document.getId()));
                    }
                    // Initialize and set the trips adapter
                    tripsAdapter = new TripsAdapter(tripsList);
                    recyclerViewTrips.setAdapter(tripsAdapter);
                    //setRecyclerViewHeightBasedOnChildren(recyclerViewTrips);
                    recyclerViewTrips.post(() -> setRecyclerViewHeightBasedOnChildren(recyclerViewTrips));
//                    recyclerViewTrips.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            setRecyclerViewHeightBasedOnChildren(recyclerViewTrips);
//                            recyclerViewTrips.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        }
//                    });
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load trips as organizer", Toast.LENGTH_SHORT).show());

        HashMap<String, Object> passengerMap = new HashMap<>();
        db.collection("users_collection").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
// Create a HashMap for the passenger object
// Unfortunately, Firestore doesn't support partial object matching within arrays.
// When using whereArrayContains, you need to match the entire object structure exactly.
                    //Thats the reason for the passengerMap.
                    passengerMap.put("available", false); //If the user is a passenger the available is always false.
                    passengerMap.put("name", documentSnapshot.getString("full_name")); // Add the name if needed
                    passengerMap.put("passenger_id", user.getUid().toString()); //We check if our user is also a passenger somewhere
                    user_full_name = documentSnapshot.getString("full_name");

                    db.collection("trips").whereArrayContains("passengers", passengerMap)
//                            .whereArrayContains("passengers", new HashMap<String, Object>() {{
//                                put("available", false); //If the user is a passenger the available is always false.
//                                put("name", user_full_name.toString()); // Add the name if needed
//                                put("passenger_id", user.getUid().toString());
//                            }})
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String tripId = document.getString("trip_id");
                                    String startLocation = document.getString("start_location_name");
                                    String endLocation = document.getString("end_location_name");

                                    String startLocText = startLocation != null ? startLocation : "N/A";
                                    String endLocText = endLocation != null ? endLocation : "N/A";

                                    // Add to jointedTripList
                                    jointedTripList.add(new JointedTrip(tripId, startLocText, endLocText, document.getId()));
                                }
                                // Initialize and set the jointed trips adapter
                                jointedTripsAdapter = new JointedTripAdapter(jointedTripList);
                                recyclerViewJointedTrips.setAdapter(jointedTripsAdapter);
                                //setRecyclerViewHeightBasedOnChildren(recyclerViewJointedTrips);
                                recyclerViewJointedTrips.post(() -> setRecyclerViewHeightBasedOnChildren(recyclerViewJointedTrips));
//                                recyclerViewJointedTrips.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                    @Override
//                                    public void onGlobalLayout() {
//                                        setRecyclerViewHeightBasedOnChildren(recyclerViewJointedTrips);
//                                        recyclerViewJointedTrips.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                    }
//                                });

                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load jointed trips", Toast.LENGTH_SHORT).show());
                }
            }
        });
        //tripsAdapter.notifyDataSetChanged();

// Second query: Trips where the user is a passenger
//        db.collection("trips")
//                //.whereArrayContains("passengers", user.getUid().toString())
//                .whereEqualTo("passengers.passenger_id", user.getUid().toString())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String tripId = document.getString("trip_id");
//                        String startLocation = document.getString("start_location_name");
//                        String endLocation = document.getString("end_location_name");
//
//                        String startLocText = startLocation != null ? startLocation + ", " + startLocation : "N/A";
//                        String endLocText = endLocation != null ? endLocation + ", " + endLocation : "N/A";
//
//                        // Add to jointedTripList
//                        jointedTripList.add(new JointedTrip(tripId, startLocText, endLocText, document.getId()));
//                    }
//                    // Initialize and set the jointed trips adapter
//                    jointedTripsAdapter = new JointedTripAdapter(jointedTripList);
//                    recyclerViewJointedTrips.setAdapter(jointedTripsAdapter);
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(getContext(), "Failed to load jointed trips", Toast.LENGTH_SHORT).show()
//                );

// Second query: Trips where the user is a passenger
//        db.collection("trips")
//                .whereArrayContains("passengers", user.getUid())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String tripId = document.getString("trip_id");
//                        String startLocation = document.getString("start_location_name");
//                        String endLocation = document.getString("end_location_name");
//
//                        String startLocText = startLocation != null ? startLocation + ", " + startLocation : "N/A";
//                        String endLocText = endLocation != null ? endLocation + ", " + endLocation : "N/A";
//
//                        // Add to jointedTripList
//                        jointedTripList.add(new JointedTrip(tripId, startLocText, endLocText, document.getId()));
//                    }
//                    // Initialize and set the jointed trips adapter
//                    jointedTripsAdapter = new JointedTripsAdapter(jointedTripList);
//                    recyclerViewJointedTrips.setAdapter(jointedTripsAdapter);
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(getContext(), "Failed to load jointed trips", Toast.LENGTH_SHORT).show()
//                );


//        db.collection("trips")
//                .whereEqualTo("organizer_id", user.getUid())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<MyTrip> tripsList = new ArrayList<>();
//                    List<JointedTrip> jointedTripList = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String tripId = document.getString("trip_id");
//                        String startLocation = document.getString("start_location_name");
//                        String endLocation = document.getString("end_location_name");
//
//                        String startLocText = startLocation != null ? startLocation.toString() + ", " + startLocation.toString() : "N/A";
//                        String endLocText = endLocation != null ? endLocation.toString() + ", " + endLocation.toString() : "N/A";
//
//                        tripsList.add(new MyTrip(tripId, startLocText, endLocText, document.getId()));
//                    }
//                    tripsAdapter = new TripsAdapter(tripsList);
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load trips", Toast.LENGTH_SHORT).show());
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

    private void confirmDeleteJointedTrip(String tripDocId) {
        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Remove from Jointed Trip")
                .setMessage("Do you confirm to be removed from your jointed trip?")
                .setPositiveButton("Yes", (dialog, which) -> deleteJointedTrip(tripDocId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteJointedTrip(String tripDocId) {
//        db.collection("trips").document(tripDocId)
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(), "Trip deleted", Toast.LENGTH_SHORT).show();
//                    loadTrips();  // Refresh the list
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting trip", Toast.LENGTH_SHORT).show());
        db.collection("trips")
                .document(tripDocId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<Map<String, Object>> passengers = (List<Map<String, Object>>) documentSnapshot.get("passengers");

                    String current_passengers = documentSnapshot.getString("current_passengers");

                    if (passengers != null) {
                        // Create a new list to store the updated passengers
                        List<Map<String, Object>> updatedPassengers = new ArrayList<>(passengers);

                        for (int i = 0; i < passengers.size(); i++) {
                            Map<String, Object> passenger = passengers.get(i);
                            if (passenger.get("passenger_id").equals(user.getUid().toString())) {
                                // Create updated passenger map
                                Map<String, Object> updatedPassenger = new HashMap<>(passenger);
                                updatedPassenger.put("available", true);
                                updatedPassenger.put("name", ""); // Add the name if needed
                                updatedPassenger.put("passenger_id", ""); //We check if our user is also a passenger somewhere

                                // Update the passenger at the specific index
                                updatedPassengers.set(i, updatedPassenger);
                                current_passengers = String.valueOf(Integer.parseInt(current_passengers) - 1);

                                // Update the entire passengers array
                                db.collection("trips")
                                        .document(tripDocId)
                                        .update("passengers", updatedPassengers,
                                                "current_passengers", current_passengers)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "You have benn removed from the trip", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to update passenger", Toast.LENGTH_SHORT).show();
                                        });
                                loadTrips();  // Refresh the list
                                break;
                            }
                        }
                    }
                });

//        db.collection("trips")
//                .document(tripDocId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    List<Map<String, Object>> passengers = (List<Map<String, Object>>) documentSnapshot.get("passengers");
//
//                    if (passengers != null) {
//                        // Find the index of the passenger with matching ID
//                        for (int i = 0; i < passengers.size(); i++) {
//                            Map<String, Object> passenger = passengers.get(i);
//                            if (passenger.get("passenger_id").equals(user.getUid().toString())) {
//                                // Found the passenger at index i
//                                int passengerIndex = i;
//                                // Now you can use the index to update specifically this passenger
//                                // For example: "passengers." + passengerIndex + ".available"
//
//                                // Update specific field of the passenger
//                                db.collection("trips")
//                                        .document(tripDocId)
//                                        .update("passengers." + passengerIndex + ".available", true)
//                                        .addOnSuccessListener(aVoid -> {
//                                            Toast.makeText(getContext(), "Updated passenger status", Toast.LENGTH_SHORT).show();
//                                        });
//                                break;
//                            }
//                        }
//                    }
//                });

//        db.collection("trips").document(tripDocId)
//                .set(map_business, SetOptions.merge())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        //Log.d(TAG, "DocumentSnapshot successfully written!");
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "Account updated.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //Log.w(TAG, "Error writing document", e);
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "Error updating the account.", Toast.LENGTH_SHORT).show();
//                    }
//                });


//        Map<String, Object> passengerToRemove = new HashMap<>();
//        passengerToRemove.put("passenger_id", user.getUid().toString());
//        passengerToRemove.put("name", userName);
//        passengerToRemove.put("available", true);
//
//        db.collection("trips")
//                .document(tripDocId)
//                .update("passengers", FieldValue.arrayRemove(passengerToRemove))
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(), "Successfully left trip", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Failed to leave trip", Toast.LENGTH_SHORT).show();
//                });

//        db.runTransaction(transaction -> {
//            DocumentSnapshot snapshot = transaction.get(db.collection("trips").document(tripDocId));
//
//            // Get current passengers count
//            String currentPassengers = snapshot.getString("current_passengers");
//            int newCount = Integer.parseInt(currentPassengers) - 1;
//
//            // Create new passenger object
//            Map<String, Object> newPassenger = new HashMap<>();
//            newPassenger.put("passenger_id", user.getUid().toString());
//            newPassenger.put("name", userName);
//            newPassenger.put("available", true);
//
//            // Perform the updates atomically
//            transaction.update(db.collection("trips").document(tripId),
//                    "passengers", FieldValue.arrayUnion(newPassenger),
//                    "current_passengers", String.valueOf(newCount)
//            );
//
//            return null;
//        }).addOnSuccessListener(result -> {
//            Toast.makeText(getContext(), "Successfully updated trip", Toast.LENGTH_SHORT).show();
//        }).addOnFailureListener(e -> {
//            Toast.makeText(getContext(), "Failed to update trip", Toast.LENGTH_SHORT).show();
//        });

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
            TextView textTripId, textTripLocations, text_trip_locations_start, text_trip_locations_end;
            ImageButton buttonDeleteTrip;

            TripViewHolder(View itemView) {
                super(itemView);
                textTripId = itemView.findViewById(R.id.text_trip_id);
                text_trip_locations_start = itemView.findViewById(R.id.text_trip_locations_start);
                text_trip_locations_end = itemView.findViewById(R.id.text_trip_locations_end);
                buttonDeleteTrip = itemView.findViewById(R.id.button_delete_trip);
            }

            void bind(MyTrip trip) {
                textTripId.setText(trip.getTripId());
                //textTripLocations.setText(trip.getStartLocation() + " - " + trip.getEndLocation());
                text_trip_locations_start.setText(trip.getStartLocation());
                text_trip_locations_end.setText(trip.getEndLocation());

                //buttonDeleteTrip.setOnClickListener(v -> deleteTrip(trip.getDocId()));
                buttonDeleteTrip.setOnClickListener(v -> confirmDeleteTrip(trip.getDocId()));
            }
        }

    }

    // RecyclerView Adapter
    private class JointedTripAdapter extends RecyclerView.Adapter<JointedTripAdapter.JointedTripViewHolder> {
        private final List<JointedTrip> jointedTripsList;

        JointedTripAdapter(List<JointedTrip> jointedTripsList) {
            this.jointedTripsList = jointedTripsList;
        }

        @NonNull
        @Override
        public JointedTripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jointed_trip, parent, false);

            return new JointedTripViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull JointedTripViewHolder holder, int position) {
            JointedTrip jointedtrip = jointedTripsList.get(position);
            holder.bind(jointedtrip);
        }

        @Override
        public int getItemCount() {
            return jointedTripsList.size();
        }

        class JointedTripViewHolder extends RecyclerView.ViewHolder {
            TextView textTripId, textTripLocations, text_trip_locations_start, text_trip_locations_end;
            ImageButton buttonDeleteJointedTrip;

            JointedTripViewHolder(View itemView) {
                super(itemView);
                textTripId = itemView.findViewById(R.id.text_jointed_trip_id);
                text_trip_locations_start = itemView.findViewById(R.id.text_jointed_trip_locations_start);
                text_trip_locations_end = itemView.findViewById(R.id.text_jointed_trip_locations_end);
                buttonDeleteJointedTrip = itemView.findViewById(R.id.button_delete_jointed_trip);
            }

            void bind(JointedTrip jointedtrip) {
                textTripId.setText(jointedtrip.getTripId());
                text_trip_locations_start.setText(jointedtrip.getStartLocation());
                text_trip_locations_end.setText(jointedtrip.getEndLocation());
                buttonDeleteJointedTrip.setOnClickListener(v -> confirmDeleteJointedTrip(jointedtrip.getDocId()));
            }
        }
    }
    public static void setRecyclerViewHeightBasedOnChildren(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View listItem = recyclerView.getLayoutManager().findViewByPosition(i);
            if (listItem == null) {
                // Create a temporary view holder to measure the height
                RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
                adapter.bindViewHolder(holder, i);
                listItem = holder.itemView;
                listItem.measure(
                        View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(1, View.MeasureSpec.UNSPECIFIED));
                totalHeight += listItem.getMeasuredHeight();
            } else {
                totalHeight += listItem.getHeight();
            }
        }

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight + (recyclerView.getItemDecorationCount() * 16); // Add padding if needed
        recyclerView.setLayoutParams(params);
        recyclerView.requestLayout();
    }
}
