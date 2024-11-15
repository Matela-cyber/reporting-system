package org.example.repoting_system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PRLController {

    @FXML
    private Button submitReportButton;
    @FXML
    private Button weeklyClassDataButton;
    @FXML
    private Button logoutButton;

    // Handle report submission
    @FXML
    private void handleSubmitReport() {

    }

    // Handle weekly class data entry
    @FXML
    private void handleWeeklyClassData() {
        handleCreateReport();

    }

    // Handle logout
    @FXML
    private void handleLogout() {
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have been logged out.");

        try {
            // Load the login view FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/repoting_system/login-view.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Get the current stage from the logout button's scene
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login view.");
        }
    }

    // Placeholder method for additional report functionality
    @FXML
    private void handleCreateReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report-view.fxml"));
            Scene attendanceScene = new Scene(loader.load());

            // Open a new stage for the attendance view
            Stage attendanceStage = new Stage();
            attendanceStage.setTitle("weekly preport");
            attendanceStage.setScene(attendanceScene);
            attendanceStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load report lecturer screen.");
        } }

    // Utility method to show information alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Overloaded utility method to show alerts with a specific AlertType
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
