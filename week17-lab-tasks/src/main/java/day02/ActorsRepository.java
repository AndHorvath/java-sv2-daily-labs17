package day02;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void saveActor(String name) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into actors (actor_name) values (?)"
            )) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot update: " + name, sqlException);
        }
    }

    public List<String> findActorsWithPrefix(String prefix) {
        List<String> actorsWithPrefix = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                //language=SQL
                "select * from actors where actor_name like ? order by actor_name"
            )) {
            preparedStatement.setString(1, prefix + "%");
            updateActorsWithPrefix(actorsWithPrefix, preparedStatement);
        } catch (SQLException sqlException) {
            throwQueryException(sqlException);
        }
        return actorsWithPrefix;
    }

    // --- private methods ----------------------------------------------------

    private void updateActorsWithPrefix(List<String> actorsWithPrefix, PreparedStatement preparedStatement) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                actorsWithPrefix.add(resultSet.getString("actor_name"));
            }
        } catch (SQLException sqlException) {
            throwQueryException(sqlException);
        }
    }

    private void throwQueryException(SQLException sqlException) {
        throw new IllegalStateException("Cannot query.", sqlException);
    }
}