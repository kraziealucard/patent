package DAO.H2;

import DAO.IContractorDAO;
import Model.Contractor;
import Model.Customer;
import Model.Supplier;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOContractor implements IContractorDAO {
    private final Connection connection;

    public H2DAOContractor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addContactor(Contractor contractor) {
        return 0;
    }

    @Override
    public boolean updateContractor(Contractor contractor) {
        return false;
    }

    @Override
    public ArrayList<Supplier> getSuppliersList(boolean onlyActive) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Customer> getCustomersList(boolean onlyActive) {
        return new ArrayList<>();
    }

    @Override
    public boolean changeTypeContractor(Contractor contractor) {
        return false;
    }
}
