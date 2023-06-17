package DAO.H2;

import DAO.IReceiptSupplyDAO;
import Model.*;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOReceiptSupply implements IReceiptSupplyDAO {
    private final Connection connection;

    public H2DAOReceiptSupply(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addReceiptSupply(ReceiptSupply receiptSupply) {
        return 0;
    }

    @Override
    public ArrayList<ReceiptSupply> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones, ArrayList<Supplier> suppliers) {
        return new ArrayList<>();
    }
}
