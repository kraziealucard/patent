package DAO;

import Model.Contractor;
import Model.Customer;
import Model.Supplier;

import java.util.ArrayList;

public interface IContractorDAO {
    long addContactor(Contractor contractor);

    boolean updateContractor(Contractor contractor);

    ArrayList<Supplier> getSuppliersList(boolean onlyActive);

    ArrayList<Customer> getCustomersList(boolean onlyActive);

    boolean changeTypeContractor(Contractor contractor);
}
