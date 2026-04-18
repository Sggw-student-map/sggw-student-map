CREATE TABLE IF NOT EXISTS event_likes (
    id_event INT NOT NULL REFERENCES eventy(id) ON DELETE CASCADE,
    id_user  INT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    PRIMARY KEY (id_event, id_user)
);

CREATE TABLE IF NOT EXISTS event_interested (
    id_event INT NOT NULL REFERENCES eventy(id) ON DELETE CASCADE,
    id_user  INT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    PRIMARY KEY (id_event, id_user)
);

CREATE TABLE IF NOT EXISTS event_comments (
    id         SERIAL PRIMARY KEY,
    id_event   INT       NOT NULL REFERENCES eventy(id) ON DELETE CASCADE,
    id_user    INT       NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_event_comments ON event_comments(id_event);