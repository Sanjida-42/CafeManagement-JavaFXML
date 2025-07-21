package com.mycompany.cafe.shop.management;

import javafx.beans.property.*;

public class Customer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();

    public Customer(int id, String name) { setId(id); setName(name); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
}
