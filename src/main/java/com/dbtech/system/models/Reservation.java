package com.dbtech.system.models;

import java.time.LocalDate;

public class Reservation {
    private int reservationId;
    private int clientId;
    private int roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;
    private String status; //Active, Past, Cancelled

    // Extra fields for more data
    private String clientName;
    private String roomNumber;

    public Reservation(int reservationId, int clientId, int roomId, LocalDate checkIn, LocalDate checkOut, double totalPrice, String status, String clientName, String roomNumber) {
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
        this.clientName = clientName;
        this.roomNumber = roomNumber;
    }

    private Reservation(){}

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


}
