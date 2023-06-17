package DAO;

import Model.*;

import java.util.ArrayList;

public interface IReceiptMovementDAO {
    long addReceiptMovement(ReceiptMovement receiptMovement);

    ArrayList<ReceiptMovement> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones);
}
