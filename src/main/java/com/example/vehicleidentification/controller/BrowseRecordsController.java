package com.example.vehicleidentification.controller;

import com.example.vehicleidentification.dao.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BrowseRecordsController {

    @FXML private ComboBox<String> tableSelector;
    @FXML private TextField searchField;
    @FXML private TableView<ObservableList<String>> recordsTable;
    @FXML private TextField pageNumberField;
    @FXML private Label totalPagesLabel;
    @FXML private Label recordInfoLabel;
    @FXML private Label statusLabel;
    @FXML private Label connectionStatus;

    private int currentPage = 1;
    private int recordsPerPage = 15;
    private int totalRecords = 0;
    private int totalPages = 1;
    private String currentTable = "";
    private String currentSearch = "";
    private Map<String, String[]> tableSchemas = new HashMap<>();
    private Map<String, String> orderByColumns = new HashMap<>();

    @FXML
    public void initialize() {
        setupTableSchemas();
        setupTableSelector();
        checkConnection();
        setupSearchListener();
    }

    private void setupTableSchemas() {
        // Schema format: column_name, display_name, column_type
        tableSchemas.put("Vehicle", new String[]{
                "vehicle_id", "ID", "int",
                "registration_number", "Registration", "string",
                "make", "Make", "string",
                "model", "Model", "string",
                "year", "Year", "int",
                "owner_id", "Owner ID", "int"
        });
        orderByColumns.put("Vehicle", "vehicle_id");

        tableSchemas.put("Customer", new String[]{
                "customer_id", "ID", "int",
                "name", "Name", "string",
                "address", "Address", "string",
                "phone", "Phone", "string",
                "email", "Email", "string"
        });
        orderByColumns.put("Customer", "customer_id");

        tableSchemas.put("ServiceRecord", new String[]{
                "service_id", "ID", "int",
                "vehicle_id", "Vehicle ID", "int",
                "service_date", "Service Date", "date",
                "service_type", "Service Type", "string",
                "cost", "Cost ($)", "double"
        });
        orderByColumns.put("ServiceRecord", "service_id");

        tableSchemas.put("InsurancePolicy", new String[]{
                "policy_id", "Policy ID", "int",
                "vehicle_id", "Vehicle ID", "int",
                "insurance_company", "Insurance Co", "string",
                "policy_number", "Policy #", "string",
                "start_date", "Start Date", "date",
                "end_date", "End Date", "date"
        });
        orderByColumns.put("InsurancePolicy", "policy_id");

        tableSchemas.put("Violation", new String[]{
                "violation_id", "ID", "int",
                "vehicle_id", "Vehicle ID", "int",
                "violation_date", "Violation Date", "date",
                "violation_type", "Violation Type", "string",
                "fine_amount", "Fine ($)", "double",
                "status", "Status", "string"
        });
        orderByColumns.put("Violation", "violation_id");
    }

    private void setupTableSelector() {
        tableSelector.setItems(FXCollections.observableArrayList(
                "Vehicle", "Customer", "ServiceRecord", "InsurancePolicy", "Violation"
        ));
        tableSelector.setValue("Vehicle");
        tableSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentTable = newVal;
            currentPage = 1;
            currentSearch = "";
            searchField.clear();
            loadTableData();
        });
    }

    private void checkConnection() {
        try {
            DatabaseConnection.getConnection();
            connectionStatus.setText("✅ Connected");
            connectionStatus.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            connectionStatus.setText("❌ Disconnected");
            connectionStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void setupSearchListener() {
        searchField.setOnAction(e -> searchRecords());
    }

    @FXML
    private void searchRecords() {
        currentSearch = searchField.getText();
        currentPage = 1;
        loadTableData();
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        currentSearch = "";
        currentPage = 1;
        loadTableData();
    }

    @FXML
    private void exportToCSV() {
        if (recordsTable.getItems().isEmpty()) {
            statusLabel.setText("No data to export");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName(currentTable + "_export.csv");

        File file = fileChooser.showSaveDialog(recordsTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write headers
                StringBuilder header = new StringBuilder();
                for (TableColumn<?, ?> col : recordsTable.getColumns()) {
                    header.append(col.getText()).append(",");
                }
                writer.println(header.toString().replaceAll(",$", ""));

                // Write data
                for (ObservableList<String> row : recordsTable.getItems()) {
                    StringBuilder line = new StringBuilder();
                    for (String value : row) {
                        // Escape commas and quotes
                        String escaped = value.replace("\"", "\"\"");
                        if (escaped.contains(",") || escaped.contains("\"")) {
                            escaped = "\"" + escaped + "\"";
                        }
                        line.append(escaped).append(",");
                    }
                    writer.println(line.toString().replaceAll(",$", ""));
                }

                statusLabel.setText("✅ Exported to " + file.getName());
                showAlert("Export Successful", "Data exported to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                statusLabel.setText("❌ Export failed: " + e.getMessage());
                showAlert("Export Failed", e.getMessage());
            }
        }
    }

    private void loadTableData() {
        if (currentTable.isEmpty()) return;

        statusLabel.setText("Loading " + currentTable + "...");
        String[] schema = tableSchemas.get(currentTable);
        if (schema == null) return;

        // Build search condition
        String searchCondition = "";
        if (!currentSearch.isEmpty()) {
            StringBuilder condition = new StringBuilder(" WHERE ");
            String[] searchableCols = getSearchableColumns(schema);
            for (int i = 0; i < searchableCols.length; i++) {
                if (i > 0) condition.append(" OR ");
                condition.append(searchableCols[i]).append(" ILIKE '%").append(currentSearch).append("%'");
            }
            searchCondition = condition.toString();
        }

        // Get total count
        String countSql = "SELECT COUNT(*) FROM " + currentTable + searchCondition;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next()) {
                totalRecords = rs.getInt(1);
                totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
                totalPagesLabel.setText(String.valueOf(totalPages));

                int start = (currentPage - 1) * recordsPerPage + 1;
                int end = Math.min(currentPage * recordsPerPage, totalRecords);
                if (totalRecords > 0) {
                    recordInfoLabel.setText("Showing " + start + "-" + end + " of " + totalRecords + " records");
                } else {
                    recordInfoLabel.setText("No records found");
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Error getting record count: " + e.getMessage());
            e.printStackTrace();
        }

        // Get paginated data
        int offset = (currentPage - 1) * recordsPerPage;
        String orderBy = orderByColumns.get(currentTable);
        String dataSql = "SELECT * FROM " + currentTable + searchCondition +
                " ORDER BY " + orderBy + " LIMIT " + recordsPerPage + " OFFSET " + offset;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(dataSql)) {

            setupTableColumns(schema);
            recordsTable.getItems().clear();

            int rowCount = 0;
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < schema.length; i += 3) {
                    String columnName = schema[i];
                    String columnType = schema[i + 2];
                    String value = getColumnValue(rs, columnName, columnType);
                    row.add(value);
                }
                recordsTable.getItems().add(row);
                rowCount++;
            }

            statusLabel.setText("✅ Loaded " + rowCount + " records from " + currentTable);
            pageNumberField.setText(String.valueOf(currentPage));

        } catch (SQLException e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] getSearchableColumns(String[] schema) {
        java.util.ArrayList<String> cols = new java.util.ArrayList<>();
        for (int i = 0; i < schema.length; i += 3) {
            String type = schema[i + 2];
            if (type.equals("string")) {
                cols.add(schema[i]);
            }
        }
        return cols.toArray(new String[0]);
    }

    private String getColumnValue(ResultSet rs, String columnName, String columnType) throws SQLException {
        switch (columnType) {
            case "int":
                int intVal = rs.getInt(columnName);
                return rs.wasNull() ? "" : String.valueOf(intVal);
            case "double":
                double dblVal = rs.getDouble(columnName);
                return rs.wasNull() ? "" : String.format("$%.2f", dblVal);
            case "date":
                Date date = rs.getDate(columnName);
                return date != null ? date.toString() : "";
            default:
                String strVal = rs.getString(columnName);
                return strVal != null ? strVal : "";
        }
    }

    private void setupTableColumns(String[] schema) {
        recordsTable.getColumns().clear();

        for (int i = 0; i < schema.length; i += 3) {
            String columnName = schema[i];
            String displayName = schema[i + 1];
            final int columnIndex = i / 3;

            TableColumn<ObservableList<String>, String> column = new TableColumn<>(displayName);
            column.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(columnIndex))
            );
            column.setPrefWidth(getColumnWidth(displayName));
            column.setResizable(true);
            recordsTable.getColumns().add(column);
        }
    }

    private double getColumnWidth(String displayName) {
        switch (displayName) {
            case "ID": return 60;
            case "Registration": return 120;
            case "Make": return 100;
            case "Model": return 100;
            case "Year": return 70;
            case "Name": return 150;
            case "Address": return 200;
            case "Email": return 180;
            case "Insurance Co": return 130;
            case "Policy #": return 120;
            default: return 100;
        }
    }

    @FXML
    private void firstPage() {
        if (currentPage > 1) {
            currentPage = 1;
            loadTableData();
        }
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadTableData();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadTableData();
        }
    }

    @FXML
    private void lastPage() {
        if (currentPage < totalPages) {
            currentPage = totalPages;
            loadTableData();
        }
    }

    @FXML
    private void goToPage() {
        try {
            int page = Integer.parseInt(pageNumberField.getText());
            if (page >= 1 && page <= totalPages) {
                currentPage = page;
                loadTableData();
            } else {
                statusLabel.setText("Page must be between 1 and " + totalPages);
                pageNumberField.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid page number");
            pageNumberField.setText(String.valueOf(currentPage));
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) tableSelector.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}