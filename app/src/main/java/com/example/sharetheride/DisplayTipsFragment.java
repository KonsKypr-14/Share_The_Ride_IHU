package com.example.sharetheride;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DisplayTipsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> tripList;

    public DisplayTipsFragment() {
        // Required empty public constructor
    }

    private static final String TEXT_ID = "text_id";

    public static DisplayTipsFragment newInstance(@StringRes int textId) {

        DisplayTipsFragment frag = new DisplayTipsFragment();

        Bundle args = new Bundle();
        args.putInt(TEXT_ID, textId);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_tips, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tripList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripList);
        recyclerView.setAdapter(tripAdapter);

        loadTripsFromDB();

        return view;
    }

    private void loadTripsFromDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips") // Change to the correct collection name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tripList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Trip trip = document.toObject(Trip.class);
                        tripList.add(trip);
                    }
                    tripAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors here
                    Log.e("DisplayTipsFragment", "Error fetching data", e);
                });
    }
}
