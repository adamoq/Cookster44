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
            if (products.length() < 1) res = "-";
            else
                for (int i = 0; i < products.length(); i++) {
                    res += products.getJSONObject(i).getString("product__name") + " (" + products.getJSONObject(i).getString("count") + products.getJSONObject(i).getString("product__unit") + ") , ";
                }

        } catch (JSONException e) {
            e.printStackTrace();
            res = "-";
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
