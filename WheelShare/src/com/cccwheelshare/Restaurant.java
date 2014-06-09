package com.cccwheelshare;

import java.util.Vector;
import com.cccwheelshare.Item;

public class Restaurant {
    
    private String restaurantName;
    private int restaurantID;
    private Vector<Item> menu = new Vector<Item> (100,1);
    
    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String name) {
        this.restaurantName = name;
    }
    
    public int getRestaurantID() {
        return restaurantID;
    }
    
}