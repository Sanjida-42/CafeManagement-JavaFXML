package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
     public static String currentUserName = null; 
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Text signUpLink;

    @FXML
    public void initialize() {
        DBUtil.initialize();
        signUpLink.setOnMouseClicked(event -> {
            try {
                CafeShopMain.setRoot("signup");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            alert("Login Failed", "Please enter both email and password.", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE email=? AND password=?"
            );
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentUserName = rs.getString("name");
                CafeShopMain.setRoot("home");
            } else {
                alert("Login Failed", "Invalid email or password.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
             e.printStackTrace();
            alert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void alert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
