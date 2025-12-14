package com.dbtech.system.controllers;

import com.dbtech.system.HelloApplication;
import com.dbtech.system.dao.OrderDAO;
import com.dbtech.system.models.Order;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class OrderController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colNumber;
    @FXML private TableColumn<Order, String> colType;
    @FXML private TableColumn<Order, String> colSupplier;
    @FXML private TableColumn<Order, Integer> colQuantity;
    @FXML private TableColumn<Order, LocalDate> colDate;
    @FXML private TableColumn<Order, Void> colActions;
    @FXML private TextField searchField;

    private OrderDAO orderDAO;

    @FXML
    public void initialize() {
        orderDAO = new OrderDAO();

        colNumber.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrderNumber()));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        colSupplier.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSupplierName()));
        colQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDate()));

        addActionsColumn();
        loadOrders();
    }

    private void loadOrders() {
        ObservableList<Order> masterData = FXCollections.observableArrayList(orderDAO.getAllOrders());
        FilteredList<Order> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(order -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return order.getOrderNumber().toLowerCase().contains(lower)
                        || order.getSupplierName().toLowerCase().contains(lower)
                        || order.getType().toLowerCase().contains(lower);
            });
        });

        SortedList<Order> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(orderTable.comparatorProperty());
        orderTable.setItems(sortedData);
    }

    private void addActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-text-fill: green; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

                btnEdit.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDialog(order);
                });

                btnDelete.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    orderDAO.deleteOrder(order.getOrderId());
                    loadOrders();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @FXML
    private void handleNewOrder() {
        showOrderDialog(null);
    }

    private void showOrderDialog(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("order_form.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(order == null ? "New Order" : "Edit Order");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(orderTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            OrderFormController controller = loader.getController();
            controller.setOrderDAO(orderDAO);
            if (order != null) controller.setOrder(order);
            else controller.setOrder(null);
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) loadOrders();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}