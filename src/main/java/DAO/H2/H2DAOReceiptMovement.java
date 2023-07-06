package DAO.H2;

import DAO.IReceiptMovementDAO;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class H2DAOReceiptMovement implements IReceiptMovementDAO {
    private final Connection connection;

    public H2DAOReceiptMovement(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addReceiptMovement(ReceiptMovement receiptMovement) {
        long res = -1;
        String query = "INSERT INTO ReceiptMovement (date, performerID, invoiceNumberField, isProduct) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(receiptMovement.getDate()));
            statement.setLong(2, receiptMovement.getPerformer().getID());
            statement.setString(3, receiptMovement.getInvoiceNumberField());
            statement.setBoolean(4, receiptMovement.isProduct());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    receiptMovement.setID(generatedKeys.getLong(1));
                }
                generatedKeys.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        query = "INSERT INTO ListOfReceiptMovement (receiptID, itemID, amount, whereCellID, FROMCELLID) " +
                "VALUES (?, ?, ?, ?, ?)";

        for (ReceiptMovement.ListOfReceiptMovement l : receiptMovement.getListMovement()) {
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, receiptMovement.getID());
                statement.setLong(2, l.getItem().getID());
                statement.setInt(3, l.getAmount());
                statement.setLong(4, l.getWhere().getID());
                statement.setLong(5, l.getFrom().getID());
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
        receiptMovement.setID(res);
        return res;
    }

    @Override
    public ArrayList<ReceiptMovement> getReceiptSupplyList(ArrayList<User> users, ArrayList<StorageItem> storageItems,
                                                           ArrayList<WarehouseZone> warehouseZones) {
        ArrayList<ReceiptMovement> data = new ArrayList<>();
        String sql = "SELECT * FROM RECEIPTMOVEMENT";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                Date sqlDate = resultSet.getDate("DATE");
                long userID = resultSet.getLong("PERFORMERID");
                String invoce = resultSet.getString("INVOICENUMBERFIELD");
                boolean isProduct = resultSet.getBoolean("ISPRODUCT");
                ReceiptMovement item = new ReceiptMovement(ID, sqlDate.toLocalDate(), findUserByID(userID, users),
                        invoce, isProduct);
                data.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (ReceiptMovement r : data) {
            sql = "SELECT * FROM ListOfReceiptMovement WHERE RECEIPTID = " + r.getID();
            ArrayList<ReceiptMovement.ListOfReceiptMovement> lists = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    long ID = resultSet.getLong("ID");
                    long ITEMID = resultSet.getLong("ITEMID");
                    int amount = resultSet.getInt("AMOUNT");
                    long from = resultSet.getLong("FROMCELLID");
                    long where = resultSet.getLong("WHERECELLID");
                    ReceiptMovement.ListOfReceiptMovement item = new ReceiptMovement.ListOfReceiptMovement(ID, r, findStorageItemByID(ITEMID, storageItems),
                            amount, findCellByID(from, warehouseZones), findCellByID(where, warehouseZones));
                    lists.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            r.setListMovement(lists);
        }

        return data;
    }

    private User findUserByID(Long ID, ArrayList<User> u) {
        if (ID == null) return null;

        for (User con : u) {
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
}
