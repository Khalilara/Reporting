-- Create archive table for PreparedData with metadata (PostgreSQL compatible)
CREATE TABLE IF NOT EXISTS sales_data_archive (
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

-- Indexes for fast queries
CREATE INDEX IF NOT EXISTS idx_archive_period ON sales_data_archive(archive_year, archive_quarter, archive_week);
CREATE INDEX IF NOT EXISTS idx_archive_batch ON sales_data_archive(batch_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_archive_batch ON sales_data_archive(batch_id, id);

-- Index for querying by period
CREATE INDEX IF NOT EXISTS idx_archive_quarterly ON sales_data_archive(archive_year, archive_quarter);
CREATE INDEX IF NOT EXISTS idx_archive_week ON sales_data_archive(archive_year, archive_quarter, archive_week);

-- (Optionnel) Ajouter des commentaires sur les colonnes
COMMENT ON COLUMN sales_data_archive.archive_year IS 'The year of the import (2025, 2026, etc)';
COMMENT ON COLUMN sales_data_archive.archive_quarter IS 'The quarter (Q1, Q2, Q3, Q4)';
COMMENT ON COLUMN sales_data_archive.archive_week IS 'The week number (1, 2, 3, 4)';
COMMENT ON COLUMN sales_data_archive.batch_id IS 'Unique identifier for this import batch';
COMMENT ON COLUMN sales_data_archive.archived_at IS 'When this version was archived';