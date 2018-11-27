package com.example.adamo.cooksterapp;

class OrderCookModel {
    private int id;
    private String provider, products, priority, date, state, comment;

    OrderCookModel(int id, String provider, String products, String priority, String date, String state, String comment) {
        this.id = id;
        this.priority = priority;
        this.provider = provider;
        this.products = products;
        this.state = state;
        this.date = date;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getProvider() {
        return provider;
    }

    public String getProducts() {
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
