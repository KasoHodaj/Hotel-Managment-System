package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Μέθοδος για εισαγωγή νέου δωματίου
    public void insertRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, floor, occupancy, price_per_night) VALUES (?, ?, ?, ?, ?)";

        // Χρησιμοποιούμε try-with-resources για να κλείνει η σύνδεση αυτόματα
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Γεμίζουμε τα κενά (?) με τα δεδομένα από το αντικείμενο room
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getFloor());
            pstmt.setInt(4, room.getOccupancy());
            pstmt.setDouble(5, room.getPricePerNight());

            // Εκτέλεση της εντολής
            pstmt.executeUpdate();
            System.out.println("✅ Το δωμάτιο " + room.getRoomNumber() + " αποθηκεύτηκε επιτυχώς!");


        } catch (SQLException e) {
            System.out.println("❌ Σφάλμα κατά την εισαγωγή δωματίου:");
            e.printStackTrace();
        }
    }


    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, floor = ?, price_per_night = ? WHERE room_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getFloor());
            pstmt.setDouble(4, room.getPricePerNight());
            pstmt.setInt(5, room.getRoomId()); // Το ID για το WHERE

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Μέθοδος διαγραφής δωματίου με βάση τον αριθμό του (π.χ. "101")
    public void deleteRoom(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);

            int rowsAffected = pstmt.executeUpdate(); // Επιστρέφει πόσες γραμμές σβήστηκαν

            if (rowsAffected > 0) {
                System.out.println("✅ Το δωμάτιο " + roomNumber + " διαγράφηκε επιτυχώς!");
            } else {
                System.out.println("⚠️ Δεν βρέθηκε δωμάτιο με αριθμό " + roomNumber);
            }

        } catch (SQLException e) {
            System.err.println("❌ Σφάλμα κατά τη διαγραφή:");
            e.printStackTrace();
        }
    }


    // Μέθοδος για ανάκτηση ΟΛΩΝ των δωματίων
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC"; // Τα φέρνουμε ταξινομημένα

        try( Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        )
        {
            // Όσο υπάρχουν γραμμές στα αποτελέσματα...
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getInt("floor"),
                        rs.getInt("occupancy"),
                        rs.getDouble("price_per_night")
                );

                // Το προσθέτουμε στη λίστα
                rooms.add(room);
            }

        }catch(SQLException e){
            System.out.println("❌ Σφάλμα κατά την ανάκτηση δωματίων:");
            e.printStackTrace();
        }
        return rooms;
    }

}
