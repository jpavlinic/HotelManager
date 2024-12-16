DROP DATABASE hotel;

SET SQL_SAFE_UPDATES = 0;
CREATE DATABASE IF NOT EXISTS hotel;

USE hotel;


CREATE TABLE IF NOT EXISTS User (
    username VARCHAR(100) PRIMARY KEY,
    password VARCHAR(100),
    usertype VARCHAR(100),
    fullName VARCHAR(100)
);


CREATE TABLE IF NOT EXISTS Role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE
);


CREATE TABLE IF NOT EXISTS UserRole (
    user_role_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    role_id INT,
    FOREIGN KEY (username) REFERENCES User(username),
    FOREIGN KEY (role_id) REFERENCES Role(role_id)
);


CREATE TABLE IF NOT EXISTS Permission (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(50) UNIQUE
);


CREATE TABLE IF NOT EXISTS RolePermission (
    role_permission_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT,
    permission_id INT,
    FOREIGN KEY (role_id) REFERENCES Role(role_id),
    FOREIGN KEY (permission_id) REFERENCES Permission(permission_id)
);


CREATE TABLE IF NOT EXISTS Location (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    Country VARCHAR(50),
    City VARCHAR(50),
    zip VARCHAR(20),
    address VARCHAR(100)
);


CREATE TABLE IF NOT EXISTS Hotel (
    hotel_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    location_id INT,
    stars INT,
    reviewRating DECIMAL(3, 2),
    numberOfRooms INT,
    posted_by VARCHAR(100),
    FOREIGN KEY (location_id) REFERENCES Location(location_id),
    FOREIGN KEY (posted_by) REFERENCES User(username)
);


CREATE TABLE IF NOT EXISTS RoomPrice (
    roomPrice_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    roomtype VARCHAR(50),
    roomprice DECIMAL(10, 2),
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)
);


CREATE TABLE IF NOT EXISTS Reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    username VARCHAR(50),
    reviewRating DECIMAL(3, 2),
    description TEXT,
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id),
    FOREIGN KEY (username) REFERENCES User(username)
);


CREATE TABLE IF NOT EXISTS Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    creditCardNumber VARCHAR(100),
    expirationDate VARCHAR(100),
    cvv VARCHAR(100),
    fullName VARCHAR(100),
    FOREIGN KEY (username) REFERENCES User(username)
);


CREATE TABLE IF NOT EXISTS Reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    username VARCHAR(50),
    checkIn DATE,
    checkOut DATE,
    room INT,
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id),
    FOREIGN KEY (username) REFERENCES User(username)
);


CREATE TABLE IF NOT EXISTS HotelAmenities (
    hotel_id INT PRIMARY KEY,
    amenitiesDescription TEXT,
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)
);


CREATE TABLE IF NOT EXISTS RoomAvailability (
    availability_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    roomtype VARCHAR(100),
    availabilityDate DATE,
    availableRooms INT,
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)
);


CREATE TABLE IF NOT EXISTS RoomType (
    roomType_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    room_type VARCHAR(100),
    capacity INT,
    description TEXT,
    price_per_night DECIMAL(10, 2),
    FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)
);

-- inserting data for adming - both username and password are admin
INSERT INTO User (username, password, usertype, fullName)
VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Admin', 'admin');
