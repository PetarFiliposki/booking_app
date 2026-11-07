CREATE TABLE businesses (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT NOT NULL REFERENCES users(id),
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    address     VARCHAR(255),
    phone       VARCHAR(50),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE services (
    id               BIGSERIAL PRIMARY KEY,
    business_id      BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    price            NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_businesses_owner ON businesses(owner_id);
CREATE INDEX idx_services_business ON services(business_id);