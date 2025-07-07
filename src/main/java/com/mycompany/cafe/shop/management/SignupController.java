package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Text loginLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Login link loaded: " + loginLink);
        loginLink.setOnMouseClicked((MouseEvent event) -> {
            try {
                CafeShopMain.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleSignup() {
        System.out.println("Signup: " + nameField.getText());
    }
}
