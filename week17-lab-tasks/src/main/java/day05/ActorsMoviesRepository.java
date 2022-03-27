package day05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ActorsMoviesRepository {

    // --- attributes ---------------------------------------------------------

    private DataSource dataSource;

    // --- constructors -------------------------------------------------------

    public ActorsMoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- getters and setters ------------------------------------------------

    public DataSource getDataSource() { return dataSource; }

    // --- public methods -----------------------------------------------------

    public void insertActorAndMovieId(Long actorId, Long movieId) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "insert into actors_movies (actor_id, movie_id) values (?, ?)"
            )
        ) {
            preparedStatement.setLong(1, actorId);
            preparedStatement.setLong(2, movieId);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot insert row.", sqlException);
        }
    }

    public Optional<Long> getActorIdByPrimaryKey(Long primaryKey) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select actor_id from actors_movies where id = ?"
            )
        ) {
            preparedStatement.setLong(1, primaryKey);
            return getActorIdByStatement(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot query.");
        }
    }

    public Optional<Long> getMovieIdByPrimaryKey(Long primaryKey) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select movie_id from actors_movies where id = ?"
            )
        ) {
            preparedStatement.setLong(1, primaryKey);
            return getMovieIdByStatement(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot query.");
        }
    }

    // --- private methods ----------------------------------------------------

    private Optional<Long> getActorIdByStatement(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong("actor_id"));
            }
            return Optional.empty();
        }
    }

    private Optional<Long> getMovieIdByStatement(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong("movie_id"));
            }
            return Optional.empty();
        }
    }
}