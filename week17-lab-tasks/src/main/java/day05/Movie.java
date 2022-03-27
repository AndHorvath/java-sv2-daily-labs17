package day05;

import java.time.LocalDate;

public class Movie {

    // --- attributes ---------------------------------------------------------

    private Long id;
    private String title;
    private LocalDate localDate;
    private double averageRating;

    // --- constructors -------------------------------------------------------

    public Movie(Long id, String title, LocalDate localDate) {
        this.id = id;
        this.title = title;
        this.localDate = localDate;
        this.averageRating = 0.0;
    }

    // --- getters and setters ------------------------------------------------

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getLocalDate() { return localDate; }
    public double getAverageRating() { return averageRating; }

    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    // --- public methods -----------------------------------------------------

    @Override
    public String toString() {
        return
            "Movie (id: " + id + ") " +
            "{ title: " + title + ", release date: " + localDate + ", average rating: " + averageRating + " }";
    }
}