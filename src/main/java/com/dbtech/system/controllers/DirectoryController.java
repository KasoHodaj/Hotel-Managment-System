package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.DirectoryDAO;
import com.dbtech.system.models.PhoneContact;
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

public class DirectoryController {

    @FXML private TableView<PhoneContact> directoryTable;
    @FXML private TableColumn<PhoneContact, String> colName;
    @FXML private TableColumn<PhoneContact, String> colPhone;
    @FXML private TableColumn<PhoneContact, String> colLocation;
    @FXML private TableColumn<PhoneContact, Void> colActions;
    @FXML private TextField searchField;

    private DirectoryDAO directoryDAO;

    @FXML
    public void initialize() {
        directoryDAO = new DirectoryDAO();

        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone()));
        colLocation.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocation()));

        addActionsColumn();
        loadContacts();
    }

    private void loadContacts() {
        ObservableList<PhoneContact> masterData = FXCollections.observableArrayList(directoryDAO.getAllContacts());
        FilteredList<PhoneContact> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(c -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return c.getName().toLowerCase().contains(lower) || c.getLocation().toLowerCase().contains(lower);
            });
        });

        SortedList<PhoneContact> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(directoryTable.comparatorProperty());
        directoryTable.setItems(sortedData);
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
                    PhoneContact c = getTableView().getItems().get(getIndex());
                    showContactDialog(c);
                });

                btnDelete.setOnAction(event -> {
                    PhoneContact c = getTableView().getItems().get(getIndex());
                    directoryDAO.deleteContact(c.getId());
                    loadContacts();
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
    private void handleNewContact() {
        showContactDialog(null);
    }

    private void showContactDialog(PhoneContact contact) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("directory_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(contact == null ? "New Contact" : "Edit Contact");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(directoryTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            DirectoryFormController controller = loader.getController();
            controller.setDirectoryDAO(directoryDAO);
            if (contact != null) controller.setContact(contact);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadContacts();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
