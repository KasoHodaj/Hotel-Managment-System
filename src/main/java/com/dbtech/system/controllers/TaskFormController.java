package com.dbtech.system.controllers;

import com.dbtech.system.dao.TaskDAO;
import com.dbtech.system.models.Task;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;

public class TaskFormController {

    @FXML private TextField txtDescription;
    @FXML private ComboBox<String> comboType;
    @FXML private DatePicker dateDue;
    @FXML private CheckBox checkCompleted;

    private TaskDAO taskDAO;
    private Task existingTask;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        comboType.setItems(FXCollections.observableArrayList(
                "Cleaning", "Maintenance", "Inventory", "Administrative", "Guest Service", "Other"
        ));
        comboType.getSelectionModel().selectFirst();
        dateDue.setValue(LocalDate.now());
    }

    public void setTaskDAO(TaskDAO dao) { this.taskDAO = dao; }

    public void setTask(Task t) {
        this.existingTask = t;
        txtDescription.setText(t.getDescription());
        comboType.setValue(t.getType());
        dateDue.setValue(t.getDate());
        checkCompleted.setSelected(t.isCompleted());
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        try {
            String desc = txtDescription.getText();
            String type = comboType.getValue();
            LocalDate date = dateDue.getValue();
            boolean done = checkCompleted.isSelected();

            int id = (existingTask == null) ? 0 : existingTask.getId();
            Task t = new Task(id, desc, date, done, type);

            if (existingTask == null) taskDAO.addTask(t);
            else taskDAO.updateTask(t);

            saveClicked = true;
            ((Stage) txtDescription.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleCancel() { ((Stage) txtDescription.getScene().getWindow()).close(); }
}