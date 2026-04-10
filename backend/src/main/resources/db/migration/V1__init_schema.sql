-- ============================================================
-- V1 - Initial schema + seed data
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    username   VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255),
    email      VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS places (
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    id       SERIAL PRIMARY KEY,
    place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    user_id  INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating   INTEGER CHECK (rating BETWEEN 1 AND 5),
    comment  TEXT
);

CREATE TABLE IF NOT EXISTS routes (
    id       SERIAL PRIMARY KEY,
    place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE
);

INSERT INTO users (first_name, last_name, username, password, email, is_active)
VALUES
    ('Jan', 'Kowalski', 'jkowalski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'jan.kowalski@sggw.edu.pl', TRUE),
    ('Anna', 'Nowak', 'anowak', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'anna.nowak@sggw.edu.pl', TRUE),
    ('Piotr', 'Wisniewski', 'pwisniewski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'piotr.wisniewski@sggw.edu.pl', TRUE),
    ('Marta', 'Wojcik', 'mwojcik', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'marta.wojcik@sggw.edu.pl', TRUE),
    ('Tomasz', 'Kaminski', 'tkaminski', '$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S', 'tomasz.kaminski@sggw.edu.pl', FALSE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO places (name, latitude, longitude)
VALUES
    ('Biblioteka Glowna SGGW', 52.16198, 21.04731),
    ('Budynek 34 - Wydzial Informatyki', 52.16087, 21.04512),
    ('Stolowka Studencka', 52.16321, 21.04603),
    ('Dom Studenta SGGW - DS1', 52.16450, 21.04820),
    ('Rektorat SGGW', 52.16255, 21.04394),
    ('Stadion SGGW', 52.16510, 21.04270),
    ('Kawiarnia przy Bibliotece', 52.16210, 21.04760),
    ('Przystanek Ursynow Polnocny', 52.16050, 21.04900)
ON CONFLICT DO NOTHING;

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

INSERT INTO routes (place_id)
VALUES
    (8),
    (5),
    (2),
    (1),
    (7),
    (3)
ON CONFLICT DO NOTHING;
