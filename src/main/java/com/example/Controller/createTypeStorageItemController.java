package com.example.Controller;

import DAO.DAOFactory;
import Model.GroupItems;
import Model.TypeOfStorageItem;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;

public class createTypeStorageItemController {
    public TextField nameField;
    public TextField weightField;
    public Button btn;
    private GroupItems selectedGroup;
    private boolean isProduct;
    private DAOFactory dao;
    private ArrayList<TypeOfStorageItem> types;

    public void init(DAOFactory dao, GroupItems selectedGroup, boolean isProduct, ArrayList<TypeOfStorageItem> types) {
        this.dao = dao;
        this.isProduct = isProduct;
        this.selectedGroup = selectedGroup;
        this.types = types;

        weightField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d*")) {
                        return change;
                    }
                    return null;
                }));
    }

    public void btnClick() {
        if (nameField.getText().isBlank() || weightField.getText().isBlank()) {
            return;
        }
        TypeOfStorageItem newItem = new TypeOfStorageItem(-1, nameField.getText(), Double.parseDouble(weightField.getText()), isProduct);
        newItem.setGroup(selectedGroup);
        newItem.setID(dao.getItemTypesDAO().addItemTypes(newItem));
        types.add(newItem);
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
