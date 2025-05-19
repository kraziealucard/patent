package DAO;

import Model.GroupItems;
import Model.TypeOfStorageItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IItemTypesDAO {
    long addItemTypes(TypeOfStorageItem type);

    ArrayList<TypeOfStorageItem> getTypeList(boolean onlyActive, ArrayList<GroupItems> groupItems);

    void updateTypeList(TypeOfStorageItem type);
    void ABCAnalyse(List<TypeOfStorageItem> types);

    TypeOfStorageItem getTypeByID(long ID, DAOFactory dao);
}
