package com.example.Controller;

import DAO.DAOFactory;
import Model.Position;
import Model.User;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class createUserController {
    public TextField nameTextField;
    public ComboBox<Position> positionComboBox;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button saveButton;
    public Label labelNameWindow;
    public GridPane root;
    private DAOFactory dao;
    private BooleanProperty isSuccess;
    private String nameNewUser;

    public void init(DAOFactory dao, BooleanProperty isSuccess) {
        this.dao = dao;
        this.isSuccess = isSuccess;
        this.nameNewUser = nameNewUser;
        updatePositions();
        labelNameWindow.setText("Создание новой записи");
        /*stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    updatePositions();
                }
            }
        });*/
    }

    private void updatePositions() {
        positionComboBox.setItems(FXCollections.observableArrayList(dao.getPositionDAO().getPositionList(true)));
        positionComboBox.getSelectionModel().selectFirst();
    }

    private boolean areAllTextFieldsFilled() {
        for (Node node : root.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                if (textField.getText().isBlank()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addNewUser(ActionEvent actionEvent) {
        if (!areAllTextFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Незаполненные поля");
            alert.setContentText("Пожалуйста, заполните все поля.");

            alert.showAndWait();
            return;
        }

        if (dao.getUserDAO().loginUserExists(loginTextField.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Логин недоступен");
            alert.setContentText("Пожалуйста, введите другой логин.");

            alert.showAndWait();
            return;
        }

        dao.getUserDAO().addUser(new User(-1L, nameTextField.getText(), positionComboBox.getValue(), loginTextField.getText(), passwordTextField.getText()));
        isSuccess.setValue(true);
        nameNewUser = loginTextField.getText();
        ((Stage) labelNameWindow.getScene().getWindow()).close();
    }
}
