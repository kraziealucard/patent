package com.example.Controller;

import DAO.DAOFactory;
import Model.Position;
import Model.User;
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
    private ArrayList<Position> positions;
    private DAOFactory dao;

    public void init(DAOFactory dao) {
        this.dao = dao;
        updatePositions();
        labelNameWindow.setText("Создание новой записи");
        positionComboBox.setItems(FXCollections.observableArrayList(positions));
        positionComboBox.getSelectionModel().selectFirst();
    }

    private void updatePositions() {
        this.positions = dao.getPositionDAO().getPositionList(true);
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

        ((Stage) nameTextField.getScene().getWindow()).close();
    }
}
