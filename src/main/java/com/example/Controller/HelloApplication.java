package com.example.Controller;

import DAO.DAOFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

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
        }
        intoSystem(stage);
    }

    private void intoSystem(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginViewController loginViewController = fxmlLoader.getController();
        loginViewController.init(DAOFactory.getDAOFactory(DAOFactory.H2));
        stage.setResizable(false);
        stage.setTitle("Авторизация!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}