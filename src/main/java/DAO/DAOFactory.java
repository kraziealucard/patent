package DAO;

import DAO.H2.H2DAOFactory;

import java.sql.SQLException;

// Abstract class DAO Factory
public abstract class DAOFactory {

    // List of DAO types supported by the factory
    public static final int H2 = 1;

    public static DAOFactory getDAOFactory(int whichFactory) throws SQLException {
        switch (whichFactory) {
            case H2 -> {
                return new H2DAOFactory();
            }
            default -> {
                return new H2DAOFactory();
            }
        }

    }


    public abstract IGroupItemsDAO getGroupItemsDAO();

    public abstract IItemTypesDAO getItemTypesDAO();

    public abstract IPositionDAO getPositionDAO();

    public abstract IReceiptDispatchDAO getReceiptDispatchDAO();

    public abstract IReceiptMovementDAO getReceiptMovementDAO();

    public abstract IReceiptSupplyDAO getReceiptSupplyDAO();

    public abstract IStorageItemDAO getStorageItemDAO();

    public abstract IUserDAO getUserDAO();

    public abstract IWarehouseZoneDAO getWarehouseZoneDAO();

    public abstract IContractorDAO getContactorDAO();

    public abstract boolean isFirstStart();

}
