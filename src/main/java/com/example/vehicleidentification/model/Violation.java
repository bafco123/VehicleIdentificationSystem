package com.example.vehicleidentification.model;

import java.time.LocalDate;

public class Violation {
    private int violationId;
    private int vehicleId;
    private LocalDate violationDate;
    private String violationType;
    private double fineAmount;
    private String status;
    private String location;
    private String officerName;
    private LocalDate paymentDate;

    public Violation(int violationId, int vehicleId, LocalDate violationDate,
                     String violationType, double fineAmount, String status) {
        this.violationId = violationId;
        this.vehicleId = vehicleId;
        this.violationDate = violationDate;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public Violation(int violationId, int vehicleId, LocalDate violationDate,
                     String violationType, double fineAmount, String status,
                     String location, String officerName) {
        this(violationId, vehicleId, violationDate, violationType, fineAmount, status);
        this.location = location;
        this.officerName = officerName;
    }

    // Getters and Setters
    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDate violationDate) { this.violationDate = violationDate; }

    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public boolean isPaid() {
        return "Paid".equals(status);
    }

    public void markAsPaid() {
        this.status = "Paid";
        this.paymentDate = LocalDate.now();
    }

    public String getFineFormatted() {
        return String.format("$%.2f", fineAmount);
    }

    public long getDaysSinceViolation() {
        return java.time.temporal.ChronoUnit.DAYS.between(violationDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Violation{id=%d, type='%s', fine=$%.2f, status='%s'}",
                violationId, violationType, fineAmount, status);
    }
}