package com.dbtech.system.controllers;

import com.dbtech.system.dao.ClientDAO;
import com.dbtech.system.dao.ReservationDAO;
import com.dbtech.system.dao.RoomDAO;
import com.dbtech.system.models.Client;
import com.dbtech.system.models.Reservation;
import com.dbtech.system.models.Room;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ReservationFormController {
    @FXML private ComboBox<Client> comboClient;
    @FXML private ComboBox<Room> comboRoom;
    @FXML private DatePicker dateCheckIn;
    @FXML private DatePicker dateCheckOut;
    @FXML private ComboBox<String> comboStatus;
    @FXML private TextField txtTotal;
    private Reservation existingReservation; // If null -> New, else -> Edit

    private ReservationDAO reservationDAO;
    private boolean saveClicked = false;

    // We will need the other DAO to populate the lists
    private ClientDAO clientDAO  = new ClientDAO();
    private RoomDAO roomDAO = new RoomDAO();

    @FXML
    public void initialize(){
        // 1. Fill of the ComboBoxes
        comboClient.setItems(FXCollections.observableArrayList(clientDAO.getAllClients()));
        comboRoom.setItems(FXCollections.observableArrayList(roomDAO.getAllRooms()));
        comboStatus.setItems(FXCollections.observableArrayList("Active", "Confirmed", "Pending", "Cancelled", "Completed"));
        comboStatus.getSelectionModel().select("Confirmed");

        // 2. Setting the view so its shows names not objects.
        setupConverters();

        // 3. Auto calculation of price when dates or rooms change.
        dateCheckOut.valueProperty().addListener((obs, oldDate, newDate) -> calculatePrice());
        dateCheckIn.valueProperty().addListener((obs, oldDate, newDate) -> calculatePrice());
        comboRoom.valueProperty().addListener((obs, oldRoom, newRoom) -> calculatePrice());
    }

    private void setupConverters() {
        // Show client's name.
        comboClient.setConverter(new StringConverter<>() {
            @Override
            public String toString(Client client){
                return client == null ? "" : client.getFullName();
            }
            @Override
            public Client fromString(String string) { return null; }
        });

        // Show room number and type.
        comboRoom.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room == null ? "" : room.getRoomNumber() + " (" + room.getRoomType() + ") - " + room.getPricePerNight() + "€";
            }
            @Override
            public Room fromString(String string) { return null; }
        });
    }

    private void calculatePrice() {
        // Έλεγχος Ασφαλείας: Υπολογίζουμε ΜΟΝΟ αν έχουν συμπληρωθεί ΟΛΑ τα απαραίτητα
        if (dateCheckIn.getValue() != null &&
                dateCheckOut.getValue() != null &&
                comboRoom.getValue() != null) {

            try {
                long days = ChronoUnit.DAYS.between(dateCheckIn.getValue(), dateCheckOut.getValue());

                if (days > 0) {
                    double pricePerNight = comboRoom.getValue().getPricePerNight();
                    double total = days * pricePerNight;
                    // Χρησιμοποιούμε το Locale.US για να βάλει τελεία (.) και όχι κόμμα (,)
                    txtTotal.setText(String.format(java.util.Locale.US, "%.2f", total));
                } else {
                    txtTotal.setText("0.00");
                }
            } catch (Exception e) {
                txtTotal.setText("0.00");
            }
        } else {
            // Αν λείπει κάτι, καθαρίζουμε την τιμή ή βάζουμε 0
            txtTotal.setText("0.00");
        }
    }

    public void setReservationDAO(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        if (comboClient.getValue() == null || comboRoom.getValue() == null || dateCheckIn.getValue() == null) {
            System.out.println("❌ Please fill all fields!");
            return;
        }

        try {
            // Διόρθωση για την τιμή:
            String priceText = txtTotal.getText();
            double finalPrice = 0.0;

            if (priceText != null && !priceText.isEmpty()) {
                // Αντικαθιστούμε το κόμμα με τελεία για σιγουριά
                finalPrice = Double.parseDouble(priceText.replace(",", "."));
            }

            int id = (existingReservation == null) ? 0 : existingReservation.getReservationId();

            Reservation res = new Reservation(
                    id,
                    comboClient.getValue().getClientId(),
                    comboRoom.getValue().getRoomId(),
                    dateCheckIn.getValue(),
                    dateCheckOut.getValue(),
                    finalPrice,
                    comboStatus.getValue(),
                    comboClient.getValue().getFullName(), // Extra info
                    comboRoom.getValue().getRoomNumber()  // Extra info
            );

            if (existingReservation == null) {
                reservationDAO.addReservation(res);
            } else {
                reservationDAO.updateReservation(res);
            }

            saveClicked = true;
            ((Stage) txtTotal.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(){
        ((Stage) txtTotal.getScene().getWindow()).close();
    }



    public void setReservation(Reservation res){
        this.existingReservation = res;

        // 1. Pre-select Client
        for(Client c: comboClient.getItems()) {
            if(c.getClientId() == res.getClientId()) {
                comboClient.setValue(c);
                break;
            }
        }

        // 2. Pre-select Room
        for(Room r: comboRoom.getItems()) {
            if (r.getRoomId() == res.getRoomId()) {
                comboRoom.setValue(r);
                break;
            }
        }

        // 3. Set Dates & Status
        dateCheckIn.setValue(res.getCheckIn());
        dateCheckOut.setValue(res.getCheckOut());
        comboStatus.setValue(res.getStatus());
        txtTotal.setText(String.valueOf(res.getTotalPrice()));
    }




}
