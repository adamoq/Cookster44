package com.example.adamo.cooksterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    String login, password = "";

    ProgressDialog progress;
    Context context;
    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress = ProgressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_desc));

            return new LoginLoader(getApplicationContext(), "mobileapi/?login=" + login + "&password=" + password);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (data.has("error"))
                    messageBox(LoginActivity.this, "Niepowodzenie w logowaniu", data.getString("error"));
                else readStream(data);
            } catch (JSONException e) {
                e.printStackTrace();
                messageBox(LoginActivity.this, "Niepowodzenie w logowaniu", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                messageBox(LoginActivity.this, "Niepowodzenie w logowaniu", e.getMessage());
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };

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

    private void readStream(JSONObject obj) throws JSONException {
        if (obj.has("error")) messageBox(this, "Niepowodzenie w logowaniu", obj.getString("error"));
        if (obj == null) showError();
        else {
            SharedPreferences.Editor editor = getSharedPreferences("login", 0).edit();
            editor.putString("name", obj.getString("name") + " " + obj.getString("surname"))
                    .putString("login", obj.getString("login"))
                    .putString("id", obj.getString("id"))
                    .putString("password", obj.getString("password"))
                    .putString("currency", obj.getString("currency"))
                    .putString("lang", obj.getString("lang"))
                    .putString("resname", obj.getString("password"))
                    .putString("supply", obj.getString("supply"))
                    .putString("takeaway", obj.getString("takeaway"));
            if (obj.getString("avatar") != null)
                editor.putString("avatar", obj.getString("avatar"));
            if (obj.has("phonenumber"))
                editor.putString("phonenumber", obj.getString("phonenumber"));
            switch (obj.getString("position")) {
                case "0":
                    editor.putString("position", getResources().getString(R.string.waiter));
                    break;
                case "1":
                    editor.putString("position", getResources().getString(R.string.cook));
                    break;
                case "2":
                    editor.putString("position", getResources().getString(R.string.provider));
                    break;
                case "3":
                    editor.putString("position", getResources().getString(R.string.supplier));
                    break;
            }
            editor.commit();
            Intent intent = new Intent(this, DishActivity.class);
            startActivity(intent);
            startService(new Intent(this, BackgroundService.class));

        }

    }

    private void showLoginPanel() {
        ((LinearLayout) findViewById(R.id.login_linear_logged_container)).removeAllViews();
        findViewById(R.id.login_linear).setVisibility(View.VISIBLE);
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
        Button button = findViewById(R.id.log_as);
        button.setText(button.getText() + settings.getString("name", "null"));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login = settings.getString("login", "null");
                password = settings.getString("password", "null");
                makeConnection();
            }
        });
        findViewById(R.id.change_account).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showLoginPanel();
            }
        });

    }

    private void makeConnection() {
        if (login.length() < 3 || password.length() < 3) {
            showError();
        } else {
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager.getLoader(0) != null)
                loaderManager.restartLoader(0, null, mLoaderCallbacks).forceLoad();
            else loaderManager.initLoader(0, null, mLoaderCallbacks).forceLoad();
        }
    }

    private void showError() {
        TextView tv = findViewById(R.id.loginerror);
        if (tv.getVisibility() == View.INVISIBLE) tv.setVisibility(TextView.VISIBLE);
    }

    private void messageBox(Context context, String method, String message) {
        Log.d("ex", "EXCEPTION: " + method + message);

        Typeface font = ResourcesCompat.getFont(context, R.font.roboto_light);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        //messageBox.setCustomTitle(textView);
        messageBox.setNeutralButton("OK", null);
        AlertDialog dialog = messageBox.show();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTypeface(font);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
