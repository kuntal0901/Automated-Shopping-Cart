package com.example.shoppingcart.models;

public class CartItem {
    public String name;
    public float weight;

    public CartItem(String name, float weight) {
        this.name = name;
        this.weight = (float) (Math.round(weight * 100.0) / 100.0);
    }
}
