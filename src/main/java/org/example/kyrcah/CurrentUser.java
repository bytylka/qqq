package org.example.kyrcah;

public class CurrentUser {
    private static int id = -1;
    private static String username = "Гость";
    private static String email = "";

    public static void login(int userId, String userName, String mail) {
        id = userId;
        username = userName;
        email = mail;
    }

    public static void logout() {
        id = -1;
        username = "Гость";
        email = "";
    }

    public static int getId() { return id; }
    public static String getUsername() { return username; }
    public static boolean isLoggedIn() { return id != -1; }
}