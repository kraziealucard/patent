package com.example.Controller;

import DAO.DAOFactory;
import Model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ContractorController {
    public TableView<Contractor> table;
    public TableColumn<Contractor, String> columnName;
    public TableColumn<Contractor, Boolean> columnIsSupplier;
    public TableColumn<Contractor, Boolean> columnIsCustomer;
    public TableColumn<Contractor, String> columnAddress;
    public TableColumn<Contractor, String> columnInformation;
    public TableColumn<Contractor, String> columnPassport;
    public TableColumn<Contractor, String> columnPhone;
    public TableColumn<Contractor, String> columnBankRequisites;
    public TableColumn<Contractor, String> columnIIN;
    public TableColumn<Contractor, String> columnKPP;
    public Button btnAdd;
    public Button btnRemove;
    public TextField TFName;
    public CheckBox CBisSupplier;
    public TextField TFaddress;
    public TextArea TAInfo;
    public TextField TFpassport;
    public TextField TFphone;
    public TextField TFbankRequisites;
    public TextField TFinn;
    public TextField TFkpp;
    public CheckBox CheckBoxSelection;
    public ComboBox<String> comboBoxContactorType;
    public VBox boxForFields;
    private ObservableList<Contractor> ObsContractors;
    private ArrayList<Contractor> contractors;
    private DAOFactory dao;

    public void init(DAOFactory dao, Tab tab, ArrayList<Contractor> contractors) {
        this.dao = dao;
        this.contractors = contractors;
        configureUI();
        updateTable();
        tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateTable();
            }
        });
    }

    private void configureUI() {
        configureClmn();
        configureTable();
        configureFields();
    }

    private void filteredCustomers() {
        ObservableList<Contractor> temp = FXCollections.observableArrayList();
        temp.addAll(ObsContractors.stream().filter(Contractor::isCustomer).toList());
        table.setItems(temp);
    }

    private void filteredSuppliers() {
        ObservableList<Contractor> temp = FXCollections.observableArrayList();
        temp.addAll(ObsContractors.stream().filter(Contractor::isSupplier).toList());
        table.setItems(temp);
    }

    private void configureClmn() {
        columnName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        columnIsSupplier.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isSupplier()));
        columnIsCustomer.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isCustomer()));
        columnAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        columnInformation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInformation()));
        columnPassport.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassport()));
        columnPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        columnBankRequisites.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBankRequisites()));
        columnIIN.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIIN()));
        columnKPP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKPP()));

        columnIsSupplier.setCellFactory(column -> new TableCell<Contractor, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Да" : "Нет"));
            }
        });

        columnIsCustomer.setCellFactory(column -> new TableCell<Contractor, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Да" : "Нет"));
            }
        });
    }

    private void configureFields() {
        TFName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                if (table.getSelectionModel().getSelectedItem() == null) return;
                table.getSelectionModel().getSelectedItem().setName(TFName.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFaddress.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setAddress(TFaddress.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TAInfo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setInformation(TAInfo.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFpassport.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setPassport(TFpassport.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFphone.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setPhone(TFphone.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFbankRequisites.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setBankRequisites(TFbankRequisites.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFinn.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setIIN(TFinn.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        TFkpp.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && table.getSelectionModel().getSelectedItem() != null) {
                table.getSelectionModel().getSelectedItem().setKPP(TFkpp.getText());
                dao.getContactorDAO().updateContractor(table.getSelectionModel().getSelectedItem());
                table.refresh();
            }
        });

        CBisSupplier.setContentDisplay(ContentDisplay.RIGHT);

        comboBoxContactorType.getItems().addAll("поставщиков", "покупателей");
        comboBoxContactorType.getSelectionModel().selectFirst();
    }

    private void configureTable() {

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) disableUIElements(true);
            else {
                disableUIElements(false);
                displaySelectedContractor(newValue);
                table.refresh();
            }
        });

    }

    private void disableUIElements(boolean res) {
        TFName.setText("");
        CBisSupplier.setSelected(false);
        TFaddress.setText("");
        TAInfo.setText("");
        TFpassport.setText("");
        TFphone.setText("");
        TFbankRequisites.setText("");
        TFinn.setText("");
        TFkpp.setText("");

        btnRemove.setDisable(res);
        TFName.setDisable(res);
        CBisSupplier.setDisable(res);
        TFaddress.setDisable(res);
        TAInfo.setDisable(res);
        TFpassport.setDisable(res);
        TFphone.setDisable(res);
        TFbankRequisites.setDisable(res);
        TFinn.setDisable(res);
        TFkpp.setDisable(res);
    }

    private void displaySelectedContractor(Contractor contractor) {
        TFName.setText(contractor.getName());
        CBisSupplier.setSelected(contractor.isSupplier());
        TFaddress.setText(contractor.getAddress());
        TAInfo.setText(contractor.getInformation());
        TFpassport.setText(contractor.getPassport());
        TFphone.setText(contractor.getPhone());
        TFbankRequisites.setText(contractor.getBankRequisites());
        TFinn.setText(contractor.getBankRequisites());
        TFkpp.setText(contractor.getKPP());
    }

    public void addContractor(ActionEvent actionEvent) {
        Contractor newContractor;
        newContractor = new Contractor(-1, "Новый контрагент");
        newContractor.setID(dao.getContactorDAO().addContactor(newContractor));
        contractors.add(newContractor);
        updateTable();
    }

    public void removeContractor(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Contractor currentContractor = table.getSelectionModel().getSelectedItem();
        currentContractor.setActive(false);
        dao.getContactorDAO().updateContractor(currentContractor);
        updateTable();
        table.refresh();
    }

    public void setSupplier(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Contractor currentContractor = table.getSelectionModel().getSelectedItem();
        currentContractor.setSupplier(((CheckBox) actionEvent.getSource()).isSelected());
        dao.getContactorDAO().updateContractor(currentContractor);
        table.refresh();
    }

    @FXML
    private void updateTable() {
        ObsContractors = contractors.stream()
                .filter(Contractor::isActive)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        table.setItems(null);
        if (!CheckBoxSelection.isSelected()) {
            table.setItems(ObsContractors);
        } else switch (comboBoxContactorType.getSelectionModel().getSelectedItem()) {
            case "поставщиков" -> filteredSuppliers();
            case "покупателей" -> filteredCustomers();
            default -> {
            }
        }
        table.refresh();
        table.getSelectionModel().selectFirst();
    }

    public void toFirst(ActionEvent actionEvent) {
        updateTable();
        table.getSelectionModel().selectFirst();
    }

    public void toLast(ActionEvent actionEvent) {
        updateTable();
        table.getSelectionModel().selectLast();
    }

    public void TFNameAction(ActionEvent actionEvent) {
        CBisSupplier.requestFocus();
    }

    public void TFaddressAction(ActionEvent actionEvent) {
        TAInfo.requestFocus();
    }

    public void TFpassportAction(ActionEvent actionEvent) {
        TFphone.requestFocus();
    }

    public void TFphoneAction(ActionEvent actionEvent) {
        TFbankRequisites.requestFocus();
    }

    public void TFbankRequisitesAction(ActionEvent actionEvent) {
        TFinn.requestFocus();
    }

    public void TFinnAction(ActionEvent actionEvent) {
        TFkpp.requestFocus();
    }

    public void TFkppAction(ActionEvent actionEvent) {
        table.requestFocus();
    }

    public void toExcel(ActionEvent actionEvent) {
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

    public void setCustomer(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Contractor currentContractor = table.getSelectionModel().getSelectedItem();
        currentContractor.setCustomer(((CheckBox) actionEvent.getSource()).isSelected());
        dao.getContactorDAO().updateContractor(currentContractor);
        table.refresh();
    }
}
