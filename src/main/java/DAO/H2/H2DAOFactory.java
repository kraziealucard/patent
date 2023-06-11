package DAO.H2;

import DAO.DAOFactory;
import DAO.IPositionDAO;
import DAO.IUserDAO;
import Model.Permission;
import Model.Position;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class H2DAOFactory extends DAOFactory {
    private final String DB_URL;
    private final H2DAOPosition DAOPosition;
    private final H2DAOUser DAOUser;

    public H2DAOFactory() {
        DB_URL = "jdbc:h2:" + System.getProperty("user.dir") + File.separator + "DB\\patentDB";
        DAOPosition = new H2DAOPosition(DB_URL);
        DAOUser = new H2DAOUser(DB_URL);
    }

    public boolean isFirstStart() {
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            try (Statement statement = dbConnection.createStatement()) {
                String sql = "SELECT COUNT(*) AS table_count " +
                        "FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_SCHEMA = 'PUBLIC'";
                ResultSet resultSet = statement.executeQuery(sql);

                int tableCount = 0;
                if (resultSet.next()) {
                    tableCount = resultSet.getInt("table_count");
                }
                if (tableCount == 16) return false;

                sql = "";
                sql = createTablePositions(sql);
                sql = createTableUsers(sql);
                sql = createTableZones(sql);
                sql = createTableCell(sql);
                sql = createTableContractor(sql);
                sql = createTableCustomer(sql);
                sql = createTableSupplier(sql);
                sql = createTableGroupTable(sql);
                sql = createTableItemsTypes(sql);
                sql = createTableReceiptDispatch(sql);
                sql = createTableReceiptMovement(sql);
                sql = createTableReceiptSupply(sql);
                sql = createTableStorageItem(sql);
                sql = createTableListOfReceiptDispatch(sql);
                sql = createTableListOfReceiptMovement(sql);
                sql = createTableListOfReceiptSupply(sql);
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addAdministratorPosition();
        return true;
    }

    private String createTablePositions(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Positions (
                    ID BIGINT PRIMARY KEY auto_increment,
                    name VARCHAR(255) NOT NULL,
                    isActive BOOLEAN NOT NULL,
                    EditingUsersAndPositions BOOLEAN NOT NULL,
                    EditingWarehouseInformation BOOLEAN NOT NULL,
                    ProductLogistic BOOLEAN NOT NULL,
                    ProductEditing BOOLEAN NOT NULL,
                    EditingContactor BOOLEAN NOT NULL,
                    ViewBook BOOLEAN NOT NULL
                );
                                
                """;
    }

    private String createTableUsers(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Users (
                    ID BIGINT PRIMARY KEY auto_increment,
                    IDPosition BIGINT NOT NULL,
                    name VARCHAR(35) NOT NULL,
                    login VARCHAR(35) NOT NULL,
                    password VARCHAR(35) NOT NULL,
                    isActive BOOLEAN NOT NULL,
                    FOREIGN KEY (IDPosition) references Positions(ID)
                );
                                
                """;
    }

    private String createTableZones(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS WarehouseZone (
                    ID BIGINT PRIMARY KEY auto_increment NOT NULL,
                    name VARCHAR(35) NOT NULL,
                    maxWeight DOUBLE NOT NULL,
                    isProductZone BOOLEAN NOT NULL,
                    isActive BOOLEAN NOT NULL
                );
                                
                """;
    }

    private String createTableCell(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Cell (
                    ID BIGINT PRIMARY KEY auto_increment,
                    IDZone BIGINT NOT NULL,
                    name VARCHAR(35) NOT NULL,
                    isActive BOOLEAN NOT NULL,
                    FOREIGN KEY (IDZone) references WarehouseZone(ID)
                );
                                
                """;
    }

    private String createTableContractor(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Contractor (
                    ID BIGINT PRIMARY KEY auto_increment,
                    name VARCHAR(255) NOT NULL,
                    address VARCHAR(255),
                    information VARCHAR(255),
                    passport VARCHAR(255),
                    phone VARCHAR(20),
                    bankRequisites VARCHAR(255),
                    IIN VARCHAR(12),
                    KPP VARCHAR(9) ,
                    isActive BOOLEAN NOT NULL
                );
                                
                """;
    }

    private String createTableCustomer(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Customer (
                    ContractorID INT,
                    PRIMARY KEY (ContractorID),
                    FOREIGN KEY (ContractorID) REFERENCES Contractor(ID)
                );
                                
                """;
    }

    private String createTableSupplier(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS Supplier (
                    ContractorID INT,
                    PRIMARY KEY (ContractorID),
                    FOREIGN KEY (ContractorID) REFERENCES Contractor(ID)
                );
                                
                """;
    }

    private String createTableGroupTable(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS GroupTable (
                    ID BIGINT PRIMARY KEY auto_increment,
                    name VARCHAR(30) NOT NULL,
                    IDParentGroup BIGINT NOT NULL,
                    FOREIGN KEY (IDParentGroup) references GroupTable(ID)
                );
                                
                """;
    }

    private String createTableItemsTypes(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ItemsTypes (
                    ID BIGINT PRIMARY KEY auto_increment,
                    IDGroup BIGINT,
                    name VARCHAR(255) NOT NULL,
                    weight DOUBLE NOT NULL,
                    isProduct BOOLEAN NOT NULL,
                    isActive BOOLEAN NOT NULL,
                    FOREIGN KEY (IDGroup) references GroupTable(ID)
                );
                                
                """;
    }

    private String createTableReceiptDispatch(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ReceiptDispatch (
                    ID INT PRIMARY KEY,
                    date DATE,
                    performerID INT,
                    invoiceNumberField VARCHAR(255),
                    isProduct BOOLEAN,
                    customerID INT,
                    FOREIGN KEY (performerID) REFERENCES Users(ID),
                    FOREIGN KEY (customerID) REFERENCES Customer(CONTRACTORID)
                );
                                
                """;
    }

    private String createTableReceiptMovement(String sql) {
        return sql += """
                 CREATE TABLE IF NOT EXISTS ReceiptMovement (
                    ID INT PRIMARY KEY auto_increment,
                    date DATE,
                    performerID INT,
                    invoiceNumberField VARCHAR(255),
                    isProduct BOOLEAN,
                    FOREIGN KEY (performerID) REFERENCES Users(ID)
                );
                                
                """;
    }

    private String createTableReceiptSupply(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ReceiptSupply (
                    ID INT PRIMARY KEY auto_increment,
                    date DATE,
                    performerID INT,
                    invoiceNumberField VARCHAR(255),
                    isProduct BOOLEAN,
                    supplierID INT,
                    FOREIGN KEY (performerID) REFERENCES Users(ID),
                    FOREIGN KEY (supplierID) REFERENCES Supplier(CONTRACTORID)
                );
                                
                """;
    }

    private String createTableStorageItem(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS StorageItem (
                    ID BIGINT PRIMARY KEY auto_increment,
                    IDItemsType BIGINT NOT NULL,
                    IDCell BIGINT NOT NULL,
                    IDSupplier BIGINT NOT NULL,
                    IDCustomer BIGINT NOT NULL,
                    FOREIGN KEY (IDItemsType) references ITEMSTYPES(ID),
                    FOREIGN KEY (IDCell) references Cell(ID),
                    FOREIGN KEY (IDSupplier) references Supplier(ContractorID),
                    FOREIGN KEY (IDCustomer) references Customer(ContractorID)
                );
                                
                """;
    }

    private String createTableListOfReceiptDispatch(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ListOfReceiptDispatch (
                    ID INT PRIMARY KEY auto_increment,
                    receiptID INT,
                    itemID INT,
                    amount INT,
                    fromCellID INT,
                    FOREIGN KEY (receiptID) REFERENCES RECEIPTDispatch(ID),
                    FOREIGN KEY (itemID) REFERENCES StorageItem(ID),
                    FOREIGN KEY (fromCellID) REFERENCES Cell(ID)
                );
                                
                """;
    }

    private String createTableListOfReceiptMovement(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ListOfReceiptMovement (
                    ID INT PRIMARY KEY auto_increment,
                    receiptID INT,
                    itemID INT,
                    amount INT,
                    fromCellID INT,
                    whereCellID INT,
                    FOREIGN KEY (receiptID) REFERENCES RECEIPTMOVEMENT(ID),
                    FOREIGN KEY (itemID) REFERENCES StorageItem(ID),
                    FOREIGN KEY (fromCellID) REFERENCES Cell(ID),
                    FOREIGN KEY (whereCellID) REFERENCES Cell(ID)
                );
                              
                """;
    }

    private String createTableListOfReceiptSupply(String sql) {
        return sql += """
                CREATE TABLE IF NOT EXISTS ListOfReceiptSupply (
                    ID INT PRIMARY KEY auto_increment,
                    receiptID INT,
                    itemID INT,
                    amount INT,
                    whereCellID INT,
                    FOREIGN KEY (receiptID) REFERENCES RECEIPTSUPPLY (ID),
                    FOREIGN KEY (itemID) REFERENCES StorageItem(ID),
                    FOREIGN KEY (whereCellID) REFERENCES Cell(ID)
                );
                                
                """;
    }

    private void addAdministratorPosition() {
        Position admin = new Position(-1, "Администратор",
                new ArrayList<Permission>(Arrays.stream(Permission.values()).toList()));
        admin.setID(DAOPosition.addPosition(admin));
    }

    @Override
    public IPositionDAO getPositionDAO() {
        return DAOPosition;
    }

    @Override
    public IUserDAO getUserDAO() {
        return DAOUser;
    }

}
