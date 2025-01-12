# Reporting System

A JavaFX-based application designed to streamline academic reporting processes at Limkokwing University. This system includes user roles for Faculty Admin, Lecturers, and Principal Lecturers, providing functionalities like managing modules, tracking attendance, submitting reports, and viewing academic data. The application integrates a MySQL database for secure and efficient data handling.

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Database Configuration](#database-configuration)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### General
- **Login System**: Role-based access for Faculty Admin, Lecturers, and Principal Lecturers.
- **Secure Credentials**: User passwords are securely stored in the database.
- **Responsive UI**: JavaFX application with user-friendly interfaces.

### Faculty Admin
- Add new lecturers, academic years, semesters, and modules.
- Assign lecturers to roles, modules, and classes.
- View assigned lecturers, modules, and semesters.

### Principal Lecturer
- Submit weekly reports for modules, including challenges and recommendations.
- Fill out pre-defined report forms for classes.

### Lecturer
- Mark student attendance.
- Define chapters and learning outcomes for classes.
- Submit forms with real-time validation.

---

## Technologies Used

- **JavaFX**: For building the graphical user interface.
- **MySQL**: For data storage and management.
- **Maven**: For project management and dependency handling.
- **FXML**: For designing UI layouts.
- **Log4j**: For logging user activities like login times.

---

## Installation

### Prerequisites
1. Install [Java JDK 11+](https://www.oracle.com/java/technologies/javase-downloads.html).
2. Install [MySQL](https://dev.mysql.com/downloads/mysql/).
3. Install [Maven](https://maven.apache.org/install.html).

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Matela-cyber/reporting-system.git
   cd reporting-system
   ```
2. Configure the database:
   - Create a MySQL database named `academic_reporting`.
   - Run the SQL script in `src/main/resources/db/schema.sql` to set up the tables.

3. Update the database connection details in `DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/academic_reporting";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```

4. Build and run the project using Maven:
   ```bash
   mvn clean install
   mvn javafx:run
   ```

---

## Usage

1. Launch the application.
2. Log in with the following roles:
   - **Faculty Admin**: Manage lecturers, modules, and academic data.
   - **Principal Lecturer**: Submit and view module reports.
   - **Lecturer**: Manage student attendance and learning outcomes.
3. Use the navigation menu to access various functionalities.

---

## Project Structure

```plaintext
reporting_system/
├── src/
│   ├── main/
│   │   ├── java/org/example/repoting_system/
│   │   │   ├── model/
│   │   │   │   ├── Lecturer.java
│   │   │   │   ├── Student.java
│   │   │   │   ├── User.java
│   │   │   ├── DatabaseConnection.java
│   │   │   ├── LoginController.java
│   │   │   ├── AdminController.java
│   │   │   ├── ...
│   │   ├── resources/org/example/repoting_system/
│   │   │   ├── login-view.fxml
│   │   │   ├── admin-view.fxml
│   │   │   ├── ...
├── pom.xml
```

---

## Database Configuration

The database schema includes the following tables:

### `users`
| Field      | Type        | Description             |
|------------|-------------|-------------------------|
| id         | INT         | Primary key             |
| username   | VARCHAR(50) | Unique username         |
| password   | VARCHAR(255)| Encrypted user password |
| role       | ENUM        | User role: `Admin`, `Lecturer`, `PRL` |

### `lecturers`
| Field      | Type        | Description              |
|------------|-------------|--------------------------|
| id         | INT         | Primary key              |
| name       | VARCHAR(100)| Lecturer's name          |
| email      | VARCHAR(100)| Lecturer's email address |
| module_id  | INT         | Foreign key to modules   |

---

## Contributing
You are welcome contributions to improve this project! To contribute:

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add new feature: your-feature-name"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.


