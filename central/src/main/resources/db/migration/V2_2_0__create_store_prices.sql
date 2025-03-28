CREATE TABLE IF NOT EXISTS store_prices (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    amount DECIMAL NOT NULL,
    UNIQUE(store_id, product_id)
);
