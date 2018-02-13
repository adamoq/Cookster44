package com.example.adamo.cookster;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class ProductsActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_products);
        super.onCreate(savedInstanceState, "api/products/");
    }

    protected void renderData(JSONObject obj) throws JSONException {
        JSONArray arr = obj.getJSONArray("objects");
        Log.d("XDD", arr.toString());
        LinearLayout linear = (LinearLayout) this.findViewById(R.id.linear_products);
        for (int i = 0; i < arr.length(); i++) {
            linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("av")));
        }
    }
}
