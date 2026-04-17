package org.example.kyrcah;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String user = loginField.getText().trim();
        String pass = passwordField.getText();
        String hashedInput = PasswordHasher.hashPassword(pass);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT user_id, username, email FROM users WHERE username = ? AND password_hash = ?")) {

            pstmt.setString(1, user);
            pstmt.setString(2, hashedInput);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CurrentUser.login(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"));
                closeLoginWindow();
            } else {
                errorLabel.setText("Неверный логин или пароль");
            }
        } catch (Exception e) {
            e.printStackTrace();   // оставил, чтобы не было лишних предупреждений
            errorLabel.setText("Ошибка подключения к базе");
        }
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.close();
        if (HelloController.instance != null) {
            HelloController.instance.updateUIForUser();   // теперь работает
        }
    }

    @FXML
    private void openRegistration() throws Exception {
        Stage stage = (Stage) loginField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);

        var cssResource = getClass().getResource("/org/example/kyrcah/style.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        Stage regStage = new Stage();
        regStage.setScene(scene);
        regStage.setTitle("Регистрация");
        regStage.show();
        stage.close();
    }
}