package com.example.adamo.cooksterapp;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class DishActivity extends BaseActivity {
    private List<DishModel> dishes;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dishes);
        SharedPreferences settings = getSharedPreferences("login", 0);
        super.onCreate(savedInstanceState, "api/resdishes/");


    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {

        super.renderData(data, R.id.nav_dishes);
        if (data.has("error")) return;
        JSONArray arr = data.getJSONArray("objects");
        dishes = new ArrayList<>();
        LinearLayout linearLayout = findViewById(R.id.linear_dishes);
        for (int i = 0; i < arr.length(); i++) {
            dishes.add(new DishModel(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getJSONObject("category").getString("category_name"), arr.getJSONObject(i).getString("category"), arr.getJSONObject(i).getJSONArray("dishproducts"), arr.getJSONObject(i).getString("av")));
            // linearLayout.addView(new DishView(this,new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av"))));
        }

        for (int i = 0; i < arr.length(); i++) {
            //dishes.add(new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av")));
            linearLayout.addView(new DishView(this, dishes.get(i)));
        }
        progress.hide();
    }

}
