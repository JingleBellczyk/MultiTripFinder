INSERT INTO User_Account (email, role)
VALUES
    ('voportno@gmail.com', 'A'),
    ('269567@student.pwr.edu.pl', 'U');

-- Insert example data into Trip
INSERT INTO Trip (user_account_id, name, start_date, end_date, save_date, passenger_count, total_cost, total_transfer_time, duration)
VALUES
    (1, 'europe vacation', '2024-12-01', '2024-12-10', '2024-11-15', 2, 1200.50, 8, 10),
    (1, 'asia adventure', '2025-01-15', '2025-01-25', '2024-11-20', 1, 1800.75, 10, 11);

-- Insert example data into Transfer
INSERT INTO Transfer (trip_id, transport_mode, carrier, start_date_time, end_date_time, duration, cost, start_address, end_address, transfer_order)
VALUES
    (1, 1, 'Air France', '2024-12-01 08:00:00', '2024-12-01 12:00:00', 240, 500, 'New York, USA', 'Paris, France', 1),
    (1, 2, 'Eurostar', '2024-12-03 10:00:00', '2024-12-03 12:00:00', 120, 200, 'Paris, France', 'London, UK', 2);

-- Insert example data into Trip_Place
INSERT INTO Trip_Place (trip_id, country, city, is_transfer, stay_duration, visit_order)
VALUES
    (1, 'France', 'Paris', FALSE, 3, 1),
    (1, 'UK', 'London', FALSE, 2, 2),
    (2, 'Japan', 'Tokyo', FALSE, 5, 1);

-- Insert example data into Search_Place
INSERT INTO Search_Place (search_id, country, city, stay_duration, entry_order)
VALUES
    (1, 'Germany', 'Berlin', 3, 1),
    (1, 'Italy', 'Rome', 4, 2);

-- Insert example data into Search
INSERT INTO Search (user_account_id, name, save_date, passenger_count, preferred_transport, optimization_criteria, trip_start_date, max_trip_duration)
VALUES
    (1, 'Winter Escape', '2024-11-01', 2, 1, 1, '2024-12-01', 15),
    (2, 'Summer Adventure', '2024-10-15', 4, 2, 2, '2025-06-01', 10);

-- Insert example data into Airports
INSERT INTO Airports (airport_code, city_code, airport_name, city, country)
VALUES
    ('JFK', 'NYC', 'John F. Kennedy International', 'New York', 'USA'),
    ('CDG', 'PAR', 'Charles de Gaulle', 'Paris', 'France'),
    ('NRT', 'TYO', 'Narita International', 'Tokyo', 'Japan');

-- Insert example data into Search_Tag
INSERT INTO Search_Tag (user_account_id, name)
VALUES
    (1, 'Family'),
    (2, 'Adventure');

-- Insert example data into Trip_Tag
INSERT INTO Trip_Tag (user_account_id, name)
VALUES
    (1, 'Romantic'),
    (1, 'Budget Friendly');

-- Insert example data into Search_Search_Tag
INSERT INTO Search_Search_Tag (search_id, tag_id)
VALUES
    (1, 1),
    (2, 2);

-- Insert example data into Trip_Trip_Tag
INSERT INTO Trip_Trip_Tag (trip_id, tag_id)
VALUES
    (1, 1),
    (1, 2);
