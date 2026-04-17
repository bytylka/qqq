package org.example.kyrcah;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KyrcahApplicationTests {
    // Тест passwordhasher
    @Test
    @Order(1)
    void testPasswordHashNotEmpty() {
        String hash = PasswordHasher.hashPassword("pass123");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
    @Test
    @Order(2)
    void testPasswordHashIsConsistent() {
        String pass = "test";
        assertEquals(PasswordHasher.hashPassword(pass), PasswordHasher.hashPassword(pass));
    }
    @Test
    @Order(3)
    void testDifferentPasswordsGiveDifferentHashes() {
        assertNotEquals(PasswordHasher.hashPassword("123"), PasswordHasher.hashPassword("321"));
    }
    // Тесты currentuser
    @Test
    @Order(4)
    void testCurrentUserLogin() {
        CurrentUser.login(10, "dev_user", "dev@mail.ru");
        assertTrue(CurrentUser.isLoggedIn());
        assertEquals(10, CurrentUser.getId());
        assertEquals("dev_user", CurrentUser.getUsername());
    }
    @Test
    @Order(5)
    void testCurrentUserLogout() {
        CurrentUser.login(1, "admin", "admin@mail.ru");
        CurrentUser.logout();
        assertFalse(CurrentUser.isLoggedIn());
        assertEquals(-1, CurrentUser.getId());
    }
    // Тесты databaseconfig
    @Test
    @Order(6)
    void testDatabaseConnection() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            assertNotNull(conn, "Проверь, запущен ли сервер PostgreSQL");
            assertFalse(conn.isClosed());
        }
    }
    @Test
    @Order(7)
    void testDatabaseQuery() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT 1")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }
    // Тесты моделей
    @Test
    @Order(8)
    void testMovieModel() {
        Movie movie = new Movie(1, "Inception", "Dream heist", 9.0, "url");
        assertEquals("Inception", movie.getTitle());
        assertEquals("Dream heist", movie.getDescription());
        assertEquals(9.0, movie.getRating());
    }
    @Test
    @Order(9)
    void testReviewModel() {
        Review review = new Review(101, "user_test", 5, "Very cool!");
        assertEquals("user_test", review.getUserLogin());
        assertEquals(5, review.getRating());
        assertEquals("Very cool!", review.getReviewText());
    }
    @Test
    @Order(10)
    void testReviewTextValidation() {
        String emptyText = "";
        assertTrue(emptyText.trim().isEmpty());
    }
    @AfterEach
    void tearDown() {
        CurrentUser.logout();
    }
}