package com.example.sharetheride;

public class Trip {
    private String destination;
    private String date;
    private String description;

    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
    }

    public Trip(String destination, String date, String description) {
        this.destination = destination;
        this.date = date;
        this.description = description;
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
