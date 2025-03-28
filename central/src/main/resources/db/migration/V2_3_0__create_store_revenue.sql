CREATE TABLE IF NOT EXISTS store_revenue (
    store_id BIGINT PRIMARY KEY,
    total DECIMAL NOT NULL,
    CONSTRAINT fk_store FOREIGN KEY(store_id) REFERENCES stores(id)
);
