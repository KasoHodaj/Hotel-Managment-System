package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.RoomDAO;
import com.dbtech.system.models.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class RoomController {

    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> colRoomNumber;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, Integer> colFloor;
    @FXML private TableColumn<Room, Double> colPrice;
    @FXML private TableColumn<Room, Void> colActions;
    @FXML private TextField searchField;

    private RoomDAO roomDAO;

    @FXML
    public void initialize() {
        roomDAO = new RoomDAO();

        // Σύνδεση στηλών
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        // Προσθήκη κουμπιών (Edit & Delete)
        addActionsColumn();

        // Φόρτωση δεδομένων
        loadRooms();
    }

    private void loadRooms() {
        ObservableList<Room> masterData = FXCollections.observableArrayList(roomDAO.getAllRooms());
        FilteredList<Room> filteredData = new FilteredList<>(masterData, p -> true);

        // Λογική Αναζήτησης
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(room -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerFilter = newValue.toLowerCase();

                if (room.getRoomNumber().toLowerCase().contains(lowerFilter)) return true;
                if (room.getRoomType().toLowerCase().contains(lowerFilter)) return true;
                return String.valueOf(room.getFloor()).contains(lowerFilter);
            });
        });

        SortedList<Room> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(roomTable.comparatorProperty());
        roomTable.setItems(sortedData);
    }

    private void addActionsColumn() {
        Callback<TableColumn<Room, Void>, TableCell<Room, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Room, Void> call(final TableColumn<Room, Void> param) {
                return new TableCell<>() {
                    // Δημιουργία των κουμπιών
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");
                    private final HBox pane = new HBox(10, btnEdit, btnDelete);

                    {
                        // Στυλ
                        btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                        btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                        // Ενέργεια EDIT
                        btnEdit.setOnAction(event -> {
                            Room room = getTableView().getItems().get(getIndex());
                            showRoomDialog(room);
                        });

                        // Ενέργεια DELETE
                        btnDelete.setOnAction(event -> {
                            Room room = getTableView().getItems().get(getIndex());
                            // Καλούμε τη μέθοδο διαγραφής με βάση τον Αριθμό Δωματίου
                            roomDAO.deleteRoom(room.getRoomNumber());
                            // Ανανεώνουμε τον πίνακα
                            loadRooms();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);
    }

    @FXML
    private void handleNewRoom() {
        showRoomDialog(null);
    }

    private void showRoomDialog(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("room_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(room == null ? "New Room" : "Edit Room");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(roomTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            RoomFormController controller = loader.getController();
            controller.setRoomDAO(roomDAO);
            if (room != null) controller.setRoom(room);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadRooms();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}