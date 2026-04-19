-- ============================================================
-- V3 - Feed (posts, likes, comments)
-- ============================================================

CREATE TABLE IF NOT EXISTS feed_posts (
    id         SERIAL PRIMARY KEY,
    author_id  INT  NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    place_id   INT           REFERENCES places(id) ON DELETE SET NULL,
    content    TEXT NOT NULL,
    image_url  TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feed_posts_created_at ON feed_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_feed_posts_author_id  ON feed_posts(author_id);
CREATE INDEX IF NOT EXISTS idx_feed_posts_place_id   ON feed_posts(place_id);

CREATE TABLE IF NOT EXISTS feed_post_likes (
    post_id    INT NOT NULL REFERENCES feed_posts(id) ON DELETE CASCADE,
    user_id    INT NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_feed_post_likes_user ON feed_post_likes(user_id);

CREATE TABLE IF NOT EXISTS feed_post_comments (
    id         SERIAL PRIMARY KEY,
    post_id    INT  NOT NULL REFERENCES feed_posts(id) ON DELETE CASCADE,
    author_id  INT  NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    content    TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feed_post_comments_post       ON feed_post_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_feed_post_comments_created_at ON feed_post_comments(created_at);

-- ------------------------------------------------------------
-- Seed data (safe if users/places from V1 exist)
-- ------------------------------------------------------------
INSERT INTO feed_posts (author_id, place_id, content, created_at)
VALUES
    (2, 1, 'Niesamowity widok z biblioteki dzis rano! Polecam zajsc po mape kampusu.', CURRENT_TIMESTAMP - INTERVAL '12 minutes'),
    (3, 3, 'Wlasnie probowalem nowe kanapki w barze - mocno polecam wersje z awokado!', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
    (1, 6, 'Dolaczylem do jutrzejszego joggingu po kampusie - ktos jeszcze? Startujemy o 7:00.', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
    (4, 3, 'Stolowka dziala teraz do 19:00. Mialam nadzieje na pozniejszy obiad, niestety...', CURRENT_TIMESTAMP - INTERVAL '3 hours')
ON CONFLICT DO NOTHING;

INSERT INTO feed_post_likes (post_id, user_id)
VALUES
    (1, 3), (1, 4),
    (2, 1), (2, 2), (2, 4),
    (3, 2)
ON CONFLICT DO NOTHING;

INSERT INTO feed_post_comments (post_id, author_id, content)
VALUES
    (1, 3, 'Tez tam byłam rano, faktycznie pieknie!'),
    (1, 4, 'A ja wlasnie ide, dzieki za info.'),
    (2, 1, 'Potwierdzam, awokado to strzał w dziesiątkę.'),
    (3, 2, 'Wchodze w to, do zobaczenia o 7!')
ON CONFLICT DO NOTHING;
