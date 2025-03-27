CREATE TABLE IF NOT EXISTS stock (
    product_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL
);
