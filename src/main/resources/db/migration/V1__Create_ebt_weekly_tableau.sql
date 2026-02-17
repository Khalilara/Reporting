-- Create ebt_weekly_tableau table
CREATE TABLE IF NOT EXISTS ebt_weekly_tableau (
    id BIGSERIAL PRIMARY KEY,
    week_number VARCHAR(10) NOT NULL,
    ca_weekly DOUBLE PRECISION NOT NULL,
    comment VARCHAR(500),
    quarter VARCHAR(5) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on quarter for faster queries
CREATE INDEX idx_ebt_weekly_quarter ON ebt_weekly_tableau(quarter);
