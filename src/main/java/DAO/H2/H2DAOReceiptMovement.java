package DAO.H2;

import DAO.IReceiptMovementDAO;
import Model.ReceiptMovement;
import Model.StorageItem;
import Model.User;
import Model.WarehouseZone;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOReceiptMovement implements IReceiptMovementDAO {
    private final Connection connection;

    public H2DAOReceiptMovement(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addReceiptMovement(ReceiptMovement receiptMovement) {
        return 0;
    }

    @Override
    public ArrayList<ReceiptMovement> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones) {
        return new ArrayList<>();
    }
}
