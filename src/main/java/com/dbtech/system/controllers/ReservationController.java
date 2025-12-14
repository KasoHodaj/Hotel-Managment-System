package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.ReservationDAO;
import com.dbtech.system.models.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
public class ReservationController {
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> colGuest;
    @FXML private TableColumn<Reservation, String> colRoom;
    @FXML private TableColumn<Reservation, LocalDate> colCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, Double> colPrice;
    @FXML private TableColumn<Reservation, Void> colActions;

    private ReservationDAO reservationDAO;

    @FXML
    public void initialize() {
        reservationDAO = new ReservationDAO();

        // --- ΔΙΟΡΘΩΣΗ: Χρήση Lambdas αντί για PropertyValueFactory ---
        // Έτσι το JavaFX βρίσκει σίγουρα τα δεδομένα και δεν "σκάει"
        colGuest.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClientName()));
        colRoom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoomNumber()));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Για τις ημερομηνίες
        colCheckIn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCheckIn()));
        colCheckOut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCheckOut()));

        // Για την τιμή
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));

        addActionsColumn();
        loadReservations();
    }

    private void loadReservations() {
        ObservableList<Reservation> list = FXCollections.observableArrayList(reservationDAO.getAllReservations());
        reservationTable.setItems(list);
    }

    @FXML
    private void handleNewReservation() {
        System.out.println("New Reservation Clicked!");
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("reservation_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            // Connection with controller
            ReservationFormController controller = loader.getController();
            controller.setReservationDAO(reservationDAO);

            dialogStage.showAndWait();


            // Refresh table if saved!
            if (controller.isSaveClicked()) {
                loadReservations();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private void addActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>(){
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                // Styling
                btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                // --- EDIT ACTION ---
                btnEdit.setOnAction(event -> {
                    Reservation res = getTableView().getItems().get(getIndex());
                    showReservationDialog(res); // Open form with data
                });

                // --- DELETE ACTION ---
                btnDelete.setOnAction(event -> {
                    Reservation res = getTableView().getItems().get(getIndex());
                    // Simple Delete (You can add an Alert confirmation here later)
                    reservationDAO.deleteReservation(res.getReservationId());
                    loadReservations(); // Refresh table
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // Helper method to open the dialog
    private void showReservationDialog(Reservation reservation) {
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("reservation_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(reservation == null ? "New Reservation" : "Edit Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ReservationFormController controller = loader.getController();
            controller.setReservationDAO(reservationDAO);

            // Pass the data if we are editing
            if (reservation != null) {
                controller.setReservation(reservation);
            }

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadReservations();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
