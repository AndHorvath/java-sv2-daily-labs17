package day04;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActorsMoviesServiceTest {

    MariaDbDataSource mariaDbDataSource;

    ActorsRepository actorsRepository;
    MoviesRepository moviesRepository;
    ActorsMoviesRepository actorsMoviesRepository;

    ActorsMoviesService actorsMoviesService;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource(mariaDbDataSource);

        Flyway flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        actorsRepository = new ActorsRepository(mariaDbDataSource);
        moviesRepository = new MoviesRepository(mariaDbDataSource);
        actorsMoviesRepository = new ActorsMoviesRepository(mariaDbDataSource);

        actorsMoviesService =
            new ActorsMoviesService(actorsRepository, moviesRepository, actorsMoviesRepository);
    }

    @Test
    void getActorsRepositoryTest() {
        assertEquals(actorsRepository, actorsMoviesService.getActorsRepository());
    }

    @Test
    void getMoviesRepositoryTest() {
        assertEquals(moviesRepository, actorsMoviesService.getMoviesRepository());
    }

    @Test
    void getActorsMoviesRepositoryTest() {
        assertEquals(actorsMoviesRepository, actorsMoviesService.getActorsMoviesRepository());
    }

    @Test
    void insertMovieWithActorsTest() {
        actorsMoviesService.insertMovieWithActors(
            "Title A", LocalDate.of(2010, 10, 20), List.of("John Doe", "Jane Doe")
        );
        actorsMoviesService.insertMovieWithActors(
            "Title B", LocalDate.of(2020, 10, 10), List.of("Jack Doe", "Jane Doe")
        );
        assertEquals("John Doe", actorsRepository.findActorByName("John Doe").orElseThrow().getName());
        assertEquals("Jane Doe", actorsRepository.findActorByName("Jane Doe").orElseThrow().getName());
        assertEquals("Jack Doe", actorsRepository.findActorByName("Jack Doe").orElseThrow().getName());
        assertEquals(1L, actorsRepository.findActorByName("John Doe").orElseThrow().getId());
        assertEquals(2L, actorsRepository.findActorByName("Jane Doe").orElseThrow().getId());
        assertEquals(3L, actorsRepository.findActorByName("Jack Doe").orElseThrow().getId());

        assertEquals("Title A", moviesRepository.findAllMovies().get(0).getTitle());
        assertEquals("Title B", moviesRepository.findAllMovies().get(1).getTitle());
        assertEquals(LocalDate.of(2010, 10, 20), moviesRepository.findAllMovies().get(0).getLocalDate());
        assertEquals(LocalDate.of(2020, 10, 10), moviesRepository.findAllMovies().get(1).getLocalDate());
        assertEquals(1L, moviesRepository.findAllMovies().get(0).getId());
        assertEquals(2L, moviesRepository.findAllMovies().get(1).getId());

        assertEquals(1L, actorsMoviesRepository.getActorIdByPrimaryKey(1L).orElseThrow());
        assertEquals(1L, actorsMoviesRepository.getMovieIdByPrimaryKey(1L).orElseThrow());

        assertEquals(2L, actorsMoviesRepository.getActorIdByPrimaryKey(2L).orElseThrow());
        assertEquals(1L, actorsMoviesRepository.getMovieIdByPrimaryKey(2L).orElseThrow());

        assertEquals(3L, actorsMoviesRepository.getActorIdByPrimaryKey(3L).orElseThrow());
        assertEquals(2L, actorsMoviesRepository.getMovieIdByPrimaryKey(3L).orElseThrow());

        assertEquals(2L, actorsMoviesRepository.getActorIdByPrimaryKey(4L).orElseThrow());
        assertEquals(2L, actorsMoviesRepository.getMovieIdByPrimaryKey(4L).orElseThrow());
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