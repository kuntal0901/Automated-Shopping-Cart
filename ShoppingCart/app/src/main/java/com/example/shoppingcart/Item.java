package com.example.shoppingcart;

public class Item {
    String name;
    float price;
    float quantity;
    float subTotal;

    public Item(String name, float price, float quantity, float subTotal) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getSubTotal() {
        return subTotal;
    }
}
