package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrdersCookActivity extends BaseActivity {
    private String provider, state = null;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            HashMap<String, String> map = new HashMap<>();
            if (provider != null && state != null)
                return new BaseLoader(OrdersCookActivity.this, "api/cooktasks/?cook=" + provider + "&state=" + state);
            else if (provider != null)
                return new BaseLoader(OrdersCookActivity.this, "api/cooktasks/?cook=" + provider);
            else return new BaseLoader(OrdersCookActivity.this, "api/cooktasks/?state=" + state);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                renderData(data);
            } catch (Exception e) {
                messageBox(OrdersCookActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        LinearLayout linearLayout = findViewById(R.id.lidear_orders_cook);
        linearLayout.removeAllViews();
        final SharedPreferences settings = getSharedPreferences("login", 0);
        int id = Integer.parseInt(settings.getString("id", null));
        for (int i = 0; i < arr.length(); i++) {
            OrderCookModel order;
            if (arr.getJSONObject(i).getJSONObject("provider").getInt("id") == id)
                order = new OrderCookModel(arr.getJSONObject(i).getInt("id"), "Zlecono dla Ciebie", arr.getJSONObject(i).getString("products"), arr.getJSONObject(i).getString("priority"), arr.getJSONObject(i).getString("created_at"), arr.getJSONObject(i).getString("state"), arr.getJSONObject(i).getString("comment"));
            else
                order = new OrderCookModel(arr.getJSONObject(i).getInt("id"), arr.getJSONObject(i).getJSONObject("provider").getString("name") + " " + arr.getJSONObject(i).getJSONObject("provider").getString("surname"), arr.getJSONObject(i).getString("products"), arr.getJSONObject(i).getString("priority"), arr.getJSONObject(i).getString("created_at"), arr.getJSONObject(i).getString("state"), arr.getJSONObject(i).getString("comment"));
            linearLayout.addView(new OrderCookView(this, order, getSupportLoaderManager()));
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_orders_cook);
        final SharedPreferences settings = getSharedPreferences("login", 0);
        super.onCreate(savedInstanceState, "api/cooktasks/?cook=" + settings.getString("id", null));
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.orders_av, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = "" + i;
                reloadOrders();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ((ToggleButton) findViewById(R.id.toggleButton)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) provider = null;
                else provider = settings.getString("id", null);
                reloadOrders();
            }
        });
    }

    private void reloadOrders() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(3) != null)
            loaderManager.restartLoader(3, null, OrderCallbacks).forceLoad();
        else loaderManager.initLoader(3, null, OrderCallbacks).forceLoad();

    }
}
