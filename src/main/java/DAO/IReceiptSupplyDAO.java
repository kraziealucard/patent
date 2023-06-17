package DAO;

import Model.*;

import java.util.ArrayList;

public interface IReceiptSupplyDAO {
    long addReceiptSupply(ReceiptSupply receiptSupply);

    ArrayList<ReceiptSupply> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones, ArrayList<Supplier> suppliers);
}
