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
import java.util.List;

public class stockDispatchController {
    public GridPane rootPane;
    public TextField idTextField;
    public TextField invoiceNumberTextField;
    public DatePicker datePicker;
    public Button addButton;
    public Button removeButton;
    public Button finishButton;
    public ComboBox<User> performerComboBox;
    public ComboBox<Customer> customersComboBox;

    public TableView<ReceiptDispatch.ListOfReceipt> table;
    public TableColumn<ReceiptDispatch.ListOfReceipt, TypeOfStorageItem> ItemColumn;
    public TableColumn<ReceiptDispatch.ListOfReceipt, Cell> cellColumn;
    public TableColumn<ReceiptDispatch.ListOfReceipt, Integer> amountOnCellColumn;
    public TableColumn<ReceiptDispatch.ListOfReceipt, Integer> amountColumn;
    public TableColumn<ReceiptDispatch.ListOfReceipt, Integer> availableAmountColumn;
    public Button ExcelBtn;
    private ArrayList<WarehouseZone> warehouseZones;
    private ArrayList<TypeOfStorageItem> typeOfStorageItems;
    private ArrayList<ReceiptDispatch> receipts;
    private ArrayList<Customer> customers;
    private ArrayList<User> users;
    private ReceiptDispatch currentReceipt;
    private ObservableList<TypeOfStorageItem> typeOfStorageItemObservableList;
    private ObservableList<User> userObservableList;
    private ObservableList<Customer> customerObservableList;
    private User author;
    private boolean isProduct;


    public void init(ArrayList<ReceiptDispatch> receipts, ArrayList<TypeOfStorageItem> items, ArrayList<WarehouseZone> zones,
                     ArrayList<Customer> customers, ArrayList<User> users, User author, boolean isProduct) {

        this.receipts = receipts;
        this.typeOfStorageItems = items;
        this.warehouseZones = zones;
        this.isProduct = isProduct;
        this.author = author;
        this.users = users;
        this.customers = customers;

        ExcelBtn.setVisible(false);

        typeOfStorageItemObservableList = FXCollections.observableArrayList();
        customerObservableList = FXCollections.observableArrayList();
        userObservableList = FXCollections.observableArrayList();

        currentReceipt = new ReceiptDispatch(-1, LocalDate.now(), author, "", null, isProduct);
        configureUI();
    }

    public void initForRead(ReceiptDispatch receiptDispatch) {
        currentReceipt = receiptDispatch;
        ObservableList<ReceiptDispatch.ListOfReceipt> temp = FXCollections.observableArrayList(currentReceipt.getLists());
        table.setItems(temp);

        performerComboBox.setItems(FXCollections.observableArrayList(currentReceipt.getPerformer()));
        performerComboBox.getSelectionModel().selectFirst();

        customersComboBox.setItems(FXCollections.observableArrayList(currentReceipt.getCustomer()));
        customersComboBox.getSelectionModel().selectFirst();

        idTextField.setText(String.valueOf(currentReceipt.getID()));
        invoiceNumberTextField.setText(currentReceipt.getInvoiceNumberField());
        availableAmountColumn.setVisible(false);

        configureTable();
        disableUI();
    }

    private void updateTable() {
        ObservableList<ReceiptSupply.ListOfReceipt> temp = FXCollections.observableArrayList(currentReceipt.getLists());
        table.setItems(temp);
        table.refresh();
    }

    private void configureUI() {
        rootPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateFields();
            }
        });

        updateFields();
        configureFields();
        configureTable();
    }

    private void configureFields() {
        customersComboBox.setItems(customerObservableList);
        performerComboBox.setItems(userObservableList);
        datePicker.setValue(LocalDate.now());
    }

    private void updateFields() {
        updateUser();
        updateCustomers();
        updateItems();
    }

    private ObservableList<Cell> updateCells() {
        List<Cell> result = new ArrayList<>();

        ObservableList<Cell> cellsObservableList = FXCollections.observableArrayList();
        for (WarehouseZone zone : warehouseZones) {
            if (!zone.isActive() || zone.isProductZone() != isProduct) continue;
            Cell[][] cells = zone.getCells();
            for (Cell[] row : cells) {
                for (Cell cell : row) {
                    if (cell.getStored().contains(table.getSelectionModel().getSelectedItem().getItem())) {
                        result.add(cell);
                    }
                }
            }
        }
        cellsObservableList.addAll(result);
        return cellsObservableList;
    }

    private void updateItems() {
        typeOfStorageItemObservableList.clear();
        for (TypeOfStorageItem typeOfStorageItem : typeOfStorageItems) {
            if (typeOfStorageItem.isProduct() == isProduct && typeOfStorageItem.isActive()) {
                typeOfStorageItemObservableList.add(typeOfStorageItem);
            }
        }
    }

    private void updateCustomers() {
        Customer temp = customersComboBox.getValue();
        customerObservableList.clear();
        for (Customer customer : customers) {
            if (customer.isActive()) customerObservableList.add(customer);
        }
        if (temp != null && temp.isActive()) customersComboBox.setValue(temp);
        else customersComboBox.setValue(null);
    }

    private void updateUser() {
        User temp = performerComboBox.getValue();
        userObservableList.clear();
        for (User user : users) {
            if (user.isActive()) userObservableList.add(user);
        }
        if (temp != null) performerComboBox.setValue(temp);
    }

    private void configureTable() {

        table.setRowFactory(tv -> {
            TableRow<Receipt.ListOfReceipt> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && availableAmountColumn.isVisible()) {
                    Integer cellValue = availableAmountColumn.getCellData(row.getIndex());

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
        cellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCell()));
        amountOnCellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAmountOnCell(cellData)));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        availableAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAvailableAmount(cellData)));

        ItemColumn.setCellFactory(column -> {
            return new TableCell<ReceiptSupply.ListOfReceipt, TypeOfStorageItem>() {
                @Override
                protected void updateItem(TypeOfStorageItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow() != null && this.getTableRow() != null && this.getTableRow().getItem() != null)
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

        /*cellColumn.setCellFactory(column -> new ComboBoxTableCell<ReceiptSupply.ListOfReceipt, Cell>(cellsObservableList) {
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
        });*/

        cellColumn.setCellFactory(column -> {
            return new TableCell<ReceiptDispatch.ListOfReceipt, Cell>() {
                private ComboBox<Cell> comboBox;

                @Override
                protected void updateItem(Cell item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow().getItem() != null) getStyleClass().add("table-cell-editable");
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (comboBox != null) {
                                comboBox.setValue(item);
                            }
                            setText(null);
                            setGraphic(comboBox);
                        } else {
                            setText(item.toString());
                            setGraphic(null);
                        }
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();

                    if (comboBox == null) {
                        comboBox = new ComboBox<>();
                        comboBox.maxWidthProperty().bind(widthProperty());
                        comboBox.setItems(updateCells());
                        comboBox.setOnAction(e -> {
                            commitEdit(comboBox.getValue());
                        });
                    }

                    comboBox.setValue(getItem());

                    setText(null);
                    setGraphic(comboBox);
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();

                    setText(getItem().getName());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Cell newValue) {
                    super.commitEdit(newValue);
                    table.getSelectionModel().getSelectedItem().setCell(newValue);
                    updateTable();
                    setText(getItem().getName());
                    setGraphic(null);
                }
            };
        });

        amountColumn.setCellFactory(column -> {
            TableCell<ReceiptSupply.ListOfReceipt, Integer> cell = new TableCell<>() {
                private TextField textField;

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow().getItem() != null) getStyleClass().add("table-cell-editable");
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

        rootPane.getStylesheets().add(getClass().getResource("editable-column.css").toExternalForm());
    }

    private Integer calculateAmountOnCell(TableColumn.CellDataFeatures<Receipt.ListOfReceipt, Integer> cellData) {
        Integer res = 0;
        if (cellData.getValue() == null || cellData.getValue().getItem() == null || cellData.getValue().getCell() == null)
            return null;

        ArrayList<StorageItem> items = cellData.getValue().getCell().getStored();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType().isActive() && items.get(i) == cellData.getValue().getItem()) res++;
        }


        return res;
    }

    public void cancelEditItem(TableColumn.CellEditEvent<Receipt.ListOfReceipt, TypeOfStorageItem> event) {
        //table.getSelectionModel().getSelectedItem().setItem(event.getOldValue());
        updateTable();
    }

    public void cancelEditCell(TableColumn.CellEditEvent<Receipt.ListOfReceipt, Cell> event) {
        //table.getSelectionModel().getSelectedItem().setCell(event.getOldValue());
        updateTable();
    }

    public void cancelEditAmount(TableColumn.CellEditEvent<Receipt.ListOfReceipt, Integer> event) {
        //table.getSelectionModel().getSelectedItem().setAmount(event.getOldValue());
        updateTable();
    }

    public void commitEditItem(TableColumn.CellEditEvent<Receipt.ListOfReceipt, TypeOfStorageItem> event) {
        //table.getSelectionModel().getSelectedItem().setItem(event.getNewValue() != null ? event.getNewValue() : event.getOldValue());
        updateTable();
    }

    public void commitEditCell(TableColumn.CellEditEvent<Receipt.ListOfReceipt, Cell> event) {
        table.getSelectionModel().getSelectedItem().setCell(event.getNewValue() == null ? event.getOldValue() : event.getNewValue());
        updateTable();
    }

    public void commitEditAmount(TableColumn.CellEditEvent<Receipt.ListOfReceipt, Integer> event) {
        table.getSelectionModel().getSelectedItem().setAmount(event.getNewValue() < 1 ? 1 : event.getNewValue());
        updateTable();
    }

    public void addItem(ActionEvent actionEvent) {
        showProductTypeList(null);
        updateTable();
    }

    public void removeItem(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        currentReceipt.getLists().remove(table.getSelectionModel().getSelectedItem());
        updateTable();
    }

    private boolean isFieldsAreFilled() {
        if (datePicker.getValue() == null || performerComboBox.getValue() == null || customersComboBox.getValue() == null ||
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

        customersComboBox.setDisable(true);
        customersComboBox.getStyleClass().add("disabled");

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

    private void removeItemsFromCell() {
        for (int i = 0; i < currentReceipt.getLists().size(); i++) {
            ReceiptDispatch.ListOfReceipt listOfRecord = currentReceipt.getLists().get(i);
            for (int j = 0; j < currentReceipt.getLists().get(i).getAmount(); j++) {
                listOfRecord.getItem().setCustomer(customersComboBox.getValue());
                listOfRecord.getCell().removeItem(listOfRecord.getItem());
            }

        }
    }

    public void finish(ActionEvent actionEvent) {
        if (isFieldsAreFilled() && isTableAreFilled() && isColumnAmountValuesNonNegative()) {
            disableUI();
            removeItemsFromCell();
            ReceiptDispatch temp = new ReceiptDispatch(receipts.size() + 1, datePicker.getValue(), performerComboBox.getValue(),
                    invoiceNumberTextField.getText(), customersComboBox.getValue(), isProduct);
            temp.setLists(currentReceipt.getLists());
            receipts.add(temp);
            idTextField.setText(String.valueOf(receipts.size()));
            Notifications notifications = Notifications.create()
                    .text("Запись успешно сохранена")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .owner(table.getScene().getWindow());
            notifications.show();
            ExcelBtn.setVisible(true);
        }
    }

    private void showProductTypeList(Receipt.ListOfReceipt itemForChange) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                if (stage.getTitle().equals("Добавление в список отгрузки" + (isProduct ? "продукта" : "материала")) && stage.isShowing()) {
                    stage.requestFocus();
                    return;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productOnStorage-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление в список отгрузки" + (isProduct ? "продукта" : "материала"));
        stage.setScene(scene);

        ProductOnStorageController productOnStorageController = fxmlLoader.getController();
        productOnStorageController.init(warehouseZones, isProduct);
        stage.setResizable(false);
        if (itemForChange == null) {
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (productOnStorageController.getItemForReturn() == null) return;
                    StorageItem itemForAdd = productOnStorageController.getItemForReturn();
                    currentReceipt.getLists().add(new Receipt.ListOfReceipt(-1, currentReceipt, itemForAdd, 1, itemForAdd.getLocationOnStorage()));
                    updateTable();
                }
            });
        } else {
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (productOnStorageController.getItemForReturn() == null || itemForChange.getItem() == null)
                        return;
                    itemForChange.setItem(productOnStorageController.getItemForReturn());
                    itemForChange.setCell(productOnStorageController.getItemForReturn().getLocationOnStorage());
                    updateTable();
                }
            });
        }
        stage.show();
    }

    private Integer calculateAvailableAmount(TableColumn.CellDataFeatures<Receipt.ListOfReceipt, Integer> cellData) {
        if (cellData.getValue() == null) return null;
        Receipt.ListOfReceipt row = cellData.getValue();
        Integer temp = 0;
        Integer res = null;
        for (int i = 0; i < table.getItems().size(); i++) {
            if (table.getItems().get(i) != null && table.getItems().get(i).getCell() == cellData.getValue().getCell() && table.getItems().get(i).getItem() == cellData.getValue().getItem()) {
                temp += (amountColumn.getCellData(i));
            }
        }

        if (row.getCell() == null) return res;
        int index = table.getItems().indexOf(cellData.getValue());
        res = amountOnCellColumn.getCellData(index) - temp;

        return res;
    }

    private boolean isColumnAmountValuesNonNegative() {
        for (int i = 0; i < table.getItems().size(); i++) {
            if (availableAmountColumn.getCellData(i) == null || availableAmountColumn.getCellData(i) < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Кол-во к отгрузке превышает кол-во в ячеке!\n" + "Строки где происходит ошибка,\n подсвечиваются красным цветов");
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }

    public void toExcel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить в Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();

            ExcelConverter.convertToExcelForDispatch(table, filePath, datePicker.getValue(),
                    customersComboBox.getValue().getName(), invoiceNumberTextField.getText(), performerComboBox.getValue().getName());
        }
    }
}
