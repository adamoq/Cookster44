package com.example.adamo.cookster;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;

import org.json.JSONObject;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, "api/resproducts/");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        progress.dismiss();

    }

    @Override
    protected void renderData(JSONObject data) {

    }
}
