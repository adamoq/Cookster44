package com.example.adamo.cookster;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by adamo on 07.02.2018.
 */

public class SettingsActivity extends BaseActivity {
    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(getApplicationContext(), "api/products/");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            //readStream(data);
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

        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks).forceLoad();
        SharedPreferences settings = getSharedPreferences("login", 0);
        ((TextView) findViewById(R.id.user_id)).setText("#" + settings.getString("id", null));
        ((TextView) findViewById(R.id.user_name)).setText(settings.getString("name", null));
        ((TextView) findViewById(R.id.user_system)).setText("Android v. " + android.os.Build.VERSION.SDK_INT + " (Zalecana wersja API > 26)");
        if (settings.getString("phonenumber", null) != null)
            ((TextView) findViewById(R.id.user_phone)).setText(settings.getString("phonenumber", null));


    }
}
