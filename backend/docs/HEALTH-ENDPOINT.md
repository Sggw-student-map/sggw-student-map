# Health endpoint — dokumentacja

Endpoint sprawdzający stan aplikacji backendu: **wstanie kontenera** oraz **połączenie z bazą danych**. Oparty o Spring Boot Actuator, hardenowany pod kątem bezpieczeństwa sieciowego (osobny port, loopback-only, whitelist endpointów, brak info-leaków).

---

## TL;DR

| Co | Gdzie |
|---|---|
| Endpoint liveness (aplikacja żyje) | `GET http://127.0.0.1:8081/actuator/health/liveness` |
| Endpoint readiness (aplikacja żyje **+ DB osiągalna**) | `GET http://127.0.0.1:8081/actuator/health/readiness` |
| Endpoint zagregowany | `GET http://127.0.0.1:8081/actuator/health` |
| Port managementu | **8081** (osobny od głównego 8080) |
| Adres bind | **127.0.0.1** (tylko loopback) |
| Docker HEALTHCHECK | uderza w `readiness` co 30 s |

Endpoint **nie jest osiągalny z zewnątrz kontenera** — port 8081 nie jest `EXPOSE`'owany ani mapowany na hosta. Dostęp wyłącznie z wnętrza kontenera (czyli przez Docker HEALTHCHECK lub `kubectl exec`).

---

## Pliki, które dotyczy task

| Plik | Co tam jest |
|---|---|
| `backend/pom.xml` | dodana zależność `spring-boot-starter-actuator` |
| `backend/src/main/resources/application.properties` | konfiguracja Actuatora (sekcja "Actuator / Health probes") + timeouty HikariCP |
| `backend/src/main/java/.../config/ActuatorSecurityChainConfig.java` | **NOWY** — dedykowany Spring Security `SecurityFilterChain` dla `/actuator/**` |
| `backend/src/main/java/.../config/GlobalErrorHandler.java` | **NOWY** — zamienia 500 *"No static resource"* na puste 404 (anty info-leak) |
| `backend/Dockerfile` | dyrektywa `HEALTHCHECK` używająca `wget` od wnętrza kontenera |

---

## Jak to działa pod spodem

1. Spring Boot Actuator wystawia `/actuator/health` z trzema grupami: zagregowaną, `liveness` i `readiness`.
2. `liveness` zwraca status `livenessState + ping` — celowo **bez DB**, żeby chwilowy outage Neona nie powodował restart-loopa kontenera.
3. `readiness` zwraca status `readinessState + db` — `DataSourceHealthIndicator` (auto-konfigurowany przez Actuator) wykonuje realne `Connection.isValid(...)` na pulę HikariCP, czyli round-trip do Postgresa.
4. Z odpowiedzi nie wycieka nic poza `{"status":"UP"}` / `{"status":"DOWN"}` — bo `show-details=never`, `show-components=never` i twardy whitelist endpointów (`exposure.include=health`).
5. Dedykowany `SecurityFilterChain` dla `/actuator/**` ma najwyższy priorytet, wyłącza CSRF/CORS/HTTPBasic/formLogin, wymusza nagłówki `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy: no-referrer`, `Cache-Control: no-store`.
6. Docker `HEALTHCHECK` co 30 s odpala `wget --spider http://127.0.0.1:8081/actuator/health/readiness`. Po 3 nieudanych próbach (~90 s) kontener oznaczany jest jako `unhealthy`.

---

## Architektura bezpieczeństwa (warstwy obronne)

| Warstwa | Mechanizm | Efekt |
|---|---|---|
| Sieciowa | `management.server.port=8081` + `management.server.address=127.0.0.1` + brak `EXPOSE 8081` | Port nieosiągalny z hosta i z innych kontenerów w sieci Docker |
| Konfiguracja Actuatora | `exposure.include=health` | Wszystkie inne endpointy (`/env`, `/beans`, `/heapdump`, `/loggers`, `/threaddump`, `/mappings`) nie są zarejestrowane |
| Format odpowiedzi | `show-details=never`, `show-components=never` | Brak nazw indikatorów, wersji bazy, schematu |
| Spring Security | `ActuatorSecurityChainConfig` (HIGHEST_PRECEDENCE) | Wyłączone niepotrzebne mechanizmy (CSRF, CORS, sesje, Basic) |
| HTTP headers | `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy: no-referrer`, `Cache-Control: no-store` | Defense-in-depth |
| Error handling | `GlobalErrorHandler` → 404 z pustym body | Brak info-leak nt. frameworka i ścieżek żądania |

---

## Wymagania do testów

- **Docker** w wersji ≥ 20 (sprawdzone na 29.2.1)
- **Docker Compose v2/v5** (do uruchomienia obrazu wystarczy sam Docker, Compose niewymagany)
- Wolny port **8080** na hoście (8081 jest tylko wewnątrz kontenera)
- Dostęp z hosta do internetu (kontener łączy się z Neon Postgres przez sieć)
- Windows PowerShell / macOS / Linux — komendy w przykładach są dla PowerShella, ale prawie wszystkie działają tak samo w bashu

> **Uwaga:** Jeśli port 8080 jest zajęty (np. przez lokalnie uruchomionego Springa albo inny kontener), zastąp w komendach `-p 8080:8080` przez `-p 9090:8080` i analogicznie odpytuj `http://localhost:9090/...` z hosta. Wszystkie testy wewnątrz kontenera (`docker exec ... 127.0.0.1:8081/...`) pozostają bez zmian.

---

## Test manualny — krok po kroku

### Krok 1 — sklonuj i wejdź do katalogu backendu

```powershell
cd c:\<scieżka>\sggwstudentmap\sggwstudentmap\backend
```

### Krok 2 — zbuduj obraz Dockera

```powershell
docker build -t sggw-backend:health-test .
```

Czekaj na komunikat `naming to docker.io/library/sggw-backend:health-test`. Build trwa ~1–3 min za pierwszym razem, kolejne korzystają z cache Maven.

### Krok 3 — uruchom kontener

```powershell
docker run -d --name sggw-health-test -p 8080:8080 sggw-backend:health-test
```

Komenda zwraca długi hash kontenera. **Nie mapujemy portu 8081 świadomie** — to test bezpieczeństwa.

### Krok 4 — odczekaj ~60–90 s na bootstrap Springa

Spring Boot potrzebuje czasu na podniesienie Tomcata, otwarcie pool'a HikariCP, ewentualny cold-start Neona. `HEALTHCHECK` ma `--start-period=60s` żeby nie liczyć błędów w tym oknie.

```powershell
docker ps --filter "name=sggw-health-test" --format "table {{.Names}}`t{{.Status}}"
```

Oczekiwana ewolucja kolumny `STATUS`:

```
Up 5 seconds (health: starting)
Up 30 seconds (health: starting)
Up 1 minute (healthy)              <-- KLUCZOWY MOMENT
```

Jeśli po 2 min nadal `(health: starting)` → patrz **Troubleshooting** poniżej.

### Krok 5 — zajrzyj w log probe'ów Dockera

```powershell
docker inspect --format='{{json .State.Health}}' sggw-health-test | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

Oczekiwane: `"Status": "healthy"`, `"FailingStreak": 0`, w `Log[]` pojawiają się wpisy z `ExitCode: 0`. To dowód, że `wget --spider http://127.0.0.1:8081/actuator/health/readiness` od wnętrza kontenera zwraca 200 OK, czyli aplikacja **żyje + DB osiągalna**.

### Krok 6 — odpytaj endpointy bezpośrednio (z wnętrza kontenera)

```powershell
docker exec sggw-health-test wget -qO- http://127.0.0.1:8081/actuator/health
docker exec sggw-health-test wget -qO- http://127.0.0.1:8081/actuator/health/liveness
docker exec sggw-health-test wget -qO- http://127.0.0.1:8081/actuator/health/readiness
```

Oczekiwane (kolejno):

```
{"groups":["liveness","readiness"],"status":"UP"}
{"status":"UP"}
{"status":"UP"}
```

### Krok 7 — test bezpieczeństwa: port 8081 z hosta MUSI być nieosiągalny

```powershell
Test-NetConnection -ComputerName 127.0.0.1 -Port 8081 -WarningAction SilentlyContinue | Select-Object TcpTestSucceeded
```

Oczekiwane: `TcpTestSucceeded : False`. Jeśli True → konfiguracja `management.server.address` jest błędna albo ktoś dodał mapowanie portu w `docker run`.

### Krok 8 — test bezpieczeństwa: niezarejestrowane endpointy → 404 z pustym body

Najprościej zainstalować `curl` w kontenerze (potrzebne tylko do testów, w produkcji wystarcza `wget` z BusyBoxa):

```powershell
docker exec sggw-health-test sh -c "apk add --no-cache curl"
```

Następnie:

```powershell
$endpoints = @('/actuator/env','/actuator/beans','/actuator/heapdump','/actuator/info','/actuator/loggers','/actuator/threaddump','/actuator/mappings')
foreach ($ep in $endpoints) {
    Write-Host "`n-- $ep --"
    docker exec sggw-health-test curl -i -s "http://127.0.0.1:8081$ep" | Select-Object -First 3
}
```

Oczekiwane dla każdego:

```
HTTP/1.1 404
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
```

I dodatkowo `Content-Length: 0` (puste body) — czyli **zero info-leak**. Sprawdź:

```powershell
docker exec sggw-health-test curl -i -s http://127.0.0.1:8081/actuator/env
```

### Krok 9 — test scenariusza "DB padła"

Odłącz kontener od sieci Dockera (symuluje brak dostępu do Neona):

```powershell
docker network disconnect bridge sggw-health-test
Start-Sleep -Seconds 10
```

Sprawdź readiness — **musi być 503 DOWN**:

```powershell
docker exec sggw-health-test curl -i -s --max-time 20 http://127.0.0.1:8081/actuator/health/readiness | Select-Object -First 2
docker exec sggw-health-test curl -s --max-time 20 http://127.0.0.1:8081/actuator/health/readiness
```

Oczekiwane:

```
HTTP/1.1 503
{"status":"DOWN"}
```

Sprawdź liveness — **musi być 200 UP** (aplikacja żyje, nie restartować!):

```powershell
docker exec sggw-health-test curl -s --max-time 5 http://127.0.0.1:8081/actuator/health/liveness
```

Oczekiwane: `{"status":"UP"}`

To jest **kluczowy patternowy test** — pokazuje, że scenariusz produkcyjny (load balancer przestaje routować ruch, ale orkiestrator nie restartuje kontenera) działa.

Przywróć sieć:

```powershell
docker network connect bridge sggw-health-test
Start-Sleep -Seconds 20
docker exec sggw-health-test curl -s http://127.0.0.1:8081/actuator/health/readiness
```

Oczekiwane: `{"status":"UP"}`

### Krok 10 — sprzątanie

```powershell
docker rm -f sggw-health-test
docker rmi sggw-backend:health-test
```

---

## Checklist — co musi się zgodzić

Zaliczasz testy jeśli **wszystkie** punkty są ✅:

- [ ] Krok 4: `docker ps` pokazuje `(healthy)` po ~60–90 s
- [ ] Krok 5: `docker inspect` pokazuje `Status: healthy`, `ExitCode: 0` w `Log`
- [ ] Krok 6: trzy endpointy zwracają `{"status":"UP"}`
- [ ] Krok 7: `Test-NetConnection -Port 8081` → `TcpTestSucceeded: False`
- [ ] Krok 8: każdy zablokowany endpoint zwraca `HTTP/1.1 404` + `Content-Length: 0`
- [ ] Krok 9 (DB down): readiness = `503 {"status":"DOWN"}`, liveness = `200 {"status":"UP"}`
- [ ] Krok 9 (recovery): po reconnect readiness wraca do `200 {"status":"UP"}`

---

## Troubleshooting

### "ports are not available: ... bind: Only one usage of each socket address"

Port 8080 zajęty przez inny proces (najczęściej lokalnie uruchomiony Spring Boot albo inny kontener Dockera).

```powershell
docker ps --filter "publish=8080"          # czy jakiś kontener trzyma 8080?
Get-NetTCPConnection -LocalPort 8080 -State Listen | Select OwningProcess
Get-Process -Id <PID>                       # co to za proces?
```

Rozwiązanie: zatrzymaj winowajcę lub odpal kontener na innym porcie hosta:

```powershell
docker run -d --name sggw-health-test -p 9090:8080 sggw-backend:health-test
```

### `(health: starting)` przez >2 minuty

Spring Boot nie zdążył wystartować. Sprawdź logi:

```powershell
docker logs sggw-health-test --tail 100
```

Typowe przyczyny:
- Neon cold-start — pierwsze zapytanie potrafi trwać 15–30 s, restart pomaga
- Błędne credentiale w `application.properties` (szukaj `password authentication failed`)
- Brak dostępu do internetu z kontenera (sprawdź `docker exec sggw-health-test ping -c1 ep-autumn-cake-al6t1mlk-pooler.c-3.eu-central-1.aws.neon.tech`)

### Docker cache uniemożliwia rebuild po zmianie kodu

```powershell
docker build --no-cache -t sggw-backend:health-test .
```

### Test pokazuje `(unhealthy)` mimo że appka działa

Sprawdź log probe'ów:

```powershell
docker inspect --format='{{json .State.Health.Log}}' sggw-health-test | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

W polu `Output` zobaczysz dokładny komunikat błędu — najczęściej "Connection refused" (Spring jeszcze nie wstał) albo timeout (DB nieosiągalna).

---

## FAQ

**Dlaczego port 8081 a nie 8080?**
Bo na 8080 chodzi aplikacja produkcyjna wystawiana publicznie. Actuator zawiera potencjalnie wrażliwe endpointy i nie powinien dzielić powierzchni ataku z publicznym API. Osobny port + bind tylko do loopbacka = endpoint nie istnieje "z zewnątrz".

**Dlaczego readiness sprawdza DB, a liveness nie?**
To standardowy Kubernetes pattern. Liveness służy do decyzji "restartować kontener?" — restart nie pomoże gdy padła zewnętrzna baza, więc nie chcemy żeby chwilowy outage Neona restartował nasz pod w pętli. Readiness służy do decyzji "kierować ruch?" — gdy DB padła, niech load balancer przestanie routować zapytania.

**Dlaczego odpowiedź nie pokazuje szczegółów?**
`show-details=never` celowo. Atakujący który dotrze do endpointu nie dowie się ani jakiej używamy bazy, ani jej wersji, ani schematu, ani nazw indikatorów. Otrzyma tylko `UP` lub `DOWN`. Dla operatora to ograniczenie nieistotne — jeśli `readiness` = `DOWN`, sprawdzasz logi aplikacji, a tam masz pełen stacktrace.

**Czy mogę dodać własny `HealthIndicator`?**
Tak, wystarczy bean implementujący `HealthIndicator`. Trzeba pamiętać żeby go dodać do odpowiedniej grupy w `application.properties`:
```
management.endpoint.health.group.readiness.include=readinessState,db,mojIndicator
```

**Jak włączyć więcej szczegółów dla operatora?**
Można dodać autoryzację Actuatora i zmienić `show-details=when-authorized`. Wtedy zwykli klienci dostają `{"status":"UP"}`, a zalogowany operator widzi pełne komponenty. Alternatywnie zostawić jak jest i operacyjne info ciągnąć z logów / metryk Prometheusa.

**Czy `/actuator/health` jest w OpenAPI / Swaggerze?**
Nie. Swagger UI obsługuje tylko endpointy na porcie 8080 (`@RestController`), Actuator chodzi na 8081 i nie wpada w skan springdoc.
