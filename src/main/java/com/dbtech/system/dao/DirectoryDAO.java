package com.dbtech.system.dao;

import com.dbtech.system.models.PhoneContact;
import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.PhoneContact;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectoryDAO {
    public List<PhoneContact> getAllContacts(){
        List<PhoneContact> list = new ArrayList<>();
        String sql = "SELECT * FROM phone_catalogue ORDER BY name ASC";

        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

                while(rs.next()){
                    list.add(new PhoneContact(
                        rs.getInt("phone_id"),
                                rs.getString("name"),
                                rs.getString("phone_number"),
                                rs.getString("address")
                    ));
                }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }



    public void addContact(PhoneContact c){
        String sql = "INSERT INTO phone_catalogue (name, phone_number, address) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getPhone());
            pstmt.setString(3, c.getLocation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateContact(PhoneContact c) {
        String sql = "UPDATE phone_catalogue SET name=?, phone_number=?, address=? WHERE phone_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getPhone());
            pstmt.setString(3, c.getLocation());
            pstmt.setInt(4, c.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteContact(int id) {
        String sql = "DELETE FROM phone_catalogue WHERE phone_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
