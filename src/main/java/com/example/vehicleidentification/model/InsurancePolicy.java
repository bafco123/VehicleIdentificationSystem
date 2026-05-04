package com.example.vehicleidentification.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InsurancePolicy {
    private int policyId;
    private int vehicleId;
    private String insuranceCompany;
    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverageDetails;
    private double premiumAmount;
    private String policyType;

    public InsurancePolicy(int policyId, int vehicleId, String insuranceCompany,
                           String policyNumber, LocalDate startDate, LocalDate endDate,
                           String coverageDetails) {
        this.policyId = policyId;
        this.vehicleId = vehicleId;
        this.insuranceCompany = insuranceCompany;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverageDetails = coverageDetails;
        this.premiumAmount = 0.0;
        this.policyType = "Comprehensive";
    }

    // Getters and Setters
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getInsuranceCompany() { return insuranceCompany; }
    public void setInsuranceCompany(String insuranceCompany) { this.insuranceCompany = insuranceCompany; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(String coverageDetails) { this.coverageDetails = coverageDetails; }

    public double getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(double premiumAmount) { this.premiumAmount = premiumAmount; }

    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public String getStatus() {
        LocalDate now = LocalDate.now();
        if (endDate.isBefore(now)) {
            return "Expired";
        } else if (startDate.isAfter(now)) {
            return "Pending";
        } else {
            return "Active";
        }
    }

    public long getDaysRemaining() {
        if (getStatus().equals("Active")) {
            return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        }
        return 0;
    }

    public boolean isExpiringSoon() {
        return getDaysRemaining() <= 30 && getDaysRemaining() > 0;
    }

    @Override
    public String toString() {
        return String.format("Policy{id=%d, company='%s', number='%s', status='%s'}",
                policyId, insuranceCompany, policyNumber, getStatus());
    }
}