package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Staff;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class StaffDAO {

    public List<Staff> getAllStaff(){
        List<Staff>  list = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY staff_id ASC";

        try(Connection conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql))
        {

            while (rs.next()) {
                // Προσοχή στα null dates
                Date sqlDate = rs.getDate("hiring_date");
                LocalDate hiringDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                list.add(new Staff(
                        rs.getInt("staff_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("phone_number"),
                        rs.getString("work_days"),
                        rs.getDouble("salary"),
                        hiringDate
                ));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public void addStaff(Staff s) {
        String sql = "INSERT INTO staff (full_name, role, phone_number, work_days, salary, hiring_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getFullName());
            pstmt.setString(2, s.getRole());
            pstmt.setString(3, s.getPhoneNumber());
            pstmt.setString(4, s.getWorkDays());
            pstmt.setDouble(5, s.getSalary());
            pstmt.setDate(6, s.getHiringDate() != null ? Date.valueOf(s.getHiringDate()) : null);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStaff(Staff s) {
        String sql = "UPDATE staff SET full_name=?, role=?, phone_number=?, work_days=?, salary=?, hiring_date=? WHERE staff_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getFullName());
            pstmt.setString(2, s.getRole());
            pstmt.setString(3, s.getPhoneNumber());
            pstmt.setString(4, s.getWorkDays());
            pstmt.setDouble(5, s.getSalary());
            pstmt.setDate(6, s.getHiringDate() != null ? Date.valueOf(s.getHiringDate()) : null);
            pstmt.setInt(7, s.getStaffId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteStaff(int id) {
        String sql = "DELETE FROM staff WHERE staff_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
