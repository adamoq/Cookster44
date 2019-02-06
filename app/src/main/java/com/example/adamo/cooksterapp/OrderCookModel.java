package com.example.adamo.cooksterapp;

import java.util.ArrayList;
import java.util.HashMap;

class OrderCookModel {
    private int id;
    ArrayList<HashMap<String, String>> products;
    Boolean active;
    private String provider, priority, date, state, comment, taskId, level, url, position;

    OrderCookModel(int id, String provider, ArrayList<HashMap<String, String>> products, String priority, String date, String state, String comment) {
        this.id = id;
        this.priority = priority;
        this.provider = provider;
        this.products = products;
        this.state = state;
        this.date = date;
        this.comment = comment;
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
        return products;
    }

    public String getPriority() {
        return priority;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

}
