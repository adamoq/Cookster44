package com.example.adamo.cooksterapp;


import org.json.JSONArray;
import org.json.JSONException;

public class DishModel {
    JSONArray products;
    private String name, id, category, categoryId, av;

    protected DishModel(String name, String id, String category, String categoryId, JSONArray products, String av) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.categoryId = categoryId;
        this.products = products;
        this.av = av;

    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getProducts() {
        String res = "";

        try {
            for (int i = 0; i < products.length(); i++) {
                res += products.getJSONObject(i).getString("name") + ", ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return res;
    }

    public String getAv() {
        return av;
    }

    public String getResourceUri() {
        return "api/resdishes/" + this.id + "/";
    }
}
