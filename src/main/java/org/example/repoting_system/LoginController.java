package org.example.repoting_system;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("Admin", "Lecturer", "PRL"));
    }

    @FXML

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please fill in all fields and select a role.");
            return;
        }

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, selectedRole);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                logLogin(username);
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + selectedRole + " " + username + "!");
                openDashboard(selectedRole, username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username, password, or role.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    private void openDashboard(String role, String username) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String viewPath;

            // Load the corresponding dashboard view based on role
            switch (role) {
                case "Admin":
                    viewPath = "/org/example/repoting_system/admin-view.fxml";
                    loader.setLocation(getClass().getResource(viewPath));
                    break;
                case "Lecturer":
                    viewPath = "/org/example/repoting_system/lecturer-view.fxml";
                    loader.setLocation(getClass().getResource(viewPath));
                    break;
                case "PRL":
                    viewPath = "/org/example/repoting_system/prl-view.fxml";
                    loader.setLocation(getClass().getResource(viewPath));
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Role Error", "Invalid role selected.");
                    return;
            }

            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Pass the username to the respective controller
            if (role.equals("Admin")) {
                AdminController controller = loader.getController();
                controller.setAdminName(username);
            } else if (role.equals("Lecturer")) {
                LecturerController controller = loader.getController();
                //controller.setLecturerName(username);
            } else if (role.equals("PRL")) {
                PRLController controller = loader.getController();
                //controller.setPRLName(username);
            }

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load dashboard for " + role + ".");
        }
    }

    private void logLogin(String username) {
        String logSql = "INSERT INTO logbook (username, login_time) VALUES (?, NOW())";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement logStatement = connection.prepareStatement(logSql);
             FileWriter fileWriter = new FileWriter("login_log.txt", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Insert into the logbook table in the database
            logStatement.setString(1, username);
            logStatement.executeUpdate();

            // Log to the text file as well
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            printWriter.println("Login - Username: " + username + ", Time: " + timestamp);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Logging Error", "Failed to log login to the database.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Logging Error", "Failed to log login to the file.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
