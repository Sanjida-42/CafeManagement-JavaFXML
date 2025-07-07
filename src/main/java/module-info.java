module com.mycompany.cafe.shop.management {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.cafe.shop.management to javafx.fxml;
    exports com.mycompany.cafe.shop.management;
}
