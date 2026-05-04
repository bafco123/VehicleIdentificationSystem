package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import com.example.vehicleidentification.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Optional;

public class CustomerController {

    @FXML private TextField nameField, phoneField, emailField;
    @FXML private TextArea addressField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName, colAddress, colPhone, colEmail;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Customer selectedCustomer;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadCustomers();
        setupTableSelection();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void setupTableSelection() {
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedCustomer = newSelection;
                        populateForm(selectedCustomer);
                    }
                }
        );
    }

    private void populateForm(Customer customer) {
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
    }

    private void loadCustomers() {
        showProgress(true);
        customerList.clear();
        String sql = "SELECT * FROM Customer ORDER BY customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                customerList.add(customer);
            }
            customerTable.setItems(customerList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void addCustomer() {
        if (!validateInput()) return;

        showProgress(true);
        String sql = "INSERT INTO Customer (name, address, phone, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, addressField.getText());
            pstmt.setString(3, phoneField.getText());
            pstmt.setString(4, emailField.getText());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert("Success", "Customer added successfully!", Alert.AlertType.INFORMATION);
            }

            loadCustomers();
            clearForm();

        } catch (SQLException e) {
            if (e.getMessage().contains("email")) {
                showAlert("Error", "Email already exists!", Alert.AlertType.ERROR);
            } else {
                showAlert("Error", "Failed to add customer: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void updateCustomer() {
        if (selectedCustomer == null) {
            showAlert("Warning", "Please select a customer to update", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInput()) return;

        showProgress(true);
        String sql = "UPDATE Customer SET name=?, address=?, phone=?, email=? WHERE customer_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, addressField.getText());
            pstmt.setString(3, phoneField.getText());
            pstmt.setString(4, emailField.getText());
            pstmt.setInt(5, selectedCustomer.getUserId());

            pstmt.executeUpdate();
            showAlert("Success", "Customer updated successfully!", Alert.AlertType.INFORMATION);
            loadCustomers();
            clearForm();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update customer: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deleteCustomer() {
        if (selectedCustomer == null) {
            showAlert("Warning", "Please select a customer to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete customer: " +
                selectedCustomer.getName() + "?\nThis will also delete their vehicles!");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM Customer WHERE customer_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedCustomer.getUserId());
                pstmt.executeUpdate();
                showAlert("Success", "Customer deleted successfully!", Alert.AlertType.INFORMATION);
                loadCustomers();
                clearForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete customer: " + e.getMessage(), Alert.AlertType.ERROR);
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void clearForm() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();
        selectedCustomer = null;
        customerTable.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all required fields (Name, Phone, Email)", Alert.AlertType.ERROR);
            return false;
        }

        if (!emailField.getText().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email address", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}