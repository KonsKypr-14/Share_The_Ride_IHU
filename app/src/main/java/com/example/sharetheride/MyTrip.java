package com.example.sharetheride;

public class MyTrip {
    private String tripId;
    private String startLocation;
    private String endLocation;
    private String docId; // Firestore document ID

    public MyTrip(String tripId, String startLocation, String endLocation, String docId) {
        this.tripId = tripId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.docId = docId;
    }

    public String getTripId() { return tripId; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public String getDocId() { return docId; }
}
