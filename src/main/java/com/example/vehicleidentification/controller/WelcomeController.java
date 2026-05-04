package com.example.vehicleidentification.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private Button enterButton;

    @FXML
    private void enterApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) enterButton.getScene().getWindow();
            Scene scene = new Scene(root);

            // IMPORTANT: Clear any existing stylesheets and apply ONLY light theme
            scene.getStylesheets().clear();

            // Apply light theme explicitly
            try {
                String cssPath = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
                System.out.println("Light theme applied to main application");
            } catch (Exception e) {
                System.out.println("Light theme CSS not found: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.setTitle("Vehicle Identification System");
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading main application: " + e.getMessage());
        }
    }
}