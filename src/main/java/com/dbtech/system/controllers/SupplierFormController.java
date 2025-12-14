package com.dbtech.system.controllers;

import com.dbtech.system.dao.SupplierDAO;
import com.dbtech.system.models.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SupplierFormController {

    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;

    private SupplierDAO supplierDAO;
    private Supplier existingSupplier;
    private boolean saveClicked = false;

    public void setSupplierDAO(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public void setSupplier(Supplier supplier) {
        this.existingSupplier = supplier;
        txtName.setText(supplier.getSupplierName());
        txtPhone.setText(supplier.getPhoneNumber());
        txtAddress.setText(supplier.getAddress());
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        try {
            String name = txtName.getText();
            String phone = txtPhone.getText();
            String address = txtAddress.getText();

            int id = (existingSupplier == null) ? 0 : existingSupplier.getSupplierId();
            Supplier supplier = new Supplier(id, name, phone, address);

            if (existingSupplier == null) {
                supplierDAO.addSupplier(supplier);
            } else {
                supplierDAO.updateSupplier(supplier);
            }

            saveClicked = true;
            ((Stage) txtName.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) txtName.getScene().getWindow()).close();
    }
}