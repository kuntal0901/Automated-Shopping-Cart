package com.example.shoppingcart;

public class Item {
    String name;
    float price;

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public Item(String name, float price) {
        this.name = name;
        this.price = price;
    }
}
