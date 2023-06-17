package DAO.H2;

import DAO.IReceiptDispatchDAO;
import Model.*;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOReceiptDispatch implements IReceiptDispatchDAO {
    private final Connection connection;

    public H2DAOReceiptDispatch(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addReceiptDispatch(ReceiptDispatch receiptDispatch) {
        return 0;
    }

    @Override
    public ArrayList<ReceiptDispatch> getReceiptDispatchList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones, ArrayList<Customer> customers) {
        return new ArrayList<>();
    }
}
