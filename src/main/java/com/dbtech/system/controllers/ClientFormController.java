package com.dbtech.system.controllers;

import com.dbtech.system.dao.ClientDAO;
import com.dbtech.system.models.Client;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientFormController {

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;

    private ClientDAO clientDAO;
    private Client existingClient; // Αν είναι null -> New, αλλιώς -> Edit
    private boolean saveClicked = false; // Για να ξέρουμε αν πατήθηκε το Save

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    // Αυτή τη μέθοδο την καλούμε όταν πατάμε "Edit" για να γεμίσουμε τα πεδία
    public void setClient(Client client) {
        this.existingClient = client;
        txtName.setText(client.getFullName());
        txtEmail.setText(client.getEmail());
        txtPhone.setText(client.getPhoneNumber());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        // Παίρνουμε τις τιμές από τα πεδία
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        if (existingClient == null) {
            // NEW CLIENT
            // Το ID είναι 0 γιατί θα το βάλει η βάση (SERIAL)
            Client newClient = new Client(0, name, email, phone);
            clientDAO.addClient(newClient);
        } else {
            // EDIT CLIENT
            // Ενημερώνουμε το υπάρχον αντικείμενο
            existingClient.setFullName(name);
            existingClient.setEmail(email);
            existingClient.setPhoneNumber(phone);
            clientDAO.updateClient(existingClient);
        }

        saveClicked = true;
        closeStage();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}