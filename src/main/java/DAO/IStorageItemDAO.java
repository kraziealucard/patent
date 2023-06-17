package DAO;

import Model.Customer;
import Model.StorageItem;
import Model.Supplier;
import Model.WarehouseZone;

import java.util.ArrayList;

public interface IStorageItemDAO {
    long addStorageItem(StorageItem storageItem);

    boolean updateStorageItem(StorageItem storageItem);

    ArrayList<StorageItem> getStorageItem(boolean onlyOnStorage, ArrayList<WarehouseZone> warehouseZones,
                                          ArrayList<Supplier> suppliers, ArrayList<Customer> customers);
}
