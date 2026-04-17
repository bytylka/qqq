package org.example.kyrcah;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleRegister() {
        String user = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля!");
            return;
        }

        String hashed = PasswordHasher.hashPassword(pass);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (username, email, password_hash, is_active) VALUES (?, ?, ?, TRUE)")) {

            pstmt.setString(1, user);
            pstmt.setString(2, email);
            pstmt.setString(3, hashed);
            pstmt.executeUpdate();

            // Получаем данные пользователя
            try (PreparedStatement select = conn.prepareStatement(
                    "SELECT user_id, username, email FROM users WHERE username = ?")) {
                select.setString(1, user);
                ResultSet rs = select.executeQuery();
                if (rs.next()) {
                    CurrentUser.login(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"));
                }
            }

            closeRegisterWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось зарегистрировать: " + e.getMessage());
        }
    }

    private void closeRegisterWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
        if (HelloController.instance != null) HelloController.instance.updateUIForUser();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}