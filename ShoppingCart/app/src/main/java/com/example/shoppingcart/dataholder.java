package com.example.shoppingcart;

import java.util.HashMap;
import java.util.Map;

public class dataholder {
    String name,profilephoto;
    Orders prevorder;
    public dataholder()
    {

    }
    public static class Orders{
        String orderid;

        public Orders() {

        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }
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

    public Orders getPrevorder() {
        return prevorder;
    }

    public void setPrevorder(Orders prevorder) {
        this.prevorder = prevorder;
    }

//    public dataholder(Orders prevorder,String profilephoto,String name ) {
//        this.name = name;
//        this.profilephoto = profilephoto;
//        this.prevorder = prevorder;
//    }


}