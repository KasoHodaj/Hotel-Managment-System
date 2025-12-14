package com.dbtech.system;

import com.dbtech.system.dao.RoomDAO;
import com.dbtech.system.models.Room;

import java.util.List;

public class TestRoomDAO {
    public static void main(String[] args){

    RoomDAO roomDAO = new RoomDAO();

        // 1. Βάζουμε μερικά δεδομένα
        System.out.println("--- Εισαγωγή Δεδομένων ---");
        roomDAO.insertRoom(new Room("101", "Single", 1,1, 50.0));
        roomDAO.insertRoom(new Room("102", "Double", 1,2,75.0));
        roomDAO.insertRoom(new Room("201", "suite", 2, 4,120.0));

        // 2. Ανάκτηση και Εμφάνιση
        System.out.println("\n--- Λίστα Δωματίων από τη Βάση ---");
        List<Room> allRooms = roomDAO.getAllRooms();

        for (Room r : allRooms){
            System.out.println(r); // Εδώ καλείται αυτόματα η toString() που φτιάξαμε στο μοντέλο
        }

    }
}
