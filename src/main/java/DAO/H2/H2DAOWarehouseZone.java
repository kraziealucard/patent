package DAO.H2;

import DAO.IWarehouseZoneDAO;
import Model.Cell;
import Model.WarehouseZone;

import java.sql.Connection;
import java.util.ArrayList;

public class H2DAOWarehouseZone implements IWarehouseZoneDAO {
    private final Connection connection;

    public H2DAOWarehouseZone(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long addWarehouseZone(WarehouseZone warehouseZone) {
        return 0;
    }

    @Override
    public boolean updateWarehouseZone(WarehouseZone warehouseZone) {
        return false;
    }

    @Override
    public ArrayList<WarehouseZone> getWarehouseZones(boolean onlyActive, boolean onlyProductZone) {
        return new ArrayList<>();
    }

    @Override
    public boolean updateCell(Cell cell) {
        return false;
    }
}
