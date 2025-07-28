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

    @FXML private Label productsCount, ordersCount, revenueLabel, usersCount;
    @FXML private TableView<OrderSummary> ordersTable;
    @FXML private TableColumn<OrderSummary, Integer> orderIdCol;
    @FXML private TableColumn<OrderSummary, String> orderUserCol, orderDateCol;
    @FXML private TableColumn<OrderSummary, Double> orderTotalCol;
    @FXML private TableView<LowStockProduct> lowStockTable;
    @FXML private TableColumn<LowStockProduct, String> lsProductCol;
    @FXML private TableColumn<LowStockProduct, Integer> lsStockCol;

    public static class OrderSummary {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty user = new SimpleStringProperty();
        private final StringProperty date = new SimpleStringProperty();
        private final DoubleProperty total = new SimpleDoubleProperty();

        public OrderSummary(int id, String user, String date, double total) {
            this.id.set(id);
            this.user.set(user);
            this.date.set(date);
            this.total.set(total);
        }
        public IntegerProperty idProperty() { return id; }
        public StringProperty userProperty() { return user; }
        public StringProperty dateProperty() { return date; }
        public DoubleProperty totalProperty() { return total; }
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
        orderIdCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        orderUserCol.setCellValueFactory(data -> data.getValue().userProperty());
        orderDateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        orderTotalCol.setCellValueFactory(data -> data.getValue().totalProperty().asObject());

        lsProductCol.setCellValueFactory(data -> data.getValue().nameProperty());
        lsStockCol.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
    }

    private void loadDashboardData() {
        try (Connection conn = DBUtil.getConnection()) {
            // Total Products
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM products");
            if (rs.next()) productsCount.setText(String.valueOf(rs.getInt("count")));

            // Total Orders
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM orders");
            if (rs.next()) ordersCount.setText(String.valueOf(rs.getInt("count")));

            // Total Revenue
            rs = stmt.executeQuery("SELECT SUM(total) AS revenue FROM orders");
            if (rs.next()) revenueLabel.setText(String.format("$%.2f", rs.getDouble("revenue")));

            // Total Users
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM users");
            if (rs.next()) usersCount.setText(String.valueOf(rs.getInt("count")));

            // Recent Orders (last 10)
            ObservableList<OrderSummary> orders = FXCollections.observableArrayList();
            rs = stmt.executeQuery("SELECT id, user, ordered_at, total FROM orders ORDER BY ordered_at DESC LIMIT 10");
            while (rs.next()) {
                orders.add(new OrderSummary(
                    rs.getInt("id"),
                    rs.getString("user"),
                    rs.getString("ordered_at"),
                    rs.getDouble("total")
                ));
            }
            ordersTable.setItems(orders);

         
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
}
