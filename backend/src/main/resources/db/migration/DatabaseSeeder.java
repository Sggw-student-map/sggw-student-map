import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DatabaseSeeder – populates the Neon PostgreSQL database with sample data.
 *
 * Dependencies (add to pom.xml / build.gradle):
 *   PostgreSQL JDBC driver: org.postgresql:postgresql:42.7.3
 *
 * Usage:
 *   Set the three environment variables below, then run main().
 *
 *   NEON_URL  – jdbc:postgresql://<host>/<db>?sslmode=require
 *   NEON_USER – your Neon username
 *   NEON_PASS – your Neon password
 */




public class DatabaseSeeder {

    // ── Connection settings (read from environment variables) ─────────────
    private static final String URL  = System.getenv().getOrDefault("NEON_URL",  "jdbc:postgresql://localhost:5432/mydb?sslmode=require");
    private static final String USER = System.getenv().getOrDefault("NEON_USER", "postgres");
    private static final String PASS = System.getenv().getOrDefault("NEON_PASS", "password");

    // Bcrypt hash of "password123"
    private static final String HASHED_PW = "$2a$10$wWfowRyDKP4xLxVvQtzpQ.h5Uk7XtWVVg6f4qfM4E5u9bLxVwct6S";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            System.out.println("Connected to Neon database.");

            seedUsers(conn);
            seedPlaces(conn);
            seedFriendships(conn);
            seedEvents(conn);
            seedUsersOnEvents(conn);
            seedFavorites(conn);
            seedReviews(conn);
            seedRoutes(conn);
            seedRoutePoints(conn);

            conn.commit();
            System.out.println("Seed completed successfully.");

        } catch (SQLException e) {
            System.err.println("Seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Users ─────────────────────────────────────────────────────────────
    private static void seedUsers(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO users (first_name, last_name, username, password, email, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (username) DO NOTHING
                """;

        Object[][] users = {
            {"Jan",    "Kowalski",   "jkowalski",   HASHED_PW, "jan.kowalski@sggw.edu.pl",    true},
            {"Anna",   "Nowak",      "anowak",      HASHED_PW, "anna.nowak@sggw.edu.pl",       true},
            {"Piotr",  "Wisniewski", "pwisniewski", HASHED_PW, "piotr.wisniewski@sggw.edu.pl", true},
            {"Marta",  "Wojcik",     "mwojcik",     HASHED_PW, "marta.wojcik@sggw.edu.pl",     true},
            {"Tomasz", "Kaminski",   "tkaminski",   HASHED_PW, "tomasz.kaminski@sggw.edu.pl",  false},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] u : users) {
                ps.setString(1, (String)  u[0]);
                ps.setString(2, (String)  u[1]);
                ps.setString(3, (String)  u[2]);
                ps.setString(4, (String)  u[3]);
                ps.setString(5, (String)  u[4]);
                ps.setBoolean(6, (boolean) u[5]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ users");
    }

    // ── Places ────────────────────────────────────────────────────────────
    private static void seedPlaces(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO places (name, latitude, longitude, description)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        Object[][] places = {
            {"Biblioteka Glowna SGGW",           52.16198, 21.04731, "Glowna biblioteka akademicka kampusu SGGW z bogatym ksiegozbiorem i strefami cichej nauki."},
            {"Budynek 34 - Wydzial Informatyki",  52.16087, 21.04512, "Siedziba Wydzialu Informatyki i Inzynierii Systemow z nowoczesnymi laboratoriami komputerowymi."},
            {"Stolowka Studencka",                52.16321, 21.04603, "Studencka stołowka oferujaca codzienne obiady w przystepnych cenach."},
            {"Dom Studenta SGGW - DS1",           52.16450, 21.04820, "Akademik DS1 zlokalizowany bezposrednio na terenie kampusu."},
            {"Rektorat SGGW",                     52.16255, 21.04394, "Glowny budynek administracyjny uczelni, siedziba wladz rektorskich."},
            {"Stadion SGGW",                      52.16510, 21.04270, "Obiekt sportowy z boiskami do pilki noznej, bieznia i kortami tenisowymi."},
            {"Kawiarnia przy Bibliotece",         52.16210, 21.04760, "Przytulna kawiarnia w sasiedztwie biblioteki, idealna na przerwe miedzy zajęciami."},
            {"Przystanek Ursynow Polnocny",       52.16050, 21.04900, "Przystanek autobusowy przy wejsciu na kampus SGGW od strony polnocnej."},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] p : places) {
                ps.setString(1, (String) p[0]);
                ps.setDouble(2, (double) p[1]);
                ps.setDouble(3, (double) p[2]);
                ps.setString(4, (String) p[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ places");
    }

    // ── Friendships ───────────────────────────────────────────────────────
    private static void seedFriendships(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO friendships (user_id1, user_id2, status_of_friendship)
                VALUES (?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        int[][] pairs = {{1,2}, {1,3}, {2,4}, {3,5}, {4,5}};
        String[] statuses = {"accepted", "accepted", "accepted", "pending", "pending"};

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < pairs.length; i++) {
                ps.setInt(1, pairs[i][0]);
                ps.setInt(2, pairs[i][1]);
                ps.setString(3, statuses[i]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ friendships");
    }

    // ── Events ────────────────────────────────────────────────────────────
    private static void seedEvents(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO eventy (name_of_event, id_place, date_of_event, comment, organizer_id)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        Object[][] events = {
            {"Inauguracja roku akademickiego", 5, LocalDateTime.of(2025,10, 1,10,0), "Uroczysta inauguracja roku akademickiego 2025/2026",       1},
            {"Hackathon SGGW",                 2, LocalDateTime.of(2025,11,15, 9,0), "Ogolnouczelniany hackathon dla studentow informatyki",     3},
            {"Dzien Sportu",                   6, LocalDateTime.of(2025, 5,20,12,0), "Zawody sportowe dla studentow i pracownikow",              2},
            {"Spotkanie kola naukowego",        2, LocalDateTime.of(2025, 4,20,18,0), "Miesieczne spotkanie kola naukowego AI",                  3},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] e : events) {
                ps.setString(1,    (String) e[0]);
                ps.setInt(2,       (int)    e[1]);
                ps.setTimestamp(3, Timestamp.valueOf((LocalDateTime) e[2]));
                ps.setString(4,    (String) e[3]);
                ps.setInt(5,       (int)    e[4]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ eventy");
    }

    // ── Users on Events ───────────────────────────────────────────────────
    private static void seedUsersOnEvents(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO users_on_events (id_eventu, id_users)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        int[][] pairs = {
            {1,1},{1,2},{1,3},{1,4},
            {2,3},{2,1},
            {3,2},{3,4},{3,5},
            {4,3},{4,1},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int[] p : pairs) {
                ps.setInt(1, p[0]);
                ps.setInt(2, p[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ users_on_events");
    }

    // ── Favorites ─────────────────────────────────────────────────────────
    private static void seedFavorites(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO favorites (id, id_place)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        int[][] pairs = {
            {1,1},{1,7},
            {2,3},{2,1},
            {3,2},{3,6},
            {4,4},{4,3},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int[] p : pairs) {
                ps.setInt(1, p[0]);
                ps.setInt(2, p[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ favorites");
    }

    // ── Reviews ───────────────────────────────────────────────────────────
    private static void seedReviews(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO reviews (place_id, user_id, rating, comment)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        Object[][] reviews = {
            {1, 1, 5, "Swietna biblioteka, duzo miejsc do nauki i cisza."},
            {1, 2, 4, "Dobry dostep do zasobow, czasem trudno znalezc wolne miejsce."},
            {2, 3, 4, "Nowoczesne sale, dobry sprzet komputerowy."},
            {2, 1, 3, "Klimatyzacja czasem nie dziala, ale ogolnie ok."},
            {3, 2, 5, "Smaczne jedzenie w rozsadnej cenie dla studenta."},
            {3, 4, 3, "Dlugie kolejki w porze lunchu."},
            {4, 3, 4, "Pokoje czyste, blisko kampusu, polecam."},
            {5, 2, 5, "Reprezentacyjny budynek, latwo trafic."},
            {6, 4, 4, "Dobry obiekt sportowy, duzo mozliwosci."},
            {7, 1, 5, "Najlepsza kawa na kampusie!"},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] r : reviews) {
                ps.setInt(1,    (int)    r[0]);
                ps.setInt(2,    (int)    r[1]);
                ps.setInt(3,    (int)    r[2]);
                ps.setString(4, (String) r[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ reviews");
    }

    // ── Routes ────────────────────────────────────────────────────────────
    private static void seedRoutes(Connection conn) throws SQLException {
        // One sample route; place_id here is the route's "starting" place reference.
        String sql = "INSERT INTO routes (place_id) VALUES (?) ON CONFLICT DO NOTHING";

        int[] startPlaces = {8, 5, 2, 1};

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int pid : startPlaces) {
                ps.setInt(1, pid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ routes");
    }

    // ── Route Points ──────────────────────────────────────────────────────
    private static void seedRoutePoints(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO route_points (route_id, place_id, stop_order)
                VALUES (?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        // Route 1: Przystanek → Rektorat → Informatyka → Biblioteka → Kawiarnia → Stolowka
        int[][] points = {
            {1, 8, 1},
            {1, 5, 2},
            {1, 2, 3},
            {1, 1, 4},
            {1, 7, 5},
            {1, 3, 6},
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int[] pt : points) {
                ps.setInt(1, pt[0]);
                ps.setInt(2, pt[1]);
                ps.setInt(3, pt[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("  ✓ route_points");
    }
}
