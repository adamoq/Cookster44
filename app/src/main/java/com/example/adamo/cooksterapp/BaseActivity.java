package com.example.adamo.cooksterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progress;
    SharedPreferences settings;
    private String dir;
    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(BaseActivity.this, dir);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                renderData(data);
            } catch (Exception e) {
                messageBox(BaseActivity.this, "Błąd przy połączeniu z serwerem", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };
    private String position;

    protected void onCreate(Bundle savedInstanceState, String dir) {
        progress = ProgressDialog.show(this, "Proszę czekać", "Pobieram dane z serwera...");
        super.onCreate(savedInstanceState);
        this.dir = dir;

        NavigationView navigationView = findViewById(R.id.nav_view);
        //  navigationView.getMenu().clear();
        // navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        settings = getSharedPreferences("login", 0);
        try {

            //((TextView)navigationView.getHeaderView(0).findViewById(R.id.logo)).
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(settings.getString("name", "null"));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.position)).setText(settings.getString("position", "null"));
        } catch (Exception e) {
            messageBox(getBaseContext(), "errorMadafaka!", e.getMessage());
            e.printStackTrace();
        }
        position = settings.getString("position", "null");
        if (position.equals("Dostawca")) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_orders_add).setVisible(false);
        }
        reloadActivity();
    }

    void reloadActivity() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(1) != null)
            loaderManager.restartLoader(1, null, mLoaderCallbacks).forceLoad();
        else loaderManager.initLoader(1, null, mLoaderCallbacks).forceLoad();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_products:
                intent = new Intent(this, ProductsActivity.class);
                break;
            case R.id.nav_emloyees:
                intent = new Intent(this, EmployeesActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.nav_dishes:
                intent = new Intent(this, DishActivity.class);
                break;
            case R.id.nav_orders:
                intent = new Intent(this, OrdersCookActivity.class);
                break;
            case R.id.nav_orders_add:
                if (position.equals("Kucharz"))
                    intent = new Intent(this, OrderCookAddActivity.class);
                else intent = new Intent(this, OrderWaiterAddActivity.class);
                break;
            case R.id.nav_logout:
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getSharedPreferences("login", 0).edit().remove("name").remove("login").remove("password").commit();
                startActivity(intent);
                finish();
                break;
            default:
                intent = new Intent(this, OrdersCookActivity.class);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    protected void renderData(JSONObject data) throws JSONException {
        if (data.has("error")) messageBox(this, "Błąd", data.getString("error"));
    }

    protected void messageBox(Context context, String method, String message) {
        Log.d("ex", "EXCEPTION: " + method + message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    protected JSONObject getJsonObject(HashMap<String, Object> args) {
        JSONObject jsonObject = new JSONObject();
        Iterator iterator = args.entrySet().iterator();
        try {
            while (iterator.hasNext()) {
                Map.Entry mentry2 = (Map.Entry) iterator.next();
                if (mentry2.getKey().equals("provider") || mentry2.getKey().equals("waiter")) {
                    jsonObject.put((String) mentry2.getKey(), "/api/resemployees/" + mentry2.getValue() + "/");
                } else jsonObject.put((String) mentry2.getKey(), mentry2.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected JSONObject getJsonObject(ArrayList<HashMap> args) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objects", args);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected JSONArray getJsonArray(ArrayList<HashMap> args) {
        JSONArray jsonObject = new JSONArray();
        jsonObject.put(args);


        return jsonObject;
    }
}
