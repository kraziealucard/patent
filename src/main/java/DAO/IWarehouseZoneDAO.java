package DAO;

import Model.Cell;
import Model.WarehouseZone;

import java.util.ArrayList;

public interface IWarehouseZoneDAO {
    long addWarehouseZone(WarehouseZone warehouseZone);

    boolean updateWarehouseZone(WarehouseZone warehouseZone);

    ArrayList<WarehouseZone> getWarehouseZones(boolean onlyActive, boolean onlyProductZone);

    boolean updateCell(Cell cell);
}