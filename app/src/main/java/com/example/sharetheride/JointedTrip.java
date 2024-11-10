package com.example.sharetheride;

public class JointedTrip {
    private String tripId;
    private String startLocation;
    private String endLocation;
    private String docId; // Firestore document ID

    public JointedTrip(String tripId, String startLocation, String endLocation, String docId) {
        this.tripId = tripId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.docId = docId;
    }

    // Getters
    public String getTripId() { return tripId; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public String getDocId() { return docId; }

//    // Setters (optional, in case you want to modify data later)
//    public void setTripId(String tripId) { this.tripId = tripId; }
//    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
//    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }
//    public void setDocId(String docId) { this.docId = docId; }
}
