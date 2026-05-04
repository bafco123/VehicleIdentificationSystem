package com.example.vehicleidentification.model;

import java.time.LocalDate;

public class WorkshopService {
    private int serviceId;
    private int vehicleId;
    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private double cost;
    private String mechanicName;
    private boolean isCompleted;

    public WorkshopService(int serviceId, int vehicleId, LocalDate serviceDate,
                           String serviceType, String description, double cost) {
        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.isCompleted = false;
    }

    public WorkshopService(int serviceId, int vehicleId, LocalDate serviceDate,
                           String serviceType, String description, double cost,
                           String mechanicName) {
        this(serviceId, vehicleId, serviceDate, serviceType, description, cost);
        this.mechanicName = mechanicName;
    }

    // Getters and Setters
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCostFormatted() {
        return String.format("$%.2f", cost);
    }

    @Override
    public String toString() {
        return String.format("Service{id=%d, type='%s', cost=$%.2f, completed=%s}",
                serviceId, serviceType, cost, isCompleted);
    }
}