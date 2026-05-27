FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Tylko port aplikacji jest publikowany na zewnatrz.
# Port managementu (8081) celowo NIE jest EXPOSE'owany - dziala wylacznie
# na loopbacku wewnatrz kontenera (patrz application.properties:
# management.server.address=127.0.0.1). Dzieki temu /actuator/** jest
# nieosiagalny z sieci hosta / dockerowej.
EXPOSE 8080

# HEALTHCHECK Dockera:
#  - sprawdza readiness (= aplikacja + polaczenie z baza) zgodnie z taskiem
#    "wstanie kontenera + laczenie z baza",
#  - --start-period=60s daje czas Spring Bootowi na bootstrap (Flyway,
#    Hibernate, otwarcie poola HikariCP, cold-start Neona),
#  - --interval=30s --timeout=5s --retries=3 -> kontener oznaczony jako
#    "unhealthy" dopiero po ~90s ciaglej niedostepnosci, czyli odpornie na
#    pojedyncze przerwy sieciowe do Neona,
#  - wget z BusyBoxa jest w obrazie alpine natywnie - nie ma potrzeby
#    doinstalowywac curla i powiekszac obrazu.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider \
      http://127.0.0.1:8081/actuator/health/readiness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]