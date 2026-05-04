package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML private Label statusLabel;
    @FXML private Label timeLabel;
    @FXML private Label statsVehicles;
    @FXML private Label statsCustomers;
    @FXML private Label statsServices;
    @FXML private Label statsViolations;
    @FXML private Label recordCountLabel;
    @FXML private AnchorPane contentArea;

    private Timeline clockTimeline;

    @FXML
    public void initialize() {
        checkDatabaseConnection();
        setupAnimations();
        setupClock();
        setupKeyboardShortcuts();
        updateQuickStats();
        applyLightTheme();
        showDashboardView();
    }

    private void checkDatabaseConnection() {
        try {
            DatabaseConnection.getConnection();
            statusLabel.setText("✅ Connected");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            statusLabel.setText("❌ Disconnected");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void updateQuickStats() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Vehicle");
            if (rs.next()) statsVehicles.setText("Vehicles: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer");
            if (rs.next()) statsCustomers.setText("Customers: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM ServiceRecord");
            if (rs.next()) statsServices.setText("Services: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM Violation WHERE status='Unpaid'");
            if (rs.next()) statsViolations.setText("Unpaid: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM Vehicle");
            if (rs.next()) recordCountLabel.setText("Total Vehicles: " + rs.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAnimations() {
        Button effectButton = new Button("✨ Effects Demo");
        effectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        effectButton.setEffect(dropShadow);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), effectButton);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.play();
    }

    private void setupClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void setupKeyboardShortcuts() {
        Scene scene = statusLabel.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.isControlDown()) {
                    switch (event.getCode()) {
                        case D -> showDashboardView();
                        case V -> showVehicleView();
                        case C -> showCustomerView();
                        case W -> showWorkshopView();
                        case I -> showInsuranceView();
                        case P -> showPoliceView();
                        case B -> showBrowseRecords();
                        case R -> handleRefresh();
                        default -> {}
                    }
                } else if (event.getCode().toString().equals("F5")) {
                    handleRefresh();
                }
            });
        }
    }

    private void applyLightTheme() {
        Scene scene = statusLabel.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            try {
                String cssPath = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
                System.out.println("Light theme applied");
            } catch (Exception e) {
                System.out.println("CSS not found");
            }
        }
    }

    @FXML
    private void showRecentActivities() {
        String activities = """
            Recent System Activities:
            
            • Vehicle ABC123 registered on 2026-04-20
            • Insurance updated for XYZ789 on 2026-04-21
            • New service record added for Toyota Camry
            • Police report filed for accident on Main St
            • Customer query about service schedule resolved
            • Insurance claim #001 approved
            • Policy #SF-2024-001 renewed
            • Speeding violation issued to vehicle DEF456
            • Workshop appointment scheduled for April 28
            • Annual inspection completed for vehicle JKL012
            
            Last 7 days activity count: 24 events
            """;
        showAlert("Recent Activities", activities, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void showBrowseRecords() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BrowseRecordsView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Browse Records - Vehicle Identification System");
            Scene scene = new Scene(root);

            // Apply light theme
            try {
                String cssPath = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception e) {}

            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.show();
            statusLabel.setText("✅ Browse Records window opened");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Error opening Browse Records");
            showAlert("Error", "Failed to open Browse Records: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showDashboardView() {
        loadView("/fxml/DashboardView.fxml");
    }

    @FXML
    private void showVehicleView() {
        loadView("/fxml/VehicleView.fxml");
    }

    @FXML
    private void showCustomerView() {
        loadView("/fxml/CustomerView.fxml");
    }

    @FXML
    private void showWorkshopView() {
        loadView("/fxml/WorkshopView.fxml");
    }

    @FXML
    private void showInsuranceView() {
        loadView("/fxml/InsuranceView.fxml");
    }

    @FXML
    private void showPoliceView() {
        loadView("/fxml/PoliceView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            statusLabel.setText("✅ Loaded: " + fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1));
            updateQuickStats();
        } catch (IOException e) {
            statusLabel.setText("❌ Error loading view");
            e.printStackTrace();
        }
    }

    @FXML
    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("ID,Registration,Make,Model,Year");

                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM Vehicle")) {

                    while (rs.next()) {
                        writer.printf("%d,%s,%s,%s,%d%n",
                                rs.getInt("vehicle_id"),
                                rs.getString("registration_number"),
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getInt("year")
                        );
                    }
                }

                showAlert("Success", "Data exported to " + file.getName(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to export: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void printView() {
        if (!contentArea.getChildren().isEmpty()) {
            Node content = contentArea.getChildren().get(0);
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(content.getScene().getWindow())) {
                boolean success = job.printPage(content);
                if (success) {
                    job.endJob();
                    showAlert("Success", "Print job completed", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Print failed", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Info", "Nothing to print", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void showShortcuts() {
        String shortcuts = """
            Keyboard Shortcuts:
            
            Ctrl + D - Dashboard
            Ctrl + V - Vehicles
            Ctrl + C - Customers
            Ctrl + W - Workshop
            Ctrl + I - Insurance
            Ctrl + P - Police
            Ctrl + B - Browse Records
            Ctrl + R - Refresh
            F5 - Refresh
            """;
        showAlert("Keyboard Shortcuts", shortcuts, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRefresh() {
        updateQuickStats();
        statusLabel.setText("✅ Refreshed");
    }

    @FXML
    private void handleExit() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        DatabaseConnection.closeConnection();
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        String about = """
            Vehicle Identification System
            Version 2.0
            © 2026
            
            Features:
            • Vehicle Management
            • Customer Management
            • Workshop Services
            • Insurance Tracking
            • Police Records
            • Dashboard Analytics
            • Browse Records with Pagination
            • Export to CSV
            • Print Support
            • Keyboard Shortcuts
            
            Shortcut: Ctrl + B to open Browse Records
            """;
        showAlert("About", about, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}