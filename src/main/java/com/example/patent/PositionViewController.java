package com.example.patent;

import Model.Permission;
import Model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Objects;

public class PositionViewController {
    public ListView<Position> positionListView;
    public TextField positionNameField;


    public CheckBox CBEditUserAndRole;
    public CheckBox CBWarehouseEdit;
    public CheckBox CBEditProduct;
    public CheckBox CBViewBooks;
    public CheckBox CBEditLogistics;
    public CheckBox CBEditCounterpart;


    public Button btnRemovePosition;
    private ArrayList<Position> positions;

    public void init(ArrayList<Position> loadedPosition) {
        positions = loadedPosition;
        configureUI();

    }

    private void configureUI() {
        configureListView();
        //Устанавливаем имя должности при смене фокуса с поля для имени должности
        positionNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                saveNewNameForPosition();
            }
        });
    }

    private void configureListView() {
        //Клонируем все должности с правами доступа и устанавливаем клонов в ListView
        ObservableList<Position> items = FXCollections.observableArrayList();
        positionListView.setItems(items);
        for (Position u : positions) {
            items.add(u.clone());
        }
        positionListView.refresh();

        //  Отслеживаем переключение элементов ListView:
        //      при переключении отображаем информацию об элементах и включаем элементы пользовательского интерфейса
        //      если фокус отсутсвует на любом из элементов или если фокус на "Администратор" ,то элементы пользовательского интерфейса отключается
        positionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (positionListView.getItems().size() != 0) displayPermissionsForPosition(newValue);
            if (newValue == null || Objects.equals(newValue.getName(), "Администратор")) disableUIElements();
            else enableUIElements();
        });
        positionListView.getSelectionModel().selectFirst();
    }

    private void disableUIElements() {
        CBEditUserAndRole.setDisable(true);
        CBWarehouseEdit.setDisable(true);
        CBEditProduct.setDisable(true);
        CBViewBooks.setDisable(true);
        positionNameField.setDisable(true);
        btnRemovePosition.setDisable(true);
    }

    private void enableUIElements() {
        CBEditUserAndRole.setDisable(false);
        CBWarehouseEdit.setDisable(false);
        CBEditProduct.setDisable(false);
        CBEditCounterpart.setDisable(false);
        CBViewBooks.setDisable(false);
        positionNameField.setDisable(false);
        btnRemovePosition.setDisable(false);
    }

    private void displayPermissionsForPosition(Position p) {
        positionNameField.setText(p.toString());
        
        CBEditUserAndRole.setSelected(false);
        CBWarehouseEdit.setSelected(false);
        CBEditLogistics.setSelected(false);
        CBEditProduct.setSelected(false);
        CBEditCounterpart.setSelected(false);
        CBViewBooks.setSelected(false);

        for (Permission per : p.getPermissions()) {
            switch (per) {
                case EditingUsersAndPositions -> CBEditUserAndRole.setSelected(true);
                case EditingWarehouseInformation -> CBWarehouseEdit.setSelected(true);
                case ProductLogistic -> CBEditLogistics.setSelected(true);
                case ProductEditing -> CBEditProduct.setSelected(true);
                case EditingContactor -> CBEditCounterpart.setSelected(true);
                case ViewBook -> CBViewBooks.setSelected(true);
            }
        }
    }

    public void setEditUserAndRolePermissions(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.EditingUsersAndPositions);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.EditingUsersAndPositions);
        }
    }

    public void setWarehouseEditPermissions(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.EditingWarehouseInformation);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.EditingWarehouseInformation);
        }
    }

    public void removePosition(ActionEvent actionEvent) {
        positionListView.getItems().remove(positionListView.getSelectionModel().getSelectedItem());
    }

    public void addPosition(ActionEvent actionEvent) {
        positionListView.getItems().add(new Position(-1, "Новая должность", new ArrayList<Permission>()));
    }

    private void showInvalidNameErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("Имя для должности \"Администратор\" недоступно");
        alert.showAndWait();
    }

    private void saveNewNameForPosition() {
        if (positionNameField.getText().equals("Администратор")) {
            showInvalidNameErrorDialog();
            positionNameField.setText("Новый администратор");
        }
        positionListView.getSelectionModel().getSelectedItem().setName(positionNameField.getText());
        positionListView.refresh();
        positionNameField.deselect();
    }

    public void TFAction(ActionEvent actionEvent) {
        CBEditUserAndRole.requestFocus();
    }

    public void setEditLogisticsPermissions(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.ProductLogistic);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.ProductLogistic);
        }
    }

    public void setEditCounterparty(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.EditingContactor);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.EditingContactor);
        }
    }

    public void setEditProductType(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.ProductEditing);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.ProductEditing);
        }
    }

    public void setViewBook(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.ViewBook);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.ViewBook);
        }
    }
}
