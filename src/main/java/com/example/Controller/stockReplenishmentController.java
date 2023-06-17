package com.example.Controller;

import Model.*;
import Model.Cell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class stockReplenishmentController {
    public TextField idTextField;
    public DatePicker datePicker;
    public ComboBox<User> performerComboBox;
    public ComboBox<Supplier> suppliersComboBox;
    public TextField invoiceNumberTextField;

    public TableView<ReceiptSupply.ListOfReceipt> table;
    public TableColumn<ReceiptSupply.ListOfReceipt, TypeOfStorageItem> ItemColumn;
    public TableColumn<ReceiptSupply.ListOfReceipt, Integer> quantityColumn;
    public TableColumn<ReceiptSupply.ListOfReceipt, Cell> cellColumn;
    public GridPane rootPane;
    public TableColumn<ReceiptSupply.ListOfReceipt, Double> ItemWeightColumn;
    public TableColumn<ReceiptSupply.ListOfReceipt, Double> availableWeightClmn;
    public TableColumn<ReceiptSupply.ListOfReceipt, Double> sumWeightItem;
    public Button addButton;
    public Button removeButton;
    public Button finishButton;
    public Button toExcelBtn;
    private ArrayList<ReceiptSupply> receipts;
    private ArrayList<TypeOfStorageItem> typeOfStorageItems;
    private ArrayList<WarehouseZone> zones;
    private ArrayList<Supplier> suppliers;
    private ArrayList<User> users;
    private User author;
    private boolean isProduct;
    private ReceiptSupply currentReceipt;
    private ObservableList<Cell> cellObservableList;
    private ObservableList<Supplier> supplierObservableList;
    private ObservableList<User> userObservableList;
    private ArrayList<GroupItems> groups;

    public void init(ArrayList<ReceiptSupply> receipts, ArrayList<TypeOfStorageItem> items, ArrayList<WarehouseZone> zones,
                     ArrayList<Supplier> suppliers, ArrayList<User> users, User author, ArrayList<GroupItems> groups, boolean isProduct) {
        this.receipts = receipts;
        this.typeOfStorageItems = items;
        this.zones = zones;
        this.suppliers = suppliers;
        this.author = author;
        this.isProduct = isProduct;
        this.groups = groups;
        this.users = users;
        this.datePicker.setValue(LocalDate.now());
        toExcelBtn.setVisible(false);

        availableWeightClmn.setVisible(true);

        cellObservableList = FXCollections.observableArrayList();
        supplierObservableList = FXCollections.observableArrayList();
        userObservableList = FXCollections.observableArrayList();

        currentReceipt = new ReceiptSupply(-1, LocalDate.now(), author, "", null, isProduct);
        configureUI();
    }

    public void initForRead(ReceiptSupply receiptSupply) {
        currentReceipt = receiptSupply;
        availableWeightClmn.setVisible(false);
        ObservableList<ReceiptSupply.ListOfReceipt> temp = FXCollections.observableArrayList(currentReceipt.getLists());
        table.setItems(temp);


        performerComboBox.setItems(FXCollections.observableArrayList(currentReceipt.getPerformer()));
        performerComboBox.getSelectionModel().selectFirst();

        suppliersComboBox.setItems(FXCollections.observableArrayList(currentReceipt.getSupplier()));
        suppliersComboBox.getSelectionModel().selectFirst();

        idTextField.setText(String.valueOf(receiptSupply.getID()));
        invoiceNumberTextField.setText(receiptSupply.getInvoiceNumberField());

        zones = new ArrayList<>();

        configureTable();
        disableUI();
    }

    private void configureUI() {
        rootPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateFields();
            }
        });

        suppliersComboBox.setItems(supplierObservableList);
        performerComboBox.setItems(userObservableList);

        updateFields();
        configureTable();
    }

    private void updateFields() {
        updateUser();
        updateSuppliers();
        updateCells();
    }

    private void configureTable() {
        table.setRowFactory(tv -> {
            TableRow<Receipt.ListOfReceipt> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    Double cellValue = availableWeightClmn.getCellData(row.getIndex());

                    if (cellValue != null && cellValue < 0) {
                        row.getStyleClass().add("error-table-cell-editable");
                    } else {
                        row.getStyleClass().remove("error-table-cell-editable");
                    }
                } else {
                    row.getStyleClass().remove("error-table-cell-editable");
                }
            });

            return row;
        });

        ItemColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getType()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        cellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCell()));
        ItemWeightColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getType().getWeight()));
        sumWeightItem.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getType().getWeight() * cellData.getValue().getAmount()));
        availableWeightClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAvailableWeight(cellData)));

        cellColumn.setCellFactory(column -> new ComboBoxTableCell<ReceiptSupply.ListOfReceipt, Cell>(cellObservableList) {
            @Override
            public void updateItem(Cell item, boolean empty) {
                super.updateItem(item, empty);
                if (this.getTableRow().getItem() != null) getStyleClass().add("table-cell-editable");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        ItemColumn.setCellFactory(column -> {
            return new TableCell<ReceiptSupply.ListOfReceipt, TypeOfStorageItem>() {
                @Override
                protected void updateItem(TypeOfStorageItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow() != null && this.getTableRow().getItem() != null)
                        getStyleClass().add("table-cell-editable");
                    if (!empty) {
                        setText(item.toString());
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                ReceiptSupply.ListOfReceipt listOfReceipt = getTableView().getItems().get(getIndex());

                                if (listOfReceipt != null) {
                                    showProductTypeList(listOfReceipt);
                                }
                            }
                        });
                    } else {
                        setText(null);
                        setOnMouseClicked(null);
                    }
                }
            };
        });

        quantityColumn.setCellFactory(column -> {
            TableCell<ReceiptSupply.ListOfReceipt, Integer> cell = new TableCell<>() {
                private TextField textField;

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow() != null && this.getTableRow().getItem() != null)
                        getStyleClass().add("table-cell-editable");
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getString());
                            }
                            setGraphic(textField);
                            setText(null);
                        } else {
                            setGraphic(null);
                            setText(getString());
                        }

                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (textField == null) {
                        createTextField();
                    }
                    textField.setText(getString());
                    setGraphic(textField);
                    setText(null);
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getString());
                    setGraphic(null);
                }


                private void createTextField() {
                    textField = new TextField(getString());
                    textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                    textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (!isNowFocused) {
                            commitEditFromString(textField.getText());
                        }
                    });
                    textField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            commitEditFromString(textField.getText());
                        }
                    });
                    textField.setTextFormatter(createTextFormatter());
                }

                private void commitEditFromString(String text) {
                    if (isEditing()) {
                        Integer newValue = getConverter().fromString(text);
                        commitEdit(newValue);
                        setGraphic(null);
                    }
                }

                private TextFormatter<Integer> createTextFormatter() {
                    StringConverter<Integer> converter = getConverter();
                    TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, 0, c -> {
                        if (c.getControlNewText().matches("-?\\d*")) {
                            return c;
                        }
                        return null;
                    });
                    return textFormatter;
                }

                private String getString() {
                    return getItem() != null ? getItem().toString() : "";
                }

                private StringConverter<Integer> getConverter() {
                    return new IntegerStringConverter() {
                        @Override
                        public Integer fromString(String text) {
                            try {
                                return super.fromString(text);
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    };
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    cell.setEditable(true);
                    cell.getTableView().edit(cell.getIndex(), cell.getTableColumn());
                }
            });

            return cell;
        });

        ItemColumn.setText(isProduct ? "Товар" : "Матераиал");
        ItemWeightColumn.setText(isProduct ? "Вес товара" : "Вес материала");

        rootPane.getStylesheets().add(getClass().getResource("editable-column.css").toExternalForm());

    }

    private Double calculateAvailableWeight(TableColumn.CellDataFeatures<ReceiptSupply.ListOfReceipt, Double> cellData) {
        ReceiptSupply.ListOfReceipt row = cellData.getValue();
        ArrayList<ReceiptSupply.ListOfReceipt> temp = new ArrayList<>();
        Double res = null;
        if (row.getCell() == null) return res;
        for (int i = 0; i < table.getItems().size(); i++) {
            if (table.getItems().get(i).getCell() == cellData.getValue().getCell()) {
                temp.add(table.getItems().get(i));
            }
        }

        res = row.getCell().getMaxWeight() - row.getCell().getCurrentWeight();

        for (ReceiptSupply.ListOfReceipt listOfReceipt : temp) {
            res -= listOfReceipt.getItem().getType().getWeight() * listOfReceipt.getAmount();
        }

        return res;
    }

    private void updateCells() {
        cellObservableList.clear();
        for (WarehouseZone zone : zones) {
            if (zone.isProductZone() == isProduct && zone.isActive()) {
                for (int j = 0; j < zone.getCells().length; j++) {
                    cellObservableList.addAll(zone.getCells()[j]);
                }
            }
        }
    }

    private void updateUser() {
        User temp = performerComboBox.getValue();
        userObservableList.clear();
        for (User user : users) {
            if (user.isActive()) userObservableList.add(user);
        }
        if (temp != null) performerComboBox.setValue(temp);
    }

    private void updateSuppliers() {
        Supplier temp = suppliersComboBox.getValue();
        supplierObservableList.clear();
        for (Supplier supplier : suppliers) {
            if (supplier.isActive()) supplierObservableList.add(supplier);
        }
        if (temp != null) suppliersComboBox.setValue(temp);
    }

    private void updateTable() {
        ObservableList<ReceiptSupply.ListOfReceipt> temp = FXCollections.observableArrayList(currentReceipt.getLists());
        table.setItems(temp);
        table.refresh();
    }

    public boolean isColumnValuesNonNegative() {
        for (ReceiptSupply.ListOfReceipt item : table.getItems()) {
            Double value = availableWeightClmn.getCellData(item);
            if (value == null || value < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Перевышен допустемый вес!\n" + "Строки где происходит ошибка,\n подсвечиваются красным цветов");
                alert.showAndWait();
                return false;
            }
        }

        return true;
    }

    private boolean isFieldsAreFilled() {
        if (datePicker.getValue() == null || performerComboBox.getValue() == null || suppliersComboBox.getValue() == null ||
                invoiceNumberTextField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Не все поля заполнены!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean isTableAreFilled() {
        if (table.getItems().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Таблица пустая!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void disableUI() {

        idTextField.setDisable(true);
        idTextField.getStyleClass().add("disabled");

        datePicker.setEditable(false);
        datePicker.setDisable(true);
        datePicker.getStyleClass().add("disabled");

        performerComboBox.setDisable(true);
        performerComboBox.getStyleClass().add("disabled");

        suppliersComboBox.setDisable(true);
        suppliersComboBox.getStyleClass().add("disabled");

        invoiceNumberTextField.setDisable(true);
        invoiceNumberTextField.getStyleClass().add("disabled");

        for (int i = 0; i < table.getColumns().size(); i++) {
            table.getColumns().get(i).setEditable(false);
            table.getColumns().get(i).getStyleClass().add("disabled");
        }

        table.setEditable(false);
        table.setDisable(true);
        table.getStyleClass().add("disabled");

        addButton.setDisable(true);
        addButton.getStyleClass().add("disabled");

        removeButton.setDisable(true);
        removeButton.getStyleClass().add("disabled");

        finishButton.setDisable(true);
        finishButton.getStyleClass().add("disabled");
    }

    private boolean addItemsIntoCell() {

        for (int i = 0; i < currentReceipt.getLists().size(); i++) {
            ReceiptSupply.ListOfReceipt listOfRecord = currentReceipt.getLists().get(i);
            ArrayList<StorageItem> tempList = new ArrayList<>();
            for (int j = 0; j < currentReceipt.getLists().get(i).getAmount(); j++) {
                listOfRecord.getItem().setSupplier(suppliersComboBox.getValue());
                listOfRecord.getItem().setLocationOnStorage(listOfRecord.getCell());
                tempList.add(listOfRecord.getItem());
            }
            if (!listOfRecord.getCell().addProductsAll(tempList)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка добавления элементов в склад!");
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }

    public void finish(ActionEvent actionEvent) {
        if (isFieldsAreFilled() && isTableAreFilled() && isColumnValuesNonNegative() && addItemsIntoCell()) {
            disableUI();
            ReceiptSupply temp = new ReceiptSupply(receipts.size() + 1, datePicker.getValue(), performerComboBox.getValue(),
                    (invoiceNumberTextField.getText()), suppliersComboBox.getValue(), isProduct);
            temp.setLists(currentReceipt.getLists());
            receipts.add(temp);
            idTextField.setText(String.valueOf(receipts.size()));
            Notifications notifications = Notifications.create()
                    .text("Запись успешно сохранена")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .owner(table.getScene().getWindow());
            notifications.show();
            toExcelBtn.setVisible(true);
        }
    }

    private void showProductTypeList(Receipt.ListOfReceipt itemForChange) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                if (stage.getTitle().equals("Добавление в список " + (isProduct ? "продукта" : "материала")) && stage.isShowing()) {
                    stage.requestFocus();
                    return;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productList-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление в список " + (isProduct ? "продукта" : "материала"));
        stage.setScene(scene);

        ProductListController productListController = fxmlLoader.getController();
        productListController.init(typeOfStorageItems, groups, isProduct);
        stage.setResizable(false);
        if (itemForChange == null) {
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (productListController.getItemForReturn() == null) return;
                    TypeOfStorageItem newTypeForAdd = productListController.getItemForReturn();
                    currentReceipt.getLists().add(new Receipt.ListOfReceipt(-1, currentReceipt, new StorageItem(-1, newTypeForAdd, null), 1, null));
                    updateTable();
                }
            });
        } else {
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (productListController.getItemForReturn() == null && itemForChange.getItem() == null) return;
                    itemForChange.getItem().setType(productListController.getItemForReturn());
                    itemForChange.setCell(null);
                    itemForChange.setAmount(1);
                    itemForChange.setID(-1);
                    updateTable();
                }
            });
        }
        stage.show();
    }

    public void addItem(ActionEvent actionEvent) {
        showProductTypeList(null);
    }

    public void removeItem(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        currentReceipt.getLists().remove(table.getSelectionModel().getSelectedItem());
        updateTable();
    }

    public void cancelEditCell(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, Cell> event) {
        //table.getSelectionModel().getSelectedItem().setCell(event.getOldValue());
        updateTable();
    }

    public void cancelEditAmount(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, Integer> event) {
        //table.getSelectionModel().getSelectedItem().setAmount(event.getOldValue());
        updateTable();
    }

    public void cancelEditItem(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, TypeOfStorageItem> event) {
        //table.getSelectionModel().getSelectedItem().getItem().setType(event.getOldValue());
        updateTable();
    }

    public void commitEditItem(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, TypeOfStorageItem> event) {
        table.getSelectionModel().getSelectedItem().getItem().setType(event.getNewValue() == null ? event.getOldValue() : event.getNewValue());
        updateTable();
    }

    public void commitEditAmount(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, Integer> event) {
        table.getSelectionModel().getSelectedItem().setAmount(event.getNewValue() < 1 ? 1 : event.getNewValue());
        updateTable();
    }

    public void commitEditCell(TableColumn.CellEditEvent<ReceiptSupply.ListOfReceipt, Cell> event) {
        table.getSelectionModel().getSelectedItem().setCell(event.getNewValue() == null ? event.getOldValue() : event.getNewValue());
        updateTable();
    }

    public void toExcel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить в Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();

            ExcelConverter.convertToExcelForSupply(table, filePath, datePicker.getValue(),
                    suppliersComboBox.getValue().getName(), invoiceNumberTextField.getText(), performerComboBox.getValue().getName());
        }
    }
}