package com.example.adamo.cookster;


public class DishModel {
    private String name, id, category, categoryId, products, av;

    protected DishModel(String name, String id, String category, String categoryId, String products, String av) {
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
        return products;
    }

    public String getAv() {
        return av;
    }

    public String getResourceUri() {
        return "api/resdishes/" + this.id + "/";
    }
}
