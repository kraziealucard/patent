package com.example.Controller;

import Model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ContractorController {
    public TableView<Contractor> table;
    public TableColumn<Contractor, String> columnName;
    public TableColumn<Contractor, Boolean> columnIsSupplier;
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
    private ObservableList<Contractor> data;
    private ArrayList<Customer> customersBefore;
    private ArrayList<Supplier> suppliersBefore;

    public void init(ArrayList<Customer> customers, ArrayList<Supplier> suppliers) {
        this.customersBefore = customers;
        this.suppliersBefore = suppliers;
        configureUI();
    }

    private void configureUI() {
        configureClmn();
        configureTable();
        configureFields();
    }

    private void filteredCustomers() {
        ObservableList<Contractor> temp = FXCollections.observableArrayList();
        temp.addAll(data.stream().filter(e -> e instanceof Customer).toList());
        table.setItems(temp);
    }

    private void filteredSuppliers() {
        ObservableList<Contractor> temp = FXCollections.observableArrayList();
        temp.addAll(data.stream().filter(e -> e instanceof Supplier).toList());
        table.setItems(temp);
    }

    private void configureClmn() {
        columnName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        columnIsSupplier.setCellValueFactory(cellData -> {
            Contractor obj = cellData.getValue();
            boolean isSupplier = obj instanceof Supplier;
            return new SimpleBooleanProperty(isSupplier);
        });

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
    }

    private void configureFields() {
        TFName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (table.getSelectionModel().getSelectedItem() == null) return;
                table.getSelectionModel().getSelectedItem().setName(TFName.getText());
                table.refresh();
            }
        });

        TFaddress.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setAddress(TFaddress.getText());
                table.refresh();
            }
        });

        TAInfo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setInformation(TAInfo.getText());
                table.refresh();
            }
        });

        TFpassport.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setPassport(TFpassport.getText());
                table.refresh();
            }
        });

        TFphone.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setPhone(TFphone.getText());
                table.refresh();
            }
        });

        TFbankRequisites.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setBankRequisites(TFbankRequisites.getText());
                table.refresh();
            }
        });

        TFinn.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setIIN(TFinn.getText());
                table.refresh();
            }
        });

        TFkpp.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                table.getSelectionModel().getSelectedItem().setKPP(TFkpp.getText());
                table.refresh();
            }
        });

        CBisSupplier.setContentDisplay(ContentDisplay.RIGHT);

        comboBoxContactorType.getItems().addAll("поставщиков", "покупателей");
        comboBoxContactorType.getSelectionModel().selectFirst();
    }

    private void configureTable() {
        data = FXCollections.observableArrayList();

        for (Customer customer : customersBefore) {
            data.add(customer.clone());
        }

        for (Supplier supplier : suppliersBefore) {
            data.add(supplier.clone());
        }

        table.setItems(data);
        table.refresh();

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) disableUIElements(true);
            else {
                disableUIElements(false);
                displaySelectedContractor(newValue);
                table.refresh();
            }
        });

        table.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && newValue) {
                updateTable();
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
        CBisSupplier.setSelected(contractor instanceof Supplier);
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
        if (!CheckBoxSelection.isSelected()) {
            newContractor = new Customer(-1, "Новый контрагент");
            newContractor.setActive(true);
            data.add(newContractor);
            table.getSelectionModel().select(newContractor);
        } else switch (comboBoxContactorType.getValue()) {
            case "поставщиков" -> {
                newContractor = new Supplier(-1, "Новый поставщик");
                newContractor.setActive(true);
                table.getItems().add(newContractor);
                table.getSelectionModel().select(newContractor);
                data.add(newContractor);
            }

            case "покупателей" -> {
                newContractor = new Customer(-1, "Новый покупатель");
                newContractor.setActive(true);
                table.getItems().add(newContractor);
                table.getSelectionModel().select(newContractor);
                data.add(newContractor);
            }
            default -> {
            }
        }

        table.refresh();
    }

    public void removeContractor(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        table.getSelectionModel().getSelectedItem().setActive(false);
        data.remove(table.getSelectionModel().getSelectedItem());
        updateTable();
    }

    public void setTypeContractor(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Contractor currentBeforeChange = table.getSelectionModel().getSelectedItem();

        if (CBisSupplier.isSelected()) {
            Supplier temp = new Supplier(currentBeforeChange.getID(), currentBeforeChange.getName());
            temp.setAddress(currentBeforeChange.getAddress());
            temp.setInformation(currentBeforeChange.getInformation());
            temp.setPassport(currentBeforeChange.getPassport());
            temp.setPhone(currentBeforeChange.getPhone());
            temp.setBankRequisites(currentBeforeChange.getBankRequisites());
            temp.setIIN(currentBeforeChange.getIIN());
            temp.setKPP(currentBeforeChange.getKPP());

            table.getItems().remove(currentBeforeChange);
            table.getItems().add(temp);
            table.getSelectionModel().select(temp);

            if (CheckBoxSelection.isSelected()) {
                data.remove(currentBeforeChange);
                data.add(temp);
            }
        } else {
            Customer temp = new Customer(currentBeforeChange.getID(), currentBeforeChange.getName());
            temp.setAddress(currentBeforeChange.getAddress());
            temp.setInformation(currentBeforeChange.getInformation());
            temp.setPassport(currentBeforeChange.getPassport());
            temp.setPhone(currentBeforeChange.getPhone());
            temp.setBankRequisites(currentBeforeChange.getBankRequisites());
            temp.setIIN(currentBeforeChange.getIIN());
            temp.setKPP(currentBeforeChange.getKPP());

            table.getItems().remove(currentBeforeChange);
            table.getItems().add(temp);
            table.getSelectionModel().select(temp);

            if (CheckBoxSelection.isSelected()) {
                data.remove(currentBeforeChange);
                data.add(temp);
            }
        }
    }

    @FXML
    private void updateTable() {

        table.setItems(null);
        if (!CheckBoxSelection.isSelected()) {
            table.setItems(data);
        } else switch (comboBoxContactorType.getSelectionModel().getSelectedItem()) {
            case "поставщиков" -> filteredSuppliers();
            case "покупателей" -> filteredCustomers();
            default -> {
            }
        }
        table.refresh();
        table.getSelectionModel().selectFirst();
    }

    public void acceptChanges(ActionEvent actionEvent) {
        table.requestFocus();
        customersBefore.clear();
        suppliersBefore.clear();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof Supplier) suppliersBefore.add((Supplier) data.get(i).clone());
            else customersBefore.add((Customer) data.get(i).clone());
        }
    }

    public void cancelChanges(ActionEvent actionEvent) {
        data.clear();

        for (Customer customer : customersBefore) {
            data.add(customer.clone());
        }

        for (Supplier supplier : suppliersBefore) {
            data.add(supplier.clone());
        }
        updateTable();
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
}
