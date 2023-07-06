package DAO;

import Model.Contractor;
import Model.StorageItem;
import Model.TypeOfStorageItem;
import Model.WarehouseZone;

import java.util.ArrayList;

public interface IStorageItemDAO {
    long addStorageItem(StorageItem storageItem);

    boolean updateStorageItem(StorageItem storageItem);

    ArrayList<StorageItem> getStorageItem(boolean onlyOnStorage, ArrayList<TypeOfStorageItem> types,
                                          ArrayList<Contractor> contractors, ArrayList<WarehouseZone> zones);
}
