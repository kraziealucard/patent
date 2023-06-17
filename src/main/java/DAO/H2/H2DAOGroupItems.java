package DAO.H2;

import DAO.IGroupItemsDAO;
import Model.GroupItems;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOGroupItems implements IGroupItemsDAO {
    private final Connection connection;

    H2DAOGroupItems(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addGroupItems(GroupItems groupItems) {
        return 0;
    }

    @Override
    public ArrayList<GroupItems> getGroupItemsList(boolean onlyActive) {
        return new ArrayList<>();
    }

    @Override
    public boolean updateGroupItem(GroupItems groupItems) {
        return false;
    }
}
