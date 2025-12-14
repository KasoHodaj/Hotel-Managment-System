package com.dbtech.system.controllers;

import com.dbtech.system.dao.StaffDAO;
import com.dbtech.system.models.Staff;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffFormController {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> comboRole; // Τώρα είναι ComboBox
    @FXML private TextField txtPhone;
    @FXML private TextField txtSalary;
    @FXML private DatePicker dateHiring;

    // Τα CheckBoxes για τις ημέρες
    @FXML private CheckBox cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun;

    private StaffDAO staffDAO;
    private Staff existingStaff;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // Προσθήκη ΠΟΛΛΩΝ ρόλων
        comboRole.setItems(FXCollections.observableArrayList(
                "Manager",
                "Assistant Manager",
                "Reception",
                "Concierge",
                "Cleaning",
                "Housekeeping",
                "Maintenance",
                "Security",
                "Chef",
                "Cook",
                "Bartender",
                "Barista",
                "Waiter",
                "Valet",
                "Bellboy",
                "Pool Attendant",
                "Event Coordinator",
                "Accountant"
        ));

        dateHiring.setValue(LocalDate.now()); // Default today
    }

    public void setStaffDAO(StaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

    public void setStaff(Staff staff) {
        this.existingStaff = staff;
        txtName.setText(staff.getFullName());
        comboRole.setValue(staff.getRole()); // Ενημέρωση του ComboBox
        txtPhone.setText(staff.getPhoneNumber());
        txtSalary.setText(String.valueOf(staff.getSalary()));
        dateHiring.setValue(staff.getHiringDate());

        // Γέμισμα των CheckBoxes από το κείμενο (π.χ. "Mon, Fri")
        String days = staff.getWorkDays();
        if (days != null) {
            if (days.contains("Mon")) cbMon.setSelected(true);
            if (days.contains("Tue")) cbTue.setSelected(true);
            if (days.contains("Wed")) cbWed.setSelected(true);
            if (days.contains("Thu")) cbThu.setSelected(true);
            if (days.contains("Fri")) cbFri.setSelected(true);
            if (days.contains("Sat")) cbSat.setSelected(true);
            if (days.contains("Sun")) cbSun.setSelected(true);
        }
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        try {
            // Συλλογή ημερών σε ένα String
            List<String> selectedDays = new ArrayList<>();
            if (cbMon.isSelected()) selectedDays.add("Mon");
            if (cbTue.isSelected()) selectedDays.add("Tue");
            if (cbWed.isSelected()) selectedDays.add("Wed");
            if (cbThu.isSelected()) selectedDays.add("Thu");
            if (cbFri.isSelected()) selectedDays.add("Fri");
            if (cbSat.isSelected()) selectedDays.add("Sat");
            if (cbSun.isSelected()) selectedDays.add("Sun");

            String workDaysString = String.join(", ", selectedDays);

            String name = txtName.getText();
            String role = comboRole.getValue(); // Παίρνουμε την τιμή από το ComboBox
            String phone = txtPhone.getText();

            // Έλεγχος για κενό μισθό
            double salary = 0.0;
            if (txtSalary.getText() != null && !txtSalary.getText().isEmpty()) {
                salary = Double.parseDouble(txtSalary.getText());
            }

            LocalDate hired = dateHiring.getValue();

            int id = (existingStaff == null) ? 0 : existingStaff.getStaffId();

            Staff staff = new Staff(id, name, role, phone, workDaysString, salary, hired);

            if (existingStaff == null) {
                staffDAO.addStaff(staff);
            } else {
                staffDAO.updateStaff(staff);
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