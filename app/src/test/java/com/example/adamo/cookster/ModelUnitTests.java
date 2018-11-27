package com.example.adamo.cookster;

import com.example.adamo.cooksterapp.DishModel;

import org.json.JSONArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ModelUnitTests {
    private DishModel dishModel;
    private String name = "Name", id = "Id", category = "category", categoryId = "Category Id", av = "Av";
    private JSONArray products = new JSONArray();

    @Test
    public void objectValidator() {
        dishModel = new DishModel(name, id, category, categoryId, products, av);
        assertEquals(dishModel.getAv(), av);
        assertEquals(dishModel.getCategory(), category);
        assertEquals(dishModel.getCategoryId(), categoryId);
        assertEquals(dishModel.getAv(), av);
        assertEquals(dishModel.getProducts(), "");
        assertEquals(dishModel.getName(), name);
        assertEquals(dishModel.getResourceUri(), "api/resdishes/" + id + "/");
        assertEquals(dishModel.getClass(), DishModel.class);
    }

}
