package day05;

import java.util.Optional;
import java.util.function.Supplier;

public class MoviesRatingsService {

    // --- attributes ---------------------------------------------------------

    private MoviesRepository moviesRepository;
    private RatingsRepository ratingsRepository;

    // --- constructors -------------------------------------------------------

    public MoviesRatingsService(MoviesRepository moviesRepository, RatingsRepository ratingsRepository) {
        this.moviesRepository = moviesRepository;
        this.ratingsRepository = ratingsRepository;
    }

    // --- getters and setters ------------------------------------------------

    public MoviesRepository getMoviesRepository() { return moviesRepository; }
    public RatingsRepository getRatingsRepository() { return ratingsRepository; }

    // --- public methods -----------------------------------------------------

    public void addRatingsToMovie(String movieTitle, Integer... ratings) {
        Optional<Movie> movie = moviesRepository.findMovieByTitle(movieTitle);
        Long movieId = movie.orElseThrow(throwArgumentException()).getId();

        ratingsRepository.insertRatingForMovie(movieId, ratings);
        double averageRating = ratingsRepository.calculateAverageRatingByMovieId(movieId);
        moviesRepository.updateAverageRatingForMovie(movieTitle, averageRating);
    }

    // --- private methods ----------------------------------------------------

    private Supplier<IllegalArgumentException> throwArgumentException() {
        return () -> new IllegalArgumentException("No movie with specified title.");
    }
}