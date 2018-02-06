package com.example.adamo.cookster;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    String login, password = "";

    ProgressDialog progress;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences settings = getSharedPreferences("login", 0);
        if (settings.getString("name", "null") != "null") {
            hideLoginPanel(settings);
        } else {
            showLoginPanel();
        }


        context = this;

    }


    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress = ProgressDialog.show(context, "Loading", "Pobieram dane z serwera...");
            return new ProductsLoader(getApplicationContext(), "mobileapi/?login=" + login + "&password=" + password);
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


    private void readStream(JSONObject obj) {
        TextView tv = (TextView) findViewById(R.id.loginerror);
        Log.d("XD", obj.toString());
        if (obj.length() == 0 && tv.getVisibility() == View.INVISIBLE)
            tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.INVISIBLE);
        Log.d("XD", obj.toString());
        /*
        JSONObject obj = null;
        String res = " ";
        try {
            JSONObject user = new JSONArray(response.toString()).getJSONObject(0).getJSONObject("fields");
            SharedPreferences settings = getSharedPreferences("login", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("name", user.getString("name") + " " + user.getString("surname"));
            editor.putString("login", user.getString("login"));
            editor.putString("password", user.getString("password"));
            switch (user.getString("position")) {
                case "0":
                    editor.putString("position", "Kelner");
                    break;
                case "1":
                    editor.putString("position", "Kucharz");
                    break;
                case "2":
                    editor.putString("position", "Dostawca");
                    break;
            }
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }
*/


    }

    private void showLoginPanel() {
        ((LinearLayout) findViewById(R.id.login_linear_logged_container)).removeAllViews();
        ((LinearLayout) findViewById(R.id.login_linear)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.login_linear)).setVisibility(View.VISIBLE);
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login = ((TextView) findViewById(R.id.login)).getText().toString();
                password = ((TextView) findViewById(R.id.password)).getText().toString();
                makeConnection();
            }
        });
    }

    private void hideLoginPanel(final SharedPreferences settings) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText(settings.getString("name", "null"));
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
        tv.setTextSize(18);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login = settings.getString("login", "null");
                password = settings.getString("password", "null");
                makeConnection();
            }
        });
        ((LinearLayout) findViewById(R.id.login_linear_logged)).addView(tv);
        ((Button) findViewById(R.id.change_account)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showLoginPanel();
            }
        });

    }

    private void makeConnection() {
        if (getSupportLoaderManager().getLoader(0) != null) getSupportLoaderManager().restartLoader(0, null, mLoaderCallbacks);
        else getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks).forceLoad();


    }
}
