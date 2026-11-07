CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE bookings (
                          id          BIGSERIAL PRIMARY KEY,
                          business_id BIGINT NOT NULL REFERENCES businesses(id),
                          service_id  BIGINT NOT NULL REFERENCES services(id),
                          customer_id BIGINT NOT NULL REFERENCES users(id),
                          start_time  TIMESTAMP WITH TIME ZONE NOT NULL,
                          end_time    TIMESTAMP WITH TIME ZONE NOT NULL,
                          status      VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                          created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                          CHECK (end_time > start_time),

                          CONSTRAINT no_overlapping_bookings EXCLUDE USING gist (
                              business_id WITH =,
                              tstzrange(start_time, end_time) WITH &&
                              ) WHERE (status = 'CONFIRMED')
);

CREATE INDEX idx_bookings_business_time ON bookings(business_id, start_time);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);