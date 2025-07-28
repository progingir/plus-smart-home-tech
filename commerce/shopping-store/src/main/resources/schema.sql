CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    image_src VARCHAR(500),
    quantity_state VARCHAR(50) NOT NULL,
    product_state VARCHAR(50) NOT NULL,
    product_category VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL
);