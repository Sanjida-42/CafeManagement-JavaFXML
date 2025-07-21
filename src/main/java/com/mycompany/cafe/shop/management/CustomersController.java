package com.mycompany.cafe.shop.management;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class CustomersController {

    @FXML private TableView<CustomerSummary> customerTableView;
    @FXML private TableColumn<CustomerSummary, String> customerNameCol, customerEmailCol;
    @FXML private TableColumn<CustomerSummary, Integer> orderCountCol;
    @FXML private TableColumn<CustomerSummary, Double> totalSpentCol;

    public static class CustomerSummary {
        private final SimpleIntegerProperty id = new SimpleIntegerProperty();
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleStringProperty email = new SimpleStringProperty();
        private final SimpleIntegerProperty orderCount = new SimpleIntegerProperty();
        private final SimpleDoubleProperty totalSpent = new SimpleDoubleProperty();

        public CustomerSummary(int id, String name, String email, int orderCount, double totalSpent) {
            this.id.set(id);
            this.name.set(name);
            this.email.set(email);
            this.orderCount.set(orderCount);
            this.totalSpent.set(totalSpent);
        }
        public IntegerProperty idProperty() { return id; }
        public StringProperty nameProperty() { return name; }
        public StringProperty emailProperty() { return email; }
        public IntegerProperty orderCountProperty() { return orderCount; }
        public DoubleProperty totalSpentProperty() { return totalSpent; }
    }

    @FXML
    public void initialize() {
        customerNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        customerEmailCol.setCellValueFactory(data -> data.getValue().emailProperty());
        orderCountCol.setCellValueFactory(data -> data.getValue().orderCountProperty().asObject());
        totalSpentCol.setCellValueFactory(data -> data.getValue().totalSpentProperty().asObject());
        loadCustomers();
    }

    private void loadCustomers() {
        customerTableView.getItems().clear();
     String sql =
      "SELECT u.id, u.name, u.email, COUNT(o.id) AS orders, COALESCE(SUM(o.total), 0) AS spent " +
      "FROM users u " +
      "JOIN orders o ON u.name = o.user " +
      "GROUP BY u.id, u.name, u.email " +
      "ORDER BY u.name";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int orderCount = rs.getInt("orders");
                double spent = rs.getDouble("spent");
                customerTableView.getItems().add(new CustomerSummary(id, name, email, orderCount, spent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
