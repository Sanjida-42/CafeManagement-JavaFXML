package com.mycompany.cafe.shop.management;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty imagePath = new SimpleStringProperty();

    public Product(int id, String name, String type, int stock, double price,
                   String status, String imagePath) {
        setId(id); setName(name); setType(type); setStock(stock);
        setPrice(price); setStatus(status); setImagePath(imagePath);
    }

    // JavaFX properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty typeProperty() { return type; }
    public IntegerProperty stockProperty() { return stock; }
    public DoubleProperty priceProperty() { return price; }
    public StringProperty statusProperty() { return status; }
    public StringProperty imagePathProperty() { return imagePath; }
    // Normal getters & setters omitted for brevity
    public int getId() { return id.get(); } public void setId(int v) { id.set(v); }
    public String getName() { return name.get(); } public void setName(String v) { name.set(v); }
    public String getType() { return type.get(); } public void setType(String v) { type.set(v); }
    public int getStock() { return stock.get(); } public void setStock(int v) { stock.set(v); }
    public double getPrice() { return price.get(); } public void setPrice(double v) { price.set(v); }
    public String getStatus() { return status.get(); } public void setStatus(String v) { status.set(v); }
    public String getImagePath() { return imagePath.get(); } public void setImagePath(String v) { imagePath.set(v); }
}
