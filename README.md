# Smart School Pickup System – Backend (Spring Boot)

This repository contains the backend API for the Smart School Pickup & Communication System.  
It is built with **Spring Boot**, **Gradle**, **MySQL**, and **JWT authentication**.  
The backend provides all core services including QR validation, student/parent management, pickup workflow processing, and real-time communication.

---

## 🚀 Technologies
- Java (JDK 17+ recommended)
- Spring Boot
- Spring Security (JWT)
- MySQL
- Gradle
- WebSockets
- Lombok (make sure your IDE supports it)

---

## ⚙️ Requirements
Before running the project, ensure the following are installed on your machine:

- **Java 17+**  
- **MySQL Server** (running and accessible)  
- **Gradle wrapper** is included, no installation needed (`./gradlew`)  
- A MySQL database created (e.g., `school_db`)  

---

## ⚠️ Important: MySQL Database Configuration
Make sure MySQL is installed and running.  
The application expects a valid database connection read application.properties configuration and configure your MySQL database accordingly.
spring.datasource.url=jdbc:mysql://localhost:3306/db_tklem
spring.datasource.username=root
spring.datasource.password=admin
If MySQL is not running, the backend **will fail to start**.

---

## 📦 Build & Run

### Clean, build and run the project:
```
./gradlew clean build
./gradlew bootRun
```

### Visit now -> http://localhost:8080
