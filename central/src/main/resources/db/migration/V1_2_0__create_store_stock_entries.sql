CREATE TABLE IF NOT EXISTS store_stock_entries (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL,
    UNIQUE(store_id, product_id)
);
