package DAO;

import Model.GroupItems;

import java.util.ArrayList;

public interface IGroupItemsDAO {
    long addGroupItems(GroupItems groupItems);

    ArrayList<GroupItems> getGroupItemsList(boolean onlyActive);

    boolean updateGroupItem(GroupItems groupItems);
}
