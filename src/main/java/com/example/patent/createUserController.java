package com.example.patent;

import Model.Position;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.ArrayList;
import java.util.Objects;

public class createUserController {
    public TextField nameTextField;
    public ComboBox<Position> positionComboBox;
    public TextField loginTextField;
    public TextField passwordTextField;
    public Button saveButton;
    public Label labelNameWindow;
    public GridPane root;
    private ArrayList<User> users;
    private UsersViewController usersViewController;

    public void init(ArrayList<User> users,ArrayList<Position> positions,UsersViewController usersViewController){
        this.usersViewController=usersViewController;
        this.users=users;
        labelNameWindow.setText("Создание новой записи");
        positionComboBox.setItems(FXCollections.observableArrayList(positions));
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
        User newUser=new User((long) (users.size() + 1),nameTextField.getText(),positionComboBox.getValue(),loginTextField.getText(),passwordTextField.getText());
        users.add(newUser);
        usersViewController.showNotification(newUser.getName());
        ((Stage)nameTextField.getScene().getWindow()).close();
    }
}
