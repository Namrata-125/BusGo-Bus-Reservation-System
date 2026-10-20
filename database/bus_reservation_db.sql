CREATE DATABASE IF NOT EXISTS bus_reservation_db;

USE bus_reservation_db;

-- ==========================================
-- USERS TABLE
-- ==========================================

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- BUSES TABLE
-- ==========================================

CREATE TABLE buses (
    bus_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    bus_name VARCHAR(100) NOT NULL,
    bus_type VARCHAR(50),
    total_seats INT NOT NULL,
    operator_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- ROUTES TABLE
-- ==========================================

CREATE TABLE routes (
    route_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    fare DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_route_bus
        FOREIGN KEY (bus_id)
        REFERENCES buses(bus_id)
        ON DELETE CASCADE
);

-- ==========================================
-- SEATS TABLE
-- ==========================================

CREATE TABLE seats (
    seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    seat_type ENUM('WINDOW', 'AISLE', 'MIDDLE') DEFAULT 'WINDOW',
    seat_status ENUM('AVAILABLE', 'BOOKED') DEFAULT 'AVAILABLE',

    CONSTRAINT fk_seat_bus
        FOREIGN KEY (bus_id)
        REFERENCES buses(bus_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_bus_seat
        UNIQUE (bus_id, seat_number)
);

-- ==========================================
-- BOOKINGS TABLE
-- ==========================================

CREATE TABLE bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    booking_status ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
    total_amount DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_booking_route
        FOREIGN KEY (route_id)
        REFERENCES routes(route_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_booking_seat
        FOREIGN KEY (seat_id)
        REFERENCES seats(seat_id)
        ON DELETE CASCADE
);

-- ==========================================
-- PAYMENTS TABLE
-- ==========================================

CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    payment_method ENUM('UPI', 'CARD', 'NET_BANKING', 'CASH') NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('SUCCESS', 'FAILED', 'PENDING', 'REFUND_PENDING', 'REFUNDED') DEFAULT 'PENDING',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(booking_id)
        ON DELETE CASCADE
);