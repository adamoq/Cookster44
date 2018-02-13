package com.example.adamo.cookster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adamo on 07.02.2018.
 */
@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        SharedPreferences settings = getSharedPreferences("login", 0);
        super.onCreate(savedInstanceState, "api/resemployees/" + settings.getString("id", null));
    }

    protected void renderData(JSONObject data) throws JSONException {
        String number = "Ustaw numer telefonu";
        if (data.has("phonenumber")) number = data.getString("phonenumber");
        Log.d("XDD", number);
        SharedPreferences settings = getSharedPreferences("login", 0);
        ((TextView) findViewById(R.id.user_id)).setText("#" + settings.getString("id", null));
        ((TextView) findViewById(R.id.user_name)).setText(settings.getString("name", null));
        ((TextView) findViewById(R.id.user_system)).setText("Android v. " + android.os.Build.VERSION.SDK_INT + " (Zalecana wersja API > 26)");
        ((Button) findViewById(R.id.user_phone)).setText(number);
        TelephonyManager tMgr = (TelephonyManager) getSystemService(SettingsActivity.this.TELEPHONY_SERVICE);

        final String mPhoneNumber;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
            mPhoneNumber = tMgr.getLine1Number();
        } else {
            mPhoneNumber = tMgr.getLine1Number();
        }
        ((Button) findViewById(R.id.user_phone)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = openPhoneDialog(view);
                dialog.show();
                Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_NEUTRAL);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("XDD", "XD2");
                        View inflatedView = LayoutInflater.from(v.getContext()).inflate(R.layout.activity_settings_popup1, null);
                        EditText text = (EditText) dialog.findViewById(R.id.xddd);
                        text.setText(mPhoneNumber);

                    }
                });
            }
        });
    }

    private AlertDialog openPhoneDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(R.layout.activity_settings_popup1)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNeutralButton("Pobierz", null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}