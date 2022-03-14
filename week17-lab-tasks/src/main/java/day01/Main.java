package day01;

import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        MariaDbDataSource mariaDbDataSource = new MariaDbDataSource();
        new Main().parameterizeDataSource(mariaDbDataSource);
        ActorsRepository actorsRepository = new ActorsRepository(mariaDbDataSource);
        actorsRepository.saveActor("Judd Doe");
        System.out.println(actorsRepository.findActorsWithPrefix("Ji"));
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