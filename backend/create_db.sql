drop database if exists multitripfinder;
create database multitripfinder;
use multitripfinder;

CREATE TABLE User_Account (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    role CHAR(1) NOT NULL
);

CREATE TABLE Trip (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_account_id INT NOT NULL,
    name VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    save_date DATE NOT NULL,
    passenger_count INT NOT NULL,
    total_cost REAL NOT NULL,
    total_transfer_time INT NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES User_Account(id)
);

CREATE TABLE Transfer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    transport_mode VARCHAR(255) NOT NULL,
    carrier VARCHAR(255) NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    cost REAL NOT NULL,
    start_address VARCHAR(255) NOT NULL,
    end_address VARCHAR(255) NOT NULL,
    transfer_order INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES Trip(id)
);

CREATE TABLE Trip_Place (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    is_transfer BOOLEAN,
    stay_duration INT,
    visit_order INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES Trip(id)
);

CREATE TABLE Search_Place (
    id INT PRIMARY KEY AUTO_INCREMENT,
    search_id INT,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    stay_duration INT,
    entry_order INT
);

CREATE TABLE Search (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_account_id INT NOT NULL,
    name VARCHAR(100),
    save_date DATE NOT NULL,
    passenger_count INT NOT NULL,
    preferred_transport INT,
    optimization_criteria INT NOT NULL,
    trip_start_date DATE NOT NULL,
    max_trip_duration INT NOT NULL,
	FOREIGN KEY (user_account_id) REFERENCES User_Account(id)
);

CREATE TABLE Airports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    airport_code CHAR(3) NOT NULL,
    city_code CHAR(3) NOT NULL,
    airport_name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL
);

CREATE TABLE Search_Tag (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_account_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES User_Account(id),
    CONSTRAINT unique_name_user_account UNIQUE (name, user_account_id)
);

CREATE TABLE Trip_Tag (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_account_id INT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (user_account_id) REFERENCES User_Account(id)
);


CREATE TABLE Search_Search_Tag (
    search_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (search_id, tag_id),
    FOREIGN KEY (search_id) REFERENCES Search(id),
    FOREIGN KEY (tag_id) REFERENCES Search_Tag(id)
);

CREATE TABLE Trip_Trip_Tag (
    trip_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (trip_id, tag_id),
    FOREIGN KEY (trip_id) REFERENCES Trip(id),
    FOREIGN KEY (tag_id) REFERENCES Trip_Tag(id)
);