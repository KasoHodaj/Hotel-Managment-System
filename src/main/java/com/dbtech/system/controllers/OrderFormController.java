package com.dbtech.system.controllers;

import com.dbtech.system.dao.OrderDAO;
import com.dbtech.system.dao.SupplierDAO;
import com.dbtech.system.models.Order;
import com.dbtech.system.models.Supplier;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class OrderFormController {

    @FXML private TextField txtNumber;
    @FXML private ComboBox<Supplier> comboSupplier;
    @FXML private TextField txtType;
    @FXML private TextField txtQuantity;
    @FXML private DatePicker datePlacement;

    private OrderDAO orderDAO;
    private SupplierDAO supplierDAO = new SupplierDAO();
    private Order existingOrder;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        comboSupplier.setItems(FXCollections.observableArrayList(supplierDAO.getAllSuppliers()));

        // Show Supplier names
        comboSupplier.setConverter(new StringConverter<>() {
            @Override public String toString(Supplier s) { return s == null ? "" : s.getSupplierName(); }
            @Override public Supplier fromString(String s) { return null; }
        });

        datePlacement.setValue(LocalDate.now());

        // Στο πεδίο Order Number γράφουμε απλά "Auto-generated"
        txtNumber.setText("Auto-generated");
        txtNumber.setDisable(true); // Το απενεργοποιούμε τελείως
    }

    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;

    }

    public void setOrder(Order order) {
        this.existingOrder = order;

        if (order != null) {
            // EDIT: Φόρτωσε τον υπάρχοντα κωδικό από τη βάση
            txtNumber.setText(order.getOrderNumber());
            txtType.setText(order.getType());
            txtQuantity.setText(String.valueOf(order.getQuantity()));
            datePlacement.setValue(order.getDate());

            for (Supplier s : comboSupplier.getItems()) {
                if (s.getSupplierId() == order.getSupplierId()) {
                    comboSupplier.setValue(s);
                    break;
                }
            }
        } else {
            // NEW: Μην καλείς τίποτα! Η βάση θα το φτιάξει.
            txtNumber.setText("Θα δημιουργηθεί αυτόματα");
        }








    }



    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        if(comboSupplier.getValue() == null) return;

        try {
            String num = txtNumber.getText();
            int supId = comboSupplier.getValue().getSupplierId();
            String type = txtType.getText();
            int qty = Integer.parseInt(txtQuantity.getText());
            LocalDate date = datePlacement.getValue();

            int id = (existingOrder == null) ? 0 : existingOrder.getOrderId();

            Order order = new Order(
                    0,
                    null, // Δεν μας νοιάζει το orderNumber, θα παραχθεί
                    comboSupplier.getValue().getSupplierId(),
                    Integer.parseInt(txtQuantity.getText()),
                    txtType.getText(),
                    null, // Δεν μας νοιάζει η ημερομηνία, θα μπει η σημερινή
                    comboSupplier.getValue().getSupplierName()
            );

            if (existingOrder == null) {
                orderDAO.addOrder(order);
            } else {
                orderDAO.updateOrder(order);
            }

            saveClicked = true;
            ((Stage) txtNumber.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleCancel() {
        ((Stage) txtNumber.getScene().getWindow()).close();
    }
}