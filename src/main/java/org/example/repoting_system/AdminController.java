package org.example.repoting_system;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AdminController {


    @FXML
    private Label adminNameLabel;

    // Set the admin's name in the label
    public void setAdminName(String adminName) {
        adminNameLabel.setText("Welcome, Admin " + adminName);
    }

    // 1. View Profile
    @FXML
    private void handleViewProfile() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM admins WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, adminNameLabel.getText().replace("Welcome, Admin ", ""));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String profileInfo = "Username: " + resultSet.getString("username") +
                        "\nEmail: " + resultSet.getString("email") +
                        "\nName: " + resultSet.getString("name");
                showAlert(Alert.AlertType.INFORMATION, "Profile", profileInfo);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Profile not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile.");
        }
    }

    // 2. Add New Lecturer
    @FXML
    private void handleAddLecturer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Lecturer");
        dialog.setHeaderText("Enter new lecturer details (name, employee number, department, role):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(lecturerDetails -> {
            String[] details = lecturerDetails.split(",");
            if (details.length == 4) {
                // Prompt for password
                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Set Password");
                passwordDialog.setHeaderText("Enter password for the new lecturer:");
                Optional<String> passwordResult = passwordDialog.showAndWait();

                if (passwordResult.isPresent()) {
                    String password = passwordResult.get().trim();

                    try (Connection connection = DatabaseConnection.connect()) {
                        // Insert into lecturers table
                        String sqlLecturer = "INSERT INTO lecturers (name, employee_number, department, role, created_at) VALUES (?, ?, ?, ?, NOW())";
                        PreparedStatement stmtLecturer = connection.prepareStatement(sqlLecturer);
                        stmtLecturer.setString(1, details[0].trim());
                        stmtLecturer.setString(2, details[1].trim());
                        stmtLecturer.setString(3, details[2].trim());
                        stmtLecturer.setString(4, details[3].trim());
                        stmtLecturer.executeUpdate();

                        // Insert into users table
                        String sqlUser = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, NOW())";
                        PreparedStatement stmtUser = connection.prepareStatement(sqlUser);
                        stmtUser.setString(1, details[0].trim()); // username is the lecturer's name
                        stmtUser.setString(2, password);          // password entered by the user
                        stmtUser.setString(3, details[3].trim()); // role from lecturer details
                        stmtUser.executeUpdate();

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Lecturer and user account added successfully.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add lecturer or user account.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Password is required for the user account.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please provide name, employee number, department, and role.");
            }
        });
    }


    // 3. Add Academic Year
    @FXML
    private void handleAddAcademicYear() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Academic Year");
        dialog.setHeaderText("Enter the new academic year (e.g., 2024-2025):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(academicYear -> {
            String[] years = academicYear.split("-");
            if (years.length == 2) {
                try {
                    int yearStart = Integer.parseInt(years[0].trim());
                    int yearEnd = Integer.parseInt(years[1].trim());

                    try (Connection connection = DatabaseConnection.connect()) {
                        String checkSql = "SELECT COUNT(*) FROM academic_years WHERE year_start = ? AND year_end = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                        checkStatement.setInt(1, yearStart);
                        checkStatement.setInt(2, yearEnd);
                        ResultSet resultSet = checkStatement.executeQuery();

                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "This academic year already exists.");
                        } else {
                            String insertSql = "INSERT INTO academic_years (year_start, year_end) VALUES (?, ?)";
                            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                            insertStatement.setInt(1, yearStart);
                            insertStatement.setInt(2, yearEnd);
                            insertStatement.executeUpdate();
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Academic year added successfully.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add academic year.");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid years.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid format. Please use YYYY-YYYY.");
            }
        });
    }

    // 4. Add Semester
    @FXML
    private void handleAddSemester() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Semester");
        dialog.setHeaderText("Enter semester name and academic year (e.g., Y2S2, 1):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] details = input.split(",");
            if (details.length == 2) {
                String semesterName = details[0].trim();
                int academicYearId;
                try {
                    academicYearId = Integer.parseInt(details[1].trim());
                    try (Connection connection = DatabaseConnection.connect()) {
                        String sql = "INSERT INTO semesters (name, academic_year_id) VALUES (?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, semesterName);
                        statement.setInt(2, academicYearId);
                        statement.executeUpdate();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Semester added successfully.");
                    }
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add semester. Please ensure valid input.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please provide both semester name and academic year ID.");
            }
        });
    }

    // 5. Add Module
    @FXML
    private void handleAddModule() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Module");
        dialog.setHeaderText("Enter module details (name, code):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(moduleDetails -> {
            String[] details = moduleDetails.split(",");
            if (details.length == 2) {
                try (Connection connection = DatabaseConnection.connect()) {
                    String sql = "INSERT INTO modules (name, code) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, details[0].trim());
                    statement.setString(2, details[1].trim());
                    statement.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Module added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add module.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please provide name and code for the module.");
            }
        });
    }

    // 6. Assign Roles
    @FXML
    private void handleAssignRoles() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Assign Role to Lecturer");
        dialog.setHeaderText("Select a lecturer and assign a new role");

        // Create ChoiceBoxes
        ChoiceBox<String> lecturerChoiceBox = new ChoiceBox<>();
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("PRL", "Lecturer"));

        // Fetch lecturers from the database
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT id, name FROM lecturers";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                lecturerChoiceBox.getItems().add(id + " - " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load lecturers.");
            return;
        }

        // Set up dialog content
        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Select Lecturer:"), lecturerChoiceBox, new Label("Select New Role:"), roleChoiceBox);
        dialog.getDialogPane().setContent(content);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle button click
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String selectedLecturer = lecturerChoiceBox.getValue();
                String selectedRole = roleChoiceBox.getValue();

                if (selectedLecturer != null && selectedRole != null) {
                    int lecturerId = Integer.parseInt(selectedLecturer.split(" - ")[0].trim());
                    String lecturerName = selectedLecturer.split(" - ")[1].trim();

                    try (Connection connection = DatabaseConnection.connect()) {
                        // Update role in lecturers table
                        String updateLecturersSql = "UPDATE lecturers SET role = ? WHERE id = ?";
                        PreparedStatement updateLecturersStmt = connection.prepareStatement(updateLecturersSql);
                        updateLecturersStmt.setString(1, selectedRole);
                        updateLecturersStmt.setInt(2, lecturerId);

                        int lecturersUpdated = updateLecturersStmt.executeUpdate();

                        // Update role in users table
                        String updateUsersSql = "UPDATE users SET role = ? WHERE username = ?";
                        PreparedStatement updateUsersStmt = connection.prepareStatement(updateUsersSql);
                        updateUsersStmt.setString(1, selectedRole);
                        updateUsersStmt.setString(2, lecturerName);

                        int usersUpdated = updateUsersStmt.executeUpdate();

                        if (lecturersUpdated > 0 && usersUpdated > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Role assigned successfully in both tables.");
                        } else if (lecturersUpdated > 0) {
                            showAlert(Alert.AlertType.WARNING, "Partial Success", "Role assigned to lecturer, but user update failed.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Update Failed", "No lecturer found with the provided ID.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update role. Please try again.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select both a lecturer and a role.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }



    // 6.1. Assign Modules
    @FXML
    private void handleAssignModules() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Assign Module to Lecturer");
        dialog.setHeaderText("Select a lecturer, module, and class");

        // Create ChoiceBoxes
        ChoiceBox<String> lecturerChoiceBox = new ChoiceBox<>();
        ChoiceBox<String> moduleChoiceBox = new ChoiceBox<>();
        ChoiceBox<String> classChoiceBox = new ChoiceBox<>();

        // Fetch lecturers from the database
        try (Connection connection = DatabaseConnection.connect()) {
            String lecturerSql = "SELECT id, name FROM lecturers";
            PreparedStatement lecturerStatement = connection.prepareStatement(lecturerSql);
            ResultSet lecturerResultSet = lecturerStatement.executeQuery();

            while (lecturerResultSet.next()) {
                int id = lecturerResultSet.getInt("id");
                String name = lecturerResultSet.getString("name");
                lecturerChoiceBox.getItems().add(id + " - " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load lecturers.");
            return;
        }

        // Fetch modules from the database
        try (Connection connection = DatabaseConnection.connect()) {
            String moduleSql = "SELECT id, name FROM modules";
            PreparedStatement moduleStatement = connection.prepareStatement(moduleSql);
            ResultSet moduleResultSet = moduleStatement.executeQuery();

            while (moduleResultSet.next()) {
                int id = moduleResultSet.getInt("id");
                String name = moduleResultSet.getString("name");
                moduleChoiceBox.getItems().add(id + " - " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load modules.");
            return;
        }

        // Fetch classes from the database
        try (Connection connection = DatabaseConnection.connect()) {
            String classSql = "SELECT id, name FROM classes";
            PreparedStatement classStatement = connection.prepareStatement(classSql);
            ResultSet classResultSet = classStatement.executeQuery();

            while (classResultSet.next()) {
                int id = classResultSet.getInt("id");
                String name = classResultSet.getString("name");
                classChoiceBox.getItems().add(id + " - " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load classes.");
            return;
        }

        // Set up dialog content
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Select Lecturer:"), lecturerChoiceBox,
                new Label("Select Module:"), moduleChoiceBox,
                new Label("Select Class:"), classChoiceBox
        );
        dialog.getDialogPane().setContent(content);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle button click
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String selectedLecturer = lecturerChoiceBox.getValue();
                String selectedModule = moduleChoiceBox.getValue();
                String selectedClass = classChoiceBox.getValue();

                if (selectedLecturer != null && selectedModule != null && selectedClass != null) {
                    int lecturerId = Integer.parseInt(selectedLecturer.split(" - ")[0].trim());
                    int moduleId = Integer.parseInt(selectedModule.split(" - ")[0].trim());
                    int classId = Integer.parseInt(selectedClass.split(" - ")[0].trim());

                    try (Connection connection = DatabaseConnection.connect()) {
                        // Insert assignment into lecturer_module_class table
                        String assignmentSql = "INSERT INTO lecturer_module_class (lecturer_id, module_id, class_id) VALUES (?, ?, ?)";
                        PreparedStatement assignmentStatement = connection.prepareStatement(assignmentSql);
                        assignmentStatement.setInt(1, lecturerId);
                        assignmentStatement.setInt(2, moduleId);
                        assignmentStatement.setInt(3, classId);

                        int rowsInserted = assignmentStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Module and class assigned to lecturer successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Assignment Failed", "Failed to assign module and class to lecturer.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to assign module and class. Please try again.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a lecturer, module, and class.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // 6.2. Assign Classes
    @FXML
    private void handleAssignClasses() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Class");
        dialog.setHeaderText("Enter class name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            if (!className.trim().isEmpty()) {
                try (Connection connection = DatabaseConnection.connect()) {
                    String sql = "INSERT INTO classes (name) VALUES (?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, className.trim());
                    statement.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Class added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add class. Please try again.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Class name cannot be empty.");
            }
        });
    }


    // 7. View Assigned Roles
    @FXML
    private void handleViewAssignedRoles() {
        try (Connection connection = DatabaseConnection.connect()) {
            // Query to get all lecturers and their roles from the lecturers table
            String sql = "SELECT name, role FROM lecturers";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Build the output string with lecturer names and their roles
            StringBuilder output = new StringBuilder("Assigned Roles:\n");
            while (resultSet.next()) {
                output.append("Lecturer: ").append(resultSet.getString("name"))
                        .append(", Role: ").append(resultSet.getString("role")).append("\n");
            }

            // Display the results in an alert dialog
            showAlert(Alert.AlertType.INFORMATION, "Assigned Roles", output.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to view assigned roles.");
        }
    }



    // 8. View Assigned Modules
    @FXML
    private void handleViewAssignedModules() {
        try (Connection connection = DatabaseConnection.connect()) {
            // SQL query to fetch the module information along with the assigned lecturer and class
            String sql = """
            SELECT modules.id AS module_id, modules.name AS module_name,
                   lecturers.name AS lecturer_name, classes.name AS class_name
            FROM lecturer_module_class
            JOIN modules ON lecturer_module_class.module_id = modules.id
            JOIN lecturers ON lecturer_module_class.lecturer_id = lecturers.id
            JOIN classes ON lecturer_module_class.class_id = classes.id
        """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder output = new StringBuilder("Assigned Modules:\n");
            while (resultSet.next()) {
                output.append("Module ID: ").append(resultSet.getInt("module_id"))
                        .append(", Module Name: ").append(resultSet.getString("module_name"))
                        .append(", Lecturer: ").append(resultSet.getString("lecturer_name"))
                        .append(", Class: ").append(resultSet.getString("class_name")).append("\n");
            }

            showAlert(Alert.AlertType.INFORMATION, "Assigned Modules", output.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to view assigned modules.");
        }
    }



    // 9. View Assigned Semesters
    @FXML
    private void handleViewAssignedSemester() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM semesters";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder output = new StringBuilder("Assigned Semesters:\n");
            while (resultSet.next()) {
                output.append("Semester ID: ").append(resultSet.getInt("id"))
                        .append(", Name: ").append(resultSet.getString("name")).append("\n");
            }
            showAlert(Alert.AlertType.INFORMATION, "Assigned Semesters", output.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to view assigned semesters.");
        }
    }

    // 10. Logout
    @FXML
    private void handleLogout() {
        try {
            // Close the current admin stage
            Stage currentStage = (Stage) adminNameLabel.getScene().getWindow();
            currentStage.close();

            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Open a new stage for the login view
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(loginScene);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }

    // Utility method to show alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
