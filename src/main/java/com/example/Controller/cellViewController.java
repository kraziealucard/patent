package com.example.Controller;

import DAO.DAOFactory;
import EditableCustomTableCell.EditableComboBoxTableCell;
import Model.TypeOfStorageItem;
import Model.WarehouseZone;
import Model.Cell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xslf.usermodel.XSLFTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class cellViewController {

    public ComboBox<WarehouseZone> ComboBoxForFilter;
    public CheckBox checkBoxForFilter;
    public TableView<Cell> table;
    public TableColumn<Cell, Long> idClmn;
    public TableColumn<Cell, String> nameClmn;
    public TableColumn<Cell, WarehouseZone> zoneClmn;
    public TableColumn<Cell, Double> weightClmn;
    public TableColumn<Cell, Double> maxWeightClmn;
    public TableColumn<Cell, String> isUsefulClmn;
    private ArrayList<WarehouseZone> zones;
    private ArrayList<Cell> cells;
    private ObservableList<Cell> items;
    private DAOFactory dao;

    public void init(Tab tab, ArrayList<WarehouseZone> zones, DAOFactory dao) {
        this.dao=dao;
        this.zones = zones;
        items = FXCollections.observableArrayList();
        cells = new ArrayList<>();
        tab.selectedProperty().addListener((tabSelected, wasSelected, isSelected) -> {
            if (isSelected) {
                refresh();
            }
        });
        ComboBoxForFilter.getSelectionModel().selectFirst();
        idClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getID()));
        nameClmn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        zoneClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getZone()));
        weightClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCurrentWeight()));
        maxWeightClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMaxWeight()));

        isUsefulClmn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getGrade()));
        ObservableList<String> characters = FXCollections.observableArrayList("Премиум", "Стандартный","Удаленный");
        isUsefulClmn.setCellFactory(col -> new EditableComboBoxTableCell<>(item -> characters));
        isUsefulClmn.setEditable(true);
        table.setEditable(true);

        doFilter();
    }

    public void refresh() {
        ObservableList<WarehouseZone> temp = zones.stream().
                filter(WarehouseZone::isActive).
                collect(Collectors.toCollection(FXCollections::observableArrayList));
        ComboBoxForFilter.setItems(temp);

        cells.clear();
        for (WarehouseZone zone : temp) {
            for (int j = 0; j < zone.getCells().length; j++) {
                cells.addAll(Arrays.asList(zone.getCells()[j]));
            }
        }


        doFilter();
    }

    public void doFilter() {
        items.clear();
        if (!checkBoxForFilter.isSelected()) {
            items.addAll(cells);
        } else {
            ObservableList<Cell> temp = FXCollections.observableArrayList();
            temp.addAll(cells.stream().filter(e -> e.getZone() == ComboBoxForFilter.getValue()).toList());
            items.addAll(temp);
        }

        table.setItems(items);
        table.refresh();
    }

    public void toFirst() {
        table.getSelectionModel().selectFirst();
    }

    public void toLast() {
        table.getSelectionModel().selectLast();
    }

    public void toExcel(ActionEvent actionEvent) {
        if (table.getItems() == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить в Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            ExcelConverter.convertToExcel(table, filePath);
        }
    }


    public void cancelEditGrade(TableColumn.CellEditEvent<Cell, String> event) {
        String value = event.getOldValue();
        Cell cell = event.getRowValue();
        cell.setGrade(value);
    }

    public void commitEditGrade(TableColumn.CellEditEvent<Cell, String> event) {
        String value = event.getNewValue();
        Cell cell = event.getRowValue();
        cell.setGrade(value);
        boolean x=dao.getWarehouseZoneDAO().updateCell(cell);
    }
}
