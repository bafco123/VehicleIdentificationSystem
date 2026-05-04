package com.example.vehicleidentification.model;

public class Police extends User {
    private String badgeNumber;
    private String department;

    public Police(int userId, String name, String email, String phone,
                  String badgeNumber, String department) {
        super(userId, name, email, phone);
        this.badgeNumber = badgeNumber;
        this.department = department;
    }

    @Override
    public String getRole() {

        return "Police Officer";
    }

    @Override
    public String getPermissions() {

        return "View vehicles, Create police reports, Issue violations";
    }

    public String getBadgeNumber() { return badgeNumber; }
    public String getDepartment() { return department; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }
    public void setDepartment(String department) { this.department = department; }
}