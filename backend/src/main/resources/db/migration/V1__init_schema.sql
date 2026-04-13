CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    username VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS places (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id1 INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_id2 INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status_of_friendship VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id1, user_id2),
    CONSTRAINT check_not_self CHECK (user_id1 != user_id2)
);

CREATE TABLE IF NOT EXISTS eventy (
    id SERIAL PRIMARY KEY,
    name_of_event VARCHAR(255) NOT NULL,
    id_place INT NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    date_of_event TIMESTAMP,
    comment TEXT,
    organizer_id INT REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS users_on_events (
    id_eventu INT NOT NULL REFERENCES eventy(id) ON DELETE CASCADE,
    id_users  INT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    PRIMARY KEY (id_eventu, id_users)
);

CREATE TABLE IF NOT EXISTS favorites (
    id       INT REFERENCES users(id)  ON DELETE CASCADE,
    id_place INT REFERENCES places(id) ON DELETE CASCADE,
    PRIMARY KEY (id, id_place)
);

CREATE TABLE IF NOT EXISTS reviews (
    id       SERIAL PRIMARY KEY,
    place_id INT NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    user_id  INT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    rating   INT CHECK (rating BETWEEN 1 AND 5),
    comment  TEXT
);

CREATE TABLE IF NOT EXISTS routes (
    id       SERIAL PRIMARY KEY,
    place_id INT NOT NULL REFERENCES places(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS route_points (
    route_id   INT REFERENCES routes(id) ON DELETE CASCADE,
    place_id   INT REFERENCES places(id) ON DELETE CASCADE,
    stop_order INT NOT NULL,
    PRIMARY KEY (route_id, place_id)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);



-- ============================================================
-- V2 - Seed data
-- ============================================================
 
-- Users (password = 'password123' bcrypt hashed)
INSERT INTO users (first_name, last_name, username, password, email, is_active)
VALUES
    ('Jan',    'Kowalski',   'jkowalski',   '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'jan.kowalski@sggw.edu.pl',   TRUE),
    ('Anna',   'Nowak',      'anowak',      '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'anna.nowak@sggw.edu.pl',      TRUE),
    ('Piotr',  'Wisniewski', 'pwisniewski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'piotr.wisniewski@sggw.edu.pl',TRUE),
    ('Marta',  'Wojcik',     'mwojcik',     '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'marta.wojcik@sggw.edu.pl',    TRUE),
    ('Tomasz', 'Kaminski',   'tkaminski',   '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'tomasz.kaminski@sggw.edu.pl', FALSE)
ON CONFLICT (username) DO NOTHING;
 
 
-- Places (SGGW campus locations)
INSERT INTO places (name, latitude, longitude)
VALUES
    ('Biblioteka Glowna SGGW',         52.16198, 21.04731),
    ('Budynek 34 - Wydzial Informatyki',52.16087, 21.04512),
    ('Stolowka Studencka',             52.16321, 21.04603),
    ('Dom Studenta SGGW - DS1',        52.16450, 21.04820),
    ('Rektorat SGGW',                  52.16255, 21.04394),
    ('Stadion SGGW',                   52.16510, 21.04270),
    ('Kawiarnia przy Bibliotece',      52.16210, 21.04760),
    ('Przystanek Ursynow Polnocny',    52.16050, 21.04900)
ON CONFLICT DO NOTHING;
 
 
-- Friendships
INSERT INTO friendships (user_id1, user_id2, status_of_friendship)
VALUES
    (1, 2, 'accepted'),
    (1, 3, 'accepted'),
    (2, 4, 'accepted'),
    (3, 5, 'pending'),
    (4, 5, 'pending')
ON CONFLICT DO NOTHING;
 
 
-- Events
INSERT INTO eventy (name_of_event, id_place, date_of_event, comment, organizer_id)
VALUES
    ('Inauguracja roku akademickiego', 5, '2025-10-01 10:00:00', 'Uroczysta inauguracja roku akademickiego 2025/2026', 1),
    ('Hackathon SGGW',                 2, '2025-11-15 09:00:00', 'Ogolnouczelniany hackathon dla studentow informatyki',  3),
    ('Dzien Sportu',                   6, '2025-05-20 12:00:00', 'Zawody sportowe dla studentow i pracownikow',            2),
    ('Spotkanie kola naukowego',       2, '2025-04-20 18:00:00', 'Miesięczne spotkanie kola naukowego AI',                 3)
ON CONFLICT DO NOTHING;
 
 
-- Users on events
INSERT INTO users_on_events (id_eventu, id_users)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 3), (2, 1),
    (3, 2), (3, 4), (3, 5),
    (4, 3), (4, 1)
ON CONFLICT DO NOTHING;
 
 
-- Favorites
INSERT INTO favorites (id, id_place)
VALUES
    (1, 1), (1, 7),
    (2, 3), (2, 1),
    (3, 2), (3, 6),
    (4, 4), (4, 3)
ON CONFLICT DO NOTHING;
 
 
-- Reviews
INSERT INTO reviews (place_id, user_id, rating, comment)
VALUES
    (1, 1, 5, 'Swietna biblioteka, duzo miejsc do nauki i cisza.'),
    (1, 2, 4, 'Dobry dostep do zasobow, czasem trudno znalezc wolne miejsce.'),
    (2, 3, 4, 'Nowoczesne sale, dobry sprzet komputerowy.'),
    (2, 1, 3, 'Klimatyzacja czasem nie dziala, ale ogolnie ok.'),
    (3, 2, 5, 'Smaczne jedzenie w rozsadnej cenie dla studenta.'),
    (3, 4, 3, 'Dlugie kolejki w porze lunchu.'),
    (4, 3, 4, 'Pokoje czyste, blisko kampusu, polecam.'),
    (5, 2, 5, 'Reprezentacyjny budynek, latwo trafic.'),
    (6, 4, 4, 'Dobry obiekt sportowy, duzo mozliwosci.'),
    (7, 1, 5, 'Najlepsza kawa na kampusie!')
ON CONFLICT DO NOTHING;
 
 
-- Routes
INSERT INTO routes (place_id)
VALUES
    (8), -- start: Przystanek Ursynow Polnocny
    (5), -- Rektorat
    (2), -- Wydzial Informatyki
    (1)  -- Biblioteka
ON CONFLICT DO NOTHING;
 
 
-- Route points (one sample route: przystanek -> rektorat -> informatyka -> biblioteka -> kawiarnia -> stolowka)
INSERT INTO route_points (route_id, place_id, stop_order)
VALUES
    (1, 8, 1),
    (1, 5, 2),
    (1, 2, 3),
    (1, 1, 4),
    (1, 7, 5),
    (1, 3, 6)
ON CONFLICT DO NOTHING;
 









-- -- ============================================================
-- -- V1 - Initial schema + seed data
-- -- ============================================================

-- CREATE TABLE IF NOT EXISTS users (
--     id         SERIAL PRIMARY KEY,
--     first_name VARCHAR(100),
--     last_name  VARCHAR(100),
--     username   VARCHAR(100) UNIQUE NOT NULL,
--     password   VARCHAR(255),
--     email      VARCHAR(255) UNIQUE NOT NULL,
--     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
--     updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
--     is_active  BOOLEAN NOT NULL DEFAULT TRUE
-- );

-- CREATE TABLE IF NOT EXISTS places (
--     id        SERIAL PRIMARY KEY,
--     name      VARCHAR(255) NOT NULL,
--     latitude  DOUBLE PRECISION NOT NULL,
--     longitude DOUBLE PRECISION NOT NULL
-- );

-- CREATE TABLE IF NOT EXISTS reviews (
--     id       SERIAL PRIMARY KEY,
--     place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE,
--     user_id  INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
--     rating   INTEGER CHECK (rating BETWEEN 1 AND 5),
--     comment  TEXT
-- );

-- CREATE TABLE IF NOT EXISTS routes (
--     id       SERIAL PRIMARY KEY,
--     place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE
-- );

-- INSERT INTO users (first_name, last_name, username, password, email, is_active)
-- VALUES
--     ('Jan', 'Kowalski', 'jkowalski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'jan.kowalski@sggw.edu.pl', TRUE),
--     ('Anna', 'Nowak', 'anowak', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'anna.nowak@sggw.edu.pl', TRUE),
--     ('Piotr', 'Wisniewski', 'pwisniewski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'piotr.wisniewski@sggw.edu.pl', TRUE),
--     ('Marta', 'Wojcik', 'mwojcik', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'marta.wojcik@sggw.edu.pl', TRUE),
--     ('Tomasz', 'Kaminski', 'tkaminski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'tomasz.kaminski@sggw.edu.pl', FALSE)
-- ON CONFLICT (username) DO NOTHING;

-- INSERT INTO places (name, latitude, longitude)
-- VALUES
--     ('Biblioteka Glowna SGGW', 52.16198, 21.04731),
--     ('Budynek 34 - Wydzial Informatyki', 52.16087, 21.04512),
--     ('Stolowka Studencka', 52.16321, 21.04603),
--     ('Dom Studenta SGGW - DS1', 52.16450, 21.04820),
--     ('Rektorat SGGW', 52.16255, 21.04394),
--     ('Stadion SGGW', 52.16510, 21.04270),
--     ('Kawiarnia przy Bibliotece', 52.16210, 21.04760),
--     ('Przystanek Ursynow Polnocny', 52.16050, 21.04900)
-- ON CONFLICT DO NOTHING;

-- INSERT INTO reviews (place_id, user_id, rating, comment)
-- VALUES
--     (1, 1, 5, 'Swietna biblioteka, duzo miejsc do nauki i cisza.'),
--     (1, 2, 4, 'Dobry dostep do zasobow, czasem trudno znalezc wolne miejsce.'),
--     (2, 3, 4, 'Nowoczesne sale, dobry sprzet komputerowy.'),
--     (2, 1, 3, 'Klimatyzacja czasem nie dziala, ale ogolnie ok.'),
--     (3, 2, 5, 'Smaczne jedzenie w rozsadnej cenie dla studenta.'),
--     (3, 4, 3, 'Dlugie kolejki w porze lunchu.'),
--     (4, 3, 4, 'Pokoje czyste, blisko kampusu, polecam.'),
--     (5, 2, 5, 'Reprezentacyjny budynek, latwo trafic.'),
--     (6, 4, 4, 'Dobry obiekt sportowy, duzo mozliwosci.'),
--     (7, 1, 5, 'Najlepsza kawa na kampusie!')
-- ON CONFLICT DO NOTHING;

-- INSERT INTO routes (place_id)
-- VALUES
--     (8),
--     (5),
--     (2),
--     (1),
--     (7),
--     (3)
-- ON CONFLICT DO NOTHING;
