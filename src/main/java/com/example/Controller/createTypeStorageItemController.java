package com.example.Controller;

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
    private ArrayList<TypeOfStorageItem> typeOfStorageItems;
    private GroupItems selectedGroup;
    private boolean isProduct;

    public void init(ArrayList<TypeOfStorageItem> typeOfStorageItems,GroupItems selectedGroup, boolean isProduct) {
        this.typeOfStorageItems = typeOfStorageItems;
        this.isProduct = isProduct;
        this.selectedGroup=selectedGroup;

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
        typeOfStorageItems.add(newItem);
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
