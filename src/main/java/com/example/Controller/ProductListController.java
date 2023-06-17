package com.example.Controller;

import DAO.DAOFactory;
import Model.GroupItems;
import Model.TypeOfStorageItem;
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

public class ProductListController {
    public TableView<TypeOfStorageItem> table;
    public TableColumn<TypeOfStorageItem, Long> idColumn;
    public TableColumn<TypeOfStorageItem, String> nameColumn;
    public TableColumn<TypeOfStorageItem, Double> weightColumn;
    public TreeView<GroupItems> ListGroup;
    public Button removeButton;
    public Button explainButton;
    public Button saveToExcelButton;
    private ArrayList<TypeOfStorageItem> TypeOfStorageItem;
    private ArrayList<GroupItems> groupItems;
    private boolean isProducts;
    private TypeOfStorageItem itemForReturn;

    public void init(ArrayList<TypeOfStorageItem> typeOfStorageItems, ArrayList<GroupItems> groupItems, boolean isProducts) {
        //this.groupItems = dao.getGroupItemsDAO().getGroupItemsList(true);
        //this.TypeOfStorageItem = dao.getItemTypesDAO().getTypeList(true, isProducts, groupItems);
        this.groupItems = groupItems;
        this.TypeOfStorageItem = typeOfStorageItems;
        this.isProducts = isProducts;

        ListGroup.setRoot(new TreeItem<>(new GroupItems(-1, "Все", isProducts)));

        ListGroup.setOnMouseClicked(mouseEvent -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                updateTable();
            }
        });

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

    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(event -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem != ListGroup.getRoot()) {
                groupItems.remove(selectedItem.getValue());
                selectedItem.getParent().getChildren().remove(selectedItem);
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
            controller.init(groupItems, value, isProducts);
            stage.setResizable(false);
            stage.show();

        });

        contextMenu.getItems().addAll(deleteItem, create);

        contextMenu.setOnShowing(event -> {
            TreeItem<GroupItems> selectedItem = ListGroup.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem != ListGroup.getRoot()) {
                deleteItem.setDisable(false);
            } else deleteItem.setDisable(true);
        });

        ListGroup.setContextMenu(contextMenu);
    }

    public void initForSupply(ArrayList<TypeOfStorageItem> TypeOfStorageItem, boolean isProducts, TypeOfStorageItem returnedType) {
        this.TypeOfStorageItem = TypeOfStorageItem;
        this.isProducts = isProducts;
        this.itemForReturn = returnedType;

        configureUI();
        updateListView();
        updateTable();
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
        controller.init(TypeOfStorageItem, groupForNewType, isProducts);
        stage.setResizable(false);
        stage.show();
    }

    public void removeProduct() {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        table.getSelectionModel().getSelectedItem().setActive(false);
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
        ObservableList<TreeItem<GroupItems>> items;
        items = ListGroup.getRoot().getChildren();
        items.clear();
        HashMap<Long, TreeItem<GroupItems>> nodeMap = new HashMap<>();

        // Строим древовидную структуру
        for (GroupItems groupItems : this.groupItems) {
            if (groupItems.isProduct() != isProducts) continue;

            TreeItem<GroupItems> item = new TreeItem<>(groupItems);
            nodeMap.put(groupItems.getID(), item);

            if (groupItems.getParent() == null) {
                // Если у группы нет родителя, устанавливаем ее как корневую
                items.add(item);
            } else {
                // Ищем родительский элемент по ID и добавляем текущий элемент в его дочерние элементы
                TreeItem<GroupItems> parentItem = nodeMap.get(groupItems.getParent().getID());
                if (parentItem != null) {
                    parentItem.getChildren().add(item);
                }
            }
        }
        expandAllItems(ListGroup.getRoot());

    }

    public void updateTable() {

        ObservableList<TypeOfStorageItem> items = FXCollections.observableArrayList();
        TreeItem<GroupItems> item = ListGroup.getSelectionModel().getSelectedItem();
        if (item == ListGroup.getRoot() || item == null) {
            for (TypeOfStorageItem product : TypeOfStorageItem) {
                if (product.isActive() && product.isProduct() == isProducts) {
                    items.add(product);
                }
            }
        } else {
            items.addAll(TypeOfStorageItem.stream().filter(e -> Objects.equals(e.getGroup(), item.getValue()) && e.isActive() && e.isProduct() == isProducts).toList());
        }
        table.setItems(items);
        table.refresh();
    }


    public void cancelEditName(TableColumn.CellEditEvent<TypeOfStorageItem, String> event) {
        event.getRowValue().setName(event.getOldValue());
    }

    public void commitEditName(TableColumn.CellEditEvent<TypeOfStorageItem, String> event) {
        event.getRowValue().setName(event.getNewValue());
        updateListView();
        updateTable();
    }

    public void cancelEditGroup(TableColumn.CellEditEvent<TypeOfStorageItem, GroupItems> event) {
        event.getRowValue().setGroup(event.getOldValue());
    }

    public void commitEditGroup(TableColumn.CellEditEvent<TypeOfStorageItem, GroupItems> event) {
        event.getRowValue().setGroup(event.getNewValue());
        updateListView();
        updateTable();
    }

    public void cancelEditWeight(TableColumn.CellEditEvent<TypeOfStorageItem, Double> event) {
        event.getRowValue().setWeight(event.getOldValue());
    }

    public void commitEditWeight(TableColumn.CellEditEvent<TypeOfStorageItem, Double> event) {
        event.getRowValue().setWeight(event.getNewValue());
        updateListView();
        updateTable();
    }

}