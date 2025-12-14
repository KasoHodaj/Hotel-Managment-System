package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Order;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        // JOIN για να φέρουμε και το όνομα του προμηθευτή
        String sql = """
            SELECT o.*, s.supplier_name 
            FROM orders o
            JOIN suppliers s ON o.supplier_id = s.supplier_id
            ORDER BY o.placement_date DESC
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("order_number"),
                        rs.getInt("supplier_id"),
                        rs.getInt("quantity"),
                        rs.getString("order_type"),
                        rs.getDate("placement_date").toLocalDate(),
                        rs.getString("supplier_name") // Από το JOIN
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addOrder(Order o) {
        // Στέλνουμε ΜΟΝΟ τα 3 στοιχεία που δίνει ο χρήστης.
        // Τα order_id, order_number και date τα βάζει η βάση μόνη της!
        String sql = "INSERT INTO orders (supplier_id, quantity, order_type) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, o.getSupplierId());
            pstmt.setInt(2, o.getQuantity());
            pstmt.setString(3, o.getType());
            // Δεν στέλνουμε date, θα μπει το σημερινό αυτόματα

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order o) {
        String sql = "UPDATE orders SET order_number=?, supplier_id=?, quantity=?, order_type=?, placement_date=? WHERE order_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, o.getOrderNumber());
            pstmt.setInt(2, o.getSupplierId());
            pstmt.setInt(3, o.getQuantity());
            pstmt.setString(4, o.getType());
            pstmt.setDate(5, Date.valueOf(o.getDate()));
            pstmt.setInt(6, o.getOrderId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE order_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}