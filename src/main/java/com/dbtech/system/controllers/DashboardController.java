package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private Label lblTotalRooms;
    @FXML private Label lblTotalClients;
    @FXML private Label lblActiveReservations;
    @FXML private Label lblOccupancy;
    @FXML private VBox activityContainer;

    private DashboardDAO dashboardDAO;

    @FXML
    public void initialize() {
        dashboardDAO = new DashboardDAO();
        refreshDashboard();
    }

    public void refreshDashboard() {
        // 1. Ενημέρωση Καρτών (Στατιστικά)
        lblTotalRooms.setText(String.valueOf(dashboardDAO.getTotalRooms()));
        lblTotalClients.setText(String.valueOf(dashboardDAO.getTotalClients()));
        lblActiveReservations.setText(String.valueOf(dashboardDAO.getActiveReservations()));
        lblOccupancy.setText(String.format("%.1f%%", dashboardDAO.getOccupancyRate()));

        // 2. Ενημέρωση Recent Activity
        loadRecentActivities();
    }

    private void loadRecentActivities() {
        // Καθαρίζουμε τα παλιά (εκτός από τον τίτλο που είναι το πρώτο παιδί)
        if (activityContainer.getChildren().size() > 1) {
            activityContainer.getChildren().remove(1, activityContainer.getChildren().size());
        }

        List<String> logs = dashboardDAO.getRecentActivities();
        if (logs.isEmpty()) {
            Label placeholder = new Label("No recent activity.");
            placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            activityContainer.getChildren().add(placeholder);
        } else {
            for (String log : logs) {
                Label logLabel = new Label(log);
                logLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
                logLabel.setWrapText(true);
                activityContainer.getChildren().add(logLabel);
            }
        }
    }

    // --- QUICK ACTIONS (Ανοίγουν απευθείας τις φόρμες) ---

    @FXML private void handleQuickReservation() { openDialog("reservation_form.fxml", "New Reservation"); }
    @FXML private void handleQuickClient() { openDialog("client_form.fxml", "New Client"); }
    @FXML private void handleQuickRoom() { openDialog("room_form.fxml", "New Room"); }
    @FXML private void handleQuickTask() { openDialog("task_form.fxml", "New Task"); }

    private void openDialog(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Parent page = loader.load();

            // Ειδική διαχείριση για να περάσουμε τους DAOs στους controllers
            Object controller = loader.getController();
            if (controller instanceof ReservationFormController) ((ReservationFormController) controller).setReservationDAO(new ReservationDAO());
            if (controller instanceof ClientFormController) ((ClientFormController) controller).setClientDAO(new ClientDAO());
            if (controller instanceof RoomFormController) ((RoomFormController) controller).setRoomDAO(new RoomDAO());
            if (controller instanceof TaskFormController) ((TaskFormController) controller).setTaskDAO(new TaskDAO());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(page));
            stage.showAndWait();

            // Ανανέωση Dashboard μετά το κλείσιμο
            refreshDashboard();

        } catch (IOException e) { e.printStackTrace(); }
    }
}