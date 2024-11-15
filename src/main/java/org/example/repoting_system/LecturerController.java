package org.example.repoting_system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class LecturerController {

    @FXML
    private Button markAttendanceButton;
    @FXML
    private Button specifyChaptersButton;
    @FXML
    private Button defineLearningOutcomesButton;
    @FXML
    private Button submitReportButton;
    @FXML
    private Button fillWeeklyReportButton;
    @FXML
    private Button logoutButton;

    // Add UI elements for dynamic data input
    @FXML
    private TextField chapterDetailsInput;
    @FXML
    private TextField learningOutcomeInput;
    @FXML
    private TextArea reportContentInput;
    @FXML
    private TextArea weeklyChallengesInput;
    @FXML
    private TextArea weeklyRecommendationsInput;

    // Handle marking attendance
    @FXML
    private void handleMarkAttendance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Attendance-view.fxml"));
            Scene attendanceScene = new Scene(loader.load());

            // Open a new stage for the attendance view
            Stage attendanceStage = new Stage();
            attendanceStage.setTitle("Attendance");
            attendanceStage.setScene(attendanceScene);
            attendanceStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Attendance screen.");
        }
    }

    // Handle specifying chapters
    @FXML
    private void handleSpecifyChapters() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO chapters (lecturer_id, chapter_details) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getCurrentLecturerId());
            statement.setString(2, chapterDetailsInput.getText().trim()); // Fetch actual input from UI
            statement.executeUpdate();
            showAlert("Specify Chapters", "Chapters have been specified successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to specify chapters.");
        }
    }

    // Handle defining learning outcomes
    @FXML
    private void handleDefineLearningOutcomes() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO learning_outcomes (lecturer_id, outcome_details) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getCurrentLecturerId());
            statement.setString(2, learningOutcomeInput.getText().trim()); // Fetch actual input from UI
            statement.executeUpdate();
            showAlert("Define Learning Outcomes", "Learning outcomes have been defined successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to define learning outcomes.");
        }
    }

    // Handle submitting reports for each module
    @FXML
    private void handleSubmitReport() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO reports (lecturer_id, module_id, report_date, report_content) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getCurrentLecturerId());
            statement.setInt(2, getCurrentModuleId());
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(4, reportContentInput.getText().trim()); // Fetch actual report content from UI
            statement.executeUpdate();
            showAlert("Submit Report", "Report submitted successfully for the module.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to submit report.");
        }
    }

    // Handle filling weekly reports with no pre-populated data
    @FXML
    private void handleFillWeeklyReport() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO weekly_reports (lecturer_id, week_start, week_end, challenges, recommendations) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getCurrentLecturerId());
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(4, weeklyChallengesInput.getText().trim()); // Fetch challenges from UI
            statement.setString(5, weeklyRecommendationsInput.getText().trim()); // Fetch recommendations from UI
            statement.executeUpdate();
            showAlert("Weekly Report", "Weekly report has been filled successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fill weekly report.");
        }
    }

    // Handle logout
    @FXML
    private void handleLogout() {
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have been logged out.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/repoting_system/login-view.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login view.");
        }
    }

    // Utility method to get current lecturer ID
    private int getCurrentLecturerId() {
        // Placeholder: Retrieve the lecturer ID from session or context
        return 1; // Replace with actual lecturer ID logic
    }

    // Utility method to get current module ID
    private int getCurrentModuleId() {
        // Placeholder: Retrieve the module ID based on lecturer's assigned module
        return 101; // Replace with actual module ID logic
    }

    // Utility method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Overloaded utility method for custom AlertType
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
