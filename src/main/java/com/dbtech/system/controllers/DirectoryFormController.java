package com.dbtech.system.controllers;

import com.dbtech.system.dao.DirectoryDAO;
import com.dbtech.system.models.PhoneContact;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DirectoryFormController {

    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtLocation;

    private DirectoryDAO directoryDAO;
    private PhoneContact existingContact;
    private boolean saveClicked = false;

    public void setDirectoryDAO(DirectoryDAO dao) { this.directoryDAO = dao; }

    public void setContact(PhoneContact c) {
        this.existingContact = c;
        txtName.setText(c.getName());
        txtPhone.setText(c.getPhone());
        txtLocation.setText(c.getLocation());
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        try {
            String name = txtName.getText();
            String phone = txtPhone.getText();
            String loc = txtLocation.getText();
            int id = (existingContact == null) ? 0 : existingContact.getId();

            PhoneContact c = new PhoneContact(id, name, phone, loc);

            if (existingContact == null) directoryDAO.addContact(c);
            else directoryDAO.updateContact(c);

            saveClicked = true;
            ((Stage) txtName.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleCancel() { ((Stage) txtName.getScene().getWindow()).close(); }
}