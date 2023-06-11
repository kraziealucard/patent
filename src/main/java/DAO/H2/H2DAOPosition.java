package DAO.H2;

import DAO.IPositionDAO;
import Model.Permission;
import Model.Position;

import java.sql.*;
import java.util.ArrayList;

public class H2DAOPosition implements IPositionDAO {
    private final String DB_URL;

    public H2DAOPosition(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    @Override
    public ArrayList<Position> getPositionList(boolean onlyActive) {
        ArrayList<Position> loadedPositions = new ArrayList<>();
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            Statement statement = dbConnection.createStatement();
            String sql = "SELECT * FROM Positions";
            if (onlyActive) {
                sql += " WHERE isActive = true";
            }
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                boolean isActive = resultSet.getBoolean("isActive");

                Position position = new Position(id, name, new ArrayList<>());
                position.setActive(isActive);

                boolean editingUsersAndPositions = resultSet.getBoolean("EditingUsersAndPositions");
                boolean editingWarehouseInformation = resultSet.getBoolean("EditingWarehouseInformation");
                boolean productLogistic = resultSet.getBoolean("ProductLogistic");
                boolean productEditing = resultSet.getBoolean("ProductEditing");
                boolean editingContactor = resultSet.getBoolean("EditingContactor");
                boolean viewBook = resultSet.getBoolean("ViewBook");

                if (editingUsersAndPositions) {
                    position.addPermission(Permission.EditingUsersAndPositions);
                }
                if (editingWarehouseInformation) {
                    position.addPermission(Permission.EditingWarehouseInformation);
                }
                if (productLogistic) {
                    position.addPermission(Permission.ProductLogistic);
                }
                if (productEditing) {
                    position.addPermission(Permission.ProductEditing);
                }
                if (editingContactor) {
                    position.addPermission(Permission.EditingContactor);
                }
                if (viewBook) {
                    position.addPermission(Permission.ViewBook);
                }

                loadedPositions.add(position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadedPositions;
    }

    @Override
    public boolean updatePosition(Position position) {
        boolean isSuccessful = false;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            PreparedStatement statement = dbConnection.prepareStatement(
                    "UPDATE Positions SET name = ?, isActive = ?, EditingUsersAndPositions = ?, " +
                            "EditingWarehouseInformation = ?, ProductEditing = ?, ProductLogistic = ?, " +
                            "EditingContactor = ?, ViewBook = ? WHERE ID = ?"
            );
            statement.setString(1, position.getName());
            statement.setBoolean(2, position.isActive());
            statement.setBoolean(3, position.hasPermissions(Permission.EditingUsersAndPositions));
            statement.setBoolean(4, position.hasPermissions(Permission.EditingWarehouseInformation));
            statement.setBoolean(5, position.hasPermissions(Permission.ProductEditing));
            statement.setBoolean(6, position.hasPermissions(Permission.ProductLogistic));
            statement.setBoolean(7, position.hasPermissions(Permission.EditingContactor));
            statement.setBoolean(8, position.hasPermissions(Permission.ViewBook));
            statement.setLong(9, position.getID());
            statement.executeUpdate();

            isSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

    @Override
    public long addPosition(Position position) {
        long newID = -1;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            PreparedStatement statement = dbConnection.prepareStatement(
                    "INSERT INTO Positions (name, isActive, EditingUsersAndPositions, EditingWarehouseInformation, " +
                            "ProductLogistic, ProductEditing, EditingContactor, ViewBook) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, position.getName());
            statement.setBoolean(2, position.isActive());
            statement.setBoolean(3, position.hasPermissions(Permission.EditingUsersAndPositions));
            statement.setBoolean(4, position.hasPermissions(Permission.EditingWarehouseInformation));
            statement.setBoolean(5, position.hasPermissions(Permission.ProductLogistic));
            statement.setBoolean(6, position.hasPermissions(Permission.ProductEditing));
            statement.setBoolean(7, position.hasPermissions(Permission.EditingContactor));
            statement.setBoolean(8, position.hasPermissions(Permission.ViewBook));

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
}
