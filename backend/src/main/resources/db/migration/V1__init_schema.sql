-- -- ============================================================
-- -- V1 - Initial schema + test data
-- -- ============================================================
--
-- -- ------------------------------------------------------------
-- -- USERS
-- -- ------------------------------------------------------------
-- CREATE TABLE users (
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
--
-- -- ------------------------------------------------------------
-- -- PLACES
-- -- ------------------------------------------------------------
-- CREATE TABLE places (
--     id        SERIAL PRIMARY KEY,
--     name      VARCHAR(255) NOT NULL,
--     latitude  DOUBLE PRECISION NOT NULL,
--     longitude DOUBLE PRECISION NOT NULL
-- );
--
-- -- ------------------------------------------------------------
-- -- REVIEWS
-- -- ------------------------------------------------------------
-- CREATE TABLE reviews (
--     id       SERIAL PRIMARY KEY,
--     place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE,
--     user_id  INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
--     rating   INTEGER CHECK (rating BETWEEN 1 AND 5),
--     comment  TEXT
-- );
--
-- -- ------------------------------------------------------------
-- -- ROUTES
-- -- ------------------------------------------------------------
-- CREATE TABLE routes (
--     id       SERIAL PRIMARY KEY,
--     place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE
-- );
--

-- ============================================================
-- TEST DATA
-- ============================================================

-- Users (passwords are plaintext placeholders — hash them once auth is added)
INSERT INTO users (first_name, last_name, username, password, email, is_active) VALUES
    ('Jan',     'Kowalski',   'jkowalski',  'haslo123',  'jan.kowalski@sggw.edu.pl',   TRUE),
    ('Anna',    'Nowak',      'anowak',     'haslo123',  'anna.nowak@sggw.edu.pl',     TRUE),
    ('Piotr',   'Wiśniewski', 'pwisniewski','haslo123',  'piotr.wisniewski@sggw.edu.pl', TRUE),
    ('Marta',   'Wójcik',     'mwojcik',    'haslo123',  'marta.wojcik@sggw.edu.pl',   TRUE),
    ('Tomasz',  'Kaminski',   'tkaminski',  'haslo123',  'tomasz.kaminski@sggw.edu.pl', FALSE);

-- Places (real SGGW campus locations in Warsaw)
INSERT INTO places (name, latitude, longitude) VALUES
    ('Biblioteka Główna SGGW',              52.16198, 21.04731),
    ('Budynek 34 - Wydział Informatyki',    52.16087, 21.04512),
    ('Stołówka Studencka',                  52.16321, 21.04603),
    ('Dom Studenta SGGW - DS1',             52.16450, 21.04820),
    ('Rektorat SGGW',                       52.16255, 21.04394),
    ('Stadion SGGW',                        52.16510, 21.04270),
    ('Kawiarnia przy Bibliotece',           52.16210, 21.04760),
    ('Przystanek Ursynów Północny',         52.16050, 21.04900);

-- Reviews
INSERT INTO reviews (place_id, user_id, rating, comment) VALUES
    (1, 1, 5, 'Świetna biblioteka, dużo miejsc do nauki i cisza.'),
    (1, 2, 4, 'Dobry dostęp do zasobów, czasem trudno znaleźć wolne miejsce.'),
    (2, 3, 4, 'Nowoczesne sale, dobry sprzęt komputerowy.'),
    (2, 1, 3, 'Klimatyzacja czasem nie działa, ale ogólnie ok.'),
    (3, 2, 5, 'Smaczne jedzenie w rozsądnej cenie dla studenta.'),
    (3, 4, 3, 'Długie kolejki w porze lunchu.'),
    (4, 3, 4, 'Pokoje czyste, blisko kampusu, polecam.'),
    (5, 2, 5, 'Reprezentacyjny budynek, łatwo trafić.'),
    (6, 4, 4, 'Dobry obiekt sportowy, dużo możliwości.'),
    (7, 1, 5, 'Najlepsza kawa na kampusie!');

-- Routes (example route passing through key campus spots)
INSERT INTO routes (place_id) VALUES
    (8),  -- Przystanek Ursynów Północny (start)
    (5),  -- Rektorat
    (2),  -- Wydział Informatyki
    (1),  -- Biblioteka Główna
    (7),  -- Kawiarnia
    (3);  -- Stołówka (end)
