package com.example.vehicleidentification.model;

import java.time.LocalDate;

public class Claim {
    private int claimId;
    private int policyId;
    private LocalDate claimDate;
    private double claimAmount;
    private String status;
    private String description;
    private String approvedBy;
    private LocalDate resolutionDate;

    public Claim(int claimId, int policyId, LocalDate claimDate, double claimAmount, String status) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.claimDate = claimDate;
        this.claimAmount = claimAmount;
        this.status = status;
    }

    public Claim(int claimId, int policyId, LocalDate claimDate, double claimAmount,
                 String status, String description) {
        this(claimId, policyId, claimDate, claimAmount, status);
        this.description = description;
    }

    // Getters and Setters
    public int getClaimId() { return claimId; }
    public void setClaimId(int claimId) { this.claimId = claimId; }

    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }

    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }

    public double getClaimAmount() { return claimAmount; }
    public void setClaimAmount(double claimAmount) { this.claimAmount = claimAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDate getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(LocalDate resolutionDate) { this.resolutionDate = resolutionDate; }

    public boolean isPending() {
        return "Pending".equals(status);
    }

    public boolean isApproved() {
        return "Approved".equals(status);
    }

    public String getStatusColor() {
        switch (status) {
            case "Approved": return "green";
            case "Rejected": return "red";
            default: return "orange";
        }
    }

    @Override
    public String toString() {
        return String.format("Claim{id=%d, amount=$%.2f, status='%s'}",
                claimId, claimAmount, status);
    }
}