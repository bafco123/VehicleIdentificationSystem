package com.example.vehicleidentification.model;

public class Customer extends User {
    private String address;

    public Customer(int userId, String name, String address, String phone, String email) {
        super(userId, name, email, phone);
        this.address = address;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String getPermissions() {
        return "View own vehicles, Create service requests";
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}