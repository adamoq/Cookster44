package com.example.adamo.cookster;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, "api/products/");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv = findViewById(R.id.welcome_login);
        tv.setText(tv.getText() + getSharedPreferences("login", 0).getString("login", null));
        tv = findViewById(R.id.welcome_name);
        tv.setText(tv.getText() + getSharedPreferences("login", 0).getString("name", null));
        progress.dismiss();
        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.open_drawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
    }
}
