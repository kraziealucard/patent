package DAO;

import Model.*;

import java.util.ArrayList;

public interface IReceiptDispatchDAO {
    long addReceiptDispatch(ReceiptDispatch receiptDispatch);

    ArrayList<ReceiptDispatch> getReceiptDispatchList(ArrayList<User> users, ArrayList<StorageItem> storageItems, ArrayList<WarehouseZone> warehouseZones, ArrayList<Contractor> customers);
}
