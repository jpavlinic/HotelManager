🛠️ HotelManager Project

A Java-based hotel management system with JavaFX user interface, robust database connectivity, and a layered architecture for better separation of concerns.

🔥 About the Project

The HotelManager project is a collaborative Java application built with a focus on database connectivity and data persistence. The application uses JavaFX for the graphical user interface (GUI) and follows a layered architecture, including the Database Layer, Data Persistence Layer, Business Layer, and Presentation Layer. This multi-layered design promotes modularity, maintainability, and ease of future enhancements.
The system provides user registration, login with password protection, and role-based access control. Admins have access to logs and can manage system users, while SQL injection protection ensures secure interactions with the database.

🎉 Features

🔐 User Registration & Login: Allows users to sign up and log in, with secure password handling.

🧑‍💼 Admin Role & Logs: Admins have access to user activity logs and system management tools.

🧱 Layered Architecture: Database Layer, Data Persistence Layer, Business Layer, and Presentation Layer.

🛡️ SQL Injection Protection: Ensures safe interaction with the database, preventing SQL injection attacks.

📡 Database Connectivity: Full integration with a relational database for user, room, and booking information.

📋 Data Persistence: Uses a Data Persistence Layer to handle database transactions.

🖥️ JavaFX User Interface: Interactive and user-friendly UI built with JavaFX.

🛠️ Tech Stack

Languages:

Java (core logic)

Frameworks & Libraries:

JavaFX (for UI design)

JDBC (for database connectivity)

Database:

MySQL (for relational data storage)

Other Tools:

GitLab (for collaborative development and version control)

JavaFX Scene Builder (for visual design of UI)

📡 Core Functionalities

1️⃣ User Authentication

Registration: New users can create an account with a username, email, and password.

Login/Logout: Users can securely log in to access their dashboard.

Role-Based Access Control:

Regular Users: Can log in, view bookings, and manage their hotel reservations.

Admin Users: Can view logs, manage users, and access additional system features.

2️⃣ Database Management

SQL Injection Protection: Uses parameterized queries to prevent SQL injection attacks.

Persistent Data Storage: All user, room, and booking data is stored in a MySQL database.

3️⃣ Layered Architecture

Database Layer: Manages connection and communication with the relational database.

Data Persistence Layer: Handles database read/write operations, ensuring secure and efficient data storage.

Business Layer: Contains business logic for user roles, booking logic, and system rules.

Presentation Layer: The user interface built with JavaFX to provide a responsive and interactive experience
