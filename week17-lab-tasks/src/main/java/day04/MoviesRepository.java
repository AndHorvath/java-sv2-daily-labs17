package day04;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    // --- attributes ---------------------------------------------------------

    DataSource dataSource;

    // --- constructors -------------------------------------------------------

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- getters and setters ------------------------------------------------

    public DataSource getDataSource() { return dataSource; }

    // --- public methods -----------------------------------------------------

    public Long saveMovie(String title, LocalDate releaseDate) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "INSERT INTO movies (title, release_date) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS
            )
        ) {
            preparedStatement.setString(1, title);
            preparedStatement.setDate(2, Date.valueOf(releaseDate));
            preparedStatement.executeUpdate();
            return getMovieId(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot connect.", sqlException);
        }
    }

    public List<Movie> findAllMovies() {
        List<Movie> allMovies = new ArrayList<>();
        //language=SQL
        String query = "select * from movies";
        try (
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)
        ) {
            updateAllMovies(allMovies, resultSet);
        } catch (SQLException sqlException) {
            throw new  IllegalStateException("Cannot query.", sqlException);
        }
        return allMovies;
    }

    // --- private methods ----------------------------------------------------

    private Long getMovieId(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new IllegalStateException("Cannot get id.");
        }
    }

    private void updateAllMovies(List<Movie> allMovies, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            allMovies.add(new Movie(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getDate("release_date").toLocalDate()
            ));
        }
    }
}