package com.example.patent;

import Model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BooksController {

    public TableView<Receipt> table;
    public TableColumn<Receipt,Long> IDColumn;
    public TableColumn<Receipt, LocalDate> dateColumn;
    public TableColumn<Receipt, User> userColumn;
    public RadioButton RSupply;
    public RadioButton RRepl;
    public RadioButton RMove;
    public CheckBox inCheckBox;
    public DatePicker inDateP;
    public CheckBox byCheckBox;
    public DatePicker byDateP;
    public CheckBox contactorCheckBox;
    public ComboBox<Contractor> contractComboBox;
    public CheckBox userCheckBox;
    public ComboBox<User> userComboBox;
    public CheckBox typeOfItemCheckBox;
    public ComboBox<String> typeOfItemComboBox;
    private ArrayList<ReceiptSupply> receiptSupplies;
    private ArrayList<ReceiptDispatch> receiptDispatches;
    private ArrayList<ReceiptMovement> receiptMovements;
    private ArrayList<Supplier> suppliers;
    private ArrayList<Customer> customers;
    private ArrayList<User> users;
    private  ToggleGroup toggleGroup;
    private TabPane tabPane;

    public void init(ArrayList<ReceiptSupply> receiptSupplies,ArrayList<ReceiptDispatch> receiptDispatches,ArrayList<ReceiptMovement> receiptMovements,
                     ArrayList<User> users, ArrayList<Supplier> suppliers, ArrayList<Customer> customers,TabPane tabPane)
    {
        this.receiptSupplies=receiptSupplies;
        this.receiptDispatches=receiptDispatches;
        this.receiptMovements=receiptMovements;
        this.users=users;
        this.suppliers=suppliers;
        this.customers=customers;
        this.tabPane=tabPane;
        toggleGroup=new ToggleGroup();

        typeOfItemComboBox.setItems(FXCollections.observableArrayList("Товары","Материалы"));
        typeOfItemComboBox.getSelectionModel().selectFirst();
        configureUI();
    }

    private void configureUI(){
        configureTypeRadio();
        updatePersonComboBox();
        configureTable();
    }

    private void updatePersonComboBox(){
        ObservableList<Contractor> temp=FXCollections.observableArrayList();
        temp.addAll(suppliers);
        temp.addAll(customers);
        contractComboBox.setItems(temp);

        userComboBox.setItems(FXCollections.observableArrayList(users));
    }

    private void configureTypeRadio(){
        RSupply.setToggleGroup(toggleGroup);
        RMove.setToggleGroup(toggleGroup);
        RRepl.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // Все радиокнопки выключены
            } else if (newValue == RSupply) {
                RRepl.setSelected(false);
                RMove.setSelected(false);
            } else if (newValue == RRepl) {
                RSupply.setSelected(false);
                RMove.setSelected(false);
            } else if (newValue == RMove) {
                RSupply.setSelected(false);
                RRepl.setSelected(false);
            }
            refreshTable();
        });
        RSupply.setSelected(true);
    }

    private void configureTable(){
        IDColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getID()));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        userColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPerformer()));
    }

    private void sortOnTypeBook(List<Receipt> items){
        if (RSupply.isSelected()) {
            items.addAll(receiptSupplies);
        }
        if (RRepl.isSelected()) {
            items.addAll(receiptDispatches);
        }
        if (RMove.isSelected()) {
            items.addAll(receiptMovements);
        }
        if (!RSupply.isSelected() && !RRepl.isSelected() && !RMove.isSelected()) {
            items.addAll(receiptSupplies);
            items.addAll(receiptDispatches);
            items.addAll(receiptMovements);
        }
    }

    private void sortOnTypeItem(List<Receipt> items){
        if (!typeOfItemCheckBox.isSelected()) return;
        boolean isProductType= Objects.equals(typeOfItemComboBox.getValue(), "Товары");
        items.removeIf(item->item.isProduct()!=isProductType);
    }

    private void sortOnDate(List<Receipt> items){
        if (inCheckBox.isSelected() && inDateP.getValue()!=null) {
            items.removeIf(item -> item.getDate().isBefore(inDateP.getValue()));
        }

        if (byCheckBox.isSelected() && byDateP.getValue()!=null){
            items.removeIf(item -> item.getDate().isAfter(byDateP.getValue()));
        }
    }

    private void sortOnPerson(List<Receipt> items){
        if (contactorCheckBox.isSelected() && contractComboBox.getValue() != null && !RMove.isSelected()) {
            items.removeIf(item -> (item instanceof ReceiptMovement));
            items.removeIf(item -> {
                        if (item instanceof ReceiptSupply) {
                            return ((ReceiptSupply) item).getSupplier() != contractComboBox.getValue();
                        }
                        if (item instanceof ReceiptDispatch) {
                            return ((ReceiptDispatch) item).getCustomer() != contractComboBox.getValue();
                        }
                        return false;
                    });
        }

        if (userCheckBox.isSelected() && userComboBox.getValue() != null) {
            items.removeIf(item -> item.getPerformer() != userComboBox.getValue());
        }
    }

    public void refreshTable(){
        List<Receipt> items= new ArrayList<>();
        sortOnTypeBook(items);
        sortOnTypeItem(items);
        sortOnDate(items);
        sortOnPerson(items);
        table.setItems(FXCollections.observableArrayList(items));
    }

    public void more(ActionEvent actionEvent) throws IOException {
        Receipt select=table.getSelectionModel().getSelectedItem();
        if (select==null) return;
        if (select instanceof ReceiptMovement)showReceiptMovement((ReceiptMovement) select);
        if (select instanceof ReceiptDispatch)showReceiptDispatch((ReceiptDispatch) select);
        if (select instanceof ReceiptSupply)showReceiptSupply((ReceiptSupply) select);
    }

    private void showReceiptSupply(ReceiptSupply receipt) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Запись о поступлении N"+receipt.getID())) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Запись о поступлении N"+receipt.getID());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockReplenishment-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        stockReplenishmentController stockReplenishmentController = fxmlLoader.getController();
        stockReplenishmentController.initForRead(receipt);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void showReceiptDispatch(ReceiptDispatch receipt) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Запись о отгрузке N"+receipt.getID())) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Запись о отгрузке N"+receipt.getID());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("stockDispatch-veiw.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        stockDispatchController stockDispatchController = fxmlLoader.getController();
        stockDispatchController.initForRead(receipt);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void showReceiptMovement(ReceiptMovement receiptMovement) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Запись о перемещении N"+receiptMovement.getID())) {
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab = new Tab("Запись о перемещении N"+receiptMovement.getID());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("movementProduct-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        movementController movementController = fxmlLoader.getController();
        movementController.initForRead(receiptMovement);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
