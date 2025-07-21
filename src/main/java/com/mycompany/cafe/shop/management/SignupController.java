package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Text loginLink;

    @FXML
    public void initialize() {
        DBUtil.initialize();
        loginLink.setOnMouseClicked(event -> {
            try {
                CafeShopMain.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            alert("Signup Failed", "All fields are required.", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            alert("Success", "Signup successful. Please login.", Alert.AlertType.INFORMATION);
            CafeShopMain.setRoot("login");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                alert("Signup Failed", "Email already registered.", Alert.AlertType.ERROR);
            } else {
                alert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void alert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
