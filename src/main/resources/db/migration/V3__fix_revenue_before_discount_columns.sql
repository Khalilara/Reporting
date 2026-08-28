-- Drop and recreate archive table with correct column order
-- Previous version had revenue and before_discount swapped

DROP TABLE IF EXISTS sales_data_archive CASCADE;

-- Create archive table for PreparedData with metadata (corrected order)
CREATE TABLE sales_data_archive (
    id BIGSERIAL PRIMARY KEY,
    
    -- Original PreparedData columns (full copy)
    reseller VARCHAR,
    reseller_type VARCHAR,
    second_reseller VARCHAR,
    region VARCHAR,
    subsidiary VARCHAR,
    end_customer VARCHAR,
    end_customer_industry VARCHAR,
    prod_subdinary VARCHAR,
    prod_subdinary_subdinary VARCHAR,
    license VARCHAR,
    year NUMERIC,
    month VARCHAR,
    before_discount NUMERIC(15,2),
    licence_quantity NUMERIC,
    discount_rate NUMERIC,
    revenue NUMERIC,
    
    -- Joined/enriched columns
    reseller_type_name VARCHAR,
    channel VARCHAR,
    customer_type VARCHAR,
    product_type VARCHAR,
    
    -- Archive metadata
    archive_year INT NOT NULL,
    archive_quarter VARCHAR(2) NOT NULL,
    archive_week INT NOT NULL,
    week_date DATE,
    
    -- Audit columns
    batch_id UUID NOT NULL,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes separately (PostgreSQL style)
CREATE INDEX IF NOT EXISTS idx_archive_period ON sales_data_archive(archive_year, archive_quarter, archive_week);
CREATE INDEX IF NOT EXISTS idx_archive_batch ON sales_data_archive(batch_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_archive_batch ON sales_data_archive(batch_id, id);

-- Additional indexes for querying
CREATE INDEX IF NOT EXISTS idx_archive_quarterly ON sales_data_archive(archive_year, archive_quarter);
CREATE INDEX IF NOT EXISTS idx_archive_week ON sales_data_archive(archive_year, archive_quarter, archive_week);