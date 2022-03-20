package day02;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MoviesRepositoryTest {

    MoviesRepository moviesRepository;
    MariaDbDataSource mariaDbDataSource;
    Flyway flyway;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource(mariaDbDataSource);

        flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        moviesRepository = new MoviesRepository(mariaDbDataSource);
    }

    @Test
    void getDataSourceTest() {
        assertEquals(mariaDbDataSource, moviesRepository.getDataSource());
    }

    @Test
    void saveMovieTest() {
        moviesRepository.saveMovie("Title", LocalDate.of(2010, 10, 20));
        assertEquals(1, moviesRepository.findAllMovies().size());
        assertEquals(
            "Movie (id: 1) { title: Title, release date: 2010-10-20 }",
            moviesRepository.findAllMovies().get(0).toString()
        );
    }

    @Test
    void findAllMoviesTest() {
        moviesRepository.saveMovie("Title A", LocalDate.of(2010, 10, 20));
        moviesRepository.saveMovie("Title B", LocalDate.of(2020, 10, 10));

        assertEquals(2, moviesRepository.findAllMovies().size());

        assertEquals(1L, moviesRepository.findAllMovies().get(0).getId());
        assertEquals("Title A", moviesRepository.findAllMovies().get(0).getTitle());
        assertEquals(LocalDate.of(2010, 10, 20), moviesRepository.findAllMovies().get(0).getLocalDate());

        assertEquals(2L, moviesRepository.findAllMovies().get(1).getId());
        assertEquals("Title B", moviesRepository.findAllMovies().get(1).getTitle());
        assertEquals(LocalDate.of(2020, 10, 10), moviesRepository.findAllMovies().get(1).getLocalDate());
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