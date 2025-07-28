CREATE TABLE IF NOT EXISTS warehouse_products (
    id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DECIMAL NOT NULL,
    height DECIMAL NOT NULL,
    depth DECIMAL NOT NULL,
    weight DECIMAL NOT NULL,
    quantity BIGINT NOT NULL
);