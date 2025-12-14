package com.dbtech.system.controllers;

import com.dbtech.system.dao.ClientDAO;
import com.dbtech.system.models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TextField;
import com.dbtech.system.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ClientInfoStatus;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, Void> colActions;
    @FXML private TextField searchField;

    private ClientDAO clientDAO;

    @FXML
    public void initialize(){
        clientDAO = new ClientDAO();

        // Σύνδεση στηλών με πεδία του Model
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        addActionsColumn();
        loadClients();
    }

    private void loadClients() {
        // 1. Φέρνουμε τα δεδομένα από τη βάση
        ObservableList<Client> masterData = FXCollections.observableArrayList(clientDAO.getAllClients());

        // 2. Τα βάζουμε σε μια FilteredList (αρχικά δείχνει τα πάντα)
        FilteredList<Client> filteredData = new FilteredList<>(masterData, p -> true);

        // 3. Προσθέτουμε ακροατή (Listener) στο πεδίο αναζήτησης
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                // Αν το πεδίο είναι άδειο, δείξε τους όλους
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Μετατροπή σε πεζά για να μην παίζει ρόλο αν γράφεις κεφαλαία/μικρά
                String lowerCaseFilter = newValue.toLowerCase();

                // Έλεγχος: Περιέχει το όνομα αυτό που γράψαμε;
                if (client.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Βρέθηκε στο όνομα
                } else if (client.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Βρέθηκε στο email
                } else if (client.getPhoneNumber().contains(lowerCaseFilter)) {
                    return true; // Βρέθηκε στο τηλέφωνο
                }
                return false; // Δεν βρέθηκε πουθενά
            });
        });

        // 4. Wrap σε SortedList για να δουλεύει και η ταξινόμηση (κλικ στις επικεφαλίδες)
        SortedList<Client> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(clientTable.comparatorProperty());

        // 5. Βάζουμε τα φιλτραρισμένα δεδομένα στον πίνακα
        clientTable.setItems(sortedData);
    }

    @FXML
    private void handleNewClient() {
        // Στέλνουμε null, άρα η φόρμα καταλαβαίνει ότι είναι ΝΕΟΣ πελάτης
        showClientDialog(null);
    }

    private void addActionsColumn() {
        Callback<TableColumn<Client, Void>, TableCell<Client, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Client, Void> call(final TableColumn<Client, Void> param) {
                return new TableCell<>() {
                    // Κρατάμε το Edit και βάζουμε Delete αντί για View
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");
                    private final HBox pane = new HBox(10, btnEdit, btnDelete);

                    {
                        // Styling: Edit (Μπλε), Delete (Κόκκινο)
                        btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                        btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                        // Edit Action
                        btnEdit.setOnAction(event -> {
                            Client client = getTableView().getItems().get(getIndex());
                            showClientDialog(client);
                        });

                        // Delete Action
                        btnDelete.setOnAction(event -> {
                            Client client = getTableView().getItems().get(getIndex());
                            // Διαγραφή από τη βάση
                            clientDAO.deleteClientSafe(client.getClientId());
                            // Ανανέωση πίνακα
                            loadClients();
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

    // Βοηθητική μέθοδος που ανοίγει το παράθυρο (Διάλογο)
    private void showClientDialog(Client client) {
        try {
            // 1. Φόρτωση του FXML
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("client_form.fxml"));
            Parent page = loader.load();

            // 2. Δημιουργία του παραθύρου (Stage)
            Stage dialogStage = new Stage();
            dialogStage.setTitle(client == null ? "New Client" : "Edit Client");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Μπλοκάρει το πίσω παράθυρο
            dialogStage.initOwner(clientTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. Σύνδεση με τον Controller της φόρμας
            ClientFormController controller = loader.getController();
            controller.setClientDAO(clientDAO); // Του δίνουμε τον DAO για να σώσει στη βάση

            if (client != null) {
                controller.setClient(client); // Αν είναι Edit, του δίνουμε τα στοιχεία
            }

            // 4. Εμφάνιση και Αναμονή (Περιμένουμε να κλείσει)
            dialogStage.showAndWait();

            // 5. Ανανέωση του πίνακα αν πατήθηκε το Save
            if (controller.isSaveClicked()) {
                loadClients(); // Ξαναφορτώνουμε τη λίστα από τη βάση
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα: Δεν βρέθηκε το αρχείο client_form.fxml");
        }
    }
}

