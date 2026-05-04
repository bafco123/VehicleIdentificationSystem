package com.example.vehicleidentification.model;

public abstract class User {
    protected int userId;
    protected String name;
    protected String email;
    protected String phone;

    public User(int userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public abstract String getRole();
    public abstract String getPermissions();

    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDisplayInfo() {
        return name + " (" + getRole() + ")";
    }
}