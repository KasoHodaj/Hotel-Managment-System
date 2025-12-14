package com.dbtech.system.models;

public class Room {
    // Τα πεδία της κλάσης (Fields)
    private int roomId;
    private String roomNumber;
    private String roomType;
    private int floor;
    private int occupancy;
    private double pricePerNight;

    // Empty Constructor) - Χρήσιμος για βιβλιοθήκες
    public Room(){}

    // Full Constructor
    public Room(int roomId, String roomNumber, String roomType, int floor, int occupancy, double pricePerNight){
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.occupancy = occupancy;
        this.pricePerNight = pricePerNight;
    }

    // Κατασκευαστής για ΝΕΑ δωμάτια (χωρίς ID)
    // Όταν φτιάχνουμε ένα δωμάτιο για να το σώσουμε πρώτη φορά, δεν ξέρουμε ακόμα το ID του.
    public Room(String roomNumber, String roomType, int floor, int occupancy, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.occupancy = occupancy;
        this.pricePerNight = pricePerNight;
    }

    // Getters και Setters (Για να έχουμε πρόσβαση στα δεδομένα)
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getOccupancy() { return occupancy; }
    public void setOccupancy(int occupancy) { this.occupancy = occupancy; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    @Override
    public String toString(){
        return "Δωμάτιο " + roomNumber + " (" + roomType + ")";
    }
}
