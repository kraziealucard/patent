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

import java.util.ArrayList;

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
    ArrayList<User> users;

    public void init(DAOFactory dao, BooleanProperty isSuccess, ArrayList<User> users) {
        this.dao = dao;
        this.isSuccess = isSuccess;
        this.users = users;
        updatePositions();
        labelNameWindow.setText("Создание новой записи");
    }

    public void init(DAOFactory dao, BooleanProperty isSuccess) {
        this.dao = dao;
        this.isSuccess = isSuccess;
        updatePositions();
        labelNameWindow.setText("Создание новой записи");
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

        User newUser = new User(-1L, nameTextField.getText(), positionComboBox.getValue(), loginTextField.getText(), passwordTextField.getText());
        newUser.setID(dao.getUserDAO().addUser(newUser));
        if (users != null) users.add(newUser);
        isSuccess.setValue(true);
        ((Stage) labelNameWindow.getScene().getWindow()).close();
    }
}
