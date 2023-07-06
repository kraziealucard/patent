package DAO;

import Model.GroupItems;
import Model.TypeOfStorageItem;

import java.util.ArrayList;

public interface IItemTypesDAO {
    long addItemTypes(TypeOfStorageItem type);

    ArrayList<TypeOfStorageItem> getTypeList(boolean onlyActive, ArrayList<GroupItems> groupItems);

    void updateTypeList(TypeOfStorageItem type);

    TypeOfStorageItem getTypeByID(long ID, DAOFactory dao);
}
