package com.example.Controller;

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
    private ArrayList<WarehouseZone> zones;

    public void init(ArrayList<WarehouseZone> zones){
        this.zones=zones;

        lengthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Проверка, что новое значение содержит только цифры
                lengthField.setText(newValue.replaceAll("[^\\d]", "")); // Удаление всех символов, кроме цифр
            }
        });

        widthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Проверка, что новое значение содержит только цифры
                widthField.setText(newValue.replaceAll("[^\\d]", "")); // Удаление всех символов, кроме цифр
            }
        });
        CBoxZoneFor.getSelectionModel().selectFirst();
    }

    private void showAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("Не все поля заполнены.");
        alert.showAndWait();
    }

    public void btnClick(){
        if (zoneNameField.getText().isBlank() || lengthField.getText().isBlank() || widthField.getText().isBlank() || maxWeightField.getText().isBlank())
        {
            showAlert();
            return;
        }
        WarehouseZone temp=new WarehouseZone(zones.size()+1,zoneNameField.getText(),
                Integer.parseInt(lengthField.getText()), Integer.parseInt(widthField.getText()),true,Double.parseDouble(maxWeightField.getText()));
        temp.setProductZone(CBoxZoneFor.getValue().equals("продуктов"));
        zones.add(temp);

        ((Stage)zoneNameField.getScene().getWindow()).close();
    }
}
