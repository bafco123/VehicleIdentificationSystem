module com.example.vehicleidentification {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.vehicleidentification to javafx.fxml;
    opens com.example.vehicleidentification.controller to javafx.fxml;
    opens com.example.vehicleidentification.model to javafx.base;

    exports com.example.vehicleidentification;
    exports com.example.vehicleidentification.controller;
    exports com.example.vehicleidentification.model;
}