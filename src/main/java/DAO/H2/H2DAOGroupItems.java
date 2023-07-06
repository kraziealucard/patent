package DAO.H2;

import DAO.IGroupItemsDAO;
import Model.GroupItems;
import Model.TypeOfStorageItem;
import Model.WarehouseZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class H2DAOGroupItems implements IGroupItemsDAO {
    private final Connection connection;

    H2DAOGroupItems(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addGroupItems(GroupItems groupItems) {
        String query = "INSERT INTO GroupTable (name, IDParentGroup, isActive, isProduct) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, groupItems.getName());
            if (groupItems.getParent() == null) {
                statement.setNull(2, java.sql.Types.BIGINT);
            } else {
                statement.setLong(2, groupItems.getParent().getID());
            }
            statement.setBoolean(3, groupItems.isActive());
            statement.setBoolean(4, groupItems.isProduct());

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
    public ArrayList<GroupItems> getGroupItemsList(boolean onlyActive) {
        ArrayList<GroupItems> data = new ArrayList<>();

        String sql = "SELECT * FROM GroupTable";
        if (onlyActive) {
            sql += " WHERE isActive = true";
        }
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                boolean isActive = resultSet.getBoolean("isActive");
                Long idParent = resultSet.getLong("IDParentGroup");
                boolean isProduct = resultSet.getBoolean("ISPRODUCT");
                GroupItems item = new GroupItems(ID, name, isProduct);
                item.setActive(isActive);
                item.setParent(new GroupItems(idParent, "", isProduct));
                data.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setParent(findParentItem(data, data.get(i).getParent().getID()));
        }
        return data;
    }

    @Override
    public GroupItems getGroupByID(Long ID) {
        if (ID == null) return null;
        GroupItems res = null;
        String sql = "SELECT * FROM GroupTable WHERE ID = " + ID;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isProduct = resultSet.getBoolean("isProduct");
                Long idParent = resultSet.getLong("IDParentGroup");
                res = new GroupItems(ID, name, isProduct);
                res.setActive(isActive);
                res.setParent(getGroupByID(idParent));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    private GroupItems findParentItem(ArrayList<GroupItems> groupItemsList, long parentID) {
        for (GroupItems groupItem : groupItemsList) {
            if (groupItem.getID() == parentID) {
                return groupItem;
            }
        }
        return null;
    }

    @Override
    public boolean updateGroupItem(GroupItems items) {
        String query = "UPDATE GroupTable SET name = ?, isProduct = ?, IDParentGroup = ?, isActive = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, items.getName());
            statement.setBoolean(2, items.isProduct());
            if (items.getParent() == null) {
                statement.setNull(3, java.sql.Types.BIGINT);
            } else {
                statement.setLong(3, items.getParent().getID());
            }
            statement.setBoolean(4, items.isActive());
            statement.setLong(5, items.getID());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
