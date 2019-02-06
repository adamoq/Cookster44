package com.example.adamo.cooksterapp;

import java.util.ArrayList;
import java.util.HashMap;

class OrderWaiterModel {
    ArrayList<HashMap<String, String>> dishes;
    Boolean active;
    private int id;
    private String provider, price, date, state, comment, taskId, level, url, position;

    OrderWaiterModel(int id, String provider, ArrayList<HashMap<String, String>> dishes, String price, String date,
                     String state, String level, String comment, Boolean active, String taskId, String url, String position) {
        this.id = id;
        this.price = price;
        this.provider = provider;
        this.dishes = dishes;
        this.state = state;
        this.date = date;
        this.comment = comment;
        this.active = active;
        this.taskId = taskId;
        this.level = level;
        this.url = url;
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public String getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public String gettaskId() {
        return taskId;
    }

    public String getComment() {
        return comment;
    }

    public Boolean isActive() {
        return active;
    }

    public String getProvider() {
        return provider;
    }

    public String getLevel() {
        return level;
    }

    public ArrayList<HashMap<String, String>> getProducts() {
        return dishes;
    }

    public String getPriority() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

}
