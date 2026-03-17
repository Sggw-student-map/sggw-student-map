-- Tworzenie bazy danych
CREATE DATABASE sggw_student_map;

-- Połączenie z bazą
\c sggw_student_map;

-- Tabela: uzytkownicy
CREATE TABLE uzytkownicy (
    id_uzytk SERIAL PRIMARY KEY
);

-- Tabela: miejsca
CREATE TABLE miejsca (
    id_miejsca SERIAL PRIMARY KEY,
    nazwa VARCHAR(255) NOT NULL,
    szer_geo FLOAT NOT NULL,
    dl_geo FLOAT NOT NULL
);

-- Tabela: recenzje
CREATE TABLE recenzje (
    id_rec SERIAL PRIMARY KEY,
    id_miejsca INT NOT NULL,
    id_uzytk INT NOT NULL,
    ocena INT CHECK (ocena >= 1 AND ocena <= 5),
    komentarz TEXT,

    CONSTRAINT fk_miejsca
        FOREIGN KEY(id_miejsca)
        REFERENCES miejsca(id_miejsca)
        ON DELETE CASCADE,

    CONSTRAINT fk_uzytk
        FOREIGN KEY(id_uzytk)
        REFERENCES uzytkownicy(id_uzytk)
        ON DELETE CASCADE
);

-- Tabela: trasa
CREATE TABLE trasa (
    id_trasy SERIAL PRIMARY KEY,
    id_miejsca INT NOT NULL,

    CONSTRAINT fk_trasa_miejsca
        FOREIGN KEY(id_miejsca)
        REFERENCES miejsca(id_miejsca)
        ON DELETE CASCADE
);