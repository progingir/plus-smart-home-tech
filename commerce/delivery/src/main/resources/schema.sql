CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS addresses (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    country VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(100),
    house VARCHAR(100),
    flat VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS delivery (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    order_id UUID NOT NULL,
    state VARCHAR(50) NOT NULL,
    from_address_id UUID REFERENCES addresses(id),
    to_address_id UUID REFERENCES addresses(id),
    delivery_weight DECIMAL,
    delivery_volume DECIMAL,
    fragile BOOLEAN
);