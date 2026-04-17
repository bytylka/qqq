package org.example.kyrcah;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.sql.*;

public class HelloController {

    public static HelloController instance;

    @FXML private ListView<Movie> movieListView;
    @FXML private VBox detailsPane;
    @FXML private Label titleLabel, descriptionLabel, ratingLabel, userLabel;
    @FXML private TextArea reviewsArea, myReviewArea;
    @FXML private Slider ratingSlider;
    @FXML private TabPane tabPane;
    @FXML private TextField searchField;
    @FXML private Button btnLogin, btnRegister, btnProfile, btnLogout;
    @FXML private ImageView posterImageView;

    private ObservableList<Movie> allMovies = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        instance = this;

        movieListView.setCellFactory(param -> new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        movieListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showMovieDetails(newVal);
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            Movie selected = movieListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if ("Отзывы".equals(newTab.getText())) loadReviews(selected.getId());
            else if ("Оценить".equals(newTab.getText())) loadMyReview(selected.getId());
        });

        updateUIForUser();
        loadMovies();

        FilteredList<Movie> filtered = new FilteredList<>(allMovies, p -> true);
        movieListView.setItems(filtered);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            filtered.setPredicate(movie -> newVal == null || newVal.isEmpty() ||
                    movie.getTitle().toLowerCase().contains(newVal.toLowerCase()));
        });
    }

    public void updateUIForUser() {
        boolean logged = CurrentUser.isLoggedIn();
        userLabel.setText(CurrentUser.getUsername());
        btnLogin.setVisible(!logged);
        btnRegister.setVisible(!logged);
        btnProfile.setVisible(logged);
        btnLogout.setVisible(logged);
    }

    private void loadMovies() {
        allMovies.clear();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY title")) {
            while (rs.next()) {
                allMovies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("average_rating"),
                        rs.getString("poster_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMovieDetails(Movie movie) {
        detailsPane.setVisible(true);
        titleLabel.setText(movie.getTitle());
        descriptionLabel.setText(movie.getDescription());
        ratingLabel.setText(String.format("Рейтинг: %.2f / 10", movie.getRating()));

        // Постер с рамкой
        Image image = new Image(movie.getPosterUrl(), true);
        posterImageView.setImage(image);
    }

    @FXML
    private void handleSubmitReview() {
        Movie selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите фильм!");
            return;
        }
        if (!CurrentUser.isLoggedIn()) {
            showAlert("Ошибка", "Войдите в аккаунт!");
            return;
        }
        if (myReviewArea.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Напишите отзыв!");
            return;
        }

        String sql = "CALL add_or_update_review(?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, CurrentUser.getId());
            pstmt.setInt(2, selected.getId());
            pstmt.setInt(3, (int) ratingSlider.getValue());
            pstmt.setString(4, myReviewArea.getText().trim());
            pstmt.execute();

            Platform.runLater(() -> {
                myReviewArea.clear();
                ratingSlider.setValue(5);
                loadMovies();
                loadReviews(selected.getId());
                loadMyReview(selected.getId());
                showAlert("Успех", "Отзыв сохранён!");
            });
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось сохранить: " + e.getMessage());
        }
    }

    private void loadReviews(int movieId) {
        reviewsArea.clear();
        String sql = "SELECT u.username, r.rating, r.review_text " +
                "FROM reviews r JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.movie_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reviewsArea.appendText(String.format("[%d/10] %s — %s\n\n",
                        rs.getInt("rating"), rs.getString("username"), rs.getString("review_text")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadMyReview(int movieId) {
        if (!CurrentUser.isLoggedIn()) return;
        myReviewArea.clear();
        ratingSlider.setValue(5);

        String sql = "SELECT rating, review_text FROM reviews WHERE movie_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, CurrentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ratingSlider.setValue(rs.getInt("rating"));
                myReviewArea.setText(rs.getString("review_text"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void openLogin() { openWindow("login-view.fxml", "Вход"); }
    @FXML private void openRegister() { openWindow("register-view.fxml", "Регистрация"); }

    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            Scene scene = new Scene(loader.load());
            var css = getClass().getResource("/org/example/kyrcah/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Мой профиль");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        CurrentUser.logout();
        updateUIForUser();
        detailsPane.setVisible(false);
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            var css = getClass().getResource("/org/example/kyrcah/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenLink() {
        try {
            String title = titleLabel.getText().replace(" ", "+");
            Desktop.getDesktop().browse(new URI("https://www.google.com/search?q=смотреть+" + title));
        } catch (Exception e) { e.printStackTrace(); }
    }
}