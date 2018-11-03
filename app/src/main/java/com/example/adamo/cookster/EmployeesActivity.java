package com.example.adamo.cookster;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class EmployeesActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_products);
        super.onCreate(savedInstanceState, "api/resemployees/");
    }

    protected void renderData(JSONObject obj) throws JSONException {
        JSONArray arr = obj.getJSONArray("objects");
        LinearLayout linear = this.findViewById(R.id.linear_products);
        for (int i = 0; i < arr.length(); i++) {
            linear.addView(new EmployeeView(this, arr.getJSONObject(i).getString("name") + " " + arr.getJSONObject(i).getString("surname"), arr.getJSONObject(i).getString("position"), arr.getJSONObject(i).getString("phonenumber")));
        }
    }
}
