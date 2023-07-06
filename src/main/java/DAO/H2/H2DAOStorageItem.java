package DAO.H2;

import DAO.DAOFactory;
import DAO.IStorageItemDAO;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class H2DAOStorageItem implements IStorageItemDAO {
    private final Connection connection;

    public H2DAOStorageItem(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addStorageItem(StorageItem storageItem) {
        String sql = "INSERT INTO STORAGEITEM (IDITEMSTYPE, IDCELL, IDSupplier) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, storageItem.getType().getID());
            statement.setLong(2, storageItem.getLocationOnStorage().getID());
            statement.setLong(3, storageItem.getSupplier().getID());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                generatedKeys.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public boolean updateStorageItem(StorageItem storageItem) {
        String query = "UPDATE StorageItem SET IDITEMSTYPE = ?, IDCELL = ?, IDSupplier = ?, IDCustomer = ?" +
                " WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, storageItem.getType().getID());
            if (storageItem.getLocationOnStorage() == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, storageItem.getLocationOnStorage().getID());
            }
            statement.setLong(3, storageItem.getSupplier().getID());
            if (storageItem.getCustomer() == null) {
                statement.setNull(4, Types.BIGINT);
            } else {
                statement.setLong(4, storageItem.getCustomer().getID());
            }

            statement.setLong(5, storageItem.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<StorageItem> getStorageItem(boolean onlyOnStorage, ArrayList<TypeOfStorageItem> types,
                                                 ArrayList<Contractor> contractors, ArrayList<WarehouseZone> zones) {
        ArrayList<StorageItem> data = new ArrayList<>();

        String sql = "SELECT * FROM STORAGEITEM";
        if (onlyOnStorage) {
            sql += " WHERE IDCUSTOMER IS NULL";
        }

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                long IDITEMSTYPE = resultSet.getLong("IDITEMSTYPE");
                Long IDCELL = resultSet.getLong("IDCELL");
                long IDSupplier = resultSet.getLong("IDSupplier");
                Long IDCustomer = resultSet.getLong("IDCustomer");
                Cell cell = null;
                TypeOfStorageItem t = findTypeByID(IDITEMSTYPE, types);
                Contractor supplier = findContractorByID(IDSupplier, contractors);
                Contractor customer = findContractorByID(IDCustomer, contractors);
                StorageItem item = new StorageItem(ID, t, null);
                for (WarehouseZone w : zones) {
                    cell = findCellByID(IDCELL, w, item);
                    if (cell != null) {
                        item.setLocationOnStorage(cell);
                        break;
                    }
                }
                item.setCustomer(customer);
                item.setSupplier(supplier);
                data.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private Contractor findContractorByID(Long ID, ArrayList<Contractor> c) {
        if (ID == null) return null;

        for (Contractor con : c) {
            if (con.getID() == ID) return con;
        }
        return null;
    }

    private Cell findCellByID(Long ID, WarehouseZone w, StorageItem item) {
        if (ID == null) return null;

        for (int i = 0; i < w.getCells().length; i++) {
            for (int j = 0; j < w.getCells()[i].length; j++) {
                if (w.getCells()[i][j].getID() == ID) {
                    w.getCells()[i][j].addProduct(item);
                    return w.getCells()[i][j];
                }
            }
        }

        return null;
    }

    private TypeOfStorageItem findTypeByID(Long ID, ArrayList<TypeOfStorageItem> typeOfStorageItems) {
        if (ID == null) return null;

        for (TypeOfStorageItem t : typeOfStorageItems) {
            if (t.getID() == ID) return t;
        }

        return null;
    }
}
