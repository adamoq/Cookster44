package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class ErrorActivity extends BaseActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_error);
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, "");


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String title = (String) getIntent().getExtras().get("title");
        String desc = (String) getIntent().getExtras().get("desc");
        if (title != null) ((TextView) findViewById(R.id.error_title)).setText(title);
        if (desc != null) ((TextView) findViewById(R.id.error_desc)).setText(desc);
        progress.hide();
        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        final LinearLayout linearLayout = findViewById(R.id.open_drawer);
        final Context context = getApplicationContext();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView animationTarget = linearLayout.findViewById(R.id.imageView);

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.spin_animation);
                animationTarget.startAnimation(animation);
                int activ = (int) getIntent().getExtras().get("activity");
                Log.d("activityId", "activityId: " + activ);
                Log.d("activityId", "R.id.nav_dishes: " + R.id.nav_dishes);
                if (hasInternetConnection()) {
                    selectItem(activ);

                }

            }
        });


    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
    }

    private Boolean hasInternetConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    private void selectItem(int item) {
        // Handle navigation view item clicks here.

        String position = settings.getString("position", "null");
        Intent intent;
        switch (item) {
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
                startActivity(intent);
                finish();
                break;
            case R.id.nav_exit:
                stopService(new Intent(this, BackgroundService.class));
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            default:
                intent = new Intent(this, OrdersCookActivity.class);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        startActivity(intent);

    }
}
