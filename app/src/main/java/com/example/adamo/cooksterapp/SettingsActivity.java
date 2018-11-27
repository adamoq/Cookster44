package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adamo on 07.02.2018.
 */
@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsActivity extends BaseActivity {
    private EditText phone, passwordOld, passwordNew;
    private SharedPreferences settings;
    private android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject> ResetPhoneCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            return new LoginLoader(SettingsActivity.this, "mobilereset/phone/?login=" + settings.getString("login", null) + "&password=" + settings.getString("password", null) + "&phonenumber=" + phone.getText());
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (data.has("name")) {

                    Toast.makeText(getBaseContext(), "Pomyślnie zaktualizowano numer w bazie.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Nie udało się zmienić numeru telefonu", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };
    private android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject> ResetPasswordCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            return new LoginLoader(SettingsActivity.this, "mobilereset/password/?login=" + settings.getString("login", null) + "&passwordOld=" + passwordOld.getText() + "&passwordNew=" + passwordNew.getText());
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (data.has("name")) {
                    settings.edit().putString("password", passwordNew.getText().toString()).commit();
                    Toast.makeText(getBaseContext(), "Pomyślnie zaktualizowano hasło w bazie.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Hasło nie zostało zmienione", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        SharedPreferences settings = getSharedPreferences("login", 0);
        super.onCreate(savedInstanceState, "api/resemployees/" + settings.getString("id", null));
    }

    protected void renderData(JSONObject data) throws JSONException {
        final String number;
        if (data.getString("phonenumber").isEmpty()) number = "Ustaw numer";
        else number = data.getString("phonenumber");
        settings = getSharedPreferences("login", 0);
        ((TextView) findViewById(R.id.user_id)).setText("#" + settings.getString("id", null));
        ((TextView) findViewById(R.id.user_name)).setText(settings.getString("name", null));
        ((TextView) findViewById(R.id.user_system)).setText("Android v. " + android.os.Build.VERSION.SDK_INT + " (Zalecana wersja API > 26)");
        ((Button) findViewById(R.id.user_phone)).setText(number);


        findViewById(R.id.user_phone).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                AlertDialog dialog = openPhoneDialog(view);
                if (number.charAt(0) != 'U') phone.setText(number);
            }
        });
        findViewById(R.id.user_password).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                AlertDialog dialog = openPasswordDialog(view);

            }
        });
    }

    private AlertDialog openPhoneDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        View view2 = View.inflate(SettingsActivity.this, R.layout.activity_settings_popup_phonenumber, null);
        phone = view2.findViewById(R.id.popup_phonenumber);

        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (phone.getText().length() > 8 && phone.getText().length() < 11) {
                            getSupportLoaderManager().initLoader(1, null, ResetPhoneCallbacks).forceLoad();
                        } else {
                            Toast.makeText(getBaseContext(), "Nie udało się zmienić numeru telefonu", Toast.LENGTH_LONG)
                                    .show();
                            phone.setText("");
                            phone.setHint("Zły format");
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;

    }

    private AlertDialog openPasswordDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        View view2 = View.inflate(SettingsActivity.this, R.layout.activity_settings_popup_password, null);
        passwordOld = view2.findViewById(R.id.popup_passwordOld);
        passwordNew = view2.findViewById(R.id.popup_passwordNew);
        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (passwordNew.getText().length() > 6 && passwordNew.getText().length() < 15) {
                            getSupportLoaderManager().initLoader(2, null, ResetPasswordCallbacks).forceLoad();
                        } else {
                            Toast.makeText(getBaseContext(), "Wpisane dane nie są poprawne", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;

    }
}