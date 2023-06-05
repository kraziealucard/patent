package com.example.patent;

import Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainViewController {
    public Label LabelNameUser;
    public TabPane tabPane;
    public Button UserBtn;
    public Button PositionBtn;
    public Button ZonesBtn;
    public Button ContractorsBtn;
    public Button ProductListBtn;
    public Button MaterialListBtn;
    
    
    
    public VBox catalogsVbox;
    public Button myCompanyBtn;
    public Button productOnStorageBtn;
    public Button materialOnStorageBtn;
    public VBox recordsVbox;
    public Button supplyProductBtn;
    public Button supplyMaterialBtn;
    public Button dispProductBtn;
    public Button dispMaterialBtn;
    public Button moveProductBtn;
    public Button moveMaterialBtn;
    public Button bookBtn;
    public Button recordMenu;
    public Button catalogMenu;
    private User currentUser;
    private ArrayList<User> userList;
    private ArrayList<Supplier> supplierList;
    private ArrayList<Customer> customerList;
    private ArrayList<Position> positionList;
    private ArrayList<GroupItems> groupItemsList;
    private ArrayList<TypeOfStorageItem> typeOfStorageItemList;
    private ArrayList<WarehouseZone> zones;
    private ArrayList<ReceiptSupply> receiptSupplies;
    private ArrayList<ReceiptDispatch> receiptDispatches;
    private ArrayList<ReceiptMovement> receiptMovements;

    public void init(User user) {
        loadGroupList();
        loadPositions();
        loadCustomersAndSuppliers();
        loadUsers();
        loadStorageItemList();
        loadZones();
        loadReceiptsSupplies();
        loadReceiptsDispatches();
        loadReceiptsMovements();
        currentUser = userList.get(0);

        settingAccess();
        recordMenuClick();
    }

    private void settingAccess(){
        for (int i = 0; i < catalogsVbox.getChildren().size(); i++) {
            catalogsVbox.getChildren().get(i).setManaged(false);
            catalogsVbox.getChildren().get(i).setVisible(false);
        }

        for (int i = 0; i < recordsVbox.getChildren().size(); i++) {
            recordsVbox.getChildren().get(i).setManaged(false);
            recordsVbox.getChildren().get(i).setVisible(false);
        }

        ArrayList<Permission> currentPermissions=currentUser.getPosition().getPermissions();
        for (int i = 0; i < currentPermissions.size(); i++) {
            switch (currentPermissions.get(i))
            {
                case EditingContactor -> {
                    ContractorsBtn.setManaged(true);
                    ContractorsBtn.setVisible(true);
                }

                case ProductEditing -> {
                    ProductListBtn.setManaged(true);
                    ProductListBtn.setVisible(true);

                    MaterialListBtn.setManaged(true);
                    MaterialListBtn.setVisible(true);
                }

                case EditingWarehouseInformation -> {
                    ZonesBtn.setManaged(true);
                    ZonesBtn.setVisible(true);
                }

                case EditingUsersAndPositions -> {
                    UserBtn.setManaged(true);
                    UserBtn.setVisible(true);

                    PositionBtn.setManaged(true);
                    PositionBtn.setVisible(true);
                }

                case ProductLogistic -> {
                    supplyProductBtn.setManaged(true);
                    supplyProductBtn.setVisible(true);

                    supplyMaterialBtn.setManaged(true);
                    supplyMaterialBtn.setVisible(true);

                    dispProductBtn.setManaged(true);
                    dispProductBtn.setVisible(true);

                    dispMaterialBtn.setManaged(true);
                    dispMaterialBtn.setVisible(true);

                    moveProductBtn.setManaged(true);
                    moveProductBtn.setVisible(true);

                    moveMaterialBtn.setManaged(true);
                    moveMaterialBtn.setVisible(true);
                }

                case ViewBook -> {
                    bookBtn.setManaged(true);
                    bookBtn.setVisible(true);
                }
            }

            productOnStorageBtn.setManaged(true);
            productOnStorageBtn.setVisible(true);

            materialOnStorageBtn.setManaged(true);
            materialOnStorageBtn.setVisible(true);

            myCompanyBtn.setManaged(true);
            myCompanyBtn.setVisible(true);
        }
    }


    private void loadGroupList(){groupItemsList=new ArrayList<>();}
    private void loadReceiptsMovements(){receiptMovements=new ArrayList<>();}
    private void loadReceiptsDispatches() {
        receiptDispatches = new ArrayList<>();
    }

    private void loadReceiptsSupplies() {
        receiptSupplies = new ArrayList<>();
    }

    private void loadZones() {
        zones = new ArrayList<>();
        zones.add(new WarehouseZone(1, "Товары", 3, 3, true, 50));
        zones.add(new WarehouseZone(2, "Материалы", 3, 3, false, 50));
    }

    private void loadStorageItemList() {
        typeOfStorageItemList = new ArrayList<>();
        TypeOfStorageItem temp = new TypeOfStorageItem(1, "Товар", 20, true);
        TypeOfStorageItem tampM = new TypeOfStorageItem(2, "Материал", 20, false);
        typeOfStorageItemList.add(temp);
        typeOfStorageItemList.add(tampM);
    }

    private void loadUsers() {
        userList = new ArrayList<>();
        userList.add(new User(1L, "Админнистратор", positionList.get(0), "****************", "***************"));
        User notAdmin = new User(2L, "Не админ", positionList.get(1), "123", "123");
        userList.add(notAdmin);
        notAdmin.setActive(true);
        userList.get(0).setActive(true);
    }
    private void loadCustomersAndSuppliers() {
        customerList = new ArrayList<>();
        customerList.add(new Customer(-1, "Customer"));
        customerList.get(0).setActive(true);

        supplierList = new ArrayList<>();
        supplierList.add(new Supplier(-1, "Supplier"));
        supplierList.get(0).setActive(true);
    }

    private void loadPositions() {
        positionList = new ArrayList<>();
        Position admin = new Position(1, "Администратор",
                new ArrayList<Permission>(Arrays.stream(Permission.values()).toList()));
        Position notAdmin = new Position(2, "Не администратор", new ArrayList<>(Arrays.stream(Permission.values()).toList()));
        positionList.add(admin);
        positionList.add(notAdmin);
    }

    public void toUsersView(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Пользователи")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("users-view.fxml"));
        Tab tab = new Tab("Пользователи");
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        UsersViewController usersViewController = fxmlLoader.getController();
        usersViewController.init(currentUser, userList, positionList);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toPositionView(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Должности")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Должности");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("position-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        PositionViewController positionViewController = fxmlLoader.getController();
        positionViewController.init(positionList);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toContractorView(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Контрагенты")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Контрагенты");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Contractor-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ContractorController contractorController = fxmlLoader.getController();
        contractorController.init(customerList, supplierList);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toMaterialList() throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Материалы")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Материалы");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productList-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ProductListController productListController = fxmlLoader.getController();
        productListController.explainButton.setVisible(false);
        productListController.init(typeOfStorageItemList,groupItemsList, false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toProductList(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Продукты")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Продукты");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productList-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ProductListController productListController = fxmlLoader.getController();
        productListController.explainButton.setVisible(false);
        productListController.init(typeOfStorageItemList, groupItemsList,true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toZoneView() throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Зоны склада")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Зоны склада");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("zone-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ZoneViewController productListController = fxmlLoader.getController();
        productListController.init(zones, tabPane);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toStockReplenishmentProduct(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Поставка товара");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockReplenishment-view.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        stockReplenishmentController stockReplenishmentController = fxmlLoader.getController();
        stockReplenishmentController.init(receiptSupplies, typeOfStorageItemList, zones, supplierList, userList, currentUser, groupItemsList,true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toStockReplenishmentMaterial(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Поставка материала");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockReplenishment-view.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        stockReplenishmentController stockReplenishmentController = fxmlLoader.getController();
        stockReplenishmentController.init(receiptSupplies, typeOfStorageItemList, zones, supplierList, userList, currentUser, groupItemsList,false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toStockDispatchProduct(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Отгрузка товара");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockDispatch-veiw.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        stockDispatchController stockReplenishmentController = fxmlLoader.getController();
        stockReplenishmentController.init(receiptDispatches, typeOfStorageItemList, zones, customerList, userList, currentUser, true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toProductOnWareHouse(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Товары на складе")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Товары на складе");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productOnStorage-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ProductOnStorageController productOnStorageController = fxmlLoader.getController();
        productOnStorageController.init(zones, true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toMovementProduct(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Перемещение товара");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("movementProduct-view.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        movementController movementController = fxmlLoader.getController();
        movementController.init(receiptMovements, typeOfStorageItemList, zones, userList, currentUser, true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toMovementMaterial(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Перемещение материала");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("movementProduct-view.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        movementController movementController = fxmlLoader.getController();
        movementController.init(receiptMovements, typeOfStorageItemList, zones, userList, currentUser, false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toBooksController(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Книга записей")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Книга записей");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Books.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        BooksController productListController = fxmlLoader.getController();
        productListController.init(receiptSupplies, receiptDispatches,receiptMovements,userList,supplierList,customerList,tabPane);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toMaterialOnWareHouse(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Материалы на складе")) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Материалы на складе");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productOnStorage-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        ProductOnStorageController productOnStorageController = fxmlLoader.getController();
        productOnStorageController.init(zones, false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void toStockDispatchMaterial(ActionEvent actionEvent) throws IOException {
        Tab tab = new Tab("Отгрузка материала");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockDispatch-veiw.fxml"));
        Parent root = fxmlLoader.load();

        tab.setContent(root);
        stockDispatchController stockReplenishmentController = fxmlLoader.getController();
        stockReplenishmentController.init(receiptDispatches, typeOfStorageItemList, zones, customerList, userList, currentUser, false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void catalogMenuClick(){
        recordsVbox.setVisible(false);
        recordsVbox.setManaged(false);

        catalogsVbox.setVisible(true);
        catalogsVbox.setManaged(true);
    }

    public void recordMenuClick() {
        catalogsVbox.setVisible(false);
        catalogsVbox.setManaged(false);

        recordsVbox.setVisible(true);
        recordsVbox.setManaged(true);
    }
}