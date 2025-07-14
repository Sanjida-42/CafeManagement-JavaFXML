package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Text signUpLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBUtil.initialize(); // Ensure DB/table exists
        signUpLink.setOnMouseClicked((MouseEvent event) -> {
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
            showAlert(Alert.AlertType.ERROR, "Please enter email and password.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CafeShopMain.setRoot("home");
            } else {
                showAlert(Alert.AlertType.ERROR, "No account found or wrong password.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
