package day05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ActorsMoviesRepositoryTest {

    ActorsMoviesRepository actorsMoviesRepository;
    MariaDbDataSource mariaDbDataSource;
    Flyway flyway;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource(mariaDbDataSource);

        flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        actorsMoviesRepository = new ActorsMoviesRepository(mariaDbDataSource);
    }

    @Test
    void getDataSourceTest() {
        assertEquals(mariaDbDataSource, actorsMoviesRepository.getDataSource());
    }

    @Test
    void insertActorAndMovieIdTest() {
        actorsMoviesRepository.insertActorAndMovieId(7L, 9L);

        assertEquals(7L, actorsMoviesRepository.getActorIdByPrimaryKey(1L).orElseThrow());
        assertEquals(9L, actorsMoviesRepository.getMovieIdByPrimaryKey(1L).orElseThrow());

        assertFalse(actorsMoviesRepository.getActorIdByPrimaryKey(2L).isPresent());
        assertFalse(actorsMoviesRepository.getMovieIdByPrimaryKey(2L).isPresent());
    }

    @Test
    void getActorIdByPrimaryKeyTest() {
        fillDatabase();
        assertEquals(5L, actorsMoviesRepository.getActorIdByPrimaryKey(2L).orElseThrow());
    }

    @Test
    void getMovieIdByPrimaryKeyTest() {
        fillDatabase();
        assertEquals(4L, actorsMoviesRepository.getMovieIdByPrimaryKey(3L).orElseThrow());
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

    private void fillDatabase() {
        actorsMoviesRepository.insertActorAndMovieId(1L, 2L);
        actorsMoviesRepository.insertActorAndMovieId(5L, 5L);
        actorsMoviesRepository.insertActorAndMovieId(9L, 4L);
    }
}