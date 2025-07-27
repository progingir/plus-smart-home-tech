CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS carts (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    state VARCHAR(50) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID references carts(id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id)
);