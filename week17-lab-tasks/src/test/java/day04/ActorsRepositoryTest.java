package day04;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ActorsRepositoryTest {

    ActorsRepository actorsRepository;
    MariaDbDataSource mariaDbDataSource;
    Flyway flyway;

    @BeforeEach
    void setUp() {
        mariaDbDataSource = new MariaDbDataSource();
        parametrizeDataSource(mariaDbDataSource);

        flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.clean();
        flyway.migrate();

        actorsRepository = new ActorsRepository(mariaDbDataSource);
    }

    @Test
    void getDataSourceTest() {
        assertEquals(mariaDbDataSource, actorsRepository.getDataSource());
    }

    @Test
    void saveActorTest() {
        assertEquals(1L, actorsRepository.saveActor("John Doe"));
        assertTrue(actorsRepository.findActorByName("John Doe").isPresent());
    }

    @Test
    void findActorsByNameTest() {
        actorsRepository.saveActor("John Doe");
        actorsRepository.saveActor("Jane Doe");
        actorsRepository.saveActor("Jack Doe");

        assertEquals(4L, actorsRepository.saveActor("Jack Doe"));
        assertEquals(3L, actorsRepository.findActorByName("Jack Doe").orElseThrow().getId());
        assertEquals("Jack Doe", actorsRepository.findActorByName("Jack Doe").orElseThrow().getName());

        assertFalse(actorsRepository.findActorByName("Jill Doe").isPresent());
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