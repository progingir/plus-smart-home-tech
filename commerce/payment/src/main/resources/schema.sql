CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS payments (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    order_id UUID NOT NULL,
    total_payment DECIMAL NOT NULL,
    delivery_total DECIMAL NOT NULL,
    products_total DECIMAL NOT NULL,
    state VARCHAR(50) NOT NULL
);