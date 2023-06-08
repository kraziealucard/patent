package com.example.Controller;

import Model.GroupItems;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CreateGroupController {

    public TextField groupNameTextField;
    ArrayList<GroupItems> groups;
    GroupItems parent;
    boolean isProduct;
    public void init(ArrayList<GroupItems> groups, GroupItems parent, boolean isProduct){
        this.groups=groups;
        this.isProduct=isProduct;
        this.parent=parent;
    }


    public void addButtonClicked(ActionEvent actionEvent) {
        if (groupNameTextField.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Введите название группы");
            alert.showAndWait();
            return;
        }
        GroupItems g= new GroupItems(groups.size()+1,groupNameTextField.getText(),isProduct);
        g.setParent(parent);
        groups.add(g);
        Stage stage = (Stage) groupNameTextField.getScene().getWindow();
        stage.close();
    }
}
