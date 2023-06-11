package DAO.H2;

import DAO.IUserDAO;
import Model.Permission;
import Model.Position;
import Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class H2DAOUser implements IUserDAO {
    private final String DB_URL;

    public H2DAOUser(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    @Override
    public long addUser(User user) {
        long newID = -1;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            String query = "INSERT INTO Users (IDPosition, name, login, password, isActive) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, user.getPosition().getID());
            statement.setString(2, user.getName());
            statement.setString(3, user.getLogin());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.isActive());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newID = generatedKeys.getLong(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newID;
    }

    @Override
    public ArrayList<User> getUserList(boolean onlyActive, ArrayList<Position> positions) {
        ArrayList<User> loadedUsers = new ArrayList<>();
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            Statement statement = dbConnection.createStatement();
            String sql = "SELECT * FROM Users";
            if (onlyActive) {
                sql += " WHERE isActive = true";
            }
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Long userID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                Long positionID = resultSet.getLong("IDPosition");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                boolean isActive = resultSet.getBoolean("isActive");

                Optional<Position> optionalPosition = positions.stream()
                        .filter(position -> position.getID() == positionID)
                        .findFirst();

                Position position = null;
                if (optionalPosition.isPresent()) {
                    position = optionalPosition.get();
                }

                User user = new User(userID, name, position, login, password);
                user.setActive(isActive);

                loadedUsers.add(user);
            }

            return loadedUsers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean updateUser(User user) {
        boolean isSuccessful = false;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            PreparedStatement statement = dbConnection.prepareStatement(
                    "UPDATE Users SET name = ?, login = ?, password = ?, isActive = ?, IDPosition = ? WHERE ID = ?"
            );
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBoolean(4, user.isActive());
            statement.setLong(5, user.getPosition().getID());
            statement.setLong(6, user.getID());
            statement.executeUpdate();

            isSuccessful = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

    @Override
    public boolean userExists(String login) {
        return false;
    }

    @Override
    public boolean userExists(String login, String Password) {
        return false;
    }
}
