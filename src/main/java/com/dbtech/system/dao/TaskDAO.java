package com.dbtech.system.dao;

import com.dbtech.system.db.DBUtil;
import com.dbtech.system.models.Task;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY is_completed ASC, due_date ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("due_date");
                LocalDate date = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                list.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("description"),
                        date,
                        rs.getBoolean("is_completed"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addTask(Task t) {
        String sql = "INSERT INTO tasks (description, due_date, type, is_completed) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getDescription());
            pstmt.setDate(2, Date.valueOf(t.getDate()));
            pstmt.setString(3, t.getType());
            pstmt.setBoolean(4, t.isCompleted());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task t) {
        String sql = "UPDATE tasks SET description=?, due_date=?, type=?, is_completed=? WHERE task_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getDescription());
            pstmt.setDate(2, Date.valueOf(t.getDate()));
            pstmt.setString(3, t.getType());
            pstmt.setBoolean(4, t.isCompleted());
            pstmt.setInt(5, t.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE task_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}