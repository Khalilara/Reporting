-- Create archive table for PreparedData with metadata
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
    
    -- Joined/enriched columns from PreparedData
    reseller_type_name VARCHAR,
    channel VARCHAR,
    customer_type VARCHAR,
    product_type VARCHAR,
    
    -- Archive metadata (3 new columns as confirmed)
    archive_year INT NOT NULL COMMENT 'The year of the import (2025, 2026, etc)',
    archive_quarter VARCHAR(2) NOT NULL COMMENT 'The quarter (Q1, Q2, Q3, Q4)',
    archive_week INT NOT NULL COMMENT 'The week number (1, 2, 3, 4)',
    
    -- Audit columns
    batch_id UUID NOT NULL COMMENT 'Unique identifier for this import batch',
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When this version was archived',
    
    -- Indexes for fast queries
    INDEX idx_archive_period (archive_year, archive_quarter, archive_week),
    INDEX idx_archive_batch (batch_id),
    UNIQUE INDEX uk_archive_batch (batch_id, id)
);

-- Create index for querying by period
CREATE INDEX IF NOT EXISTS idx_archive_quarterly 
ON sales_data_archive(archive_year, archive_quarter);

-- Create index for quick lookup of archived versions
CREATE INDEX IF NOT EXISTS idx_archive_week 
ON sales_data_archive(archive_year, archive_quarter, archive_week);
