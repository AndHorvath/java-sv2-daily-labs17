package day05;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class ActorsRepository {

    // --- attributes ---------------------------------------------------------

    private DataSource dataSource;

    // --- constructors -------------------------------------------------------

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- getters and setters ------------------------------------------------

    public DataSource getDataSource() { return dataSource; }

    // --- public methods -----------------------------------------------------

    public Long saveActor(String name) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "insert into actors (actor_name) values (?)", Statement.RETURN_GENERATED_KEYS
            )
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            return getActorId(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot update: " + name, sqlException);
        }
    }

    public Optional<Actor> findActorByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                 //language=SQL
                 "select * from actors where actor_name = ?"
             )) {
            preparedStatement.setString(1, name);
            return getActorIfPresent(preparedStatement);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot query.", sqlException);
        }
    }

    // --- private methods ----------------------------------------------------

    private Long getActorId(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new IllegalStateException("Cannot get id.");
        }
    }

    private Optional<Actor> getActorIfPresent(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(new Actor(resultSet.getLong("id"), resultSet.getString("actor_name")));
            }
            return Optional.empty();
        }
    }
}