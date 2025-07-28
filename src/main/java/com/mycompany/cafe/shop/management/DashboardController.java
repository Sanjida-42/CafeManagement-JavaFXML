package com.mycompany.cafe.shop.management;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label productsCount, ordersCount, revenueLabel, couponsUsedCount, totalSavingsLabel, totalSellingLabel;

    // Recent Orders Table
    @FXML private TableView<OrderSummary> ordersTable;
    @FXML private TableColumn<OrderSummary, Integer> orderIdCol;
    @FXML private TableColumn<OrderSummary, String> orderUserCol, orderDateCol, orderStatusCol;
    @FXML private TableColumn<OrderSummary, Double> orderTotalCol;

    // Coupon Orders Table
    @FXML private TableView<CouponOrderSummary> couponOrdersTable;
    @FXML private TableColumn<CouponOrderSummary, Integer> couponOrderIdCol, discountPercentCol;
    @FXML private TableColumn<CouponOrderSummary, String> couponUserCol, couponCodeCol, couponOrderDateCol;
    @FXML private TableColumn<CouponOrderSummary, Double> originalTotalCol, finalTotalCol, savingsCol;

    // Low Stock Table
    @FXML private TableView<LowStockProduct> lowStockTable;
    @FXML private TableColumn<LowStockProduct, String> lsProductCol;
    @FXML private TableColumn<LowStockProduct, Integer> lsStockCol;

    public static class OrderSummary {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty user = new SimpleStringProperty();
        private final StringProperty date = new SimpleStringProperty();
        private final DoubleProperty total = new SimpleDoubleProperty();
        private final StringProperty couponStatus = new SimpleStringProperty();

        public OrderSummary(int id, String user, String date, double total, String couponStatus) {
            this.id.set(id);
            this.user.set(user);
            this.date.set(date);
            this.total.set(total);
            this.couponStatus.set(couponStatus);
        }
        public IntegerProperty idProperty() { return id; }
        public StringProperty userProperty() { return user; }
        public StringProperty dateProperty() { return date; }
        public DoubleProperty totalProperty() { return total; }
        public StringProperty couponStatusProperty() { return couponStatus; }
    }

    public static class CouponOrderSummary {
        private final IntegerProperty orderId = new SimpleIntegerProperty();
        private final StringProperty user = new SimpleStringProperty();
        private final StringProperty couponCode = new SimpleStringProperty();
        private final IntegerProperty discountPercent = new SimpleIntegerProperty();
        private final DoubleProperty originalTotal = new SimpleDoubleProperty();
        private final DoubleProperty finalTotal = new SimpleDoubleProperty();
        private final DoubleProperty savings = new SimpleDoubleProperty();
        private final StringProperty orderDate = new SimpleStringProperty();

        public CouponOrderSummary(int orderId, String user, String couponCode, int discountPercent, 
                                  double originalTotal, double finalTotal, double savings, String orderDate) {
            this.orderId.set(orderId);
            this.user.set(user);
            this.couponCode.set(couponCode);
            this.discountPercent.set(discountPercent);
            this.originalTotal.set(originalTotal);
            this.finalTotal.set(finalTotal);
            this.savings.set(savings);
            this.orderDate.set(orderDate);
        }
        public IntegerProperty orderIdProperty() { return orderId; }
        public StringProperty userProperty() { return user; }
        public StringProperty couponCodeProperty() { return couponCode; }
        public IntegerProperty discountPercentProperty() { return discountPercent; }
        public DoubleProperty originalTotalProperty() { return originalTotal; }
        public DoubleProperty finalTotalProperty() { return finalTotal; }
        public DoubleProperty savingsProperty() { return savings; }
        public StringProperty orderDateProperty() { return orderDate; }
    }

    public static class LowStockProduct {
        private final StringProperty name = new SimpleStringProperty();
        private final IntegerProperty stock = new SimpleIntegerProperty();

        public LowStockProduct(String name, int stock) {
            this.name.set(name);
            this.stock.set(stock);
        }
        public StringProperty nameProperty() { return name; }
        public IntegerProperty stockProperty() { return stock; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadDashboardData();
    }

    private void setupTableColumns() {
        // Recent Orders Table
        orderIdCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        orderUserCol.setCellValueFactory(data -> data.getValue().userProperty());
        orderDateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        orderTotalCol.setCellValueFactory(data -> data.getValue().totalProperty().asObject());
        orderStatusCol.setCellValueFactory(data -> data.getValue().couponStatusProperty());

        // Coupon Orders Table
        couponOrderIdCol.setCellValueFactory(data -> data.getValue().orderIdProperty().asObject());
        couponUserCol.setCellValueFactory(data -> data.getValue().userProperty());
        couponCodeCol.setCellValueFactory(data -> data.getValue().couponCodeProperty());
        discountPercentCol.setCellValueFactory(data -> data.getValue().discountPercentProperty().asObject());
        originalTotalCol.setCellValueFactory(data -> data.getValue().originalTotalProperty().asObject());
        finalTotalCol.setCellValueFactory(data -> data.getValue().finalTotalProperty().asObject());
        savingsCol.setCellValueFactory(data -> data.getValue().savingsProperty().asObject());
        couponOrderDateCol.setCellValueFactory(data -> data.getValue().orderDateProperty());

        // Low Stock Table
        lsProductCol.setCellValueFactory(data -> data.getValue().nameProperty());
        lsStockCol.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
    }

    private void loadDashboardData() {
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Total Products
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM products");
            if (rs.next()) productsCount.setText(String.valueOf(rs.getInt("count")));

            // Total Orders
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM orders");
            if (rs.next()) ordersCount.setText(String.valueOf(rs.getInt("count")));

            // Total Revenue (with clean formatting)
            rs = stmt.executeQuery("SELECT SUM(total) AS revenue FROM orders");
            if (rs.next()) {
                java.math.BigDecimal revenue = rs.getBigDecimal("revenue");
                revenueLabel.setText(formatMoney(revenue, 2)); // 2 decimals for revenue
            }

            // Coupons Used Count
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM coupons WHERE used = TRUE");
            if (rs.next()) couponsUsedCount.setText(String.valueOf(rs.getInt("count")));

            // Total Savings from Coupons 
            rs = stmt.executeQuery(
                "SELECT SUM((c.discount_percent / 100.0) * " +
                "    (o.total / (1 - (c.discount_percent / 100.0)))) AS total_savings " +
                "FROM coupons c " +
                "JOIN orders o ON c.order_id = o.id " +
                "WHERE c.used = TRUE"
            );
            if (rs.next()) {
                java.math.BigDecimal savings = rs.getBigDecimal("total_savings");
                totalSavingsLabel.setText(formatMoney(savings, 1)); // 1 decimal for savings as requested
            }

            // Total Selling (with clean formatting)
            rs = stmt.executeQuery("SELECT SUM(total) AS selling FROM orders");
            if (rs.next()) {
                java.math.BigDecimal selling = rs.getBigDecimal("selling");
                totalSellingLabel.setText(formatMoney(selling, 2)); // 2 decimals for selling
            }

            // Recent Orders (last 10)
            ObservableList<OrderSummary> orders = FXCollections.observableArrayList();
            rs = stmt.executeQuery(
                "SELECT o.id, o.user, o.ordered_at, o.total, " +
                "CASE WHEN c.code IS NOT NULL THEN CONCAT('Yes (', c.code, ')') ELSE 'No' END AS coupon_status " +
                "FROM orders o " +
                "LEFT JOIN coupons c ON o.id = c.order_id AND c.used = TRUE " +
                "ORDER BY o.ordered_at DESC LIMIT 10"
            );
            while (rs.next()) {
                orders.add(new OrderSummary(
                    rs.getInt("id"),
                    rs.getString("user"),
                    rs.getString("ordered_at"),
                    rs.getDouble("total"),
                    rs.getString("coupon_status")
                ));
            }
            ordersTable.setItems(orders);

            // Orders with Coupons Applied
            ObservableList<CouponOrderSummary> couponOrders = FXCollections.observableArrayList();
            rs = stmt.executeQuery(
                "SELECT o.id, o.user, c.code, c.discount_percent, o.total, o.ordered_at, " +
                "       (o.total / (1 - (c.discount_percent / 100.0))) AS original_total, " +
                "       ((o.total / (1 - (c.discount_percent / 100.0))) - o.total) AS savings " +
                "FROM orders o " +
                "JOIN coupons c ON o.id = c.order_id " +
                "WHERE c.used = TRUE " +
                "ORDER BY o.ordered_at DESC LIMIT 10"
            );
            while (rs.next()) {
                double originalTotal = rs.getDouble("original_total");
                double finalTotal = rs.getDouble("total");
                double savings = Math.round(rs.getDouble("savings") * 10.0) / 10.0; 
                
                couponOrders.add(new CouponOrderSummary(
                    rs.getInt("id"),
                    rs.getString("user"),
                    rs.getString("code"),
                    rs.getInt("discount_percent"),
                    originalTotal,
                    finalTotal,
                    savings,
                    rs.getString("ordered_at")
                ));
            }
            couponOrdersTable.setItems(couponOrders);

            // Low Stock Products (stock < 5)
            ObservableList<LowStockProduct> lowStock = FXCollections.observableArrayList();
            rs = stmt.executeQuery("SELECT name, stock FROM products WHERE stock < 5 ORDER BY stock ASC");
            while (rs.next()) {
                lowStock.add(new LowStockProduct(rs.getString("name"), rs.getInt("stock")));
            }
            lowStockTable.setItems(lowStock);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    // Add this public method to DashboardController
public void refreshData() {
    loadDashboardData(); 
}

    
    // Updated helper method for money formatting (custom precision)
    private String formatMoney(java.math.BigDecimal value, int decimalPlaces) {
        if (value == null) return "$0.0";
        value = value.setScale(decimalPlaces, java.math.RoundingMode.HALF_UP); 
        return "$" + value.toPlainString(); 
    }
}
