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

public class HomeController {
    @FXML private Label username;
    @FXML private AnchorPane dashboard_form, inventory_form, menu_form, customers_form;
    @FXML private ComboBox<String> statusCombo;
    @FXML private GridPane menu_gridPane;
    @FXML private Button dashboard_btn, inventory_btn, menu_btn, customers_btn, logout_btn;

    // Inventory FXML:
    @FXML private TableView<Product> inventory_tableView;
    @FXML private TableColumn<Product, Integer> idCol, stockCol;
    @FXML private TableColumn<Product, String> nameCol, typeCol, statusCol, dateCol, imagePathCol;
    @FXML private TableColumn<Product, Double> priceCol;
    @FXML private TextField idField, nameField, typeField, stockField, priceField,
            imagePathField;
    private Product selectedProduct;
    
   //Menu cart
    
    @FXML private VBox cartBox;
   @FXML private Label cartTotalLabel;
   @FXML private Button payBtn, clearCartBtn;
   private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
private double cartTotal = 0.0;
// Track stock for limiting spinner
private final Map<Integer, Integer> productStock = new HashMap<>();

   

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
                    AnchorPane dashboardRoot = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    dashboard_form.getChildren().setAll(dashboardRoot);
                    dashboardLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dashboard_form.setVisible(true);
            break;
        case "inventory": inventory_form.setVisible(true); break;
        case "menu": menu_form.setVisible(true); break;
        case "customers":
            // Only load FXML the first time!
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
    
    
    
    // Inventory CRUD Operation Code
    
private void showProductDetails(Product p) {
    selectedProduct = p;
    if (p != null) {
        idField.setText(String.valueOf(p.getId()));
        nameField.setText(p.getName());
        typeField.setText(p.getType());
        stockField.setText(String.valueOf(p.getStock()));
        priceField.setText(String.valueOf(p.getPrice()));
  
        statusCombo.setItems(FXCollections.observableArrayList("Available", "Not Available"));
//        dateField.setText(p.getDate());
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
//            ps.setString(6, dateField.getText());
            ps.setString(7, imagePathField.getText());
            ps.setInt(8, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            loadInventory(); clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML private void deleteProduct() {
        if (selectedProduct == null) return;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedProduct.getId());
            ps.executeUpdate();
            loadInventory(); clearFields();
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
    // Make sure not to add more than available stock
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
    updateCartDisplay();
}

    
    @FXML private void payOrder() {
    if (cartItems.isEmpty()) return;
    String username = this.username.getText(); // or LoginController.currentUserName
    try (Connection conn = DBUtil.getConnection()) {
        conn.setAutoCommit(false);
        // 1. Insert order
        PreparedStatement psOrder = conn.prepareStatement(
            "INSERT INTO orders (user, total) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        psOrder.setString(1, username);
        psOrder.setDouble(2, cartTotal);
        psOrder.executeUpdate();
        int orderId = 0;
        try (ResultSet rs = psOrder.getGeneratedKeys()) {
            if (rs.next()) orderId = rs.getInt(1);
        }
        // 2. Insert order items & update product stock
        for (CartItem item : cartItems) {
            // Insert order_items
            PreparedStatement psItem = conn.prepareStatement(
                "INSERT INTO order_items (order_id,product_id,product_name,quantity,price) VALUES (?,?,?,?,?)");
            psItem.setInt(1, orderId);
            psItem.setInt(2, item.product.getId());
            psItem.setString(3, item.product.getName());
            psItem.setInt(4, item.quantity);
            psItem.setDouble(5, item.product.getPrice());
            psItem.executeUpdate();
            // Update stock
            PreparedStatement psStock = conn.prepareStatement(
                "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?");
            psStock.setInt(1, item.quantity);
            psStock.setInt(2, item.product.getId());
            psStock.setInt(3, item.quantity);
            int rows = psStock.executeUpdate();
            if (rows == 0) throw new SQLException("Not enough stock for " + item.product.getName());
        }
        conn.commit();
        new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!").showAndWait();
        cartItems.clear();
        updateCartDisplay();
        loadMenuCards();
        loadInventory(); // Update inventory if open
    } catch (Exception e) {
        new Alert(Alert.AlertType.ERROR, "Order failed: "+e.getMessage()).showAndWait();
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
    cartTotalLabel.setText(String.format("$%.2f", cartTotal));
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
