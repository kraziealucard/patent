package DAO.H2;

import DAO.IUserDAO;
import Model.Position;
import Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class H2DAOUser implements IUserDAO {
    private final Connection connection;

    public H2DAOUser(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addUser(User user) {
        String query = "INSERT INTO Users (IDPosition, name, login, password, isActive) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, user.getPosition().getID());
            statement.setString(2, user.getName());
            statement.setString(3, user.getLogin());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.isActive());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                generatedKeys.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public ArrayList<User> getUserList(boolean onlyActive, ArrayList<Position> positions) {
        ArrayList<User> loadedUsers = new ArrayList<>();

        String sql = "SELECT * FROM Users";
        if (onlyActive) {
            sql += " WHERE isActive = true";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long userID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                long positionID = resultSet.getLong("IDPosition");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                boolean isActive = resultSet.getBoolean("isActive");

                Optional<Position> optionalPosition = positions.stream()
                        .filter(position -> position.getID() == positionID)
                        .findFirst();

                Position position = optionalPosition.orElse(null);

                User user = new User(userID, name, position, login, password);
                user.setActive(isActive);

                loadedUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadedUsers;
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE Users SET name = ?, login = ?, password = ?, isActive = ?, IDPosition = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBoolean(4, user.isActive());
            statement.setLong(5, user.getPosition().getID());
            statement.setLong(6, user.getID());
            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean loginUserExists(String login) {
        String query = "SELECT COUNT(*) FROM Users WHERE login = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUserByLoginAndPassword(String login, String password, ArrayList<Position> positions) {
        String query = "SELECT * FROM Users WHERE login = ? AND password = ? AND isActive = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setBoolean(3, true);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                long userID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                long positionID = resultSet.getLong("IDPosition");

                Optional<Position> optionalPosition = positions.stream()
                        .filter(position -> position.getID() == positionID)
                        .findFirst();

                Position position = optionalPosition.orElse(null);

                return new User(userID, name, position, login, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
