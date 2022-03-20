package day02;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        MariaDbDataSource mariaDbDataSource = new MariaDbDataSource();
        new Main().parameterizeDataSource(mariaDbDataSource);

        Flyway flyway = Flyway.configure().dataSource(mariaDbDataSource).load();
        flyway.migrate();

        ActorsRepository actorsRepository = new ActorsRepository(mariaDbDataSource);
        actorsRepository.saveActor("John Doe");
        actorsRepository.saveActor("Jane Doe");
        actorsRepository.saveActor("Jack Doe");

        MoviesRepository moviesRepository = new MoviesRepository(mariaDbDataSource);
        moviesRepository.saveMovie("Titanic", LocalDate.of(1997, 12, 11));
        moviesRepository.saveMovie("Lord of the Rings", LocalDate.of(2000, 12, 23));

        System.out.println(actorsRepository.findActorsWithPrefix("Ja"));
        System.out.println(moviesRepository.findAllMovies());
    }

    // --- private methods ----------------------------------------------------

    private void parameterizeDataSource(MariaDbDataSource mariaDbDataSource) {
        try {
            mariaDbDataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors?useUnicode=true");
            mariaDbDataSource.setUserName("root");
            mariaDbDataSource.setPassword("root");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot reach database.", sqlException);
        }
    }
}