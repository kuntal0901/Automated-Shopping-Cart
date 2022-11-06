package com.example.shoppingcart;

import java.util.HashMap;
import java.util.Map;

public class dataholder {
    String name,profilephoto;
    Map<String, Map<String,Integer>> orders;

    public dataholder(String name, String profilephoto, Map<String, Map<String, Integer>> orders) {
        this.name = name;
        this.profilephoto = profilephoto;
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public Map<String, Map<String, Integer>> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Map<String, Integer>> orders) {
        this.orders = orders;
    }
}