package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.UUID;
import java.sql.Timestamp;

public class HomeController {
    @FXML private Label username;
    @FXML private AnchorPane dashboard_form, inventory_form, menu_form, customers_form;
    @FXML private ComboBox<String> statusCombo;
    @FXML private GridPane menu_gridPane;
    @FXML private Button dashboard_btn, inventory_btn, menu_btn, customers_btn, logout_btn;

    // Inventory FXML:
    @FXML private TableView<Product> inventory_tableView;
    @FXML private TableColumn<Product, Integer> idCol, stockCol;
    @FXML private TableColumn<Product, String> nameCol, typeCol, statusCol, imagePathCol;
    @FXML private TableColumn<Product, Double> priceCol;
    @FXML private TextField idField, nameField, typeField, stockField, priceField,
            imagePathField;
    private Product selectedProduct;

    // Menu cart
    @FXML private VBox cartBox;
    @FXML private Label cartTotalLabel;
    @FXML private Button payBtn, clearCartBtn;
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private double cartTotal = 0.0;
    // Track stock for limiting spinner
    private final Map<Integer, Integer> productStock = new HashMap<>();

    private DashboardController dashboardController; 

    
    // Coupon fields
    @FXML private TextField couponField;
    private double appliedDiscount = 0.0;
    private String appliedCouponCode = null; // To track code for marking as used

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList("Available", "Not Available"));
        if (LoginController.currentUserName != null) {
            username.setText(LoginController.currentUserName);
        } else {
            username.setText("User");
        }
        showForm("dashboard");
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());
        stockCol.setCellValueFactory(cell -> cell.getValue().stockProperty().asObject());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
        imagePathCol.setCellValueFactory(cell -> cell.getValue().imagePathProperty());
        inventory_tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showProductDetails(newVal)
        );
        loadInventory();
    }

    @FXML
    private void switchForm(javafx.event.ActionEvent event) {
        if (event.getSource() == dashboard_btn) {
            showForm("dashboard");
        } else if (event.getSource() == inventory_btn) {
            showForm("inventory");
            loadInventory();
        } else if (event.getSource() == menu_btn) {
            showForm("menu");
            loadMenuCards();
        } else if (event.getSource() == customers_btn) {
            showForm("customers");
        }
    }

    private boolean customersLoaded = false;
    private boolean dashboardLoaded = false;
    private void showForm(String form) {
        dashboard_form.setVisible(false);
        inventory_form.setVisible(false);
        menu_form.setVisible(false);
        customers_form.setVisible(false);
        switch (form) {
            case "dashboard":
                 if (!dashboardLoaded) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            AnchorPane dashboardRoot = loader.load();
            dashboardController = loader.getController(); // Get controller instance
            dashboard_form.getChildren().setAll(dashboardRoot);
            dashboardLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    if (dashboardController != null) {
        dashboardController.refreshData(); // Refresh data every time shown
    }
    dashboard_form.setVisible(true);
    break;
            case "inventory": inventory_form.setVisible(true); break;
            case "menu": menu_form.setVisible(true); break;
            case "customers":
                if (!customersLoaded) {
                    try {
                        AnchorPane customersRoot = FXMLLoader.load(getClass().getResource("customers.fxml"));
                        customers_form.getChildren().setAll(customersRoot);
                        customersLoaded = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                customers_form.setVisible(true);
                break;
        }
    }

    // Inventory CRUD
    private void showProductDetails(Product p) {
        selectedProduct = p;
        if (p != null) {
            idField.setText(String.valueOf(p.getId()));
            nameField.setText(p.getName());
            typeField.setText(p.getType());
            stockField.setText(String.valueOf(p.getStock()));
            priceField.setText(String.valueOf(p.getPrice()));
            statusCombo.setValue(p.getStatus());
            imagePathField.setText(p.getImagePath());
        }
    }

    private void loadInventory() {
        inventory_tableView.getItems().clear();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                inventory_tableView.getItems().add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("stock"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("imagePath")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void addProduct() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO products (name, type, stock, price, status, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText());
            ps.setString(2, typeField.getText());
            ps.setInt(3, Integer.parseInt(stockField.getText()));
            ps.setDouble(4, Double.parseDouble(priceField.getText()));
            ps.setString(5, statusCombo.getValue());
            ps.setString(6, imagePathField.getText());
            ps.executeUpdate();
            loadInventory();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void updateProduct() {
        if (selectedProduct == null) return;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE products SET name=?,type=?,stock=?,price=?,status=?,imagePath=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText());
            ps.setString(2, typeField.getText());
            ps.setInt(3, Integer.parseInt(stockField.getText()));
            ps.setDouble(4, Double.parseDouble(priceField.getText()));
            ps.setString(5, statusCombo.getValue());
            ps.setString(6, imagePathField.getText());
            ps.setInt(7, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            loadInventory();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void deleteProduct() {
        if (selectedProduct == null) return;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedProduct.getId());
            ps.executeUpdate();
            loadInventory();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void clearFields() {
        idField.clear(); nameField.clear(); typeField.clear();
        stockField.clear(); priceField.clear();
        imagePathField.clear(); 
        statusCombo.setValue(null);
        selectedProduct = null;
    }

    @FXML private void chooseImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        java.io.File f = fc.showOpenDialog(imagePathField.getScene().getWindow());
        if (f != null) imagePathField.setText(f.toURI().toString());
    }

    public static class CartItem {
        Product product;
        int quantity;
        CartItem(Product product, int quantity) { this.product = product; this.quantity = quantity; }
        double getSubtotal() { return product.getPrice() * quantity; }
    }

    public void addToCart(Product product, int quantity) {
        int available = product.getStock();
        for (CartItem item : cartItems) {
            if (item.product.getId() == product.getId()) {
                int newQty = Math.min(item.quantity + quantity, available);
                item.quantity = newQty;
                updateCartDisplay();
                return;
            }
        }
        cartItems.add(new CartItem(product, Math.min(quantity, available)));
        updateCartDisplay();
    }

    @FXML private void clearCart() {
      cartItems.clear();
    appliedDiscount = 0.0;
    appliedCouponCode = null;
    couponField.clear();
    updateCartDisplay();
    }

  @FXML private void generateCouponForOrder(int orderId, int userId, int discountPercent) {
        String code = "CAFE" + discountPercent + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // 30 days

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coupons (code, user_id, order_id, discount_percent, expires_at) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, code);
            ps.setInt(2, userId);
            ps.setInt(3, orderId);
            ps.setInt(4, discountPercent);
            ps.setTimestamp(5, expiresAt);
            ps.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Coupon generated: " + code + " for " + discountPercent + "% off!").show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML private void applyCoupon() {
    String code = couponField.getText().trim();
    if (code.isEmpty()) return;

    try (Connection conn = DBUtil.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT discount_percent FROM coupons WHERE code = ? AND used = FALSE AND expires_at > NOW() AND user_id = ?");
        ps.setString(1, code);
        ps.setInt(2, LoginController.currentUserId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            appliedDiscount = rs.getInt("discount_percent") / 100.0;
            appliedCouponCode = code; // Ensure set
            System.out.println("Coupon applied: " + code + " for user " + LoginController.currentUserId); // Debug log
            updateCartDisplay();
            new Alert(Alert.AlertType.INFORMATION, "Coupon applied!").show();
        } else {
            System.out.println("Invalid coupon attempt: " + code); // Debug log
            new Alert(Alert.AlertType.ERROR, "Invalid or expired coupon!").show();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Error applying coupon: " + e.getMessage()).show();
    }
}


@FXML private void payOrder() {
    if (cartItems.isEmpty()) return;
    String username = this.username.getText();
    try (Connection conn = DBUtil.getConnection()) {
        conn.setAutoCommit(false);
        // 1. Insert order
        PreparedStatement psOrder = conn.prepareStatement(
            "INSERT INTO orders (user, total) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        psOrder.setString(1, username);
        psOrder.setDouble(2, cartTotal * (1 - appliedDiscount)); // Apply discount to saved total
        psOrder.executeUpdate();
        int orderId = 0;
        try (ResultSet rs = psOrder.getGeneratedKeys()) {
            if (rs.next()) orderId = rs.getInt(1);
        }
        // 2. Insert order items & update product stock
        for (CartItem item : cartItems) {
            PreparedStatement psItem = conn.prepareStatement(
                "INSERT INTO order_items (order_id,product_id,product_name,quantity,price) VALUES (?,?,?,?,?)");
            psItem.setInt(1, orderId);
            psItem.setInt(2, item.product.getId());
            psItem.setString(3, item.product.getName());
            psItem.setInt(4, item.quantity);
            psItem.setDouble(5, item.product.getPrice());
            psItem.executeUpdate();
            PreparedStatement psStock = conn.prepareStatement(
                "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?");
            psStock.setInt(1, item.quantity);
            psStock.setInt(2, item.product.getId());
            psStock.setInt(3, item.quantity);
            int rows = psStock.executeUpdate();
            if (rows == 0) throw new SQLException("Not enough stock for " + item.product.getName());
        }

        // Mark coupon as used if applied (before commit for safety)
        if (appliedCouponCode != null) {
            System.out.println("Attempting to mark coupon " + appliedCouponCode + " as used for user " + LoginController.currentUserId);
            PreparedStatement psUse = conn.prepareStatement("UPDATE coupons SET used = TRUE WHERE code = ? AND user_id = ?");
            psUse.setString(1, appliedCouponCode);
            psUse.setInt(2, LoginController.currentUserId);
            int rowsUpdated = psUse.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Success: Coupon " + appliedCouponCode + " marked as used.");
            } else {
                System.out.println("Failure: No rows updated for coupon " + appliedCouponCode + ". Check code/user_id.");
                new Alert(Alert.AlertType.WARNING, "Coupon applied but status not updated in DB. Check console.").show();
            }
        } else {
            System.out.println("No coupon applied for this order.");
        }

        conn.commit();

        // Generate new coupon
        generateCouponForOrder(orderId, LoginController.currentUserId, 10); // 10% discount example

        new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!").showAndWait();
        cartItems.clear();
        appliedDiscount = 0.0;
        appliedCouponCode = null;
        couponField.clear();
        updateCartDisplay();
        loadMenuCards();
        loadInventory();
        if (dashboardController != null && dashboard_form.isVisible()) {
    dashboardController.refreshData(); 
}
    } catch (Exception e) {
        new Alert(Alert.AlertType.ERROR, "Order failed: " + e.getMessage()).showAndWait();
        e.printStackTrace();
     
    }
}

    private void updateCartDisplay() {
        cartBox.getChildren().clear();
        cartTotal = 0.0;
        for (CartItem item : cartItems) {
            HBox line = new HBox(10);
            Label name = new Label(item.product.getName());
            name.setPrefWidth(120);
            Label qty = new Label("x" + item.quantity);
            qty.setPrefWidth(35);
            Label sub = new Label(String.format("$%.2f", item.getSubtotal()));
            Button remove = new Button("Remove");
            remove.setOnAction(e -> {
                cartItems.remove(item);
                updateCartDisplay();
            });
            line.getChildren().addAll(name, qty, sub, remove);
            cartBox.getChildren().add(line);
            cartTotal += item.getSubtotal();
        }
        double finalTotal = cartTotal * (1 - appliedDiscount);
        cartTotalLabel.setText(String.format("$%.2f", finalTotal));
        payBtn.setDisable(cartItems.isEmpty());
        clearCartBtn.setDisable(cartItems.isEmpty());
    }
// Load menu products as cards
private void loadMenuCards() {
    menu_gridPane.getChildren().clear();
    ArrayList<Product> products = new ArrayList<>();
    try (Connection conn = DBUtil.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE status='Available' AND stock > 0")) {
        while (rs.next()) {
            products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("stock"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("imagePath")));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
        int col = 0, row = 0;
        for (Product p : products) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("card_form.fxml"));
            AnchorPane cardPane = loader.load();
            Card_formController cardController = loader.getController();
            cardController.setProduct(p, this); // Pass HomeController reference to allow cart add
            menu_gridPane.add(cardPane, col, row);
            col++;
            if (col == 2) { col = 0; row++; }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
  
    

    @FXML
    private void logout() {
        try {
            CafeShopMain.setRoot("login");
        } catch (Exception e) { e.printStackTrace(); }
    }
}
