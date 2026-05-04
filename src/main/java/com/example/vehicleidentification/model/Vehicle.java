package com.example.vehicleidentification.model;

public class Vehicle {
    private int vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private int ownerId;

    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, int ownerId) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
    }

    // Getters
    public int getVehicleId() { return vehicleId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getOwnerId() { return ownerId; }

    // Setters
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getFullName() {
        return year + " " + make + " " + model;
    }

    @Override
    public String toString() {
        return registrationNumber + " - " + make + " " + model;
    }
}