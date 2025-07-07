package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Text signUpLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SignUp link loaded: " + signUpLink);
        signUpLink.setOnMouseClicked((MouseEvent event) -> {
            try {
                CafeShopMain.setRoot("signup");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleLogin() {
        System.out.println("Login with: " + emailField.getText());
    }
}
