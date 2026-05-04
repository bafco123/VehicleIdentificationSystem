package com.example.vehicleidentification.model;

import java.time.LocalDateTime;

public class CustomerQuery {
    private int queryId;
    private int customerId;
    private int vehicleId;
    private LocalDateTime queryDate;
    private String queryText;
    private String responseText;
    private String status;
    private LocalDateTime responseDate;
    private String respondedBy;

    public CustomerQuery(int queryId, int customerId, int vehicleId,
                         LocalDateTime queryDate, String queryText, String responseText) {
        this.queryId = queryId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.queryDate = queryDate;
        this.queryText = queryText;
        this.responseText = responseText;
        this.status = responseText != null && !responseText.isEmpty() ? "Answered" : "Pending";
    }

    public CustomerQuery(int queryId, int customerId, int vehicleId,
                         LocalDateTime queryDate, String queryText) {
        this(queryId, customerId, vehicleId, queryDate, queryText, null);
    }

    // Getters and Setters
    public int getQueryId() { return queryId; }
    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public LocalDateTime getQueryDate() { return queryDate; }
    public void setQueryDate(LocalDateTime queryDate) { this.queryDate = queryDate; }

    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }

    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) {
        this.responseText = responseText;
        this.status = "Answered";
        this.responseDate = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDateTime responseDate) { this.responseDate = responseDate; }

    public String getRespondedBy() { return respondedBy; }
    public void setRespondedBy(String respondedBy) { this.respondedBy = respondedBy; }

    public boolean isAnswered() {
        return "Answered".equals(status);
    }

    public long getResponseTimeHours() {
        if (responseDate != null && queryDate != null) {
            return java.time.Duration.between(queryDate, responseDate).toHours();
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Query{id=%d, status='%s', responseTime=%dh}",
                queryId, status, getResponseTimeHours());
    }
}