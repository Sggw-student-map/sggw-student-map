-- Tabela: users
CREATE TABLE users (
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


-- Tabela: places
CREATE TABLE places (
    id SERIAL PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL
);

-- Tabela znajomosci
CREATE TABLE friendships (
    user_id1 INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_id2 INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status_of_friendship VARCHAR(20) DEFAULT 'pending', 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id1, user_id2),
    CONSTRAINT check_not_self CHECK (user_id1 <> user_id2)
);


-- Tabela eventy
CREATE TABLE eventy (
    id SERIAL PRIMARY KEY,
    name_of_event VARCHAR(255) NOT NULL,
    --id_user INT REFERENCES users(id) ON DELETE CASCADE,
    id_place INT NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    date_of_event TIMESTAMP,
    comment text,
    organizer_id INT REFERENCES users(id) ON DELETE SET NULL
);


CREATE TABLE users_on_events(
    id_eventu int NOT NULLREFERENCES eventy(id) N DELETE CASCADE, 
    id_users int not NULLREFERENCES users(id) N DELETE CASCADE
);

CREATE TABLE favortites (
    id INT REFERENCES users(id) ON DELETE CASCADE,
    id_place INT REFERENCES places(id) ON DELETE CASCADE,
    PRIMARY KEY (id, id_place)
);


-- Tabela: reviews
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    place_id INT NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,

    CONSTRAINT fk_reviews_place
        FOREIGN KEY(place_id)
        REFERENCES places(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Tabela: routes
CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    place_id INT NOT NULL REFERENCES places(id) ON DELETE CASCADE,

    CONSTRAINT fk_routes_place
        FOREIGN KEY(place_id)
        REFERENCES places(id)
        ON DELETE CASCADE
);


CREATE TABLE route_points (
    route_id INT REFERENCES routes(id) ON DELETE CASCADE,
    place_id INT REFERENCES places(id) ON DELETE CASCADE,
    stop_order INT NOT NULL, -- kolejność zwiedzania
    PRIMARY KEY (route_id, place_id)
);


-- Indeksy
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
