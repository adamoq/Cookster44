package com.example.adamo.cookster;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
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
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        dishes = new ArrayList<>();
        LinearLayout linearLayout = findViewById(R.id.linear_dishes);
        for (int i = 0; i < arr.length(); i++) {
            dishes.add(new DishModel(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getJSONObject("category").getString("name"), arr.getJSONObject(i).getString("category"), arr.getJSONObject(i).getJSONArray("products"), arr.getJSONObject(i).getString("av")));
            // linearLayout.addView(new DishView(this,new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av"))));
        }
        dishes.sort(new Comparator<DishModel>() {
            @Override
            public int compare(DishModel dishModel, DishModel t1) {
                int compareResult = dishModel.getCategory().compareToIgnoreCase(t1.getCategory());
                if (compareResult == 0)
                    return dishModel.getName().compareToIgnoreCase(t1.getName());
                else return compareResult;
            }
        });
        for (int i = 0; i < arr.length(); i++) {
            //dishes.add(new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av")));
            linearLayout.addView(new DishView(this, dishes.get(i)));
        }
    }

}
