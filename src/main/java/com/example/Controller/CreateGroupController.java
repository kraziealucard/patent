package com.example.Controller;

import DAO.DAOFactory;
import Model.GroupItems;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CreateGroupController {

    public TextField groupNameTextField;
    GroupItems parent;
    DAOFactory dao;
    boolean isProduct;
    ArrayList<GroupItems> groupItems;

    public void init(DAOFactory dao, GroupItems parent, boolean isProduct, ArrayList<GroupItems> groupItems) {
        this.dao = dao;
        this.isProduct = isProduct;
        this.parent = parent;
        this.groupItems = groupItems;
    }


    public void addButtonClicked(ActionEvent actionEvent) {
        if (groupNameTextField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Введите название группы");
            alert.showAndWait();
            return;
        }
        GroupItems g = new GroupItems(-1, groupNameTextField.getText(), isProduct);
        g.setParent(parent);
        g.setID(dao.getGroupItemsDAO().addGroupItems(g));
        groupItems.add(g);
        Stage stage = (Stage) groupNameTextField.getScene().getWindow();
        stage.close();
    }
}
