package com.example.Controller;

import DAO.DAOFactory;
import Model.User;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {

        DAOFactory DAO = DAOFactory.getDAOFactory(DAOFactory.H2);
        if (DAO.isFirstStart()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Первый запуск системы");
            alert.setHeaderText(null);
            alert.setContentText("Добро пожаловать в систему! По всей видимости, это первый запуск системы.\nПожалуйста, установите логин и пароль для супер администратора.");
            Label label = new Label(alert.getContentText());
            VBox vbox = new VBox(label);
            alert.getDialogPane().setContent(vbox);
            alert.showAndWait();
            createUser(stage, DAO);
        } else intoSystem(stage, DAO);
    }

    private void createUser(Stage stag, DAOFactory dao) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createUser-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BooleanProperty isSuccess = new SimpleBooleanProperty(false);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление пользователя");
        stage.setOnHidden(event -> {
            if (isSuccess.getValue()) {
                try {
                    intoSystem(stag, dao);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.setScene(scene);
        createUserController controller = fxmlLoader.getController();
        controller.init(dao, isSuccess);
        stage.showAndWait();
    }

    private void intoSystem(Stage stage, DAOFactory dao) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginViewController loginViewController = fxmlLoader.getController();
        loginViewController.init(dao);
        stage.setResizable(false);
        stage.setTitle("Авторизация!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}