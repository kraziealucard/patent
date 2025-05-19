package com.example.Controller;

import DAO.DAOFactory;
import Model.WarehouseZone;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class createWarehouseZoneController {

    public Button btn;
    public TextField zoneNameField;
    public TextField lengthField;
    public TextField widthField;
    public ComboBox<String> CBoxZoneFor;
    public TextField maxWeightField;
    private DAOFactory dao;
    private ArrayList<WarehouseZone> zones;

    public void init(DAOFactory dao, ArrayList<WarehouseZone> zones) {
        this.dao = dao;
        this.zones = zones;

        lengthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                lengthField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        widthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                widthField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        maxWeightField.textProperty().addListener((observable,oldValue,newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                maxWeightField.setText(oldValue);
            }
        });

        CBoxZoneFor.getSelectionModel().selectFirst();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("Не все поля заполнены.");
        alert.showAndWait();
    }

    public void btnClick() {
        if (zoneNameField.getText().isBlank() || lengthField.getText().isBlank() || widthField.getText().isBlank() || maxWeightField.getText().isBlank()) {
            showAlert();
            return;
        }
        WarehouseZone temp = new WarehouseZone(-1, zoneNameField.getText(),
                Integer.parseInt(lengthField.getText()), Integer.parseInt(widthField.getText()), true, Double.parseDouble(maxWeightField.getText()));
        temp.setProductZone(CBoxZoneFor.getValue().equals("продуктов"));
        dao.getWarehouseZoneDAO().addWarehouseZone(temp);
        zones.add(temp);

        ((Stage) zoneNameField.getScene().getWindow()).close();
    }
}
