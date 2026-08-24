ALTER TABLE trips ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE trips ADD COLUMN share_token VARCHAR(100) UNIQUE;

CREATE UNIQUE INDEX idx_trips_share_token ON trips(share_token);
