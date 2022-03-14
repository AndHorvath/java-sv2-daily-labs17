package day01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;

class ActorsRepositoryTest {

    ActorsRepository actorsRepository;
    DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new MariaDbDataSource();
        actorsRepository = new ActorsRepository(dataSource);
    }

    @Test
    void getDataSourceTest() {
        assertEquals(dataSource, actorsRepository.getDataSource());
    }
}