package com.example.sharetheride;

public class userProfileList {
    private String name, surname, phone_number, car_size, car_model, email, vehicle_plate, role;

    public userProfileList() {}

    public userProfileList(String car_model, String car_size, String email, String name, String phone_number, String role, String surname, String vehicle_plate) {
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.car_size = car_size;
        this.car_model = car_model;
        this.email = email;
        this.vehicle_plate = vehicle_plate;
        this.role = role;
    }

    public String getUserName() {return name;}
    public String getUserSurname() {return surname;}
    public String getUserPhone() {return phone_number;}
    public String getUserCarSize() {return car_size;}
    public String getUserCarModel() {return car_model;}
    public String getUserEmail() {return email;}
    public String getUserVehiclePlate() {return vehicle_plate;}
    public String getUserRole() {return role;}
}
