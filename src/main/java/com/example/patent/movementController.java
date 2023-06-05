package com.example.patent;

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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class movementController {
    public GridPane rootPane;
    public TextField idTextField;
    public DatePicker datePicker;
    public ComboBox<User> performerComboBox;
    public TextField invoiceNumberTextField;

    public TableView<ReceiptMovement.ListOfReceiptMovement> table;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, TypeOfStorageItem> ItemColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Integer> amountOnCellColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Integer> amountColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Double> weightItemColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Cell> fromColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Double> sumWeightColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Cell> whereColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Double> availableWeightColumn;
    public TableColumn<ReceiptMovement.ListOfReceiptMovement, Integer>  availableAmountColumn;
    public Button addButton;
    public Button removeButton;
    public Button finishButton;
    private ArrayList<ReceiptMovement> receipts;
    private ArrayList<TypeOfStorageItem> items;
    private ArrayList<WarehouseZone> zones;
    private ArrayList<User> users;
    private User author;
    private boolean isProduct;
    private ObservableList<Model.Cell> cellObservableList;
    private ReceiptMovement currentReceipt;

    public void init(ArrayList<ReceiptMovement> receipts, ArrayList<TypeOfStorageItem> items,
                     ArrayList<WarehouseZone> zones,
                     ArrayList<User> users, User author, boolean isProduct) {

        this.receipts = receipts;
        this.items = items;
        this.zones = zones;
        this.users = users;
        this.author = author;
        this.isProduct = isProduct;
        currentReceipt=new ReceiptMovement(-1, LocalDate.now(), author, "",isProduct);
        cellObservableList=FXCollections.observableArrayList();
        datePicker.setValue(LocalDate.now());

        configureUI();
    }

    public void initForRead(ReceiptMovement receiptMovement) {
        currentReceipt = receiptMovement;
        ObservableList<ReceiptMovement.ListOfReceiptMovement> temp = FXCollections.observableArrayList(currentReceipt.getListMovement());
        table.setItems(temp);

        System.out.println(currentReceipt.getListMovement());

        performerComboBox.setItems(FXCollections.observableArrayList(currentReceipt.getPerformer()));
        performerComboBox.getSelectionModel().selectFirst();

        idTextField.setText(String.valueOf(currentReceipt.getID()));
        invoiceNumberTextField.setText(currentReceipt.getInvoiceNumberField());
        availableAmountColumn.setVisible(false);
        availableWeightColumn.setVisible(false);

        configureTable();
        disableUI();
    }

    private void updateTable() {
        ObservableList<ReceiptMovement.ListOfReceiptMovement> temp = FXCollections.observableArrayList(currentReceipt.getListMovement());
        table.setItems(temp);
        table.refresh();
    }

    private void configureUI(){
        rootPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateFields();
            }
        });

        performerComboBox.setItems(FXCollections.observableArrayList());
        updateFields();
        configureTable();
    }

    private Integer calculateAvailableAmount(TableColumn.CellDataFeatures<ReceiptMovement.ListOfReceiptMovement, Integer> cellData) {
        if (cellData.getValue() == null) return null;
        ReceiptMovement.ListOfReceiptMovement row = cellData.getValue();
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


    private void updateFields() {
        updateUser();
        updateCells();
    }
    private void configureTable(){
        table.setRowFactory(tv -> {
            TableRow<ReceiptMovement.ListOfReceiptMovement> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && availableAmountColumn.isVisible()) {
                    Integer availableAmount = availableAmountColumn.getCellData(row.getIndex());
                    Double availableWeight=availableWeightColumn.getCellData(row.getIndex());
                    Cell where=whereColumn.getCellData(row.getIndex());
                    if ((availableAmount == null || availableAmount < 0)  || (availableWeight== null || availableWeight<0) || where==null) {
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
        weightItemColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getType().getWeight()));
        fromColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getLocationOnStorage()));
        amountOnCellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAmountOnCell(cellData)));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        sumWeightColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getType().getWeight() * cellData.getValue().getAmount()));
        whereColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getWhere()));
        availableWeightColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAvailableWeight(cellData)));
        availableAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAvailableAmount(cellData)));

        ItemColumn.setCellFactory(column -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(TypeOfStorageItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow()!=null && this.getTableRow().getItem() != null) getStyleClass().add("table-cell-editable");
                    if (!empty) {
                        setText(item.toString());
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                ReceiptMovement.ListOfReceiptMovement listOfReceipt = getTableView().getItems().get(0);

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
        amountColumn.setCellFactory(column -> {
            TableCell<ReceiptMovement.ListOfReceiptMovement, Integer> cell = new TableCell<>() {
                private TextField textField;
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow()!=null && this.getTableRow().getItem() != null) getStyleClass().add("table-cell-editable");
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
        whereColumn.setCellFactory(column -> new TableCell<ReceiptMovement.ListOfReceiptMovement, Cell>() {
            private final ComboBox<Cell> comboBox = new ComboBox<>(cellObservableList);
            {
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (isEditing()) {
                        commitEdit(comboBox.getValue());
                    }
                });

                comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                comboBox.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        startEdit();
                    }
                });
            }

            @Override
            protected void updateItem(Cell item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        comboBox.setValue(item);
                        setText(comboBox.getValue().getName());
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
                if (!isEmpty()) {
                    setGraphic(comboBox);
                    setText(null);
                    comboBox.requestFocus();
                }
            }
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem().getName());
                setGraphic(null);
            }
        });

        ItemColumn.setText(isProduct ? "Товар" : "Матераиал");
        rootPane.getStylesheets().add(getClass().getResource("editable-column.css").toExternalForm());
    }
    private void showProductTypeList(ReceiptMovement.ListOfReceiptMovement itemForChange) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                if (stage.getTitle().equals("Добавление в список перемещения" + (isProduct ? "продукта" : "материала")) && stage.isShowing()) {
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
        productOnStorageController.init(zones, isProduct);
        stage.setResizable(false);
        if (itemForChange == null) {
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (productOnStorageController.getItemForReturn() == null) return;
                    StorageItem itemForAdd = productOnStorageController.getItemForReturn();
                    currentReceipt.getListMovement().add(new ReceiptMovement.ListOfReceiptMovement(-1, currentReceipt, itemForAdd, 1, itemForAdd.getLocationOnStorage(),null));
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
                    itemForChange.setFrom(productOnStorageController.getItemForReturn().getLocationOnStorage());
                    itemForChange.setAmount(1);
                    itemForChange.setWhere(null);
                    updateTable();
                }
            });
        }
        stage.show();
    }
    private Double calculateAvailableWeight(TableColumn.CellDataFeatures<ReceiptMovement.ListOfReceiptMovement, Double> cellData) {
        ReceiptMovement.ListOfReceiptMovement row = cellData.getValue();
        ArrayList<ReceiptMovement.ListOfReceiptMovement> tempWhere = new ArrayList<>();
        ArrayList<ReceiptMovement.ListOfReceiptMovement> tempFrom = new ArrayList<>();

        Double res = null;
        if (row.getWhere() == null) return res;
        for (int i = 0; i < table.getItems().size(); i++) {
            if (table.getItems().get(i).getWhere() == cellData.getValue().getWhere()) {
                tempWhere.add(table.getItems().get(i));
            }

            if (table.getItems().get(i).getWhere()==cellData.getValue().getFrom()) {
                tempFrom.add(table.getItems().get(i));
            }
        }

        res = row.getWhere().getMaxWeight() - row.getWhere().getCurrentWeight();

        for (ReceiptMovement.ListOfReceiptMovement listOfReceipt : tempWhere) {
            res -= listOfReceipt.getItem().getType().getWeight() * listOfReceipt.getAmount();
        }

        for (ReceiptMovement.ListOfReceiptMovement listOfReceipt : tempFrom) {
            res += listOfReceipt.getItem().getType().getWeight() * listOfReceipt.getAmount();
        }

        return res;
    }
    private Integer calculateAmountOnCell(TableColumn.CellDataFeatures<ReceiptMovement.ListOfReceiptMovement, Integer> cellData) {
        Integer res = 0;
        if (cellData.getValue() == null || cellData.getValue().getItem() == null || cellData.getValue().getCell() == null)
            return null;

        ArrayList<StorageItem> items = cellData.getValue().getCell().getStored();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType().isActive() && items.get(i) == cellData.getValue().getItem()) res++;
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

        if (table.getSelectionModel().getSelectedItem()==null) return;
        cellObservableList.remove(table.getSelectionModel().getSelectedItem().getItem().getLocationOnStorage());
    }
    private void updateUser() {
        User temp = performerComboBox.getValue();
        performerComboBox.getItems().clear();
        for (User user : users) {
            if (user.isActive()) performerComboBox.getItems().add(user);
        }
        if (temp != null) performerComboBox.setValue(temp);
    }
    private boolean isFieldsAreFilled() {
        if (datePicker.getValue() == null || performerComboBox.getValue() == null  ||
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
    private boolean isColumnAmountValuesNonNegative() {
        for (int i = 0; i < table.getItems().size(); i++) {
            if (availableWeightColumn.getCellData(i) == null || availableWeightColumn.getCellData(i) < 0) {
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
    private void disableUI() {

        idTextField.setDisable(true);
        idTextField.getStyleClass().add("disabled");

        datePicker.setEditable(false);
        datePicker.setDisable(true);
        datePicker.getStyleClass().add("disabled");

        performerComboBox.setDisable(true);
        performerComboBox.getStyleClass().add("disabled");

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

    private void moveItems(){
        for (int i = 0; i < currentReceipt.getLists().size(); i++) {
            ReceiptMovement.ListOfReceiptMovement listOfRecord = currentReceipt.getListMovement().get(i);
            for (int j = 0; j < currentReceipt.getLists().get(i).getAmount(); j++) {
                listOfRecord.getWhere().addProduct(listOfRecord.getItem());
                listOfRecord.getCell().removeItem(listOfRecord.getItem());
            }
        }
    }

    public void finish(ActionEvent actionEvent) {
        if (isFieldsAreFilled() && isTableAreFilled() && isColumnAmountValuesNonNegative()) {
            disableUI();
            moveItems();
            ReceiptMovement temp = new ReceiptMovement(receipts.size() + 1, datePicker.getValue(), performerComboBox.getValue(),
                    invoiceNumberTextField.getText(),isProduct);
            temp.setListMovement(currentReceipt.getListMovement());
            receipts.add(temp);
            idTextField.setText(String.valueOf(receipts.size()));
            Notifications notifications = Notifications.create()
                    .text("Запись успешно сохранена")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .owner(table.getScene().getWindow());
            notifications.show();
        }
    }

    public void removeItem(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        currentReceipt.getListMovement().remove(table.getSelectionModel().getSelectedItem());
        updateTable();
    }

    public void addItem(ActionEvent actionEvent) {
        showProductTypeList(null);
        updateTable();
    }

    public void commitEditAmount(TableColumn.CellEditEvent<ReceiptMovement.ListOfReceiptMovement, Integer>  cellEditEvent) {
        if (cellEditEvent.getNewValue()==null) return;
        table.getSelectionModel().getSelectedItem().setAmount(cellEditEvent.getNewValue());
        updateTable();
    }

    public void commitEditItem(TableColumn.CellEditEvent<ReceiptMovement.ListOfReceiptMovement,TypeOfStorageItem>  cellEditEvent) {
        updateTable();
    }

    public void commitWhereEdit(TableColumn.CellEditEvent<ReceiptMovement.ListOfReceiptMovement, Cell> event) {
        if (event.getNewValue()==null) return;
        table.getSelectionModel().getSelectedItem().setWhere(event.getNewValue());
        System.out.println("s"+table.getSelectionModel().getSelectedItem().getWhere());
        updateTable();
    }
}
