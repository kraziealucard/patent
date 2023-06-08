package com.example.Controller;

import Model.*;
import Model.Cell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ProductOnStorageController {

    public TableView<StorageItem> table;
    public TableColumn<StorageItem, Long> idColumn;
    public TableColumn<StorageItem, String> nameColumn;
    public TableColumn<StorageItem, GroupItems> groupColumn;
    public TableColumn<StorageItem, Supplier> supplierColumn;
    public TableColumn<StorageItem, WarehouseZone> zoneColumn;
    public TableColumn<StorageItem, Cell> cellColumn;
    public TableColumn<StorageItem, Long> amountOnWarehouseColumn;
    public TableColumn<StorageItem, Long> amountOnZoneColumn;
    public TableColumn<StorageItem, Long> amountOnCellColumn;
    public Label labelStorage;
    public CheckBox groupFilterCheckBox;
    public ComboBox<GroupItems> groupFilterComboBox;
    public CheckBox zoneFilterCheckBox;
    public ComboBox<WarehouseZone> zoneFilterComboBox;
    public TextField searchTextField;
    private ObservableList<GroupItems> groupItemsObservableList;
    private ObservableList<WarehouseZone> zoneObservableList;
    private ObservableList<StorageItem> storageItemObservableList;
    private ArrayList<WarehouseZone> zones;
    private StorageItem itemForReturn;
    private boolean isProduct;

    public void init(ArrayList<WarehouseZone> zones, boolean isProduct) {
        this.isProduct = isProduct;
        this.zones = zones;

        groupItemsObservableList = FXCollections.observableArrayList();
        groupFilterComboBox.setItems(groupItemsObservableList);
        groupFilterComboBox.getSelectionModel().selectFirst();

        zoneObservableList = FXCollections.observableArrayList();
        zoneFilterComboBox.setItems(zoneObservableList);
        zoneFilterComboBox.getSelectionModel().selectFirst();

        storageItemObservableList = FXCollections.observableArrayList();
        table.setItems(storageItemObservableList);

        labelStorage.setText(isProduct ? "Товары: " : "Материалы");
        configureUI();
        updateTable();
    }

    public void toExplain() {
        if (table.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось добавить " + (isProduct ? "продукт" : "материал") + " в список поставки");
            alert.setContentText("Пожалуйста, выделите желаемый " + (isProduct ? "продукт" : "материал") + " для добавления в список поставки.");
            alert.showAndWait();
            return;
        }

        itemForReturn = table.getSelectionModel().getSelectedItem();
        ((Stage) table.getScene().getWindow()).close();
    }

    public StorageItem getItemForReturn() {
        return itemForReturn;
    }

    private void updateTable() {
        ArrayList<WarehouseZone> tempZoneList = zones.stream()
                .filter(zone -> zone.isActive() && zone.isProductZone() == isProduct)
                .collect(Collectors.toCollection(ArrayList::new));


        /*storageItemObservableList.addAll(tempZoneList.stream()
                .flatMap(warehouseZone -> Arrays.stream(warehouseZone.getCells())
                        .flatMap(Arrays::stream)
                        .flatMap(cell -> cell.getStored().stream()))
                .filter(storageItem -> storageItem.getSupplier() != null &&
                        storageItem.getLocationOnStorage() != null &&
                        storageItem.getType() != null)
                .toList());*/


        updateAllStorageItems(tempZoneList);
        updateGroupComboBox();
        updateZoneComboBox(tempZoneList);
        filter();
    }

    private void updateAllStorageItems(ArrayList<WarehouseZone> tempZoneList) {
        for (WarehouseZone zone : tempZoneList) {
            for (Cell[] row : zone.getCells()) {
                for (Cell cell : row) {
                    for (StorageItem storageItem : cell.getStored()) {
                        boolean isUnique = true;
                        for (StorageItem existingItem : storageItemObservableList) {
                            if (existingItem.getType() == storageItem.getType() &&
                                    existingItem.getLocationOnStorage() == storageItem.getLocationOnStorage() &&
                                    existingItem.getSupplier() == storageItem.getSupplier()) {
                                isUnique = false;
                                break;
                            }
                        }
                        if (isUnique) {
                            storageItemObservableList.add(storageItem);
                        }
                    }
                }
            }
        }
    }

    private void updateZoneComboBox(ArrayList<WarehouseZone> tempZoneList) {
        WarehouseZone oneTempZone = zoneFilterComboBox.getValue();
        zoneObservableList.addAll(tempZoneList);
        zoneFilterComboBox.getSelectionModel().selectFirst();
        if (oneTempZone != null && zoneObservableList.contains(oneTempZone))
            zoneFilterComboBox.getSelectionModel().select(oneTempZone);
    }

    private void updateGroupComboBox() {
        Set<GroupItems> uniqueGroupItems = storageItemObservableList.stream()
                .map(obj -> obj.getType().getGroup())
                .collect(Collectors.toSet());

        GroupItems tempGroupItems = groupFilterComboBox.getValue();
        groupItemsObservableList.clear();
        groupItemsObservableList.addAll(uniqueGroupItems);

        groupFilterComboBox.getSelectionModel().selectFirst();
        if (tempGroupItems != null && groupItemsObservableList.contains(tempGroupItems))
            groupFilterComboBox.getSelectionModel().select(tempGroupItems);
    }

    private void configureUI() {
        configureTable();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType().getID()));
        nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType().getName()));
        groupColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType().getGroup()));
        supplierColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSupplier()));
        zoneColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLocationOnStorage().getZone()));
        cellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLocationOnStorage()));
        amountOnCellColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAmountOnCellColumn(cellData)));
        amountOnZoneColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAmountOnZoneColumn(cellData)));
        amountOnWarehouseColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(calculateAmountOnWarehouseColumn(cellData)));
    }

    private Long calculateAmountOnWarehouseColumn(TableColumn.CellDataFeatures<StorageItem, Long> cellData) {
        long res = table.getItems().stream()
                .filter(storageItem -> storageItem.getType() == cellData.getValue().getType() &&
                        storageItem.getSupplier() == cellData.getValue().getSupplier())
                .mapToLong(item -> amountOnZoneColumn.getCellData(item)).sum();

        return res == 0 ? null : res;
    }

    private Long calculateAmountOnZoneColumn(TableColumn.CellDataFeatures<StorageItem, Long> cellData) {
        long res = table.getItems().stream()
                .filter(storageItem -> storageItem.getType() == cellData.getValue().getType() &&
                        storageItem.getSupplier() == cellData.getValue().getSupplier() &&
                        storageItem.getLocationOnStorage().getZone() == cellData.getValue().getLocationOnStorage().getZone())
                .mapToLong(item -> amountOnCellColumn.getCellData(item))
                .sum();

        return res == 0 ? null : res;
    }

    private Long calculateAmountOnCellColumn(TableColumn.CellDataFeatures<StorageItem, Long> cellData) {
        Long res = null;
        if (cellData.getValue() == null) return res;
        res = cellData.getValue().getLocationOnStorage().getStored().stream()
                .filter(storageItem -> storageItem.getType().equals(cellData.getValue().getType()) &&
                        storageItem.getSupplier().equals(cellData.getValue().getSupplier()))
                .count();
        return res;
    }

    public void toFirst(ActionEvent actionEvent) {
        table.getSelectionModel().selectFirst();
    }

    public void toLast(ActionEvent actionEvent) {
        table.getSelectionModel().selectLast();
    }


    public void filter() {
        ObservableList<StorageItem> temp = FXCollections.observableArrayList(storageItemObservableList);

        if (groupFilterCheckBox.isSelected()) {
            temp.stream().filter(item -> !Objects.equals(item.getType().getGroup(), groupFilterComboBox.getValue()))
                    .forEach(temp::remove);
        }

        if (zoneFilterCheckBox.isSelected()) {
            temp.stream().filter(item -> !item.getLocationOnStorage().getZone().equals(zoneFilterComboBox.getValue()))
                    .forEach(temp::remove);
        }

        table.setItems(temp);

        if (!searchTextField.getText().isBlank()) {
            table.getItems().removeIf(item -> {
                for (int j = 0; j < table.getColumns().size(); j++) {
                    if (table.getColumns().get(j).getCellData(item) != null && Objects.equals(table.getColumns().get(j).getCellData(item).toString(), searchTextField.getText())) {
                        return false;
                    }
                }
                return true;
            });
        }
    }
}
