package com.dbtech.system.controllers;

import com.dbtech.system.dao.RoomDAO;
import com.dbtech.system.models.Room;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoomFormController {

    @FXML private TextField txtNumber;
    @FXML private TextField txtType;
    @FXML private TextField txtFloor;
    @FXML private TextField txtPrice;

    private RoomDAO roomDAO;
    private Room existingRoom;
    private boolean saveClicked = false;

    public void setRoomDAO(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public void setRoom(Room room) {
        this.existingRoom = room;
        txtNumber.setText(room.getRoomNumber());
        txtType.setText(room.getRoomType());
        txtFloor.setText(String.valueOf(room.getFloor()));
        txtPrice.setText(String.valueOf(room.getPricePerNight()));
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        try {
            // Μετατροπή των κειμένων σε αριθμούς
            int floor = Integer.parseInt(txtFloor.getText());
            double price = Double.parseDouble(txtPrice.getText());
            String number = txtNumber.getText();
            String type = txtType.getText();

            if (existingRoom == null) {
                // ΝΕΟ ΔΩΜΑΤΙΟ
                Room newRoom = new Room(number, type, floor, 1, price); // Βάζω occupancy 1 προσωρινά
                roomDAO.insertRoom(newRoom);
            } else {
                // EDIT
                existingRoom.setRoomNumber(number);
                existingRoom.setRoomType(type);
                existingRoom.setFloor(floor);
                existingRoom.setPricePerNight(price);
                roomDAO.updateRoom(existingRoom);
            }
            saveClicked = true;
            closeStage();

        } catch (NumberFormatException e) {
            System.err.println("❌ Λάθος τύπος δεδομένων! Το Floor πρέπει να είναι ακέραιος και το Price αριθμός.");
        }
    }

    @FXML private void handleCancel() { closeStage(); }

    private void closeStage() {
        ((Stage) txtNumber.getScene().getWindow()).close();
    }
}