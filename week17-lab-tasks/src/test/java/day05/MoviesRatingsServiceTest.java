package day05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MoviesRatingsServiceTest {

    MariaDbDataSource mariaDbDataSource;
    Flyway flyway;

    ActorsRepository actorsRepository;
    MoviesRepository moviesRepository;
    ActorsMoviesRepository actorsMoviesRepository;
    RatingsRepository ratingsRepository;

    ActorsMoviesService actorsMoviesService;
    MoviesRatingsService moviesRatingsService;

    Throwable exception;
    double epsilon;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource(mariaDbDataSource);

        flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        actorsRepository = new ActorsRepository(mariaDbDataSource);
        moviesRepository = new MoviesRepository(mariaDbDataSource);
        ratingsRepository = new RatingsRepository(mariaDbDataSource);
        actorsMoviesRepository = new ActorsMoviesRepository(mariaDbDataSource);

        actorsMoviesService = new ActorsMoviesService(actorsRepository, moviesRepository, actorsMoviesRepository);
        moviesRatingsService = new MoviesRatingsService(moviesRepository, ratingsRepository);

        epsilon = Math.pow(10, -5);
    }

    @Test
    void getMoviesRepositoryTest() {
        assertEquals(moviesRepository, moviesRatingsService.getMoviesRepository());
    }

    @Test
    void getRatingsRepositoryTest() {
        assertEquals(ratingsRepository, moviesRatingsService.getRatingsRepository());
    }

    @Test
    void addRatingsToMovieTest() {
        actorsMoviesService.insertMovieWithActors(
            "Title A", LocalDate.of(2010, 10, 20), List.of("John Doe", "Jane Doe"));
        actorsMoviesService.insertMovieWithActors(
            "Title B", LocalDate.of(2020, 10, 10), List.of("Jack Doe", "Jane Doe"));

        moviesRatingsService.addRatingsToMovie("Title A", 5, 4);
        moviesRatingsService.addRatingsToMovie("Title B", 4, 5);
        moviesRatingsService.addRatingsToMovie("Title A", 4, 4);
        moviesRatingsService.addRatingsToMovie("Title B", 5, 4, 6);

        assertEquals(
            moviesRepository.findMovieByTitle("Title A").orElseThrow().getId(),
            ratingsRepository.findRatingById(1L).orElseThrow().getMovieId());
        assertEquals(5, ratingsRepository.findRatingById(1L).orElseThrow().getRatingValue());
        assertEquals(
            moviesRepository.findMovieByTitle("Title A").orElseThrow().getId(),
            ratingsRepository.findRatingById(2L).orElseThrow().getMovieId());
        assertEquals(4, ratingsRepository.findRatingById(2L).orElseThrow().getRatingValue());
        assertEquals(
            moviesRepository.findMovieByTitle("Title A").orElseThrow().getId(),
            ratingsRepository.findRatingById(5L).orElseThrow().getMovieId());
        assertEquals(4, ratingsRepository.findRatingById(5L).orElseThrow().getRatingValue());
        assertEquals(
            moviesRepository.findMovieByTitle("Title A").orElseThrow().getId(),
            ratingsRepository.findRatingById(6L).orElseThrow().getMovieId());
        assertEquals(4, ratingsRepository.findRatingById(6L).orElseThrow().getRatingValue());

        assertEquals(
            moviesRepository.findMovieByTitle("Title B").orElseThrow().getId(),
            ratingsRepository.findRatingById(3L).orElseThrow().getMovieId());
        assertEquals(4, ratingsRepository.findRatingById(3L).orElseThrow().getRatingValue());
        assertEquals(
            moviesRepository.findMovieByTitle("Title B").orElseThrow().getId(),
            ratingsRepository.findRatingById(4L).orElseThrow().getMovieId());
        assertEquals(5, ratingsRepository.findRatingById(4L).orElseThrow().getRatingValue());

        assertEquals(4.25, moviesRepository.getAverageRatingByTitle("Title A"), epsilon);
        assertEquals(4.5, moviesRepository.getAverageRatingByTitle("Title B"), epsilon);

        assertEquals(Optional.empty(), ratingsRepository.findRatingById(7L));

        exception = assertThrows(
            IllegalArgumentException.class, () -> moviesRatingsService.addRatingsToMovie("Title C", 5));
        assertEquals("No movie with specified title.", exception.getMessage());
    }

    // --- private methods ----------------------------------------------------

    private void parametrizeDataSource(MariaDbDataSource mariaDbDataSource) {
        try {
            mariaDbDataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors_test?useUnicode=true");
            mariaDbDataSource.setUserName("root");
            mariaDbDataSource.setPassword("root");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot reach database.", sqlException);
        }
    }
}