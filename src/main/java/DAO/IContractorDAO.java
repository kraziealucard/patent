package DAO;

import Model.Cell;
import Model.Contractor;

import java.util.ArrayList;

public interface IContractorDAO {
    long addContactor(Contractor contractor);

    boolean updateContractor(Contractor contractor);

    ArrayList<Contractor> getContractorsList(boolean onlyActive);

    ArrayList<Contractor> getSupplierList(boolean onlyActive);

    Contractor getContractorByID(Long ID);

}
