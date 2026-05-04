package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class WorkshopController {

    @FXML private TextField vehicleIdField, serviceTypeField, costField;
    @FXML private DatePicker serviceDatePicker;
    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> colServiceId, colVehicleId;
    @FXML private TableColumn<ServiceRecord, String> colServiceDate, colServiceType;
    @FXML private TableColumn<ServiceRecord, Double> colCost;
    @FXML private ProgressBar progressBar;

    private ObservableList<ServiceRecord> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        serviceDatePicker.setValue(LocalDate.now());
        setupTable();
        loadServices();
    }

    private void setupTable() {
        colServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void loadServices() {
        showProgress(true);
        serviceList.clear();
        String sql = "SELECT * FROM ServiceRecord ORDER BY service_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ServiceRecord service = new ServiceRecord(
                        rs.getInt("service_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("service_date").toString(),
                        rs.getString("service_type"),
                        rs.getDouble("cost")
                );
                serviceList.add(service);
            }
            serviceTable.setItems(serviceList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load services: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void addService() {
        if (vehicleIdField.getText().isEmpty() || serviceTypeField.getText().isEmpty() ||
                costField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all fields");
            return;
        }

        showProgress(true);
        String sql = "INSERT INTO ServiceRecord (vehicle_id, service_date, service_type, cost) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(vehicleIdField.getText()));
            pstmt.setDate(2, Date.valueOf(serviceDatePicker.getValue()));
            pstmt.setString(3, serviceTypeField.getText());
            pstmt.setDouble(4, Double.parseDouble(costField.getText()));

            pstmt.executeUpdate();
            showAlert("Success", "Service record added successfully!");
            loadServices();
            clearForm();

        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to add service: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void clearForm() {
        vehicleIdField.clear();
        serviceDatePicker.setValue(LocalDate.now());
        serviceTypeField.clear();
        costField.clear();
    }

    private void showProgress(boolean show) {
        progressBar.setVisible(show);
        if (show) {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ServiceRecord {
        private int serviceId, vehicleId;
        private String serviceDate, serviceType;
        private double cost;

        public ServiceRecord(int serviceId, int vehicleId, String serviceDate, String serviceType, double cost) {
            this.serviceId = serviceId;
            this.vehicleId = vehicleId;
            this.serviceDate = serviceDate;
            this.serviceType = serviceType;
            this.cost = cost;
        }

        public int getServiceId() { return serviceId; }
        public int getVehicleId() { return vehicleId; }
        public String getServiceDate() { return serviceDate; }
        public String getServiceType() { return serviceType; }
        public double getCost() { return cost; }
    }
}