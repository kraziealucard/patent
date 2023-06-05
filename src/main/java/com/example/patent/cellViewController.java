package com.example.patent;

import Model.WarehouseZone;
import Model.Cell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class cellViewController {

    public ComboBox<WarehouseZone> ComboBoxForFilter;
    public CheckBox checkBoxForFilter;
    public TableView<Cell> table;
    public TableColumn<Cell, Long> idClmn;
    public TableColumn<Cell, String> nameClmn;
    public TableColumn<Cell, WarehouseZone> zoneClmn;
    public TableColumn<Cell, Double> weightClmn;
    public TableColumn<Cell, Double> maxWeightClmn;
    private ArrayList<WarehouseZone> zones;
    private ArrayList<Cell> cells;
    private ObservableList<Cell> items;

    public void init(ArrayList<WarehouseZone> zones, Tab tab){
        this.zones=zones;
        tab.selectedProperty().addListener((tabSelected, wasSelected, isSelected) -> {
            if (isSelected) {
                refresh();
            }
        });
        items=FXCollections.observableArrayList();
        cells=new ArrayList<>();
        ComboBoxForFilter.getSelectionModel().selectFirst();
        idClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getID()));
        nameClmn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        zoneClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getZone()));
        weightClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCurrentWeight()));
        maxWeightClmn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMaxWeight()));
        doFilter();
    }
    public void refresh(){
        HashSet<WarehouseZone> uniqueZone = new HashSet<>();
        for (WarehouseZone zone : zones) {
            if (zone.isActive()) uniqueZone.add(zone);
        }
        ComboBoxForFilter.setItems(FXCollections.observableArrayList(uniqueZone));

        cells.clear();
        for (WarehouseZone zone : zones) {
            if (zone.isActive()) {
                for (int j = 0; j < zone.getCells().length; j++) {
                    cells.addAll(Arrays.asList(zone.getCells()[j]));
                }
            }
        }

        System.out.println(zones.size());

        doFilter();
    }

    public void doFilter(){
        items.clear();
        if (!checkBoxForFilter.isSelected()){
            for (int i = 0; i < cells.size(); i++) {
                if (cells.get(i).isActive()) items.add(cells.get(i));
            }
        }
        else {
            ObservableList<Cell> temp= FXCollections.observableArrayList();
            temp.addAll(cells.stream().filter(e -> e.getZone()==ComboBoxForFilter.getValue() && e.isActive()).toList());
            items.addAll(temp);
        }

        table.setItems(items);
        table.refresh();
    }
    public void toFirst(){table.getSelectionModel().selectFirst();}
    public void toLast(){table.getSelectionModel().selectLast();}
}
