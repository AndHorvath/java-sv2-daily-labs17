package day05;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                "INSERT INTO movies (title, release_date, average_rating) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            executeSave(preparedStatement, title, releaseDate);
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

    public Optional<Movie> findMovieByTitle(String title) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select * from movies where title = ?"
            )
        ) {
            preparedStatement.setString(1, title);
            return getMovieIfPresent(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot select.", sqlException);
        }
    }

    public void updateAverageRatingForMovie(String movieTitle, double averageRating) {
        validateParameter(movieTitle);
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "update movies set average_rating = ? where title = ?"
            )
        ) {
            executeAverageRatingUpdate(preparedStatement, movieTitle, averageRating);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot update database.", sqlException);
        }
    }

    public double getAverageRatingByTitle(String movieTitle) {
        validateParameter(movieTitle);
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select average_rating from movies where title = ?"
            )
        ) {
            return executeAverageRatingSelect(preparedStatement, movieTitle);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot execute query.", sqlException);
        }
    }

    // --- private methods ----------------------------------------------------

    private void executeSave(
        PreparedStatement preparedStatement, String title, LocalDate releaseDate) throws SQLException {

        preparedStatement.setString(1, title);
        preparedStatement.setDate(2, Date.valueOf(releaseDate));
        preparedStatement.setDouble(3, 0.0);
        preparedStatement.executeUpdate();
    }

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
            allMovies.add(createMovieFromQueryResult(resultSet));
        }
    }

    private Optional<Movie> getMovieIfPresent(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(createMovieFromQueryResult(resultSet));
            }
            return Optional.empty();
        }
    }

    private Movie createMovieFromQueryResult(ResultSet resultSet) throws SQLException {
        Movie movie = new Movie(
            resultSet.getLong("id"),
            resultSet.getString("title"),
            resultSet.getDate("release_date").toLocalDate());
        movie.setAverageRating(resultSet.getDouble("average_rating"));
        return movie;
    }

    private void validateParameter(String movieTitle) {
        if (findMovieByTitle(movieTitle).isEmpty()) {
            throw new IllegalArgumentException(getMissingMovieMessage());
        }
    }

    private void executeAverageRatingUpdate(
        PreparedStatement preparedStatement, String movieTitle, double averageRating) throws SQLException {

        preparedStatement.setDouble(1, averageRating);
        preparedStatement.setString(2, movieTitle);
        preparedStatement.executeUpdate();
    }

    private double executeAverageRatingSelect(
        PreparedStatement preparedStatement, String movieTitle) throws SQLException {

        preparedStatement.setString(1, movieTitle);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("average_rating");
            }
            throw new IllegalArgumentException(getMissingMovieMessage());
        }
    }

    private String getMissingMovieMessage() {
        return "No movie with specified title in database.";
    }
}