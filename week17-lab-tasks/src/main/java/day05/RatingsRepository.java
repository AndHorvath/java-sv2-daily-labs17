package day05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RatingsRepository {

    // --- attributes ---------------------------------------------------------

    private DataSource dataSource;

    private static final int RATING_MINVALUE = 1;
    private static final int RATING_MAXVALUE = 5;
    private static final double EPSILON = Math.pow(10, -5);

    // --- constructors -------------------------------------------------------

    public RatingsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- getters and setters ------------------------------------------------

    public DataSource getDataSource() { return dataSource; }

    // --- public methods -----------------------------------------------------

    public void insertRatingForMovie(Long movieId, Integer... ratings) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            executeInsert(connection, movieId, ratings);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot insert.", sqlException);
        }
    }

    public Optional<Rating> findRatingById(Long id) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select * from ratings where id = ?"
            )
        ) {
            preparedStatement.setLong(1, id);
            return getRatingIfPresent(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot select.", sqlException);
        }
    }

    public double calculateAverageRatingByMovieId(Long movieId) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select avg(rating) from ratings where movie_id = ?"
            )
        ) {
            preparedStatement.setLong(1, movieId);
            return getAverageRating(preparedStatement, movieId);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot execute query.", sqlException);
        }
    }

    // --- private methods ----------------------------------------------------

    private void executeInsert(Connection connection, Long movieId, Integer... ratings) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
            //language=SQL
            "insert into ratings (movie_id, rating) values (?, ?)"
        )) {
            for (int rating : ratings) {
                if (isRatingInvalid(rating)) {
                    connection.rollback();
                    return;
                }
                preparedStatement.setLong(1, movieId);
                preparedStatement.setInt(2, rating);
                preparedStatement.executeUpdate();
            }
            connection.commit();
        }
    }

    private Optional<Rating> getRatingIfPresent(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(new Rating(resultSet.getLong("movie_id"), resultSet.getInt("rating")));
            }
            return Optional.empty();
        }
    }

    private boolean isRatingInvalid(int rating) {
        return rating < RATING_MINVALUE || rating > RATING_MAXVALUE;
    }

    private double getAverageRating(PreparedStatement preparedStatement, Long movieId) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            double averageRating = resultSet.getDouble("avg(rating)");
            if (isPositive(averageRating)) {
                return averageRating;
            }
            throw new IllegalArgumentException("No ratings for specified movie id in database: " + movieId);
        }
    }

    private boolean isPositive(double value) {
        return value > 0.0 + EPSILON;
    }
}