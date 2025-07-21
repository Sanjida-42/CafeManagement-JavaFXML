package com.mycompany.cafe.shop.management;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card_formController {
    @FXML private Label prod_name;
    @FXML private Label prod_price;
    @FXML private ImageView prod_imageView;
    @FXML private Spinner<Integer> prod_spinner;
    @FXML private Button prod_addBtn;

    private Product product;
    private HomeController homeController;

    public void setProduct(Product product, HomeController homeController) {
        this.product = product;
        this.homeController = homeController;
        prod_name.setText(product.getName());
        prod_price.setText(String.format("$%.2f", product.getPrice()));
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                prod_imageView.setImage(new Image(product.getImagePath(), true));
            } catch (Exception e) { prod_imageView.setImage(null); }
        } else {
            prod_imageView.setImage(null);
        }
        prod_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1, Math.max(1, product.getStock()), 1));
        prod_addBtn.setDisable(product.getStock() <= 0);
    }

    @FXML
    private void addBtn() {
        int qty = prod_spinner.getValue();
        homeController.addToCart(product, qty);
    }
}
