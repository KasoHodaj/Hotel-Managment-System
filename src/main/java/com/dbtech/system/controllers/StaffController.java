package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.StaffDAO;
import com.dbtech.system.models.Staff;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffController {

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> colName;
    @FXML private TableColumn<Staff, String> colRole; // Εδώ θα μπουν τα χρώματα
    @FXML private TableColumn<Staff, String> colPhone;
    @FXML private TableColumn<Staff, String> colWorkDays;
    @FXML private TableColumn<Staff, Double> colSalary;
    @FXML private TableColumn<Staff, Void> colActions;
    @FXML private TextField searchField;

    private StaffDAO staffDAO;

    @FXML
    public void initialize() {
        staffDAO = new StaffDAO();

        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFullName()));
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNumber()));
        colWorkDays.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWorkDays()));
        colSalary.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSalary()).asObject());

        // Εφαρμογή χρωμάτων στον Ρόλο
        setupRoleColumn();
        addActionsColumn();
        loadStaff();
    }

    private void setupRoleColumn() {
        colRole.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);

                if (empty || role == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(role);
                    setStyle("-fx-font-weight: bold;"); // Έντονα γράμματα

                    // Απλά χρώματα κειμένου (Text Colors)
                    switch (role) {
                        case "Manager":
                            setTextFill(Color.web("#16a34a")); // Πράσινο
                            break;
                        case "Reception":
                            setTextFill(Color.web("#2563eb")); // Μπλε
                            break;
                        case "Cleaning":
                            setTextFill(Color.web("#ea580c")); // Πορτοκαλί
                            break;
                        default:
                            setTextFill(Color.web("#475569")); // Γκρι
                            break;
                    }
                }
            }
        });
    }

    private void loadStaff() {
        ObservableList<Staff> masterData = FXCollections.observableArrayList(staffDAO.getAllStaff());

        FilteredList<Staff> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(staff -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return staff.getFullName().toLowerCase().contains(lower) || staff.getRole().toLowerCase().contains(lower);
            });
        });

        SortedList<Staff> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(staffTable.comparatorProperty());
        staffTable.setItems(sortedData);
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
                    Staff staff = getTableView().getItems().get(getIndex());
                    showStaffDialog(staff);
                });

                btnDelete.setOnAction(event -> {
                    Staff staff = getTableView().getItems().get(getIndex());
                    staffDAO.deleteStaff(staff.getStaffId());
                    loadStaff();
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
    private void handleNewStaff() {
        showStaffDialog(null);
    }

    private void showStaffDialog(Staff staff) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("staff_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(staff == null ? "New Staff" : "Edit Staff");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(staffTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            StaffFormController controller = loader.getController();
            controller.setStaffDAO(staffDAO);
            if (staff != null) controller.setStaff(staff);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadStaff();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}