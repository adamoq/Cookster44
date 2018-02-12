package com.example.adamo.cookster;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(getApplicationContext(), "api/products/");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            readStream(data);
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_products);
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks).forceLoad();


    }

    private void readStream(JSONObject obj) {

        try {
            JSONArray arr = obj.getJSONArray("objects");
            Log.d("xD", arr.toString());
            LinearLayout linear = (LinearLayout) this.findViewById(R.id.linear_products);
            for (int i = 0; i < arr.length(); i++) {
                linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("av")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
