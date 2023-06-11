package com.example.Controller;

import DAO.DAOFactory;
import Model.Permission;
import Model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
    public GridPane root;
    private ArrayList<Position> positions;
    DAOFactory dao;

    public void init(DAOFactory dao, Tab tab, ArrayList<Position> positions) {
        this.dao = dao;
        this.positions = positions;
        configureUI(tab);
    }

    private void configureUI(Tab tab) {
        configureListView();
        //Устанавливаем имя должности при смене фокуса с поля для имени должности
        positionNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                saveNewNameForPosition();
            }
        });

        tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateListView();
            }
        });
    }

    private void updateListView() {
        Position temp = null;
        if (positionListView.getSelectionModel().getSelectedItem() != null) {
            temp = positionListView.getSelectionModel().getSelectedItem();
        }
        ObservableList<Position> items = FXCollections.observableArrayList();
        positionListView.setItems(items);
        items.addAll(positions);
        positionListView.refresh();
        if (temp != null) positionListView.getSelectionModel().select(temp);
    }

    private void configureListView() {
        updateListView();

        //  Отслеживаем переключение элементов ListView:
        //      при переключении отображаем информацию об элементах и включаем элементы пользовательского интерфейса
        //      если фокус отсутсвует на любом из элементов или если фокус на "Администратор" ,то элементы пользовательского интерфейса отключается
        positionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (positionListView.getItems().size() != 0) displayPermissionsForPosition(newValue);
            if (newValue == null || Objects.equals(newValue.getName(), "Администратор")) switchDisableUI(true);
            else switchDisableUI(false);
        });
        positionListView.getSelectionModel().selectFirst();
    }

    private void switchDisableUI(boolean value) {
        CBEditUserAndRole.setDisable(value);
        CBWarehouseEdit.setDisable(value);
        CBEditProduct.setDisable(value);
        CBViewBooks.setDisable(value);
        CBEditLogistics.setDisable(value);
        CBEditCounterpart.setDisable(value);

        positionNameField.setDisable(value);
        btnRemovePosition.setDisable(value);
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
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }

    public void setWarehouseEditPermissions(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.EditingWarehouseInformation);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.EditingWarehouseInformation);
        }
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }

    public void changeActive(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        positionListView.getSelectionModel().getSelectedItem().setActive(!positionListView.getSelectionModel().getSelectedItem().isActive());
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
        positions.remove(positionListView.getSelectionModel().getSelectedItem());
        updateListView();
    }

    public void addPosition(ActionEvent actionEvent) {
        Position temp = new Position(-1, "Новая должность", new ArrayList<Permission>());
        temp.setID(dao.getPositionDAO().addPosition(temp));
        positions.add(temp);
        updateListView();
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
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
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
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }

    public void setEditCounterparty(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.EditingContactor);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.EditingContactor);
        }
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }

    public void setEditProductType(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.ProductEditing);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.ProductEditing);
        }
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }

    public void setViewBook(ActionEvent actionEvent) {
        if (positionListView.getSelectionModel().getSelectedItem() == null) return;
        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        if (checkBox.isSelected()) {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().add(Permission.ViewBook);
        } else {
            positionListView.getSelectionModel().getSelectedItem().getPermissions().remove(Permission.ViewBook);
        }
        dao.getPositionDAO().updatePosition(positionListView.getSelectionModel().getSelectedItem());
    }
}
