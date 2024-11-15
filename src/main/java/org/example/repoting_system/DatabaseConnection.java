package org.example.repoting_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/academic_reporting";  // Database name
    private static final String USER = "root";  // Replace with your DB username
    private static final String PASSWORD = "123456";  // Replace with your DB password

    // Method to establish a connection to the database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to test the connection
    public static void testConnection() {
        try (Connection connection = connect()) {
            if (connection != null) {
                System.out.println("Connection to the database was successful!");
            }
        } catch (SQLException e) {
            System.out.println("Error: Unable to connect to the database.");
            e.printStackTrace();
        }
    }

    // Main method to run the test connection
    public static void main(String[] args) {
        testConnection();
    }
}
