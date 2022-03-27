package day05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RatingsRepositoryTest {

    RatingsRepository ratingsRepository;
    MariaDbDataSource mariaDbDataSource;
    Flyway flyway;

    double epsilon;
    Throwable exception;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource();

        flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        ratingsRepository = new RatingsRepository(mariaDbDataSource);

        epsilon = Math.pow(10, -5);
    }

    @Test
    void getDataSourceTest() {
        assertEquals(mariaDbDataSource, ratingsRepository.getDataSource());
    }

    @Test
    void insertRatingForMovieTest() {
        ratingsRepository.insertRatingForMovie(2L, 5, 4, 3, 5);
        assertEquals(2L, ratingsRepository.findRatingById(1L).orElseThrow().getMovieId());
        assertEquals(5, ratingsRepository.findRatingById(1L).orElseThrow().getRatingValue());
        assertEquals(2L, ratingsRepository.findRatingById(2L).orElseThrow().getMovieId());
        assertEquals(4, ratingsRepository.findRatingById(2L).orElseThrow().getRatingValue());
        assertEquals(2L, ratingsRepository.findRatingById(3L).orElseThrow().getMovieId());
        assertEquals(3, ratingsRepository.findRatingById(3L).orElseThrow().getRatingValue());
        assertEquals(2L, ratingsRepository.findRatingById(4L).orElseThrow().getMovieId());
        assertEquals(5, ratingsRepository.findRatingById(4L).orElseThrow().getRatingValue());

        ratingsRepository.insertRatingForMovie(2L, 5, 4, 3, 6);
        assertEquals(Optional.empty(), ratingsRepository.findRatingById(5L));
    }

    @Test
    void findRatingByIdTest() {
        ratingsRepository.insertRatingForMovie(2L, 5);
        ratingsRepository.insertRatingForMovie(4L, 4);
        ratingsRepository.insertRatingForMovie(2L, 4);
        assertEquals(2L, ratingsRepository.findRatingById(1L).orElseThrow().getMovieId());
        assertEquals(2L, ratingsRepository.findRatingById(3L).orElseThrow().getMovieId());
        assertEquals(5, ratingsRepository.findRatingById(1L).orElseThrow().getRatingValue());
        assertEquals(4, ratingsRepository.findRatingById(3L).orElseThrow().getRatingValue());
        assertEquals(Optional.empty(), ratingsRepository.findRatingById(4L));
    }

    @Test
    void calculateAverageRatingByMovieIdTest() {
        ratingsRepository.insertRatingForMovie(2L, 5, 3);
        ratingsRepository.insertRatingForMovie(4L, 5, 5);
        ratingsRepository.insertRatingForMovie(2L, 2, 5);
        assertEquals(3.75, ratingsRepository.calculateAverageRatingByMovieId(2L), epsilon);

        exception = assertThrows(
            IllegalArgumentException.class, () -> ratingsRepository.calculateAverageRatingByMovieId(1L));
        assertEquals("No ratings for specified movie id in database: 1", exception.getMessage());
    }

    // --- private methods ----------------------------------------------------

    private void parametrizeDataSource() {
        try {
            mariaDbDataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors_test?useUnicode=true");
            mariaDbDataSource.setUserName("root");
            mariaDbDataSource.setPassword("root");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot reach database.", sqlException);
        }
    }
}