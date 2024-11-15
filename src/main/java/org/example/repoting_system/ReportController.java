package org.example.repoting_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReportController {

    @FXML
    private DatePicker attendanceDatePicker;
    @FXML
    private ComboBox<String> moduleComboBox;
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<String> lecturerComboBox;
    @FXML
    private TextArea challengesField;
    @FXML
    private TextArea recommendationsField;
    @FXML
    private Button submitReportButton;

    private Map<String, Integer> moduleMap = new HashMap<>();
    private Map<String, Integer> classMap = new HashMap<>();
    private Map<String, Integer> lecturerMap = new HashMap<>();

    @FXML
    public void initialize() {
        populateComboBox(moduleComboBox, "modules", "id", "name", moduleMap);
        populateComboBox(classComboBox, "classes", "id", "name", classMap);
        populateComboBox(lecturerComboBox, "lecturers", "id", "name", lecturerMap);
    }

    private void populateComboBox(ComboBox<String> comboBox, String tableName, String idColumn, String nameColumn, Map<String, Integer> map) {
        ObservableList<String> items = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT " + idColumn + ", " + nameColumn + " FROM " + tableName;
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(idColumn);
                String name = resultSet.getString(nameColumn);
                items.add(name);
                map.put(name, id);
            }
            comboBox.setItems(items);
            comboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(String name) {
                    return name;
                }
                @Override
                public String fromString(String name) {
                    return name;
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Failed to load data for " + tableName + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitReport() {
        LocalDate reportDate = attendanceDatePicker.getValue();
        String selectedModule = moduleComboBox.getValue();
        String selectedClass = classComboBox.getValue();
        String selectedLecturer = lecturerComboBox.getValue();
        String challenges = challengesField.getText();
        String recommendations = recommendationsField.getText();

        if (reportDate == null || selectedModule == null || selectedClass == null || selectedLecturer == null || challenges.isEmpty() || recommendations.isEmpty()) {
            showAlert("Error", "All fields, including the date, must be filled out.");
            return;
        }

        Integer moduleId = moduleMap.get(selectedModule);
        Integer classId = classMap.get(selectedClass);
        Integer lecturerId = lecturerMap.get(selectedLecturer);

        try (Connection conn = DatabaseConnection.connect()) {
            String submitReportQuery =
                    "INSERT INTO reports (module_id, class_id, lecturer_id, challenges, recommendations, report_date) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(submitReportQuery);
            preparedStatement.setInt(1, moduleId);
            preparedStatement.setInt(2, classId);
            preparedStatement.setInt(3, lecturerId);
            preparedStatement.setString(4, challenges);
            preparedStatement.setString(5, recommendations);
            preparedStatement.setDate(6, java.sql.Date.valueOf(reportDate));

            preparedStatement.executeUpdate();
            showAlert("Success", "Weekly report submitted successfully.");
            clearFields();

        } catch (SQLException e) {
            showAlert("Error", "Failed to submit report: " + e.getMessage());
        }
    }

    private void clearFields() {
        attendanceDatePicker.setValue(null);
        moduleComboBox.setValue(null);
        classComboBox.setValue(null);
        lecturerComboBox.setValue(null);
        challengesField.clear();
        recommendationsField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
