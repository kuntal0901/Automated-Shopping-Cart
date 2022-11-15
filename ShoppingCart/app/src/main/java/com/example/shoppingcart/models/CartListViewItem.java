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
        float total_price = (float) (Math.round((price * quantity) * 100.0) / 100.0);
        return String.valueOf(total_price);
    }
    public String getQuantity(){
        return String.valueOf(quantity);
    }

    public String getPrice(){
        return String.valueOf(price);
    }
}
