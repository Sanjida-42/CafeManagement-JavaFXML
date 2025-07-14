/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cafe.shop.management;
import java.sql.*;

public class DBUtil {
    private static final String DB_NAME = "cafe-shop";
    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://localhost/" + DB_NAME + "?useSSL=false";
    private static final String USER = "root"; // Change if needed
    private static final String PASS = ""; 
    
      // Create database if it doesn't exist
    
     public static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     
     
      // Create users table if it doesn't exist
    public static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100)," +
                "email VARCHAR(100) UNIQUE," +
                "password VARCHAR(100))";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialize database and tables
    public static void initialize() {
        createDatabaseIfNotExists();
        createTableIfNotExists();
    }

    // Get connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_WITH_DB, USER, PASS);
    }
}
    
    
