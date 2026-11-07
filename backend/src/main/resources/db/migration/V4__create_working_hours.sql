CREATE TABLE working_hours (
                               id          BIGSERIAL PRIMARY KEY,
                               business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                               day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),  -- 1=понеделник ... 7=недела
                               open_time   TIME NOT NULL,
                               close_time  TIME NOT NULL,
                               CHECK (close_time > open_time),
                               UNIQUE (business_id, day_of_week)
);

CREATE INDEX idx_working_hours_business ON working_hours(business_id);