package com.example.adamo.cooksterapp;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.O)
abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progress;
    SharedPreferences settings;
    private String dir;
    private LoaderManager.LoaderCallbacks<JSONObject> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            //progress.show();
            return new BaseLoader(BaseActivity.this, dir);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                isDataCorrect(data, R.id.nav_dishes);
                renderData(data);
            } catch (Exception e) {
                //messageBox(BaseActivity.this, "Błąd przy połączeniu z serwerem", e.getMessage());
                e.printStackTrace();
                isDataCorrect(data, R.id.nav_dishes);
            }

        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            //progress.dismiss();
        }
    };
    private String position;

    protected void onCreate(Bundle savedInstanceState, String dir) {
        progress = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_desc));
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
            if (!settings.getString("avatar", "null").equals("null")) {
                byte[] decodedString = Base64.decode(settings.getString("avatar", "null"), Base64.DEFAULT);
                ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.menu_avatar)).setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }
        } catch (Exception e) {
            //messageBox(getBaseContext(), "errorMadafaka!", e.getMessage());
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
        //progress.hide();
        if (!dir.isEmpty()) {
            LoaderManager loaderManager = getSupportLoaderManager();

            if (loaderManager.getLoader(1) != null)
                loaderManager.restartLoader(1, null, mLoaderCallbacks).forceLoad();
            else loaderManager.initLoader(1, null, mLoaderCallbacks).forceLoad();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        progress.dismiss();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
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
            case R.id.nav_reservation:
                intent = new Intent(this, ReservationsActivity.class);
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
                if (position.equals("Kucharz"))
                    intent = new Intent(this, OrdersCookChoice.class);
                else intent = new Intent(this, OrdersWaiterActivity.class);
                break;
            case R.id.nav_orders_add:
                if (position.equals("Kucharz"))
                    intent = new Intent(this, OrderCookAddActivity.class);
                else intent = new Intent(this, OrderWaiterAddActivity.class);
                break;

            case R.id.nav_logout:
                stopService(new Intent(this, BackgroundService.class));
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getSharedPreferences("login", 0).edit().remove("name").remove("login").remove("password").commit();
                break;
            case R.id.nav_exit:
                stopService(new Intent(this, BackgroundService.class));
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(0);
                finishAffinity();
                //finish();
                return true;
            default:
                intent = new Intent(this, OrdersCookActivity.class);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    protected void renderData(JSONObject data) throws JSONException {
/*
        Log.d("DATA RENDER", "ASD"+ data.toString());
        if (data.has("error")) {
            if(data.getString("error").equals("offline")) messageBox(this, getString(R.string.error_title), getString(R.string.error_description));//checkDataForError("","", R.id.nav_dishes);
            else messageBox(this, getString(R.string.error_title), data.getString("error"));//checkDataForError("","", R.id.nav_dishes);
        }
        */
    }

    protected void renderData(JSONObject data, int activityID) throws JSONException {
        Log.d("DATA RENDER", "ASD123123" + data.toString());
        if (data.has("error")) {
            Log.d("DATA RENDER", "XXXX" + data.getString("error"));
            Log.d("DATA RENDER", "XXX12X" + data.getString("error").contains("offli"));
        }
        isDataCorrect(data, activityID);
    }

    protected boolean isDataCorrect(JSONObject data, int activityId) {
        String title = null;
        String desc = null;
        if (data.has("error")) {
            try {
                if (!data.getString("error").contains("offli")) desc = data.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent mIntent = new Intent(this, ErrorActivity.class);
            Log.d("activityId", "activityId: " + activityId);
            mIntent.putExtra("activity", activityId);
            //if (!title.isEmpty())mIntent.putExtra("title", title);
            // if (!desc.isEmpty())mIntent.putExtra("desc", desc);

            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
            startActivity(mIntent);
            return false;
        }
        //finish();
        return true;
    }

    protected void messageBox(Context context, String method, String message) {
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
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        progress.dismiss();
        super.onStop();
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
        if (isPhoneLocked)
            finish();
    }
}
