package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    // 1. Μετράει συνολικά δωμάτια
    public int getTotalRooms() {
        return getCount("SELECT COUNT(*) FROM rooms");
    }

    // 2. Μετράει ενεργούς πελάτες
    public int getTotalClients() {
        return getCount("SELECT COUNT(*) FROM clients");
    }

    // 3. Μετράει ενεργές κρατήσεις
    public int getActiveReservations() {
        return getCount("SELECT COUNT(*) FROM reservations WHERE status = 'Active' OR status = 'Confirmed'");
    }

    // 4. Υπολογίζει πληρότητα (Occupancy %)
    public double getOccupancyRate() {
        int total = getTotalRooms();
        if (total == 0) return 0.0;

        // Θεωρούμε "κατειλημμένα" τα δωμάτια που έχουν Active κράτηση σήμερα
        int occupied = getCount("SELECT COUNT(*) FROM reservations WHERE status = 'Active'");
        return ((double) occupied / total) * 100.0;
    }

    // Βοηθητική μέθοδος για να μην γράφουμε πολλά try-catch
    private int getCount(String sql) {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 5. Φέρνει τα 5 τελευταία logs
    public List<String> getRecentActivities() {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT description, log_timestamp FROM app_logs ORDER BY log_timestamp DESC LIMIT 5";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String desc = rs.getString("description");
                Timestamp time = rs.getTimestamp("log_timestamp");
                // Μορφή: "New entry added... (2025-12-06 10:30)"
                logs.add("• " + desc + " (" + time.toString().substring(0, 16) + ")");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }
}