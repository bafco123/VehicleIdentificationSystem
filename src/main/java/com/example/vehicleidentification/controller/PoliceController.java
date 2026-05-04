package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class PoliceController {

    // Police Report Fields
    @FXML private TextField reportVehicleIdField;
    @FXML private TextField officerNameField;
    @FXML private TextArea reportDescriptionField;
    @FXML private TextField reportLocationField;
    @FXML private DatePicker reportDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TableColumn<PoliceReport, Integer> colReportId;
    @FXML private TableColumn<PoliceReport, Integer> colReportVehicleId;
    @FXML private TableColumn<PoliceReport, String> colReportDate;
    @FXML private TableColumn<PoliceReport, String> colReportType;
    @FXML private TableColumn<PoliceReport, String> colReportDescription;
    @FXML private TableColumn<PoliceReport, String> colReportLocation;
    @FXML private TableColumn<PoliceReport, String> colOfficerName;

    // Violation Fields
    @FXML private TextField violationVehicleIdField;
    @FXML private TextField fineAmountField;
    @FXML private TextField violationLocationField;
    @FXML private DatePicker violationDatePicker;
    @FXML private ComboBox<String> violationTypeCombo;
    @FXML private ComboBox<String> violationStatusCombo;
    @FXML private TableView<Violation> violationTable;
    @FXML private TableColumn<Violation, Integer> colViolationId;
    @FXML private TableColumn<Violation, Integer> colViolationVehicleId;
    @FXML private TableColumn<Violation, String> colViolationDate;
    @FXML private TableColumn<Violation, String> colViolationType;
    @FXML private TableColumn<Violation, String> colViolationLocation;
    @FXML private TableColumn<Violation, Double> colFineAmount;
    @FXML private TableColumn<Violation, String> colViolationStatus;
    @FXML private ProgressBar progressBar;

    private ObservableList<PoliceReport> reportList = FXCollections.observableArrayList();
    private ObservableList<Violation> violationList = FXCollections.observableArrayList();
    private PoliceReport selectedReport;
    private Violation selectedViolation;

    @FXML
    public void initialize() {
        reportDatePicker.setValue(LocalDate.now());
        violationDatePicker.setValue(LocalDate.now());

        // Populate report type combo box
        reportTypeCombo.setItems(FXCollections.observableArrayList("Accident", "Theft", "Violation", "Stolen"));
        reportTypeCombo.setValue("Accident");

        // Populate violation type combo box
        violationTypeCombo.setItems(FXCollections.observableArrayList(
                "Speeding", "Running Red Light", "Illegal Parking",
                "No Seatbelt", "Expired Registration", "Drunk Driving",
                "Reckless Driving", "No Insurance", "Wrong Way Driving"
        ));
        violationTypeCombo.setValue("Speeding");

        // Populate violation status combo box
        violationStatusCombo.setItems(FXCollections.observableArrayList("Unpaid", "Paid"));
        violationStatusCombo.setValue("Unpaid");

        setupReportTable();
        setupViolationTable();
        loadReports();
        loadViolations();
        setupSelections();
    }

    private void setupReportTable() {
        colReportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colReportVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colReportType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colReportDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colReportLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colOfficerName.setCellValueFactory(new PropertyValueFactory<>("officerName"));
    }

    private void setupViolationTable() {
        colViolationId.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colViolationVehicleId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colViolationLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colViolationStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupSelections() {
        reportTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newReport) -> {
                    if (newReport != null) {
                        selectedReport = newReport;
                        populateReportForm(selectedReport);
                    }
                }
        );

        violationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newViolation) -> {
                    if (newViolation != null) {
                        selectedViolation = newViolation;
                        populateViolationForm(selectedViolation);
                    }
                }
        );
    }

    private void populateReportForm(PoliceReport report) {
        reportVehicleIdField.setText(String.valueOf(report.getVehicleId()));
        reportDatePicker.setValue(LocalDate.parse(report.getReportDate()));
        reportTypeCombo.setValue(report.getReportType());
        reportDescriptionField.setText(report.getDescription());
        reportLocationField.setText(report.getLocation());
        officerNameField.setText(report.getOfficerName());
    }

    private void populateViolationForm(Violation violation) {
        violationVehicleIdField.setText(String.valueOf(violation.getVehicleId()));
        violationDatePicker.setValue(LocalDate.parse(violation.getViolationDate()));
        violationTypeCombo.setValue(violation.getViolationType());
        violationLocationField.setText(violation.getLocation());
        fineAmountField.setText(String.valueOf(violation.getFineAmount()));
        violationStatusCombo.setValue(violation.getStatus());
    }

    private void loadReports() {
        showProgress(true);
        reportList.clear();
        String sql = "SELECT * FROM PoliceReport ORDER BY report_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PoliceReport report = new PoliceReport(
                        rs.getInt("report_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("report_date").toString(),
                        rs.getString("report_type"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("officer_name")
                );
                reportList.add(report);
            }
            reportTable.setItems(reportList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load reports: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    private void loadViolations() {
        showProgress(true);
        violationList.clear();
        String sql = "SELECT * FROM Violation ORDER BY violation_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Violation violation = new Violation(
                        rs.getInt("violation_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("violation_date").toString(),
                        rs.getString("violation_type"),
                        rs.getString("location"),
                        rs.getDouble("fine_amount"),
                        rs.getString("status")
                );
                violationList.add(violation);
            }
            violationTable.setItems(violationList);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load violations: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void addReport() {
        if (reportVehicleIdField.getText().isEmpty() || officerNameField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all required fields");
            return;
        }

        showProgress(true);
        String sql = "INSERT INTO PoliceReport (vehicle_id, report_date, report_type, description, location, officer_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(reportVehicleIdField.getText()));
            pstmt.setDate(2, Date.valueOf(reportDatePicker.getValue()));
            pstmt.setString(3, reportTypeCombo.getValue());
            pstmt.setString(4, reportDescriptionField.getText());
            pstmt.setString(5, reportLocationField.getText());
            pstmt.setString(6, officerNameField.getText());

            pstmt.executeUpdate();
            showAlert("Success", "Police report added successfully!");
            loadReports();
            clearReportForm();

        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to add report: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deleteReport() {
        if (selectedReport == null) {
            showAlert("Warning", "Please select a report to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this report?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM PoliceReport WHERE report_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedReport.getReportId());
                pstmt.executeUpdate();
                showAlert("Success", "Report deleted successfully!");
                loadReports();
                clearReportForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete report: " + e.getMessage());
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void addViolation() {
        if (violationVehicleIdField.getText().isEmpty() || fineAmountField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all violation fields");
            return;
        }

        showProgress(true);
        String sql = "INSERT INTO Violation (vehicle_id, violation_date, violation_type, location, fine_amount, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(violationVehicleIdField.getText()));
            pstmt.setDate(2, Date.valueOf(violationDatePicker.getValue()));
            pstmt.setString(3, violationTypeCombo.getValue());
            pstmt.setString(4, violationLocationField.getText());
            pstmt.setDouble(5, Double.parseDouble(fineAmountField.getText()));
            pstmt.setString(6, violationStatusCombo.getValue());

            pstmt.executeUpdate();
            showAlert("Success", "Violation added successfully!");
            loadViolations();
            clearViolationForm();

        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to add violation: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void updateViolation() {
        if (selectedViolation == null) {
            showAlert("Warning", "Please select a violation to update");
            return;
        }

        showProgress(true);
        String sql = "UPDATE Violation SET violation_type=?, location=?, fine_amount=?, status=? WHERE violation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, violationTypeCombo.getValue());
            pstmt.setString(2, violationLocationField.getText());
            pstmt.setDouble(3, Double.parseDouble(fineAmountField.getText()));
            pstmt.setString(4, violationStatusCombo.getValue());
            pstmt.setInt(5, selectedViolation.getViolationId());

            pstmt.executeUpdate();
            showAlert("Success", "Violation updated successfully!");
            loadViolations();
            clearViolationForm();

        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to update violation: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void deleteViolation() {
        if (selectedViolation == null) {
            showAlert("Warning", "Please select a violation to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this violation?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            showProgress(true);
            String sql = "DELETE FROM Violation WHERE violation_id=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, selectedViolation.getViolationId());
                pstmt.executeUpdate();
                showAlert("Success", "Violation deleted successfully!");
                loadViolations();
                clearViolationForm();

            } catch (SQLException e) {
                showAlert("Error", "Failed to delete violation: " + e.getMessage());
            } finally {
                showProgress(false);
            }
        }
    }

    @FXML
    private void markAsPaid() {
        if (selectedViolation == null) {
            showAlert("Warning", "Please select a violation to mark as paid");
            return;
        }

        if (selectedViolation.getStatus().equals("Paid")) {
            showAlert("Info", "This violation is already marked as paid");
            return;
        }

        showProgress(true);
        String sql = "UPDATE Violation SET status='Paid' WHERE violation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedViolation.getViolationId());
            pstmt.executeUpdate();
            showAlert("Success", "Violation marked as paid!");
            loadViolations();
            clearViolationForm();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update violation: " + e.getMessage());
        } finally {
            showProgress(false);
        }
    }

    @FXML
    private void clearReportForm() {
        reportVehicleIdField.clear();
        reportDatePicker.setValue(LocalDate.now());
        reportTypeCombo.setValue("Accident");
        reportDescriptionField.clear();
        reportLocationField.clear();
        officerNameField.clear();
        selectedReport = null;
        reportTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void clearViolationForm() {
        violationVehicleIdField.clear();
        violationDatePicker.setValue(LocalDate.now());
        violationTypeCombo.setValue("Speeding");
        violationLocationField.clear();
        fineAmountField.clear();
        violationStatusCombo.setValue("Unpaid");
        selectedViolation = null;
        violationTable.getSelectionModel().clearSelection();
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisible(show);
            if (show) {
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Police Report Model
    public static class PoliceReport {
        private final int reportId, vehicleId;
        private final String reportDate, reportType, description, location, officerName;

        public PoliceReport(int reportId, int vehicleId, String reportDate, String reportType,
                            String description, String location, String officerName) {
            this.reportId = reportId;
            this.vehicleId = vehicleId;
            this.reportDate = reportDate;
            this.reportType = reportType;
            this.description = description != null ? description : "";
            this.location = location != null ? location : "";
            this.officerName = officerName != null ? officerName : "";
        }

        public int getReportId() { return reportId; }
        public int getVehicleId() { return vehicleId; }
        public String getReportDate() { return reportDate; }
        public String getReportType() { return reportType; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public String getOfficerName() { return officerName; }
    }

    // Violation Model
    public static class Violation {
        private final int violationId, vehicleId;
        private final String violationDate, violationType, location, status;
        private final double fineAmount;

        public Violation(int violationId, int vehicleId, String violationDate, String violationType,
                         String location, double fineAmount, String status) {
            this.violationId = violationId;
            this.vehicleId = vehicleId;
            this.violationDate = violationDate;
            this.violationType = violationType;
            this.location = location != null ? location : "";
            this.fineAmount = fineAmount;
            this.status = status != null ? status : "Unpaid";
        }

        public int getViolationId() { return violationId; }
        public int getVehicleId() { return vehicleId; }
        public String getViolationDate() { return violationDate; }
        public String getViolationType() { return violationType; }
        public String getLocation() { return location; }
        public double getFineAmount() { return fineAmount; }
        public String getStatus() { return status; }
    }
}