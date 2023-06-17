package com.example.Controller;

import DAO.DAOFactory;
import Model.Permission;
import Model.Position;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;

public class LoginViewController {
    public PasswordField TFPassword;
    public TextField TFLogin;
    User currentUser;
    private DAOFactory dao;

    public void init(DAOFactory dao) {
        this.dao = dao;
    }

    public void Action_btnSigIn(ActionEvent actionEvent) {
        User user = dao.getUserDAO().getUserByLoginAndPassword(TFLogin.getText(), TFPassword.getText(), dao.getPositionDAO().getPositionList(false));
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пользователь не найден");
            alert.setContentText("Пользователь с указанным логином и паролем не найден.");
            alert.showAndWait();
            return;
        }
        currentUser = user;
        enterToProgram();
        TFLogin.getScene().getWindow().hide();
    }

    private void enterToProgram() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Склад");
        stage.setScene(scene);
        MainViewController controller = fxmlLoader.getController();
        controller.init(dao, currentUser);
        stage.show();
    }
}