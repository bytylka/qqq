package org.example.kyrcah;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/moviedb";
    private static final String USER = "moviedb";
    private static final String PASSWORD = "user";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}