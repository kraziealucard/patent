package DAO;

import Model.Cell;
import Model.WarehouseZone;

import java.util.ArrayList;

public interface IWarehouseZoneDAO {
    void addWarehouseZone(WarehouseZone warehouseZone);

    boolean updateWarehouseZone(WarehouseZone warehouseZone);

    ArrayList<WarehouseZone> getWarehouseZones(boolean onlyActive);

    ArrayList<WarehouseZone> getProductZones(boolean onlyActive);

    ArrayList<WarehouseZone> getMaterialZones(boolean onlyActive);

    Cell getCellByID(Long ID);

    WarehouseZone getZoneByID(Long ID);

    boolean updateCell(Cell cell);
}