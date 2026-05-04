package com.example.vehicleidentification.model;

import java.time.LocalDate;

public class PoliceReport {
    private int reportId;
    private int vehicleId;
    private LocalDate reportDate;
    private String reportType;
    private String description;
    private String officerName;
    private String location;
    private String caseNumber;
    private boolean isResolved;

    public PoliceReport(int reportId, int vehicleId, LocalDate reportDate,
                        String reportType, String description, String officerName) {
        this.reportId = reportId;
        this.vehicleId = vehicleId;
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.description = description;
        this.officerName = officerName;
        this.isResolved = false;
    }

    public PoliceReport(int reportId, int vehicleId, LocalDate reportDate,
                        String reportType, String description, String officerName,
                        String location, String caseNumber) {
        this(reportId, vehicleId, reportDate, reportType, description, officerName);
        this.location = location;
        this.caseNumber = caseNumber;
    }

    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }

    public String getReportTypeIcon() {
        switch (reportType.toLowerCase()) {
            case "theft": return "🚗";
            case "accident": return "💥";
            case "violation": return "📋";
            default: return "📄";
        }
    }

    @Override
    public String toString() {
        return String.format("Report{id=%d, type='%s', officer='%s', resolved=%s}",
                reportId, reportType, officerName, isResolved);
    }
}