package org.example.kyrcah;

public class Movie {
    private int id;
    private String title;
    private String description;
    private double rating;
    private String posterUrl;

    public Movie(int id, String title, String description, double rating, String posterUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public String getPosterUrl() {
        return posterUrl != null && !posterUrl.isEmpty()
                ? posterUrl
                : "https://picsum.photos/id/1/300/400";
    }
}