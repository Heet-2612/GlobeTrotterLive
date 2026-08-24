CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    cost_index DECIMAL(3,2) NOT NULL DEFAULT 1.00,
    popularity INT NOT NULL DEFAULT 50,
    image_url VARCHAR(500)
);

CREATE TABLE trip_stops (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    city_id BIGINT NOT NULL,
    stop_order INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    notes TEXT,
    CONSTRAINT fk_trip_stops_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_trip_stops_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT
);

CREATE INDEX idx_trip_stops_trip_id_order ON trip_stops(trip_id, stop_order);
CREATE INDEX idx_cities_search ON cities(name, country, region);

-- Seed initial city dataset
INSERT INTO cities (name, country, region, cost_index, popularity, image_url) VALUES
('Mumbai', 'India', 'Asia', 2.50, 85, 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f'),
('Goa', 'India', 'Asia', 2.00, 90, 'https://images.unsplash.com/photo-1512343879784-a960bf40e7f2'),
('Bangalore', 'India', 'Asia', 2.20, 80, 'https://images.unsplash.com/photo-1596176530529-78163a4f7af2'),
('Delhi', 'India', 'Asia', 2.30, 88, 'https://images.unsplash.com/photo-1587474260584-136574528ed5'),
('Jaipur', 'India', 'Asia', 1.80, 82, 'https://images.unsplash.com/photo-1477587458883-47145ed94245'),
('Paris', 'France', 'Europe', 4.20, 98, 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34'),
('Rome', 'Italy', 'Europe', 3.80, 95, 'https://images.unsplash.com/photo-1552832230-c0197dd311b5'),
('Tokyo', 'Japan', 'Asia', 4.50, 96, 'https://images.unsplash.com/photo-1503899036084-c55cdd92da26');
