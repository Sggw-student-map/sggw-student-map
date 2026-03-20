-- Dodanie profesjonalnych pól do tabeli uzytkownicy
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS imie VARCHAR(100);
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS nazwisko VARCHAR(100);
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS username VARCHAR(100) UNIQUE;
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE uzytkownicy ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;

-- Indeksy dla wydajności
CREATE INDEX IF NOT EXISTS idx_uzytkownicy_username ON uzytkownicy(username);
CREATE INDEX IF NOT EXISTS idx_uzytkownicy_email ON uzytkownicy(email);
