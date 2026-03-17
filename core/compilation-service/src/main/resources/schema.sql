DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;

CREATE TABLE IF NOT EXISTS compilations (
    id      BIGSERIAL PRIMARY KEY,
    pinned  BOOLEAN NOT NULL DEFAULT FALSE,
    title   VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_compilations_pinned ON compilations(pinned);
CREATE INDEX IF NOT EXISTS ix_compilation_events_event ON compilation_events(event_id);