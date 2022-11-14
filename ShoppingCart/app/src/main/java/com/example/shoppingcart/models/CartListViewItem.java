package com.example.shoppingcart.models;

public class CartListViewItem {
//    public int name;
    public String name;
    float price;
    float quantity;

    public CartListViewItem(String name, float price, float quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getTotalPrice(){
        return String.valueOf((price * quantity));
    }
    public String getQuantity(){
        return String.valueOf(quantity);
    }

    public String getPrice(){
        return String.valueOf(price);
    }
}
