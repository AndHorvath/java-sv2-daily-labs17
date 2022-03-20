package day02;

import java.time.LocalDate;

public class Movie {

    // --- attributes ---------------------------------------------------------

    private Long id;
    private String title;
    private LocalDate localDate;

    // --- constructors -------------------------------------------------------

    public Movie(Long id, String title, LocalDate localDate) {
        this.id = id;
        this.title = title;
        this.localDate = localDate;
    }

    // --- getters and setters ------------------------------------------------

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getLocalDate() { return localDate; }

    // --- public methods -----------------------------------------------------

    @Override
    public String toString() {
        return "Movie (id: " + id + ") { title: " + title + ", release date: " + localDate + " }";
    }
}