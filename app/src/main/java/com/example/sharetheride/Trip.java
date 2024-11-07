package com.example.sharetheride;

import java.util.Date;
import java.util.List;

public class Trip {
    private String destination;
    //private String date;
    private String date;
    private String description;


    private String trip_id;
    private String organizerId;
    private String organizerName;
    private String organizer_name;
    private String vehiclePlate;
    private String carModel;
    private String start_location;
    private String start_location_name;
    private String end_location;
    private String end_location_name;
    private List<PickupPoint> pickupPoints;
    private String startTime;
    private String endTime;
    private String maxPassengers;
    private String currentPassengers;
    private List<Passenger> passengers;
    private String tripStatus;
    private String pricePerSeat;
    private String price_per_seat;
    private String rating;
    private Date createdAt;
    private Date updatedAt;

    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
    }

    // Full constructor
    public Trip(String trip_id, String organizerId, String organizerName, String organizer_name, String vehiclePlate,
                String carModel, String start_location, String end_location, String start_location_name, String end_location_name,
                List<PickupPoint> pickupPoints, String startTime, String endTime,
                String maxPassengers, String currentPassengers, List<Passenger> passengers,
                String tripStatus, String pricePerSeat, String price_per_seat, String rating, Date createdAt, Date updatedAt) {
        this.trip_id = trip_id;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.organizer_name = organizer_name;
        this.vehiclePlate = vehiclePlate;
        this.carModel = carModel;
        this.start_location = start_location;
        this.start_location_name = start_location_name;
        this.end_location = end_location;
        this.end_location_name = end_location_name;
        this.pickupPoints = pickupPoints;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPassengers = maxPassengers;
        this.currentPassengers = currentPassengers;
        this.passengers = passengers;
        this.tripStatus = tripStatus;
        this.pricePerSeat = pricePerSeat;
        this.price_per_seat = price_per_seat;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters for all fields

    public String gettrip_id() {
        return trip_id;
    }

    public void settrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getorganizer_name() {
        return organizer_name;
    }

    public void setorganizer_name(String organizer_name) {
        this.organizer_name = organizer_name;
    }

    public String getvehicle_plate() {
        return vehiclePlate;
    }

    public void setvehicle_plate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getcar_model() {
        return carModel;
    }

    public void setcar_model(String carModel) {
        this.carModel = carModel;
    }

    public String getstart_location() {
        return start_location;
    }

    public void setstart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getstart_location_name() {
        return start_location_name;
    }

    public void setstart_location_name(String start_location_name) {
        this.start_location_name = start_location_name;
    }

    public String getend_location() {
        return end_location;
    }

    public void setend_location(String end_location) {
        this.end_location = end_location;
    }

    public String getend_location_name() {
        return end_location_name;
    }

    public void setend_location_name(String end_location_name) {
        this.end_location_name = end_location_name;
    }

    public List<PickupPoint> getPickupPoints() {
        return pickupPoints;
    }

    public void setPickupPoints(List<PickupPoint> pickupPoints) {
        this.pickupPoints = pickupPoints;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getend_time() {
        return endTime;
    }

    public void setend_time(String endTime) {
        this.endTime = endTime;
    }

    public String getmax_passengers() {
        return maxPassengers;
    }

    public void setmax_passengers(String maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public String getcurrent_passengers() {
        return currentPassengers;
    }

    public void setcurrent_passengers(String currentPassengers) {
        this.currentPassengers = currentPassengers;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(String pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public String getprice_per_seat() {
        return price_per_seat;
    }

    public void setprice_per_seat(String price_per_seat) {
        this.price_per_seat = price_per_seat;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getcreated_at() {
        return createdAt;
    }

    public void setcreated_at(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    // Inner classes for PickupPoint and Passenger

    public static class PickupPoint {
        private String location;
        private String label;

        public PickupPoint() {
        }

        public PickupPoint(String location, String label) {
            this.location = location;
            this.label = label;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class Passenger {
        private String passengerId;
        private String name;
        private boolean pickupConfirmed;
        private boolean available;

        public Passenger() {
        }

        public Passenger(String passengerId, String name, boolean pickupConfirmed, boolean available) {
            this.passengerId = passengerId;
            this.name = name;
            this.pickupConfirmed = pickupConfirmed;
            this.available = available;
        }

        public String getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(String passengerId) {
            this.passengerId = passengerId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isPickupConfirmed() {
            return pickupConfirmed;
        }

        public void setPickupConfirmed(boolean pickupConfirmed) {
            this.pickupConfirmed = pickupConfirmed;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    /*
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
     */
}
