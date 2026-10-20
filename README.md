# BusGo – Bus Reservation Management System

BusGo is a full-stack bus reservation web application developed using Java, Spring Boot, MySQL, HTML, CSS, and JavaScript.

## Features

- User registration and login
- Search buses and routes
- View available seats and select seats
- Book bus tickets
- Simulated payment processing
- View booking history
- Cancel bookings and release seats
- Simulated payment refund status after cancellation
- Admin dashboard for managing and viewing records

## Technology Stack

- **Backend:** Java 21, Spring Boot, Maven
- **Database:** MySQL
- **Frontend:** HTML, CSS, JavaScript
- **Testing:** JUnit and Postman

## Project Structure

```text
Bus-Reservation-System/
├── backend/
│   └── bus-reservation-backend/
│       └── bus-reservation-backend/
├── database/
│   └── bus_reservation_db.sql
├── frontend/
│   ├── css/
│   ├── js/
│   └── *.html
├── .gitignore
└── README.md
```

## Setup

### 1. Create the database

Open phpMyAdmin or a MySQL client and run the SQL script:

`database/bus_reservation_db.sql`

### 2. Configure the backend

Install Java 21 and MySQL. Copy `application.properties.example` to `application.properties` in the backend resources directory.

Update the database URL, username, and password for your local MySQL installation.

### 3. Run the backend

Open PowerShell in the backend Maven project directory and run:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend is configured to run at:

`http://localhost:8080`

### 4. Open the frontend

Open `frontend/index.html` in a browser. Ensure the backend is running and the frontend API URL matches your backend address.

## Refund Note

Payments and refunds in this project are simulated for demonstration and learning purposes. The application does not process real financial transactions or transfer real money.

## Database

The SQL script defines the database tables for users, buses, routes, seats, bookings, and payments.

## Author

Namrata Avhale

## License

This project is available for educational and portfolio demonstration purposes.
