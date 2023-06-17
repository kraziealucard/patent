package DAO.H2;

import DAO.IItemTypesDAO;
import Model.GroupItems;
import Model.TypeOfStorageItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2DAOItemTypes implements IItemTypesDAO {
    private final Connection connection;

    public H2DAOItemTypes(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addItemTypes(TypeOfStorageItem type) {
        String query = "INSERT INTO ItemsTypes (name, weight, isProduct, isActive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, type.getName());
            statement.setDouble(2, type.getWeight());
            statement.setBoolean(3, type.isProduct());
            statement.setBoolean(4, type.isActive());

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
    public ArrayList<TypeOfStorageItem> getTypeList(boolean onlyActive, boolean isProduct, ArrayList<GroupItems> groupItems) {
        ArrayList<TypeOfStorageItem> data = new ArrayList<>();

        String sql = "SELECT * FROM ItemsTypes WHERE isProduct = " + isProduct;
        if (onlyActive) {
            sql += " AND isActive = true";
        }

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                double weight = resultSet.getDouble("weight");

                TypeOfStorageItem item = new TypeOfStorageItem(ID, name, weight, isProduct);
                data.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //return data;
        return new ArrayList<>();
    }

    @Override
    public void updateTypeList(TypeOfStorageItem type) {
        String query = "UPDATE ItemsTypes SET name = ?, weight = ?, isProduct = ?, isActive = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, type.getName());
            statement.setDouble(2, type.getWeight());
            statement.setBoolean(3, type.isProduct());
            statement.setBoolean(4, type.isActive());
            statement.setLong(5, type.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
