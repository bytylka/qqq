package org.example.kyrcah;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int movieId;
    private String userLogin;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;
    public Review(int movieId, String userLogin, int rating, String reviewText) {
        this.movieId = movieId;
        this.userLogin = userLogin;
        this.rating = rating;
        this.reviewText = reviewText;
    }
    public Review(int id, int movieId, String userLogin, int rating, String reviewText, LocalDateTime createdAt) {
        this.id = id;
        this.movieId = movieId;
        this.userLogin = userLogin;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }
    public String getUserLogin() { return userLogin; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public String toFormattedString() {
        return String.format("[%d/10] Пользователь: %s\n\"%s\"\n-------------------\n",
                rating, userLogin, reviewText);
    }
}