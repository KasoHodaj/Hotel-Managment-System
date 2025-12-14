package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ClientDAO {

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY client_id ASC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                clients.add(new Client(
                    rs.getInt("client_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone_number")
                ));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return clients;
    }

    // Προσθήκη νέου πελάτη
    public void addClient(Client client) {
        String sql = "INSERT INTO clients (full_name, email, phone_number) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getFullName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getPhoneNumber());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ενημέρωση υπάρχοντος πελάτη
    public void updateClient(Client client) {
        String sql = "UPDATE clients SET full_name = ?, email = ?, phone_number = ? WHERE client_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getFullName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getPhoneNumber());
            pstmt.setInt(4, client.getClientId()); // Χρειαζόμαστε το ID για να βρούμε ποιον θα αλλάξουμε

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Μέθοδος διαγραφής πελάτη με ΣΥΝΑΛΛΑΓΗ (Transaction)
    public void deleteClientSafe(int clientId) {
        String deleteReservationsSQL = "DELETE FROM reservations WHERE client_id = ?";
        String deleteClientSQL = "DELETE FROM clients WHERE client_id = ?";

        Connection conn = null;

        try {
            conn = DBUtil.getConnection();

            // 1. Απενεργοποίηση αυτόματης αποθήκευσης (Start Transaction)
            conn.setAutoCommit(false);

            // 2. Διαγραφή Κρατήσεων Πελάτη (Πρώτα αυτό, λόγω Foreign Key)
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteReservationsSQL)) {
                pstmt1.setInt(1, clientId);
                pstmt1.executeUpdate();
            }

            // 3. Διαγραφή του Πελάτη
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteClientSQL)) {
                pstmt2.setInt(1, clientId);
                pstmt2.executeUpdate();
            }

            // 4. Αν όλα πήγαν καλά -> ΟΡΙΣΤΙΚΟΠΟΙΗΣΗ
            conn.commit();
            System.out.println("Transaction Successful: Client and reservations deleted.");

        } catch (SQLException e) {
            // 5. Αν έγινε λάθος -> ΑΚΥΡΩΣΗ ΟΛΩΝ (ROLLBACK)
            if (conn != null) {
                try {
                    System.err.println("Transaction Failed. Rolling back...");
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Επαναφορά του AutoCommit και κλείσιμο
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
