package org.example.kyrcah;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.*;

public class ProfileController {

    @FXML private TextField fullNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextArea statusArea;
    @FXML private TextField genreField;
    @FXML private TextField avatarUrlField;
    @FXML private ImageView avatarImageView;
    @FXML private Label reviewsCountLabel;

    @FXML
    public void initialize() {
        if (CurrentUser.isLoggedIn()) {
            loadProfile();
        } else {
            showAlert("Ошибка", "Вы не авторизованы!");
        }
    }

    private void loadProfile() {
        try (Connection conn = DatabaseConfig.getConnection()) {

            // 1. Получаем или создаём профиль
            PreparedStatement check = conn.prepareStatement(
                    "INSERT INTO profiles (user_id, full_name, avatar_url) " +
                            "VALUES (?, ?, ?) ON CONFLICT (user_id) DO NOTHING");
            check.setInt(1, CurrentUser.getId());
            check.setString(2, CurrentUser.getUsername());
            check.setString(3, "https://i.pravatar.cc/300?img=" + (CurrentUser.getId() % 70 + 1));
            check.executeUpdate();

            // 2. Загружаем данные
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT full_name, birth_date, status, favorite_genre, avatar_url, " +
                            "(SELECT COUNT(*) FROM reviews WHERE user_id = ?) as review_count " +
                            "FROM profiles WHERE user_id = ?");

            pstmt.setInt(1, CurrentUser.getId());
            pstmt.setInt(2, CurrentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fullNameField.setText(rs.getString("full_name"));
                if (rs.getDate("birth_date") != null) {
                    birthDatePicker.setValue(rs.getDate("birth_date").toLocalDate());
                }
                statusArea.setText(rs.getString("status"));
                genreField.setText(rs.getString("favorite_genre"));

                String avatar = rs.getString("avatar_url");
                if (avatar != null && !avatar.isEmpty()) {
                    avatarUrlField.setText(avatar);
                    avatarImageView.setImage(new Image(avatar, true));
                }

                reviewsCountLabel.setText("Отзывов оставлено: " + rs.getInt("review_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка загрузки", e.getMessage());
        }
    }

    @FXML
    private void handleSaveProfile() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO profiles (user_id, full_name, birth_date, status, favorite_genre, avatar_url) " +
                             "VALUES (?, ?, ?, ?, ?, ?) " +
                             "ON CONFLICT (user_id) DO UPDATE SET " +
                             "full_name = EXCLUDED.full_name, " +
                             "birth_date = EXCLUDED.birth_date, " +
                             "status = EXCLUDED.status, " +
                             "favorite_genre = EXCLUDED.favorite_genre, " +
                             "avatar_url = EXCLUDED.avatar_url")) {

            pstmt.setInt(1, CurrentUser.getId());
            pstmt.setString(2, fullNameField.getText().trim());
            pstmt.setDate(3, birthDatePicker.getValue() != null ? java.sql.Date.valueOf(birthDatePicker.getValue()) : null);
            pstmt.setString(4, statusArea.getText().trim());
            pstmt.setString(5, genreField.getText().trim());
            pstmt.setString(6, avatarUrlField.getText().trim());

            pstmt.executeUpdate();
            loadProfile(); // обновляем сразу
            showAlert("Успех", "Профиль сохранён!");
        } catch (Exception e) {
            showAlert("Ошибка сохранения", e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        ((Stage) fullNameField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}