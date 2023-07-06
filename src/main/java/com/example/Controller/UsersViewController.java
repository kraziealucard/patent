package com.example.Controller;

import DAO.DAOFactory;
import Model.Position;
import Model.User;
import javafx.beans.property.BooleanProperty;
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
import javafx.stage.*;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private ArrayList<Position> positions;
    private ArrayList<User> users;
    User currentUser;
    DAOFactory dao;
    ObservableList<User> ObsUser;
    ObservableList<Position> ObsPositions;

    public void init(DAOFactory dao, Tab tab, User currentUser, ArrayList<User> users, ArrayList<Position> positions) {
        this.dao = dao;
        this.currentUser = currentUser;
        this.positions = positions;
        this.users = users;

        ObsUser = users.stream().
                filter(User::isActive).
                collect(Collectors.toCollection(FXCollections::observableArrayList));

        ObsPositions = positions.stream().
                filter(Position::isActive).
                collect(Collectors.toCollection(FXCollections::observableArrayList));

        ComboBoxFilter.setItems(ObsPositions);
        userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setCellValueFactory();
        configColumns();

        toFilter();

        tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updatePositions();
                toFilter();
            }
        });

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
                    if (getTableRow() != null) {
                        TableView<User> tableView = getTableView();
                        User user = tableView.getItems().get(getTableRow().getIndex());
                        user.setActive(checkBox.isSelected());
                        dao.getUserDAO().updateUser(user);
                    }
                });

            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                    if (getTableRow() != null) {
                        TableView<User> tableView = getTableView();
                        User user = tableView.getItems().get(getTableRow().getIndex());
                        checkBox.setDisable(user.getID() == 1 || Objects.equals(user.getID(), currentUser.getID()));
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (isEmpty()) {
                    return;
                }
                checkBox.setDisable(false);
                checkBox.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                checkBox.setDisable(true);
            }
        });

    }

    private void configPasswordColumn() {
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<User, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<User, String> user) {
                if (user.getTableView().getSelectionModel().getSelectedItem() == null || Objects.equals(user.getNewValue(), "")) {
                    user.getTableView().refresh();
                } else if (user.getNewValue().length() < 4) {
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

    private void updatePositions() {
        Position temp = ComboBoxFilter.getValue();
        ObsPositions.clear();
        ObsPositions.addAll(positions.stream().
                filter(Position::isActive).
                collect(Collectors.toCollection(FXCollections::observableArrayList)));
        ComboBoxFilter.setValue(temp);
    }

    private void configPositionColumn() {
        positionColumn.setCellFactory(column -> new ComboBoxTableCell<User, Position>(FXCollections.observableArrayList(positions)) {
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
            if (user == null || user.getID() == 1) return;
            user.setPosition(event.getNewValue());
            dao.getUserDAO().updateUser(user);
            toFilter();
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
        alert.setContentText("Длина пароля должна быть не менее 4 символов");
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

        BooleanProperty isSuccess = new SimpleBooleanProperty(false);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Добавление пользователя");

        stage.setScene(scene);
        stage.setOnHidden(event -> {
            if (isSuccess.getValue()) {
                showNotification();
            }
        });
        createUserController controller = fxmlLoader.getController();
        controller.init(dao, isSuccess, users);
        stage.show();
    }

    public void toSearch() {
        toFilter();
        if (searchTF.getText().isBlank()) return;
        ObservableList<User> filteredData = FXCollections.observableArrayList();
        for (User user : ObsUser) {
            if (containsSearchText(user, searchTF.getText().toLowerCase())) {
                if (user.getID() == 1 && currentUser != user) continue;
                filteredData.add(user);
            }
        }

        userTable.setItems(filteredData);
    }

    private boolean containsSearchText(User user, String searchText) {
        String lowerCaseSearchText = searchText.toLowerCase();

        return user.getName().toLowerCase().contains(lowerCaseSearchText)
                || user.getPosition().toString().toLowerCase().contains(lowerCaseSearchText)
                || user.getLogin().toLowerCase().contains(lowerCaseSearchText)
                || user.getPassword().toLowerCase().contains(lowerCaseSearchText)
                || user.getID().toString().toLowerCase().contains(lowerCaseSearchText);
    }

    public void showNotification() {
        Notifications notifications = Notifications.create()
                .text("Пользователь успешно добавлен")
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(2))
                .owner(userTable.getScene().getWindow());

        notifications.show();
        toFilter();
    }

    public void toFilter() {
        ObsUser.clear();
        ObsUser.addAll(users.stream().
                collect(Collectors.toCollection(FXCollections::observableArrayList)));

        if (!CheckBoxFilter.isSelected()) {
            userTable.setItems(ObsUser);
        } else {
            ObsUser.removeIf(u -> !u.getPosition().equals(ComboBoxFilter.getValue()));
        }
        if (currentUser.getID() != 1) {
            userTable.getItems().removeIf(item -> item.getID() == 1);
        }
        userTable.refresh();
        userTable.getSelectionModel().selectFirst();
    }

    public void toExcel(ActionEvent actionEvent) {
        if (userTable.getItems() == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить в Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(userTable.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            ExcelConverter.convertToExcel(userTable, filePath);
        }
    }
}
