package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.SupplierDAO;
import com.dbtech.system.models.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SupplierController {

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colPhone;
    @FXML private TableColumn<Supplier, String> colAddress;
    @FXML private TableColumn<Supplier, Void> colActions;
    @FXML private TextField searchField;

    private SupplierDAO supplierDAO;

    @FXML
    public void initialize() {
        supplierDAO = new SupplierDAO();

        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSupplierName()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNumber()));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress()));

        addActionsColumn();
        loadSuppliers();
    }

    private void loadSuppliers() {
        ObservableList<Supplier> masterData = FXCollections.observableArrayList(supplierDAO.getAllSuppliers());

        FilteredList<Supplier> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(supplier -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return supplier.getSupplierName().toLowerCase().contains(lower);
            });
        });

        SortedList<Supplier> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(supplierTable.comparatorProperty());
        supplierTable.setItems(sortedData);
    }

    private void addActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                btnEdit.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    showSupplierDialog(supplier);
                });

                btnDelete.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    supplierDAO.deleteSupplier(supplier.getSupplierId());
                    loadSuppliers();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @FXML
    private void handleNewSupplier() {
        showSupplierDialog(null);
    }

    private void showSupplierDialog(Supplier supplier) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("supplier_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(supplier == null ? "New Supplier" : "Edit Supplier");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(supplierTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            SupplierFormController controller = loader.getController();
            controller.setSupplierDAO(supplierDAO);
            if (supplier != null) controller.setSupplier(supplier);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadSuppliers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}