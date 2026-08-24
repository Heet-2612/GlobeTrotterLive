CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    estimated_duration_minutes INT NOT NULL DEFAULT 60,
    estimated_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'USD',
    image_url VARCHAR(500),
    CONSTRAINT fk_activities_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

CREATE TABLE trip_activities (
    id BIGSERIAL PRIMARY KEY,
    trip_stop_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    scheduled_date DATE NOT NULL,
    start_time TIME,
    notes TEXT,
    custom_cost DECIMAL(10,2),
    activity_order INT NOT NULL,
    CONSTRAINT fk_trip_activities_stop FOREIGN KEY (trip_stop_id) REFERENCES trip_stops(id) ON DELETE CASCADE,
    CONSTRAINT fk_trip_activities_activity FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE RESTRICT
);

CREATE INDEX idx_activities_city_category ON activities(city_id, category);
CREATE INDEX idx_trip_activities_stop_order ON trip_activities(trip_stop_id, activity_order);
