package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import com.example.vehicleidentification.model.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Optional;

public class VehicleController {

    @FXML private TextField regNumberField, makeField, modelField, yearField, ownerIdField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colId;
    @FXML private TableColumn<Vehicle, String> colRegNumber, colMake, colModel, colOwnerName;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ProgressBar progressBar;

    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private ObservableList<Vehicle> filteredList = FXCollections.observableArrayList();
    private Vehicle selectedVehicle;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadVehicles();
        setupTableSelection();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colRegNumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colOwnerName.setCellValueFactory(cellData ->
                new SimpleStringProperty(getOwnerName(cellData.getValue().getOwnerId())));
    }

    private void setupSearchFilter() {
        filterCombo.setItems(FXCollections.observableArrayList("All", "Registration", "Make", "Model", "Year"));
        filterCombo.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterVehicles());
    }

    private void filterVehicles() {
        String searchText = searchField.getText().toLowerCase();
        String filterBy = filterCombo.getValue();

        if (searchText.isEmpty()) {
            vehicleTable.setItems(vehicleList);
            return;
        }

        filteredList.clear();

        for (Vehicle vehicle : vehicleList) {
            switch (filterBy) {
                case "Registration":
                    if (vehicle.getRegistrationNumber().toLowerCase().contains(searchText))
                        filteredList.add(vehicle);
                    break;
                case "Make":
                    if (vehicle.getMake().toLowerCase().contains(searchText))
                        filteredList.add(vehicle);
                    break;
                case "Model":
                    if (vehicle.getModel().toLowerCase().contains(searchText))
                        filteredList.add(vehicle);
                    break;
                case "Year":
                    if (String.valueOf(vehicle.getYear()).contains(searchText))
                        filteredList.add(vehicle);
                    break;
                default:
                    if (vehicle.getRegistrationNumber().toLowerCase().contains(searchText) ||
                            vehicle.getMake().toLowerCase().contains(searchText) ||
                            vehicle.getModel().toLowerCase().contains(searchText) ||
                            String.valueOf(vehicle.getYear()).contains(searchText))
                        filteredList.add(vehicle);
                    break;
            }
        }
        vehicleTable.setItems(filteredList);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        filterCombo.setValue("All");
        vehicleTable.setItems(vehicleList);
    }

    private void setupTableSelection() {
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedVehicle = newSelection;
                        populateForm(selectedVehicle);
                    }
                }
        );
    }

    private void populateForm(Vehicle vehicle) {
        regNumberField.setText(vehicle.getRegistrationNumber());
        makeField.setText(vehicle.getMake());
        modelField.setText(vehicle.getModel());
        yearField.setText(String.valueOf(vehicle.getYear()));
        ownerIdField.setText(String.valueOf(vehicle.getOwnerId()));
    }

    private void loadVehicles() {
        showProgress(true);
        vehicleList.clear();
        String sql = "SELECT * FROM Vehicle ORDER BY vehicle_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("owner_id")
                );
                vehicleList.add(vehicle);
            }
            vehicleTable.setItems(vehicleList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load vehicles: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void addVehicle() {
        if (!validateInput()) return;

        showProgress(true);
        String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regNumberField.getText());
            pstmt.setString(2, makeField.getText());
            pstmt.setString(3, modelField.getText());
            pstmt.setInt(4, Integer.parseInt(yearField.getText()));
            pstmt.setInt(5, Integer.parseInt(ownerIdField.getText()));

            pstmt.executeUpdate();
            showAlert("Success", "Vehicle added successfully!", Alert.AlertType.INFORMATION);
            loadVehicles();
            clearForm();

        } catch (SQLException e) {
            showAlert("Error", "Failed to add vehicle: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void updateVehicle() {
        if (selectedVehicle == null) {
            showAlert("Warning", "Please select a vehicle to update", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInput()) return;

        showProgress(true);
        String sql = "UPDATE Vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=? WHERE vehicle_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regNumberField.getText());
            pstmt.setString(2, makeField.getText());
            pstmt.setString(3, modelField.getText());
            pstmt.setInt(4, Integer.parseInt(yearField.getText()));
            pstmt.setInt(5, Integer.parseInt(ownerIdField.getText()));
            pstmt.setInt(6, selectedVehicle.getVehicleId());

            pstmt.executeUpdate();
            showAlert("Success", "Vehicle updated successfully!", Alert.AlertType.INFORMATION);
            loadVehicles();
            clearForm();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update vehicle: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deleteVehicle() {
        if (selectedVehicle == null) {
            showAlert("Warning", "Please select a vehicle to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete vehicle " +
                selectedVehicle.getRegistrationNumber() + "?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM Vehicle WHERE vehicle_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedVehicle.getVehicleId());
                pstmt.executeUpdate();
                showAlert("Success", "Vehicle deleted successfully!", Alert.AlertType.INFORMATION);
                loadVehicles();
                clearForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete vehicle: " + e.getMessage(), Alert.AlertType.ERROR);
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void clearForm() {
        regNumberField.clear();
        makeField.clear();
        modelField.clear();
        yearField.clear();
        ownerIdField.clear();
        selectedVehicle = null;
        vehicleTable.getSelectionModel().clearSelection();
        clearSearch();
    }

    private boolean validateInput() {
        if (regNumberField.getText().isEmpty() || makeField.getText().isEmpty() ||
                modelField.getText().isEmpty() || yearField.getText().isEmpty() ||
                ownerIdField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all fields", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText());
            if (year < 1900 || year > 2026) {
                showAlert("Validation Error", "Year must be between 1900 and 2026", Alert.AlertType.ERROR);
                return false;
            }
            Integer.parseInt(ownerIdField.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Year and Owner ID must be numbers", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private String getOwnerName(int ownerId) {
        String sql = "SELECT name FROM Customer WHERE customer_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            return "Unknown";
        }
        return "Unknown";
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
        progressBar.setVisible(show);
        if (show) {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}