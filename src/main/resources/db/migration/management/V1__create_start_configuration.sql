-- Sequenciador para código dos clientes
CREATE SEQUENCE customer_code_seq START WITH 1000 INCREMENT BY 1;

-- Clientes do SaaS
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code INTEGER NOT NULL UNIQUE
        DEFAULT nextval('customer_code_seq'),
    name VARCHAR(255) NOT NULL
);

-- Tenants dos clientes
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL UNIQUE,
    schema_name VARCHAR(63) NOT NULL UNIQUE,

    CONSTRAINT fk_tenants_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE CASCADE
);