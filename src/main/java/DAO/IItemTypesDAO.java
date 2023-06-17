package DAO;

import Model.GroupItems;
import Model.TypeOfStorageItem;

import java.util.ArrayList;

public interface IItemTypesDAO {
    long addItemTypes(TypeOfStorageItem type);

    ArrayList<TypeOfStorageItem> getTypeList(boolean onlyActive, boolean isProduct, ArrayList<GroupItems> groupItems);

    void updateTypeList(TypeOfStorageItem type);
}
