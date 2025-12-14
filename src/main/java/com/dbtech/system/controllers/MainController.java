package com.dbtech.system.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainController {

    @FXML private BorderPane mainContainer;

    // Δηλώνουμε όλα τα κουμπιά για να τα χειριστούμε
    @FXML private Button btnDashboard;
    @FXML private Button btnReservations;
    @FXML private Button btnClients;
    @FXML private Button btnRooms;
    @FXML private Button btnStaff;
    @FXML private Button btnSuppliers;
    @FXML private Button btnOrders;
    @FXML private Button btnDirectory;
    @FXML private Button btnTodo;

    @FXML
    public void initialize() {
        // Ξεκινάμε με τα Rooms
        handleRooms();
    }

    // --- ΒΟΗΘΗΤΙΚΗ ΜΕΘΟΔΟΣ ΓΙΑ ΤΟ HIGHLIGHT ---
    private void setActiveButton(Button activeButton) {
        List<Button> buttons = Arrays.asList(
                btnDashboard, btnReservations, btnClients, btnRooms,
                btnStaff, btnSuppliers, btnOrders, btnDirectory, btnTodo
        );

        // Αφαιρούμε την κλάση 'menu-button-active' από όλα
        for (Button btn : buttons) {
            btn.getStyleClass().remove("menu-button-active");
        }

        // Την προσθέτουμε μόνο σε αυτό που πατήθηκε
        activeButton.getStyleClass().add("menu-button-active");
    }

    // --- ACTIONS ---

    @FXML
    private void handleDashboard() {
        setActiveButton(btnDashboard);
        loadPage("dashboard");
    }

    @FXML
    private void handleReservations() {
        setActiveButton(btnReservations);
        loadPage("reservations");
    }

    @FXML
    private void handleClients() {
        setActiveButton(btnClients);
        loadPage("clients");
    }

    @FXML
    private void handleRooms() {
        setActiveButton(btnRooms);
        loadPage("rooms");
    }

    @FXML
    private void handleStaff() {
        setActiveButton(btnStaff);
        loadPage("staff");
    }

    @FXML
    private void handleSuppliers() {
        setActiveButton(btnSuppliers);
        loadPage("suppliers");
    }

    @FXML
    private void handleOrders() {
        setActiveButton(btnOrders);
        loadPage("orders");
    }

    @FXML
    private void handleDirectory() {
        setActiveButton(btnDirectory);
        loadPage("directory");
    }

    @FXML
    private void handleTodo() {
        setActiveButton(btnTodo);
        loadPage("tasks");
    }

    private void loadPage(String pageName) {
        try {
            String path = "/com/dbtech/system/" + pageName + ".fxml";
            if (getClass().getResource(path) == null) {
                System.err.println("❌ ΣΦΑΛΜΑ: Δεν βρέθηκε: " + path);
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            mainContainer.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}