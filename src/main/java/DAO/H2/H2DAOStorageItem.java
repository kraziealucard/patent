package DAO.H2;

import DAO.IStorageItemDAO;
import Model.Customer;
import Model.StorageItem;
import Model.Supplier;
import Model.WarehouseZone;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOStorageItem implements IStorageItemDAO {
    private final Connection connection;

    public H2DAOStorageItem(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addStorageItem(StorageItem storageItem) {
        return 0;
    }

    @Override
    public boolean updateStorageItem(StorageItem storageItem) {
        return false;
    }

    @Override
    public ArrayList<StorageItem> getStorageItem(boolean onlyOnStorage, ArrayList<WarehouseZone> warehouseZones,
                                                 ArrayList<Supplier> suppliers, ArrayList<Customer> customers) {
        return new ArrayList<>();
    }
}
