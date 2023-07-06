package DAO.H2;

import DAO.IContractorDAO;
import Model.Cell;
import Model.Contractor;
import Model.TypeOfStorageItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class H2DAOContractor implements IContractorDAO {
    private final Connection connection;

    public H2DAOContractor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addContactor(Contractor contractor) {
        String query = "INSERT INTO Contractor (name, isActive, ISCUSTOMER, ISSUPPLIER) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, contractor.getName());
            statement.setBoolean(2, contractor.isActive());
            statement.setBoolean(3, contractor.isCustomer());
            statement.setBoolean(4, contractor.isSupplier());

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
    public boolean updateContractor(Contractor contractor) {

        String query = "UPDATE CONTRACTOR SET name = ?, address = ?, information = ?, passport = ?, phone = ?" +
                ", bankRequisites = ?, IIN = ?, KPP = ?, isActive = ?, ISCUSTOMER = ?, ISSUPPLIER = ?" +
                " WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, contractor.getName());
            statement.setString(2, contractor.getAddress());
            statement.setString(3, contractor.getInformation());
            statement.setString(4, contractor.getPassport());
            statement.setString(5, contractor.getPhone());
            statement.setString(6, contractor.getBankRequisites());
            statement.setString(7, contractor.getIIN());
            statement.setString(8, contractor.getKPP());
            statement.setBoolean(9, contractor.isActive());
            statement.setBoolean(10, contractor.isCustomer());
            statement.setBoolean(11, contractor.isSupplier());
            statement.setLong(12, contractor.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<Contractor> getContractorsList(boolean onlyActive) {
        ArrayList<Contractor> data = new ArrayList<>();

        String sql = "SELECT * FROM Contractor";
        if (onlyActive) {
            sql += " WHERE isActive = true";
        }

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String info = resultSet.getString("information");
                String passport = resultSet.getString("passport");
                String phone = resultSet.getString("phone");
                String req = resultSet.getString("bankRequisites");
                String IIN = resultSet.getString("IIN");
                String KPP = resultSet.getString("KPP");
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isCustomer = resultSet.getBoolean("ISCUSTOMER");
                boolean isSupply = resultSet.getBoolean("ISSUPPLIER");

                Contractor item = new Contractor(ID, name);
                item.setAddress(address);
                item.setInformation(info);
                item.setPhone(phone);
                item.setPassport(passport);
                item.setBankRequisites(req);
                item.setIIN(IIN);
                item.setKPP(KPP);
                item.setActive(isActive);
                item.setSupplier(isSupply);
                item.setCustomer(isCustomer);

                data.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public ArrayList<Contractor> getSupplierList(boolean onlyActive) {
        ArrayList<Contractor> data = new ArrayList<>();

        String sql = "SELECT * FROM Contractor WHERE ISSUPPLIER = TRUE";
        if (onlyActive) {
            sql += " AND isActive = true";
        }

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long ID = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String info = resultSet.getString("information");
                String passport = resultSet.getString("passport");
                String phone = resultSet.getString("phone");
                String req = resultSet.getString("bankRequisites");
                String IIN = resultSet.getString("IIN");
                String KPP = resultSet.getString("KPP");
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isCustomer = resultSet.getBoolean("ISCUSTOMER");
                boolean isSupply = resultSet.getBoolean("ISSUPPLIER");

                Contractor item = new Contractor(ID, name);
                item.setAddress(address);
                item.setInformation(info);
                item.setPhone(phone);
                item.setPassport(passport);
                item.setBankRequisites(req);
                item.setIIN(IIN);
                item.setKPP(KPP);
                item.setActive(isActive);
                item.setSupplier(isSupply);
                item.setCustomer(isCustomer);

                data.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public Contractor getContractorByID(Long ID) {
        if (ID == null) return null;
        Contractor res = null;
        String sql = "SELECT * FROM Contractor WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String info = resultSet.getString("information");
                String passport = resultSet.getString("passport");
                String phone = resultSet.getString("phone");
                String req = resultSet.getString("bankRequisites");
                String IIN = resultSet.getString("IIN");
                String KPP = resultSet.getString("KPP");
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isCustomer = resultSet.getBoolean("ISCUSTOMER");
                boolean isSupply = resultSet.getBoolean("ISSUPPLIER");

                res = new Contractor(ID, name);
                res.setAddress(address);
                res.setInformation(info);
                res.setPhone(phone);
                res.setPassport(passport);
                res.setBankRequisites(req);
                res.setIIN(IIN);
                res.setKPP(KPP);
                res.setActive(isActive);
                res.setSupplier(isSupply);
                res.setCustomer(isCustomer);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
