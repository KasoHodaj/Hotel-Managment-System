package com.dbtech.system.models;

import java.time.LocalDate;

public class Order {
    private int orderId;
    private String orderNumber;
    private int supplierId;
    private int quantity;
    private String type;
    private LocalDate date;

    // Extra πεδίο για εμφάνιση στον πίνακα
    private String supplierName;

    public Order(int orderId, String orderNumber, int supplierId, int quantity, String type, LocalDate date, String supplierName) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.type = type;
        this.date = date;
        this.supplierName = supplierName;
    }

    // Getters & Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}