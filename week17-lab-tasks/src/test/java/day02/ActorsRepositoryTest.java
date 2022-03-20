package day02;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
        actorsRepository.saveActor("John Doe");
        assertEquals(List.of("John Doe"), actorsRepository.findActorsWithPrefix("John"));
    }

    @Test
    void findActorsWithPrefixTest() {
        actorsRepository.saveActor("John Doe");
        actorsRepository.saveActor("Jane Doe");
        actorsRepository.saveActor("Jack Doe");
        assertEquals(Arrays.asList("Jack Doe", "Jane Doe"), actorsRepository.findActorsWithPrefix("Ja"));
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