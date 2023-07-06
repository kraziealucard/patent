package DAO.H2;

import DAO.IReceiptSupplyDAO;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class H2DAOReceiptSupply implements IReceiptSupplyDAO {
    private final Connection connection;

    public H2DAOReceiptSupply(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addReceiptSupply(ReceiptSupply receiptSupply) {
        long res = -1;
        String query = "INSERT INTO ReceiptSupply (date, performerID, invoiceNumberField, isProduct, supplierID) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(receiptSupply.getDate()));
            statement.setLong(2, receiptSupply.getPerformer().getID());
            statement.setString(3, receiptSupply.getInvoiceNumberField());
            statement.setBoolean(4, receiptSupply.isProduct());
            statement.setLong(5, receiptSupply.getSupplier().getID());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    res = generatedKeys.getLong(1);
                }
                generatedKeys.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        receiptSupply.setID(res);

        query = "INSERT INTO ListOfReceiptSupply (receiptID, itemID, amount, whereCellID) " +
                "VALUES (?, ?, ?, ?)";

        for (Receipt.ListOfReceipt l : receiptSupply.getLists()) {
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, receiptSupply.getID());
                statement.setLong(2, l.getItem().getID());
                statement.setInt(3, l.getAmount());
                statement.setLong(4, l.getCell().getID());
                System.out.println(receiptSupply.getID());
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        l.setID(generatedKeys.getLong(1));
                    }
                    generatedKeys.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        receiptSupply.setID(res);
        return res;
    }

    @Override
    public ArrayList<ReceiptSupply> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems,
                                                         ArrayList<WarehouseZone> warehouseZones,
                                                         ArrayList<Contractor> suppliers) {
        ArrayList<ReceiptSupply> data = new ArrayList<>();
        String sql = "SELECT * FROM RECEIPTSUPPLY";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                Date sqlDate = resultSet.getDate("DATE");
                long userID = resultSet.getLong("PERFORMERID");
                String invoce = resultSet.getString("INVOICENUMBERFIELD");
                boolean isProduct = resultSet.getBoolean("ISPRODUCT");
                long supplierID = resultSet.getLong("SUPPLIERID");
                ReceiptSupply item = new ReceiptSupply(ID, sqlDate.toLocalDate(), findUserByID(userID, users),
                        invoce, findContractorByID(supplierID, suppliers), isProduct);
                data.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (ReceiptSupply r : data) {
            sql = "SELECT * FROM LISTOFRECEIPTSUPPLY WHERE RECEIPTID = " + r.getID();
            ArrayList<Receipt.ListOfReceipt> lists = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    long ID = resultSet.getLong("ID");
                    long ITEMID = resultSet.getLong("ITEMID");
                    int amount = resultSet.getInt("AMOUNT");
                    long CellID = resultSet.getLong("WHERECELLID");
                    Receipt.ListOfReceipt item = new Receipt.ListOfReceipt(ID, r, findStorageItemByID(ITEMID, storageItems),
                            amount, findCellByID(CellID, warehouseZones));
                    lists.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            r.setLists(lists);
        }

        return data;
    }

    private Cell findCellByID(Long ID, ArrayList<WarehouseZone> w) {
        if (ID == null) return null;

        for (WarehouseZone zone : w) {
            for (int i = 0; i < zone.getCells().length; i++) {
                for (int j = 0; j < zone.getCells()[i].length; j++) {
                    if (zone.getCells()[i][j].getID() == ID) return zone.getCells()[i][j];
                }
            }
        }

        return null;
    }

    private User findUserByID(Long ID, ArrayList<User> u) {
        if (ID == null) return null;

        for (User con : u) {
            if (con.getID() == ID) return con;
        }
        return null;
    }

    private Contractor findContractorByID(Long ID, ArrayList<Contractor> c) {
        if (ID == null) return null;

        for (Contractor con : c) {
            if (con.getID() == ID) return con;
        }
        return null;
    }

    private StorageItem findStorageItemByID(Long ID, ArrayList<StorageItem> s) {
        if (ID == null) return null;
        for (StorageItem i : s) {
            if (i.getID() == ID) return i;
        }

        return null;
    }
}
