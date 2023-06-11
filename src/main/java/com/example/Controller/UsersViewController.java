package com.example.Controller;

import DAO.DAOFactory;
import Model.Position;
import Model.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class UsersViewController {
    public TableView<User> userTable;
    public TableColumn<User, Long> idColumn;
    public TableColumn<User, String> nameColumn;
    public TableColumn<User, Position> positionColumn;
    public TableColumn<User, String> loginColumn;
    public TableColumn<User, String> passwordColumn;
    public TableColumn<User, Boolean> activeColumn;
    public CheckBox CheckBoxFilter;
    public ComboBox<Position> ComboBoxFilter;
    public TextField searchTF;
    private ArrayList<User> users;
    private ArrayList<Position> positions;
    User currentUser;
    DAOFactory dao;
    ObservableList<User> data;

    public void init(DAOFactory dao, Tab tab, ArrayList<User> userList, ArrayList<Position> positions, User currentUser) {
        this.currentUser = currentUser;
        this.dao = dao;

        ComboBoxFilter.setItems(FXCollections.observableArrayList(positions));
        idColumn.setEditable(false);
        userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setCellValueFactory();
        configColumns();

        users = userList;
        data = FXCollections.observableArrayList();
        userTable.setItems(data);
        toFilter();

        tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                toFilter();
            }
        });

        this.positions = positions;
        userTable.refresh();
    }

    private void setCellValueFactory() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<Long>(cellData.getValue().getID()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        positionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPosition()));
        loginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        passwordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        activeColumn.setCellValueFactory(cellDataFeatures -> new SimpleBooleanProperty(cellDataFeatures.getValue().isActive()));
    }

    private void configColumns() {
        configNameColumn();
        configPositionColumn();
        configLoginColumn();
        configPasswordColumn();
        configActiveColumn();
    }

    private void configActiveColumn() {
        activeColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    commitEdit(checkBox.isSelected());
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        activeColumn.setOnEditCommit(event -> {
            User user = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (user == currentUser || user == null) return;
            user.setActive(event.getNewValue());
            dao.getUserDAO().updateUser(user);
        });

    }

    private void configPasswordColumn() {
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> user) {
                if (user.getTableView().getSelectionModel().getSelectedItem() == null || Objects.equals(user.getNewValue(), ""))
                    user.getTableView().refresh();
                else if (user.getNewValue().length() < 8) {
                    showShortPasswordErrorDialog();
                    user.getTableView().refresh();
                } else {
                    user.getTableView().getSelectionModel().getSelectedItem().setPassword(user.getNewValue());
                    dao.getUserDAO().updateUser(user.getTableView().getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    private void configLoginColumn() {
        loginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        loginColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> user) {
                if (user.getTableView().getSelectionModel().getSelectedItem() == null || Objects.equals(user.getNewValue(), ""))
                    user.getTableView().refresh();
                else if (user.getNewValue().length() < 4) {
                    showShortLoginErrorDialog();
                    user.getTableView().refresh();
                } else if (isDuplicateLogin(user.getNewValue())) {
                    showInvalidLoginErrorDialog();
                    user.getTableView().refresh();
                } else {
                    user.getTableView().getSelectionModel().getSelectedItem().setLogin(user.getNewValue());
                    dao.getUserDAO().updateUser(user.getTableView().getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    private void configNameColumn() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> user) {
                if (user.getTableView().getSelectionModel().getSelectedItem() == null || Objects.equals(user.getNewValue(), ""))
                    user.getTableView().refresh();
                else {
                    user.getTableView().getSelectionModel().getSelectedItem().setName(user.getNewValue());
                    dao.getUserDAO().updateUser(user.getTableView().getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    private ObservableList<Position> updatePositions() {
        ObservableList<Position> res = FXCollections.observableArrayList(positions);
        res.removeIf(item -> !item.isActive());
        return res;
    }

    private void configPositionColumn() {
        positionColumn.setCellFactory(column -> new ComboBoxTableCell<User, Position>(updatePositions()) {
            @Override
            public void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        positionColumn.setOnEditCommit(event -> {
            TablePosition<User, Position> position = event.getTablePosition();
            User user = event.getTableView().getItems().get(position.getRow());
            if (user == null) return;
            user.setPosition(event.getNewValue());
            dao.getUserDAO().updateUser(user);
        });

    }

    private void showInvalidLoginErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Некорректный логин");
        alert.setContentText("Выбранный логин недоступен");
        alert.showAndWait();
    }

    private void showShortPasswordErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Некорректный пароль");
        alert.setContentText("Длина пароля должна быть не менее 8 символов");
        alert.showAndWait();
    }

    private void showShortLoginErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Некорректный логин");
        alert.setContentText("Длина логина должна быть не менее 4 символов");
        alert.showAndWait();
    }

    private boolean isDuplicateLogin(String login) {
        for (int i = 0; i < userTable.getItems().size(); i++) {
            if (Objects.equals(userTable.getItems().get(i).getName(), login)) return true;
        }
        return false;
    }

    public void toFirst(ActionEvent actionEvent) {
        userTable.getSelectionModel().clearSelection();
        userTable.getSelectionModel().selectFirst();
    }

    public void toLast(ActionEvent actionEvent) {
        userTable.getSelectionModel().clearSelection();
        userTable.getSelectionModel().selectLast();
    }

    public void addUser(ActionEvent actionEvent) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getTitle().equals("Добавление пользователя") && stage.isShowing()) {
                    stage.requestFocus();
                    return;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createUser-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление пользователя");
        stage.setScene(scene);
        createUserController controller = fxmlLoader.getController();
        controller.init(dao);
        stage.show();
    }

    public void toSearch() {
        toFilter();
        if (searchTF.getText().isBlank()) return;
        ObservableList<User> temp = FXCollections.observableArrayList();
        for (int i = 0; i < userTable.getItems().size(); i++) {
            if (userTable.getItems().get(i).getName().contains(searchTF.getText())) {
                temp.add(userTable.getItems().get(i));
            }
        }
        userTable.setItems(temp);
    }

    public void showNotification(String nameUser) {
        Notifications notifications = Notifications.create()
                .text("Пользователь \"" + nameUser + "\" успешно добавлен")
                .position(Pos.BOTTOM_LEFT) // позиция уведомления
                .hideAfter(Duration.seconds(5)) // скрытие уведомления через 5 секунд
                .owner(userTable.getScene().getWindow()); // задание окна, на котором будет отображаться уведомление

        notifications.show();
        toFilter();
    }

    public void toFilter() {
        userTable.setItems(null);
        data.clear();
        data.addAll(users);
        if (!CheckBoxFilter.isSelected()) {
            userTable.setItems(data);
        } else {
            ObservableList<User> temp = FXCollections.observableArrayList();
            temp.addAll(data.stream().filter(e -> e.getPosition() == ComboBoxFilter.getValue()).toList());
            userTable.setItems(temp);
        }
        if (currentUser.getID() != 1) {
            userTable.getItems().removeIf(item -> item.getID() == 1);
        }
        userTable.refresh();
        userTable.getSelectionModel().selectFirst();
    }
}
