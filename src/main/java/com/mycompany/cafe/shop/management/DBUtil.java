package com.mycompany.cafe.shop.management;

import java.sql.*;

public class DBUtil {
    private static final String DB_NAME = "cafe-shop";
    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://localhost/"+DB_NAME+"?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void createTablesIfNotExists() {
        String usersTable =
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(100)," +
            "email VARCHAR(100) UNIQUE," +
            "password VARCHAR(100)" +
            ");";
   String productsTable =
    "CREATE TABLE IF NOT EXISTS products (" +
    "id INT AUTO_INCREMENT PRIMARY KEY," +
    "name VARCHAR(100)," +
    "type VARCHAR(50)," +
    "stock INT," +
    "price DOUBLE," +
    "status VARCHAR(30)," +
    "imagePath VARCHAR(255)" +
    ");";

    String ordersTable =
        "CREATE TABLE IF NOT EXISTS orders (" +
        "id INT AUTO_INCREMENT PRIMARY KEY," +
        "user VARCHAR(100)," +                    // int if linking to users.id
        "ordered_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
        "total DOUBLE)";
    String orderItemsTable =
        "CREATE TABLE IF NOT EXISTS order_items (" +
        "id INT AUTO_INCREMENT PRIMARY KEY," +
        "order_id INT," +
        "product_id INT," +
        "product_name VARCHAR(100)," +
        "quantity INT," +
        "price DOUBLE," +
        "FOREIGN KEY (order_id) REFERENCES orders(id))";
    
    String couponsTable =
    "CREATE TABLE IF NOT EXISTS coupons (" +
    "id INT AUTO_INCREMENT PRIMARY KEY," +
    "code VARCHAR(50) UNIQUE," +    // generate Unique coupon code, like:, "CAFE10-XYZ123"
    "user_id INT," +             
    "order_id INT," +            
    "discount_percent INT," +    
    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
    "expires_at DATETIME," +     
    "used BOOLEAN DEFAULT FALSE," +
    "FOREIGN KEY (user_id) REFERENCES users(id)," +
    "FOREIGN KEY (order_id) REFERENCES orders(id)" +
    ");";
    
    
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(usersTable);
            stmt.executeUpdate(productsTable);
              stmt.executeUpdate(ordersTable);
        stmt.executeUpdate(orderItemsTable);
        stmt.executeUpdate(couponsTable);
            ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM products LIKE 'imagePath'");
            if (!rs.next()) {
                stmt.executeUpdate("ALTER TABLE products ADD COLUMN imagePath VARCHAR(255)");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void initialize() {
        createDatabaseIfNotExists();
        createTablesIfNotExists();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_WITH_DB, USER, PASS);
    }
}
