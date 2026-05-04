package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class InsuranceController {

    // Insurance Policy Fields
    @FXML private TextField vehicleIdField;
    @FXML private TextField companyField;
    @FXML private TextField policyNumberField;
    @FXML private TextArea coverageField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<InsurancePolicy> policyTable;
    @FXML private TableColumn<InsurancePolicy, Integer> colPolicyId;
    @FXML private TableColumn<InsurancePolicy, Integer> colVehicleId;
    @FXML private TableColumn<InsurancePolicy, String> colCompany;
    @FXML private TableColumn<InsurancePolicy, String> colPolicyNumber;
    @FXML private TableColumn<InsurancePolicy, String> colStartDate;
    @FXML private TableColumn<InsurancePolicy, String> colEndDate;
    @FXML private TableColumn<InsurancePolicy, String> colCoverage;
    @FXML private TableColumn<InsurancePolicy, String> colStatus;

    // Claim Fields
    @FXML private TextField claimPolicyIdField;
    @FXML private TextField claimAmountField;
    @FXML private TextArea claimDescriptionField;
    @FXML private DatePicker claimDatePicker;
    @FXML private ComboBox<String> claimStatusCombo;
    @FXML private TableView<Claim> claimTable;
    @FXML private TableColumn<Claim, Integer> colClaimId;
    @FXML private TableColumn<Claim, Integer> colClaimPolicyId;
    @FXML private TableColumn<Claim, String> colClaimDate;
    @FXML private TableColumn<Claim, Double> colClaimAmount;
    @FXML private TableColumn<Claim, String> colClaimDescription;
    @FXML private TableColumn<Claim, String> colClaimStatus;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<InsurancePolicy> policyList = FXCollections.observableArrayList();
    private ObservableList<Claim> claimList = FXCollections.observableArrayList();
    private InsurancePolicy selectedPolicy;
    private Claim selectedClaim;

    @FXML
    public void initialize() {
        // Set default dates
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusYears(1));

        // Initialize claim date picker
        if (claimDatePicker != null) {
            claimDatePicker.setValue(LocalDate.now());
        }

        // Populate ComboBox
        claimStatusCombo.setItems(FXCollections.observableArrayList("Pending", "Approved", "Rejected"));
        claimStatusCombo.setValue("Pending");

        setupPolicyTable();
        setupClaimTable();
        loadPolicies();
        loadClaims();
        setupSelections();
    }

    private void setupPolicyTable() {
        colPolicyId.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        colVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("insuranceCompany"));
        colPolicyNumber.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colCoverage.setCellValueFactory(new PropertyValueFactory<>("coverageDetails"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupClaimTable() {
        colClaimId.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        colClaimPolicyId.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        colClaimDate.setCellValueFactory(new PropertyValueFactory<>("claimDate"));
        colClaimAmount.setCellValueFactory(new PropertyValueFactory<>("claimAmount"));
        colClaimDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colClaimStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupSelections() {
        policyTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newPolicy) -> {
                    if (newPolicy != null) {
                        selectedPolicy = newPolicy;
                        populatePolicyForm(selectedPolicy);
                    }
                }
        );

        claimTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newClaim) -> {
                    if (newClaim != null) {
                        selectedClaim = newClaim;
                        claimStatusCombo.setValue(newClaim.getStatus());
                        if (claimDescriptionField != null) {
                            claimDescriptionField.setText(newClaim.getDescription());
                        }
                    }
                }
        );
    }

    private void populatePolicyForm(InsurancePolicy policy) {
        vehicleIdField.setText(String.valueOf(policy.getVehicleId()));
        companyField.setText(policy.getInsuranceCompany());
        policyNumberField.setText(policy.getPolicyNumber());
        startDatePicker.setValue(LocalDate.parse(policy.getStartDate()));
        endDatePicker.setValue(LocalDate.parse(policy.getEndDate()));
        coverageField.setText(policy.getCoverageDetails());
    }

    private void loadPolicies() {
        showProgress(true);
        policyList.clear();
        String sql = "SELECT * FROM InsurancePolicy ORDER BY policy_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InsurancePolicy policy = new InsurancePolicy(
                        rs.getInt("policy_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("insurance_company"),
                        rs.getString("policy_number"),
                        rs.getDate("start_date").toString(),
                        rs.getDate("end_date").toString(),
                        rs.getString("coverage_details")
                );
                policyList.add(policy);
            }
            policyTable.setItems(policyList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load policies: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    private void loadClaims() {
        claimList.clear();
        String sql = "SELECT * FROM Claim ORDER BY claim_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Claim claim = new Claim(
                        rs.getInt("claim_id"),
                        rs.getInt("policy_id"),
                        rs.getDate("claim_date").toString(),
                        rs.getDouble("claim_amount"),
                        rs.getString("status"),
                        rs.getString("description")
                );
                claimList.add(claim);
            }
            claimTable.setItems(claimList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load claims: " + e.getMessage());
        }
    }

    @FXML
    private void addPolicy() {
        if (vehicleIdField.getText().isEmpty() || companyField.getText().isEmpty() ||
                policyNumberField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all required fields");
            return;
        }

        showProgress(true);
        String sql = "INSERT INTO InsurancePolicy (vehicle_id, insurance_company, policy_number, start_date, end_date, coverage_details) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(vehicleIdField.getText()));
            pstmt.setString(2, companyField.getText());
            pstmt.setString(3, policyNumberField.getText());
            pstmt.setDate(4, Date.valueOf(startDatePicker.getValue()));
            pstmt.setDate(5, Date.valueOf(endDatePicker.getValue()));
            pstmt.setString(6, coverageField.getText());

            pstmt.executeUpdate();
            showAlert("Success", "Insurance policy added successfully!");
            loadPolicies();
            clearForm();

        } catch (SQLException e) {
            if (e.getMessage().contains("policy_number")) {
                showAlert("Error", "Policy number already exists!");
            } else {
                showAlert("Error", "Failed to add policy: " + e.getMessage());
            }
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deletePolicy() {
        if (selectedPolicy == null) {
            showAlert("Warning", "Please select a policy to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this policy?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM InsurancePolicy WHERE policy_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedPolicy.getPolicyId());
                pstmt.executeUpdate();
                showAlert("Success", "Policy deleted successfully!");
                loadPolicies();
                clearForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete policy: " + e.getMessage());
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void addClaim() {
        if (claimPolicyIdField.getText().isEmpty() || claimAmountField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all claim fields");
            return;
        }

        showProgress(true);
        String sql = "INSERT INTO Claim (policy_id, claim_date, claim_amount, status, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(claimPolicyIdField.getText()));
            pstmt.setDate(2, Date.valueOf(claimDatePicker.getValue()));
            pstmt.setDouble(3, Double.parseDouble(claimAmountField.getText()));
            pstmt.setString(4, claimStatusCombo.getValue());
            pstmt.setString(5, claimDescriptionField.getText());

            pstmt.executeUpdate();
            showAlert("Success", "Claim added successfully!");
            loadClaims();
            clearClaimForm();

        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to add claim: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void updateClaimStatus() {
        if (selectedClaim == null) {
            showAlert("Warning", "Please select a claim to update");
            return;
        }

        showProgress(true);
        String sql = "UPDATE Claim SET status=?, description=? WHERE claim_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, claimStatusCombo.getValue());
            pstmt.setString(2, claimDescriptionField.getText());
            pstmt.setInt(3, selectedClaim.getClaimId());

            pstmt.executeUpdate();
            showAlert("Success", "Claim updated successfully!");
            loadClaims();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update claim: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deleteClaim() {
        if (selectedClaim == null) {
            showAlert("Warning", "Please select a claim to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this claim?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM Claim WHERE claim_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedClaim.getClaimId());
                pstmt.executeUpdate();
                showAlert("Success", "Claim deleted successfully!");
                loadClaims();
                clearClaimForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete claim: " + e.getMessage());
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void clearForm() {
        vehicleIdField.clear();
        companyField.clear();
        policyNumberField.clear();
        coverageField.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusYears(1));
        selectedPolicy = null;
        policyTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void clearClaimForm() {
        claimPolicyIdField.clear();
        claimAmountField.clear();
        if (claimDescriptionField != null) {
            claimDescriptionField.clear();
        }
        if (claimDatePicker != null) {
            claimDatePicker.setValue(LocalDate.now());
        }
        claimStatusCombo.setValue("Pending");
        selectedClaim = null;
        claimTable.getSelectionModel().clearSelection();
    }

    private void showProgress(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(show);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Model class for Insurance Policy
    public static class InsurancePolicy {
        private final int policyId, vehicleId;
        private final String insuranceCompany, policyNumber, startDate, endDate, coverageDetails;

        public InsurancePolicy(int policyId, int vehicleId, String company, String policyNumber,
                               String startDate, String endDate, String coverage) {
            this.policyId = policyId;
            this.vehicleId = vehicleId;
            this.insuranceCompany = company;
            this.policyNumber = policyNumber;
            this.startDate = startDate;
            this.endDate = endDate;
            this.coverageDetails = coverage != null ? coverage : "";
        }

        public int getPolicyId() { return policyId; }
        public int getVehicleId() { return vehicleId; }
        public String getInsuranceCompany() { return insuranceCompany; }
        public String getPolicyNumber() { return policyNumber; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getCoverageDetails() { return coverageDetails; }
        public String getStatus() {
            LocalDate end = LocalDate.parse(endDate);
            return end.isBefore(LocalDate.now()) ? "Expired" : "Active";
        }
    }

    // Model class for Claim
    public static class Claim {
        private final int claimId, policyId;
        private final String claimDate, status, description;
        private final double claimAmount;

        public Claim(int claimId, int policyId, String claimDate, double amount, String status, String description) {
            this.claimId = claimId;
            this.policyId = policyId;
            this.claimDate = claimDate;
            this.claimAmount = amount;
            this.status = status;
            this.description = description != null ? description : "";
        }

        public int getClaimId() { return claimId; }
        public int getPolicyId() { return policyId; }
        public String getClaimDate() { return claimDate; }
        public double getClaimAmount() { return claimAmount; }
        public String getStatus() { return status; }
        public String getDescription() { return description; }
    }
}