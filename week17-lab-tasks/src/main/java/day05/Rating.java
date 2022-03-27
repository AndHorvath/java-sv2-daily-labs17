package day05;

public class Rating {

    // --- attributes ---------------------------------------------------------

    private Long movieId;
    private int ratingValue;

    // --- constructors -------------------------------------------------------

    public Rating(Long movieId, int ratingValue) {
        this.movieId = movieId;
        this.ratingValue = ratingValue;
    }

    // --- getters and setters ------------------------------------------------

    public Long getMovieId() { return movieId; }
    public int getRatingValue() { return ratingValue; }
}