package com.example.vehicleidentification.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private Map<String, String> users = new HashMap<>();

    @FXML
    public void initialize() {
        // Setup demo users
        users.put("Kopano", "qwerty123456");
        users.put("Bakuena", "qwerty123456");
        users.put("Mohami", "qwerty123456");
        users.put("Thakholi", "qwerty123456");

        // Add focus animations to text fields
        addFieldAnimations();
    }

    private void addFieldAnimations() {
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-text-fill: white; -fx-prompt-text-fill: #a8b2d1; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #e94560; -fx-border-radius: 8;");
            } else {
                usernameField.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-prompt-text-fill: #7c8ba0; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: transparent; -fx-border-radius: 8;");
            }
        });

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-text-fill: white; -fx-prompt-text-fill: #a8b2d1; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #e94560; -fx-border-radius: 8;");
            } else {
                passwordField.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-prompt-text-fill: #7c8ba0; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: transparent; -fx-border-radius: 8;");
            }
        });
    }

    private void animateButton(Button button) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("❌ Please enter username and password");
            fadeOutStatus();
            return;
        }

        if (users.containsKey(username) && users.get(username).equals(password)) {
            statusLabel.setText("✅ Login successful! Redirecting...");
            statusLabel.setStyle("-fx-text-fill: #2ecc71;");
            openMainApplication(username);
        } else {
            statusLabel.setText("❌ Invalid username or password");
            fadeOutStatus();
        }
    }

    private void fadeOutStatus() {
        FadeTransition fade = new FadeTransition(Duration.seconds(3), statusLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }

    private void openMainApplication(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);

            try {
                String cssPath = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception e) {}

            // Fade transition for scene change
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), usernameField.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                stage.setTitle("Vehicle Identification System - Welcome " + username);
                stage.setMaximized(true);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading application: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("");
    }
}