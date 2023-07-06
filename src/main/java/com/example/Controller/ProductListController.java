package com.example.Controller;

import DAO.DAOFactory;
import Model.GroupItems;
import Model.TypeOfStorageItem;
import Model.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductListController {
    public TableView<TypeOfStorageItem> table;
    public TableColumn<TypeOfStorageItem, Long> idColumn;
    public TableColumn<TypeOfStorageItem, String> nameColumn;
    public TableColumn<TypeOfStorageItem, Double> weightColumn;
    public TreeView<GroupItems> ListGroup;
    public Button removeButton;
    public Button explainButton;
    public Button saveToExcelButton;
    public ComboBox<GroupItems> groupComboBox;
    private ObservableList<GroupItems> itemsForComboBox;
    private ArrayList<TypeOfStorageItem> typeOfStorageItems;
    private ArrayList<GroupItems> groupItems;
    private boolean isProducts;
    private TypeOfStorageItem itemForReturn;
    private Menu moveMenu;
    private DAOFactory dao;

    public void init(DAOFactory dao, boolean isProducts, ArrayList<TypeOfStorageItem> typeOfStorageItems, ArrayList<GroupItems> groupItems) {
        this.dao = dao;
        this.typeOfStorageItems = typeOfStorageItems;
        this.groupItems = groupItems;
        this.isProducts = isProducts;

        moveMenu = new Menu("Переместить выбранный");
        ListGroup.setRoot(new TreeItem<>(new GroupItems(-1, "Все", isProducts)));

        ListGroup.setOnMouseClicked(mouseEvent -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                updateTable();
            }
        });

        itemsForComboBox = FXCollections.observableArrayList();
        groupComboBox.setItems(itemsForComboBox);

        configureUI();
        updateListView();
        updateTable();
        createContextMenu();
    }

    public void toExcel() {
        if (table.getItems() == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить в Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            ExcelConverter.convertToExcel(table, filePath);
        }
    }

    private void moveSelectedGroupItems(GroupItems targetGroupItem, boolean forTreeGroup) {
        if (forTreeGroup) {
            GroupItems selectedItem = ListGroup.getSelectionModel().getSelectedItem().getValue();
            if (selectedItem == null || selectedItem == targetGroupItem) return;
            if (targetGroupItem.getID() == -1) targetGroupItem = null;
            selectedItem.setParent(targetGroupItem);
            dao.getGroupItemsDAO().updateGroupItem(selectedItem);
            updateListView();
            return;
        }
        TypeOfStorageItem selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        if (targetGroupItem.getID() == -1) targetGroupItem = null;
        selectedItem.setGroup(targetGroupItem);
        dao.getItemTypesDAO().updateTypeList(selectedItem);
        updateTable();
    }

    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(event -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem != ListGroup.getRoot()) {
                selectedItem.getValue().setActive(false);
                dao.getGroupItemsDAO().updateGroupItem(selectedItem.getValue());
                updateListView();
            }
        });


        MenuItem create = new MenuItem("Создать");

        create.setOnAction(actionEvent -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();

            for (Window window : Window.getWindows()) {
                if (window instanceof Stage stage) {
                    if (stage.getTitle().equals(isProducts ? "Добавление группы продукта" : "Добавление группы материала") && stage.isShowing()) {
                        stage.requestFocus();
                        return;
                    }
                }
            }

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addGroupItem.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(isProducts ? "Добавление группы продукта" : "Добавление группы материала");
            stage.setScene(scene);
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    updateListView();
                }
            });
            GroupItems value;
            if (selectedItem == ListGroup.getRoot() || selectedItem == null) {
                value = null;
            } else {
                value = selectedItem.getValue();
            }
            CreateGroupController controller = fxmlLoader.getController();
            controller.init(dao, value, isProducts, groupItems);
            stage.setResizable(false);
            stage.show();

        });


        contextMenu.getItems().addAll(create, deleteItem, moveMenu);

        contextMenu.setOnShowing(event -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            moveMenu.getItems().clear();
            MenuItem rootItem = new MenuItem(ListGroup.getRoot().getValue().getName());
            rootItem.setOnAction(eventM -> moveSelectedGroupItems(ListGroup.getRoot().getValue(), true));
            moveMenu.getItems().add(rootItem);
            for (GroupItems groupItem : groupItems) {
                if (!groupItem.isActive() || groupItem.isProduct() != isProducts) continue;
                MenuItem menuItem = new MenuItem(groupItem.getName());
                menuItem.setOnAction(eventM -> moveSelectedGroupItems(groupItem, true));
                moveMenu.getItems().add(menuItem);
            }
            if (selectedItem != null && selectedItem != ListGroup.getRoot()) {
                deleteItem.setDisable(false);
            } else deleteItem.setDisable(true);
        });

        ListGroup.setContextMenu(contextMenu);
    }

    private void configureUI() {

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getID()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        configureWeightColumn();
    }

    private void configureWeightColumn() {
        weightColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getWeight()));
        weightColumn.setCellFactory(column -> {
            return new TableCell<TypeOfStorageItem, Double>() {
                private TextField textField;

                {
                    textField = new TextField();
                    textField.setOnAction(event -> commitEdit(Double.parseDouble(textField.getText())));
                    textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (!isNowFocused) {
                            commitEdit(Double.parseDouble(textField.getText()));
                        }
                    });
                }

                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.toString());
                        setGraphic(null);
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();

                    if (!isEmpty()) {
                        textField.setText(getItem().toString());
                        setGraphic(textField);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        textField.requestFocus();
                        textField.selectAll();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();

                    setText(getItem().toString());
                    setGraphic(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }

                @Override
                public void commitEdit(Double newValue) {
                    super.commitEdit(newValue);

                    setText(getItem().toString());
                    setGraphic(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            };
        });
    }

    public void toFirst() {
        table.getSelectionModel().selectFirst();
    }

    public void toLast() {
        table.getSelectionModel().selectLast();
    }

    public void toExplain() {
        if (table.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось добавить " + (isProducts ? "продукт" : "материал") + " в список поставки");
            alert.setContentText("Пожалуйста, выделите желаемый " + (isProducts ? "продукт" : "материал") + " для добавления в список поставки.");
            alert.showAndWait();
            return;
        }

        itemForReturn = table.getSelectionModel().getSelectedItem();
        ((Stage) table.getScene().getWindow()).close();
    }

    public TypeOfStorageItem getItemForReturn() {
        return itemForReturn;
    }

    public void addProduct() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                if (stage.getTitle().equals(isProducts ? "Добавление продукта" : "Добавление материала") && stage.isShowing()) {
                    stage.requestFocus();
                    return;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createTypeStorageItem-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(isProducts ? "Добавление продукта" : "Добавление материала");
        stage.setScene(scene);
        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                updateTable();
            }
        });
        createTypeStorageItemController controller = fxmlLoader.getController();
        GroupItems groupForNewType;
        if (ListGroup.getSelectionModel().getSelectedItem() == ListGroup.getRoot() || ListGroup.getSelectionModel().getSelectedItem() == null) {
            groupForNewType = null;
        } else {
            groupForNewType = ListGroup.getSelectionModel().getSelectedItem().getValue();
        }
        controller.init(dao, groupForNewType, isProducts, typeOfStorageItems);
        stage.setResizable(false);
        stage.show();
    }

    public void removeProduct() {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        table.getSelectionModel().getSelectedItem().setActive(false);
        dao.getItemTypesDAO().updateTypeList(table.getSelectionModel().getSelectedItem());
        updateListView();
        updateTable();
    }

    private void expandAllItems(TreeItem<GroupItems> item) {
        item.setExpanded(true);
        for (TreeItem<GroupItems> child : item.getChildren()) {
            expandAllItems(child);
        }
    }

    private void updateListView() {
        ArrayList<GroupItems> tempA = groupItems.stream()
                .filter(GroupItems::isActive)
                .filter(t -> t.isProduct() == isProducts)
                .collect(Collectors.toCollection(ArrayList::new));
        GroupItems temp = groupComboBox.getValue();
        itemsForComboBox.clear();
        itemsForComboBox.addAll(tempA);
        itemsForComboBox.add(0, new GroupItems(-1, "Все товары", isProducts));
        if (temp != null) groupComboBox.setValue(temp);
        else {
            groupComboBox.getSelectionModel().selectFirst();
        }
        ListGroup.getRoot().getChildren().clear();

        for (GroupItems groupItem : tempA) {
            TreeItem<GroupItems> item = new TreeItem<>(groupItem);
            if (groupItem.getParent() == null) {
                ListGroup.getRoot().getChildren().add(item);
            } else {
                findParentAndAddChild(ListGroup.getRoot(), item, groupItem.getParent());
            }
        }

        expandAllItems(ListGroup.getRoot());
    }

    private void findParentAndAddChild(TreeItem<GroupItems> currentItem, TreeItem<GroupItems> newItem, GroupItems parent) {
        if (currentItem.getValue() == parent) {
            currentItem.getChildren().add(newItem);
        } else {
            for (TreeItem<GroupItems> childItem : currentItem.getChildren()) {
                findParentAndAddChild(childItem, newItem, parent);
            }
        }
    }

    public void TypeSetGroup() {
        TypeOfStorageItem select = table.getSelectionModel().getSelectedItem();
        GroupItems selectedGroup = groupComboBox.getValue();
        if (select == null || selectedGroup == null) return;
        if (selectedGroup.getID() == -1) selectedGroup = null;
        select.setGroup(selectedGroup);
        dao.getItemTypesDAO().updateTypeList(select);
    }

    public void updateTable() {
        ObservableList<TypeOfStorageItem> items = typeOfStorageItems.stream().
                filter(TypeOfStorageItem::isActive).
                filter(t -> t.isProduct() == isProducts).
                collect(Collectors.toCollection(FXCollections::observableArrayList));

        TreeItem<GroupItems> item = ListGroup.getSelectionModel().getSelectedItem();
        if (item != ListGroup.getRoot() && item != null) {
            items.removeIf(t -> t.getGroup() == null || t.getGroup().getID() != item.getValue().getID());
        }
        table.setItems(items);
        table.refresh();
    }


    public void cancelEditName(TableColumn.CellEditEvent<TypeOfStorageItem, String> event) {
        event.getRowValue().setName(event.getOldValue());
        updateTable();
    }

    public void commitEditName(TableColumn.CellEditEvent<TypeOfStorageItem, String> event) {
        event.getRowValue().setName(event.getNewValue());
        dao.getItemTypesDAO().updateTypeList(event.getRowValue());
        updateListView();
        updateTable();
    }

    public void cancelEditWeight(TableColumn.CellEditEvent<TypeOfStorageItem, Double> event) {
        event.getRowValue().setWeight(event.getOldValue());
        dao.getItemTypesDAO().updateTypeList(event.getRowValue());
        updateListView();
        updateTable();
    }

    public void commitEditWeight(TableColumn.CellEditEvent<TypeOfStorageItem, Double> event) {
        event.getRowValue().setWeight(event.getNewValue());
        dao.getItemTypesDAO().updateTypeList(event.getRowValue());
        updateListView();
        updateTable();
    }

}