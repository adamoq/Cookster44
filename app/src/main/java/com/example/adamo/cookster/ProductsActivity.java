package com.example.adamo.cookster;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ProductsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_products);
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks).forceLoad();



    }


    private void readStream(JSONObject obj) {

        try {
            JSONArray arr = obj.getJSONArray("objects");
            LinearLayout linear = (LinearLayout) this.findViewById(R.id.linear_products);
            for (int i = 0; i < arr.length(); i++) {
                linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new ProductsLoader(getApplicationContext(),"api/products/");
        }
        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            progress.dismiss();
        }
        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };
}
