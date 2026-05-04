package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Label totalVehicles, totalCustomers, activePolicies, unpaidViolations;
    @FXML private Label lastUpdatedLabel;
    @FXML private BarChart<String, Number> vehicleChart;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private PieChart violationChart;

    @FXML
    public void initialize() {
        loadStatistics();
        loadVehicleChart();
        loadRevenueChart();
        loadViolationChart();
        updateLastUpdated();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total Vehicles
            String sql1 = "SELECT COUNT(*) FROM Vehicle";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next()) totalVehicles.setText(String.valueOf(rs1.getInt(1)));

            // Total Customers
            String sql2 = "SELECT COUNT(*) FROM Customer";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) totalCustomers.setText(String.valueOf(rs2.getInt(1)));

            // Active Policies
            String sql3 = "SELECT COUNT(*) FROM InsurancePolicy WHERE end_date > CURRENT_DATE";
            PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) activePolicies.setText(String.valueOf(rs3.getInt(1)));

            // Unpaid Violations
            String sql4 = "SELECT COUNT(*) FROM Violation WHERE status = 'Unpaid'";
            PreparedStatement pstmt4 = conn.prepareStatement(sql4);
            ResultSet rs4 = pstmt4.executeQuery();
            if (rs4.next()) unpaidViolations.setText(String.valueOf(rs4.getInt(1)));

        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        totalVehicles.setText("0");
        totalCustomers.setText("0");
        activePolicies.setText("0");
        unpaidViolations.setText("0");
    }

    private void loadVehicleChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Vehicles by Make");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT make, COUNT(*) as count FROM Vehicle GROUP BY make ORDER BY count DESC")) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("make"), rs.getInt("count")));
            }
            vehicleChart.getData().clear();
            vehicleChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRevenueChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT TO_CHAR(service_date, 'Mon') as month, COALESCE(SUM(cost), 0) as revenue " +
                             "FROM ServiceRecord GROUP BY EXTRACT(MONTH FROM service_date), month " +
                             "ORDER BY EXTRACT(MONTH FROM service_date)")) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("month"), rs.getDouble("revenue")));
            }
            revenueChart.getData().clear();
            revenueChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadViolationChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT violation_type, COUNT(*) as count FROM Violation GROUP BY violation_type")) {

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("violation_type"), rs.getInt("count")));
            }
            violationChart.setData(pieChartData);
            violationChart.setTitle("Violations by Type");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLastUpdated() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        lastUpdatedLabel.setText("Last updated: " + timestamp);
    }

    @FXML
    private void refresh() {
        loadStatistics();
        loadVehicleChart();
        loadRevenueChart();
        loadViolationChart();
        updateLastUpdated();
    }
}