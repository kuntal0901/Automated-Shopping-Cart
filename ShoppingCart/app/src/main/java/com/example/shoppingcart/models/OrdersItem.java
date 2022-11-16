package com.example.shoppingcart.models;

public class OrdersItem {
    public String id;
    public String content;
    public String total;
    public OrdersItem(String id, String content, String total) {
        this.id = id;
        this.content = content;
        this.total = total;
    }

}
