DROP TABLE IF EXISTS events CASCADE;

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    title VARCHAR(120) NOT NULL,
    category_id  BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    event_date   TIMESTAMP NOT NULL,
    created_on   TIMESTAMP NOT NULL DEFAULT now(),
    published_on TIMESTAMP NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit   INT NOT NULL DEFAULT 0,
    request_moderation  BOOLEAN NOT NULL DEFAULT TRUE,
    state VARCHAR(16) NOT NULL CHECK (state IN ('PENDING','PUBLISHED','CANCELED')),
    location_lat DOUBLE PRECISION NOT NULL,
    location_lon DOUBLE PRECISION NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_events_event_date ON events(event_date);
CREATE INDEX IF NOT EXISTS ix_events_state      ON events(state);
CREATE INDEX IF NOT EXISTS ix_events_category   ON events(category_id);
CREATE INDEX IF NOT EXISTS ix_events_paid       ON events(paid);
CREATE INDEX IF NOT EXISTS ix_events_initiator  ON events(initiator_id);
