# Lokalna integracja frontend-backend



## Najwazniejsze elementy techniczne

- Backend:
  - `application.properties` + `application-local.properties` (profil lokalny i cookie policy).
  - `AuthController` ustawia refresh cookie przez `ResponseCookie` (`HttpOnly`, `SameSite`, `Secure` zalezne od profilu).
  - `AuthSecurityRules` ma poprawiona metode dla `/auth/me` (`GET`).
- Frontend:
  - `app.config.ts`: dodane `provideHttpClient` oraz interceptor.
  - `proxy.conf.json`: przekierowanie endpointow na backend.
  - `core/auth.service.ts`: `login`, `refresh`, `me`.
  - `core/auth.interceptor.ts`: automatyczne dodawanie JWT.
  - `core/place.service.ts`: pobieranie pinow mapy.
  - `login` i `map` podpiete pod realne requesty HTTP.
- DB:
  - Flyway (`V1__init_schema.sql`) jest zrodlem prawdy dla schematu i danych startowych.
  - `db/init.sql` zostal celowo ograniczony do no-op, aby uniknac konfliktu z Flyway.

## Jak uruchomic lokalnie


1. Uruchom backend:
   - `cd backend`
   - `mvnw.cmd spring-boot:run`
2. Uruchom frontend:
   - `cd frontend`
   - `npm install`
   - `npm start`
3. Otworz aplikacje: `http://localhost:4200`.



## Przeplyw logowania (lokalnie)

1. Uzytkownik loguje sie na ekranie `login`.
2. Frontend wysyla `POST /auth/login` (przez proxy).
3. Backend zwraca access token w body i ustawia refresh token w `HttpOnly` cookie.
4. Frontend zapisuje access token i dolacza go interceptoren do kolejnych zapytan.
5. Przy odswiezaniu sesji frontend wysyla `POST /auth/refresh` z `withCredentials`, backend zwraca nowy access token.

## Uwagi operacyjne

- Dla local dev cookie refresh ma `Secure=false` (profil `local`), dzieki czemu dziala na HTTP.
- Dla srodowisk innych niz lokalne nalezy ustawic `Secure=true` i dopasowac `SameSite` do docelowej topologii.
