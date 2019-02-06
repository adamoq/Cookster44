package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

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
            progress.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.show();
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

                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Hasło nie zostało zmienione", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
            progress.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.show();
        }
    };
    private String image;
    private android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject> ImageCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            JSONObject data = new JSONObject();
            try {
                data.put("image", image);
                data.put("login", settings.getString("login", null));
                Log.d("SIZE", "image" + image.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new BaseLoader(SettingsActivity.this, "mobileapi/img/", data, "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (!data.has("error")) {
                    //settings.edit().putString("password", passwordNew.getText().toString()).commit();
                    settings.edit().putString("avatar", image);
                    byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                    ((ImageView) findViewById(R.id.menu_avatar)).setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    Toast.makeText(getBaseContext(), "Pomyślnie zaktualizowano zdjęcie w bazie.", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getBaseContext(), data.getString("error"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(SettingsActivity.this, "Hasło nie zostało zmienione", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
            progress.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.show();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        SharedPreferences settings = getSharedPreferences("login", 0);
        super.onCreate(savedInstanceState, "api/resemployees/" + settings.getString("id", null));

    }

    protected void renderData(JSONObject data) throws JSONException {

        super.renderData(data, R.id.nav_settings);
        if (data.has("error")) return;
        final String number;
        if (data.getString("phonenumber").isEmpty()) number = "Ustaw numer";
        else number = data.getString("phonenumber");
        settings = getSharedPreferences("login", 0);
        ((TextView) findViewById(R.id.user_id)).setText("#" + settings.getString("id", null));
        ((TextView) findViewById(R.id.user_name)).setText(settings.getString("name", null));
        ((TextView) findViewById(R.id.user_system)).setText("Android v. " + android.os.Build.VERSION.SDK_INT);
        ((Button) findViewById(R.id.user_phone)).setText(number);


        findViewById(R.id.user_phone).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                AlertDialog dialog = openPhoneDialog(view);
                if (number.charAt(0) != 'U') phone.setText(number);
            }
        });
        findViewById(R.id.user_avatar).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 2);

            }
        });
        findViewById(R.id.user_password).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                AlertDialog dialog = openPasswordDialog(view);

            }
        });
        progress.hide();
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        // super.onActivityResult(reqCode, resultCode, data);
        try {
            Log.d("reqCode", "reqCode" + reqCode);
            if (reqCode == 2) {
                if (resultCode == RESULT_OK) {
                    Intent cropIntent = new Intent("com.android.camera.action.CROP")
                            .setDataAndType(data.getData(), "image/*")
                            .putExtra("crop", true)
                            .putExtra("aspectX", 1)
                            .putExtra("aspectY", 1)
                            .putExtra("outputX", 130)
                            .putExtra("outputY", 130)
                            .putExtra("return-data", true);
                    startActivityForResult(cropIntent, 3);
                } else {
                    Toast.makeText(SettingsActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("CROPED", "JEST GIT");
                // get the cropped bitmap
                Bitmap selectedBitmap = data.getExtras().getParcelable("data");

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                image = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

                LoaderManager loaderManager = getSupportLoaderManager();
                if (loaderManager.getLoader(3) != null)
                    loaderManager.restartLoader(3, null, ImageCallbacks).forceLoad();
                else loaderManager.initLoader(3, null, ImageCallbacks).forceLoad();
                /// getSupportLoaderManager().initLoader(3, null, ImageCallbacks).forceLoad();


            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
    private AlertDialog openPhoneDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        View view2 = View.inflate(SettingsActivity.this, R.layout.activity_settings_popup_phonenumber, null);
        phone = view2.findViewById(R.id.popup_phonenumber);

        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (phone.getText().length() > 8 && phone.getText().length() < 11) {
                            LoaderManager loaderManager = getSupportLoaderManager();
                            if (loaderManager.getLoader(1) != null)
                                loaderManager.restartLoader(1, null, ResetPhoneCallbacks).forceLoad();
                            else loaderManager.initLoader(1, null, ResetPhoneCallbacks).forceLoad();
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