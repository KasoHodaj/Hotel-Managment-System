package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
       // JOIN Query: Return the reservation, full name of client and room number.
       String sql = """
            SELECT r.*, c.full_name, rm.room_number 
            FROM reservations r
            JOIN clients c ON r.client_id = c.client_id
            JOIN rooms rm ON r.room_id = rm.room_id
            ORDER BY r.check_in_date DESC
        """;

       try(Connection conn = DBUtil.getConnection();
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(sql)) {

           while(rs.next()){
               // Convert SQL Date -> Java LocalDate
               LocalDate inDate = rs.getDate("check_in_date").toLocalDate();
               LocalDate outDate = rs.getDate("check_out_date").toLocalDate();

               list.add(new Reservation(
                   rs.getInt("reservation_id"),
                   rs.getInt("client_id"),
                   rs.getInt("room_id"),
                   inDate,
                   outDate,
                   rs.getDouble("total_price"),
                   rs.getString("status"),
                   rs.getString("full_name"), // From table clients
                   rs.getString("room_number") // From table rooms
               ));
           }
       }catch(SQLException e){
           e.printStackTrace();
       }
       return list;
    }

    // Prosthiki kratisis


    public void addReservation(Reservation res) {
        String sql = "INSERT INTO reservations (client_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?,?,?,?,?,?)";
        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, res.getClientId());
            pstmt.setInt(2, res.getRoomId());
            pstmt.setDate(3, Date.valueOf(res.getCheckIn()));
            pstmt.setDate(4, Date.valueOf(res.getCheckOut()));
            pstmt.setDouble(5, res.getTotalPrice());
            pstmt.setString(6, res.getStatus());

            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    // UPDATE: Updates an existing reservation
    public void updateReservation(Reservation res) {
        String sql = "UPDATE reservations SET client_id=?, room_id=?, check_in_date=?, check_out_date=?, total_price=?, status=? WHERE reservation_id=?";

        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, res.getClientId());
            pstmt.setInt(2, res.getRoomId());
            pstmt.setDate(3, Date.valueOf(res.getCheckIn()));
            pstmt.setDate(4, Date.valueOf(res.getCheckOut()));
            pstmt.setDouble(5, res.getTotalPrice());
            pstmt.setString(6, res.getStatus());
            pstmt.setInt(7, res.getReservationId()); // Important: WHERE reservation_id = ...

            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    // DELETE: Deletes a reservation by ID
    public void deleteReservation(int reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1,reservationId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
