package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;

import com.example.adamo.cookster.R;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class OrdersCookChoice extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choice);
        super.onCreate(savedInstanceState, "");
        //super.onCreate(savedInstanceState);

        findViewById(R.id.orderwaiters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OrdersWaiterActivity.class);

                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });
        findViewById(R.id.orderscooks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OrdersCookActivity.class);

                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });


        progress.dismiss();


    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
    }
}
