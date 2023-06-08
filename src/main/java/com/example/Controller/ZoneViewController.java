package com.example.Controller;

import Model.WarehouseZone;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

public class ZoneViewController {

    public TableColumn<WarehouseZone, Long> idClmn;
    public TableColumn<WarehouseZone, String> nameClmn;
    public TableColumn<WarehouseZone, String> forProductClmn;
    public TableColumn<WarehouseZone, Integer> numberOfCells;
    public TableColumn<WarehouseZone, Double> maxWeightClmn;
    public TableView<WarehouseZone> table;
    public Button deleteBtn;
    public ContextMenu contextMenu;
    private ArrayList<WarehouseZone> zones;
    private ObservableList<WarehouseZone> items;
    private TabPane tabPane;

    public void init(ArrayList<WarehouseZone> zones,TabPane tabPane){
        this.zones=zones;
        this.tabPane=tabPane;
        configureUI();
    }

    private void configureUI(){
        configureClmn();
        configureTable();
    }

    @FXML
    private void updateItems(){
        items = FXCollections.observableArrayList();
        for (WarehouseZone zone : zones) {
            if (zone.isActive()) items.add(zone);
        }
        table.setItems(items);
        table.refresh();
    }

    private void configureTable(){
        updateItems();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteBtn.setDisable(newValue==null);
        });

    }

    private void configureClmn(){
        idClmn.setCellValueFactory(cellData-> new SimpleObjectProperty<Long>(cellData.getValue().getID()));
        maxWeightClmn.setCellValueFactory(cellData-> new SimpleObjectProperty<Double>(cellData.getValue().getMaxWeight()));
        nameClmn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WarehouseZone, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WarehouseZone, String> param) {
                return new SimpleStringProperty(param.getValue().getZoneName());
            }
        });
        nameClmn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameClmn.setOnEditCommit(event -> {
            WarehouseZone zone = event.getRowValue();
            String newValue = event.getNewValue();
            if (newValue.isEmpty()) {
                // Если новое значение пустое, вернуть предыдущее значение
                zone.setZoneName(event.getOldValue());
            } else {
                // В противном случае, установить новое значение
                zone.setZoneName(newValue);
            }
        });
        forProductClmn.setCellValueFactory(cellData -> {
            boolean isProductZone = cellData.getValue().isProductZone();
            String yesOrNo = isProductZone ? "Да" : "Нет";
            return new SimpleStringProperty(yesOrNo);
        });
        numberOfCells.setCellValueFactory(cellData-> new SimpleObjectProperty<Integer>(cellData.getValue().getCells().length * cellData.getValue().getCells()[0].length));
    }

    public void addZone(){
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getTitle().equals("Добавление зоны") && stage.isShowing()) {
                    stage.requestFocus();
                    return;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createNewWarehouseZone.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление зоны");
        stage.setScene(scene);
        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                updateItems();
            }
        });
        createWarehouseZoneController controller=fxmlLoader.getController();
        controller.init(zones);
        stage.setResizable(false);
        stage.show();
    }


    public void deleteZones(){
        table.getSelectionModel().getSelectedItem().setActive(false);
        table.getItems().remove(table.getSelectionModel().getSelectedItem());
    }

    public void toFirst(){
        table.getSelectionModel().selectFirst();
    }

    public void toLast(){
        table.getSelectionModel().selectLast();
    }

    public void toCell(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getText().equals("Ячейки зон")){
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        Tab tab=new Tab("Ячейки зон");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cell-view.fxml"));
        Parent root = fxmlLoader.load();
        tab.setContent(root);

        cellViewController productListController=fxmlLoader.getController();
        productListController.init(zones,tab);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}