package DAO.H2;

import DAO.IWarehouseZoneDAO;
import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H2DAOWarehouseZone implements IWarehouseZoneDAO {
    private final Connection connection;

    public H2DAOWarehouseZone(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addWarehouseZone(WarehouseZone warehouseZone) {
        String query = "INSERT INTO WarehouseZone (name, maxWeight, isProductZone, isActive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, warehouseZone.getZoneName());
            statement.setDouble(2, warehouseZone.getMaxWeight());
            statement.setBoolean(3, warehouseZone.isProductZone());
            statement.setBoolean(4, warehouseZone.isActive());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    warehouseZone.setID(generatedKeys.getLong(1));
                }
                generatedKeys.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        query = "INSERT INTO Cell (IDZone, name, isActive) VALUES (?, ?, ?)";

        for (int i = 0; i < warehouseZone.getCells().length; i++) {
            for (int j = 0; j < warehouseZone.getCells()[i].length; j++) {

                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setLong(1, warehouseZone.getID());
                    statement.setString(2, warehouseZone.getCells()[i][j].getName());
                    statement.setBoolean(3, warehouseZone.isActive());

                    int rowsInserted = statement.executeUpdate();

                    if (rowsInserted > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            warehouseZone.getCells()[i][j].setID(generatedKeys.getLong(1));
                        }
                        generatedKeys.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean updateWarehouseZone(WarehouseZone warehouseZone) {
        String query = "UPDATE WarehouseZone SET name = ?, isActive = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, warehouseZone.getZoneName());
            statement.setBoolean(2, warehouseZone.isActive());
            statement.setLong(3, warehouseZone.getID());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < warehouseZone.getCells().length; i++) {
            for (int j = 0; j < warehouseZone.getCells()[i].length; j++) {
                updateCell(warehouseZone.getCells()[i][j]);
            }
        }

        return true;
    }

    @Override
    public ArrayList<WarehouseZone> getWarehouseZones(boolean onlyActive) {
        ArrayList<WarehouseZone> data = new ArrayList<>();
        String sql = "SELECT wz.ID, wz.name, wz.isProductZone, wz.maxWeight, wz.isActive, c.name AS cellName " +
                "FROM WarehouseZone wz " +
                "LEFT JOIN (SELECT IDZone, MAX(ID) AS maxCellID FROM Cell GROUP BY IDZone) cmax ON wz.ID = cmax.IDZone " +
                "LEFT JOIN Cell c ON cmax.maxCellID = c.ID ";

        if (onlyActive) {
            sql += "WHERE wz.isActive = true ";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long zoneID = resultSet.getLong("ID");
                String zoneName = resultSet.getString("name");
                int length = 0;
                int width = 0;
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isProductZone = resultSet.getBoolean("isProductZone");
                double maxWeight = resultSet.getDouble("maxWeight");
                String nameCell = resultSet.getString("cellName");

                String regex = "\\d+ - (\\d+) \\|\\| (\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(nameCell);
                if (matcher.find()) {
                    length = Integer.parseInt(matcher.group(1));
                    width = Integer.parseInt(matcher.group(2));
                }
                WarehouseZone temp = new WarehouseZone(zoneID, zoneName, length, width, isProductZone, maxWeight);
                temp.setActive(isActive);
                data.add(temp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (WarehouseZone datum : data) {
            ArrayList<Cell> cells = new ArrayList<>();
            sql = "SELECT * FROM Cell WHERE IDZone = " + datum.getID();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("ID");
                    String name = resultSet.getString("name");
                    boolean isActive = resultSet.getBoolean("isActive");
                    Cell c = new Cell(id, name, datum, datum.getMaxWeight());
                    c.setActive(isActive);
                    cells.add(c);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            cells.sort(Comparator.comparingLong(Cell::getID));

            for (int i = 0, c = 0; i < datum.getCells().length; i++) {
                for (int j = 0; j < datum.getCells()[i].length; j++) {
                    Cell cell = datum.getCells()[i][j];
                    cell.setID(cells.get(c++).getID());
                }
            }
        }

        return data;
    }

    @Override
    public ArrayList<WarehouseZone> getProductZones(boolean onlyActive) {
        ArrayList<WarehouseZone> data = new ArrayList<>();
        String sql = "SELECT wz.ID, wz.name, wz.isProductZone, wz.maxWeight, wz.isActive, c.name AS cellName " +
                "FROM WarehouseZone wz " +
                "LEFT JOIN (SELECT IDZone, MAX(ID) AS maxCellID FROM Cell GROUP BY IDZone) cmax ON wz.ID = cmax.IDZone " +
                "LEFT JOIN Cell c ON cmax.maxCellID = c.ID " +
                "WHERE wz.isProductZone = true";

        if (onlyActive) {
            sql += "AND wz.isActive = true ";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long zoneID = resultSet.getLong("ID");
                String zoneName = resultSet.getString("name");
                int length = 0;
                int width = 0;
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isProductZone = resultSet.getBoolean("isProductZone");
                double maxWeight = resultSet.getDouble("maxWeight");
                String nameCell = resultSet.getString("cellName");

                String regex = "\\d+ - (\\d+) \\|\\| \\d+";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(nameCell);
                if (matcher.find()) {
                    length = Integer.parseInt(matcher.group(1)) - 1;
                    width = Integer.parseInt(matcher.group(2)) - 1;
                }
                WarehouseZone temp = new WarehouseZone(zoneID, zoneName, length, width, isProductZone, maxWeight);
                temp.setActive(isActive);
                data.add(temp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (WarehouseZone datum : data) {
            ArrayList<Cell> cells = new ArrayList<>();
            sql = "SELECT * FROM Cell WHERE IDZone = " + datum.getID();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("ID");
                    String name = resultSet.getString("name");
                    boolean isActive = resultSet.getBoolean("isActive");
                    double maxWeight = resultSet.getDouble("maxWeight");
                    Cell c = new Cell(id, name, datum, maxWeight);
                    c.setActive(isActive);
                    cells.add(c);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            cells.sort(Comparator.comparingLong(Cell::getID));

            for (int i = 0, c = 0; i < datum.getCells().length; i++) {
                for (int j = 0; j < datum.getCells()[i].length; j++) {
                    Cell cell = datum.getCells()[i][j];
                    cell.setID(cells.get(c++).getID());
                }
            }
        }

        return data;
    }

    @Override
    public ArrayList<WarehouseZone> getMaterialZones(boolean onlyActive) {
        ArrayList<WarehouseZone> data = new ArrayList<>();
        String sql = "SELECT wz.ID, wz.name, wz.isProductZone, wz.maxWeight, wz.isActive, c.name AS cellName " +
                "FROM WarehouseZone wz " +
                "LEFT JOIN (SELECT IDZone, MAX(ID) AS maxCellID FROM Cell GROUP BY IDZone) cmax ON wz.ID = cmax.IDZone " +
                "LEFT JOIN Cell c ON cmax.maxCellID = c.ID " +
                "WHERE wz.isProductZone = false";

        if (onlyActive) {
            sql += "AND wz.isActive = true ";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long zoneID = resultSet.getLong("ID");
                String zoneName = resultSet.getString("name");
                int length = 0;
                int width = 0;
                boolean isActive = resultSet.getBoolean("isActive");
                boolean isProductZone = resultSet.getBoolean("isProductZone");
                double maxWeight = resultSet.getDouble("maxWeight");
                String nameCell = resultSet.getString("cellName");

                String regex = "\\d+ - (\\d+) \\|\\| \\d+";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(nameCell);
                if (matcher.find()) {
                    length = Integer.parseInt(matcher.group(1)) - 1;
                    width = Integer.parseInt(matcher.group(2)) - 1;
                }
                WarehouseZone temp = new WarehouseZone(zoneID, zoneName, length, width, isProductZone, maxWeight);
                temp.setActive(isActive);
                data.add(temp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (WarehouseZone datum : data) {
            ArrayList<Cell> cells = new ArrayList<>();
            sql = "SELECT * FROM Cell WHERE IDZone = " + datum.getID();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("ID");
                    String name = resultSet.getString("name");
                    boolean isActive = resultSet.getBoolean("isActive");
                    double maxWeight = resultSet.getDouble("maxWeight");
                    Cell c = new Cell(id, name, datum, maxWeight);
                    c.setActive(isActive);
                    cells.add(c);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            cells.sort(Comparator.comparingLong(Cell::getID));

            for (int i = 0, c = 0; i < datum.getCells().length; i++) {
                for (int j = 0; j < datum.getCells()[i].length; j++) {
                    Cell cell = datum.getCells()[i][j];
                    cell.setID(cells.get(c++).getID());
                }
            }
        }

        return data;
    }

    @Override
    public Cell getCellByID(Long ID) {
        Cell res = null;
        if (ID == null) return null;
        String sql = "SELECT * FROM CELL WHERE ID = " + ID;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                WarehouseZone zone = getZoneByID(resultSet.getLong("IDZONE"));
                String name = resultSet.getString("NAME");
                boolean isActive = resultSet.getBoolean("isActive");
                res = new Cell(ID, name, zone, zone.getMaxWeight());
                res.setActive(isActive);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;

    }

    @Override
    public WarehouseZone getZoneByID(Long ID) {
        WarehouseZone res = null;
        if (ID == null) return res;
        String sql = "SELECT * FROM WAREHOUSEZONE WHERE ID = " + ID;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                double max = resultSet.getDouble("MAXWEIGHT");
                boolean isProductZone = resultSet.getBoolean("ISPRODUCTZONE");
                boolean isActive = resultSet.getBoolean("ISACTIVE");
                res = new WarehouseZone(ID, name, 0, 0, isProductZone, max);
                res.setActive(isActive);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean updateCell(Cell cell) {
        String query = "UPDATE Cell SET name = ?, isActive = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cell.getName());
            statement.setString(2, Boolean.toString(cell.isActive()));
            statement.setLong(3, cell.getID());
            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
