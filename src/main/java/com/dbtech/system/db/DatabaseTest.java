package com.dbtech.system.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
        public static void main(String[] args){
            try{
                // Προσπάθεια σύνδεσης χρησιμοποιώντας την κλάση που φτιάξαμε
                Connection conn = DBUtil.getConnection();

                if(conn != null){
                    System.out.println("✅ Successful connection with database");
                    conn.close(); // // Πάντα κλείνουμε τη σύνδεση όταν τελειώσουμε
                }

            }catch(SQLException e){
                System.out.println("❌ Fail to connect with database");
                e.printStackTrace(); // This line will show us the log errors
            }
        }
}
