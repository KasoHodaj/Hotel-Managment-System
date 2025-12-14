package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.TaskDAO;
import com.dbtech.system.models.Task;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colType;
    @FXML private TableColumn<Task, String> colTask;
    @FXML private TableColumn<Task, LocalDate> colDate;
    @FXML private TableColumn<Task, Void> colActions;
    @FXML private TextField searchField;

    private TaskDAO taskDAO;

    @FXML
    public void initialize() {
        taskDAO = new TaskDAO();

        colTask.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        colDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDate()));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));

        // Ρυθμίσεις εμφάνισης στηλών
        setupTypeColumn();
        setupTaskColumn(); // (Για το strikethrough)

        // --- ΝΕΟ: Ρύθμιση όλης της γραμμής (Γκριζάρισμα) ---
        setupRowStyle();

        addActionsColumn();
        loadTasks();
    }

    // Μέθοδος που αλλάζει το στυλ της γραμμής αν είναι ολοκληρωμένη
    private void setupRowStyle() {
        taskTable.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setStyle("");
                } else if (task.isCompleted()) {
                    // Αν είναι ολοκληρωμένο: Γκρι φόντο και ημιδιαφανές (για να φαίνονται όλα "σβησμένα")
                    setStyle("-fx-background-color: #f1f5f9; -fx-opacity: 0.6;");
                } else {
                    // Αν είναι ενεργό: Κανονικό λευκό
                    setStyle("-fx-background-color: white; -fx-opacity: 1;");
                }
            }
        });
    }

    private void setupTypeColumn() {
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(type);
                    label.setStyle("-fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 12;");

                    switch (type) {
                        case "Cleaning": label.setStyle(label.getStyle() + "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;"); break;
                        case "Maintenance": label.setStyle(label.getStyle() + "-fx-background-color: #ffedd5; -fx-text-fill: #9a3412;"); break;
                        case "Inventory": label.setStyle(label.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #166534;"); break;
                        case "Administrative": label.setStyle(label.getStyle() + "-fx-background-color: #f3e8ff; -fx-text-fill: #6b21a8;"); break;
                        case "Guest Service": label.setStyle(label.getStyle() + "-fx-background-color: #fce7f3; -fx-text-fill: #9d174d;"); break;
                        default: label.setStyle(label.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #475569;"); break;
                    }

                    HBox container = new HBox(label);
                    container.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(container);
                }
            }
        });
    }

    private void setupTaskColumn() {
        colTask.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    Task task = getTableView().getItems().get(getIndex());
                    // Προσθέτουμε και strikethrough για έξτρα έμφαση
                    if (task.isCompleted()) {
                        setStyle("-fx-text-fill: #94a3b8; -fx-strikethrough: true;");
                    } else {
                        setStyle("-fx-text-fill: #334155;");
                    }
                }
            }
        });
    }

    private void loadTasks() {
        ObservableList<Task> masterData = FXCollections.observableArrayList(taskDAO.getAllTasks());
        FilteredList<Task> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(t -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return t.getDescription().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        SortedList<Task> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(taskTable.comparatorProperty());
        taskTable.setItems(sortedData);
    }

    private void addActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            // ΑΦΑΙΡΕΣΑΜΕ ΤΟ CHECKBOX ΑΠΟ ΕΔΩ
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                btnEdit.setOnAction(e -> showTaskDialog(getTableView().getItems().get(getIndex())));

                btnDelete.setOnAction(e -> {
                    taskDAO.deleteTask(getTableView().getItems().get(getIndex()).getId());
                    loadTasks();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML private void handleNewTask() { showTaskDialog(null); }

    private void showTaskDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("task_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(task == null ? "New Task" : "Edit Task");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(taskTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            TaskFormController controller = loader.getController();
            controller.setTaskDAO(taskDAO);
            if (task != null) controller.setTask(task);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadTasks();

        } catch (IOException e) { e.printStackTrace(); }
    }
}