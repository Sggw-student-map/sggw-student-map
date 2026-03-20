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
    name VARCHAR(255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL
);

-- Tabela: reviews
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    place_id INT NOT NULL,
    user_id INT NOT NULL,
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
    place_id INT NOT NULL,

    CONSTRAINT fk_routes_place
        FOREIGN KEY(place_id)
        REFERENCES places(id)
        ON DELETE CASCADE
);

-- Indeksy
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
