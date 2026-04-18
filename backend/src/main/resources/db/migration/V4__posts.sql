CREATE TABLE IF NOT EXISTS posts (
    id         SERIAL PRIMARY KEY,
    id_place   INT       NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    id_user    INT       NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    content    TEXT      NOT NULL,
    -- image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS post_likes (
    id_post INT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    id_user INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (id_post, id_user)
);

CREATE TABLE IF NOT EXISTS post_comments (
    id         SERIAL PRIMARY KEY,
    id_post    INT       NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    id_user    INT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_post_comments ON post_comments(id_post);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts(created_at DESC);