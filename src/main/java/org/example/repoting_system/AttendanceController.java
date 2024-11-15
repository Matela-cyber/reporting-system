package org.example.repoting_system;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AttendanceController {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextArea chapterDetailsField;

    @FXML
    private TextArea learningOutcomesField;

    @FXML
    private DatePicker attendanceDatePicker;

    @FXML
    private CheckBox presentCheckBox;

    @FXML
    private CheckBox absentCheckBox;

    @FXML
    private Button submitAttendanceButton;

    private Boolean isPresent = null;

    @FXML
    private void initialize() {
        // Add a listener to ensure only one checkbox is selected at a time
        presentCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                absentCheckBox.setSelected(false);
                isPresent = true;
            } else if (!absentCheckBox.isSelected()) {
                isPresent = null;
            }
        });

        absentCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                presentCheckBox.setSelected(false);
                isPresent = false;
            } else if (!presentCheckBox.isSelected()) {
                isPresent = null;
            }
        });
    }

    @FXML
    private void handleSubmitAttendance() {
        String studentId = studentIdField.getText();
        String chapterDetails = chapterDetailsField.getText();
        String learningOutcomes = learningOutcomesField.getText();
        LocalDate attendanceDate = attendanceDatePicker.getValue();

        if (studentId.isEmpty() || chapterDetails.isEmpty() || learningOutcomes.isEmpty() || isPresent == null || attendanceDate == null) {
            showAlert("Error", "All fields, including the attendance date, must be filled.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String submitAttendanceQuery =
                    "INSERT INTO attendance (student_id, chapter_details, learning_outcomes, attendance_date, is_present) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(submitAttendanceQuery);
            preparedStatement.setInt(1, Integer.parseInt(studentId));
            preparedStatement.setString(2, chapterDetails);
            preparedStatement.setString(3, learningOutcomes);
            preparedStatement.setDate(4, java.sql.Date.valueOf(attendanceDate));
            preparedStatement.setBoolean(5, isPresent);
            preparedStatement.executeUpdate();

            showAlert("Success", "Attendance details submitted for student " + studentId + ".");
            isPresent = null;

        } catch (SQLException e) {
            showAlert("Error", "Failed to submit attendance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
