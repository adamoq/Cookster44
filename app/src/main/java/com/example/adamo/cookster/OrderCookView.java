package com.example.adamo.cookster;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;


public class OrderCookView extends LinearLayout {
    private OrderCookModel order;
    private LoaderManager loaderManager;
    private AlertDialog dialog;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            HashMap<String, String> map = new HashMap<>();
            if (order.getState().equals("0")) map.put("state", "1");
            if (order.getState().equals("1")) map.put("state", "2");
            return new BaseLoader(getContext(), "api/cooktasks/" + order.getId() + "/", map);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (data.has("code")) {
                    Toast.makeText(getContext(), "Pomyślnie zaktualizowano zamówienie w bazie.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Nie udało się zaktualizować zamówienia w bazie.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            dialog.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    public OrderCookView(Context context, OrderCookModel order, LoaderManager loaderManager) {
        super(context);
        this.order = order;
        this.loaderManager = loaderManager;
        inflate(getContext(), R.layout.activity_orders_cook_element, this);
        ((TextView) findViewById(R.id.order_id)).setText("" + order.getId());
        ((TextView) findViewById(R.id.order_products)).setText(order.getProducts());
        ((TextView) findViewById(R.id.order_date)).setText(order.getDate());
        ((TextView) findViewById(R.id.order_provider)).setText(order.getProvider());
        switch (order.getPriority()) {
            case "2":
                ((ImageView) findViewById(R.id.order_priority)).setImageResource(R.drawable.ic_state_bad);
                break;
            case "1":
                ((ImageView) findViewById(R.id.order_priority)).setImageResource(R.drawable.ic_state_middle);
                break;
            case "0":
                ((ImageView) findViewById(R.id.order_priority)).setImageResource(R.drawable.ic_state_ok);
                break;
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(view);
            }
        });
    }

    private AlertDialog openDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view2 = View.inflate(getContext(), R.layout.activity_orders_cook_element_popup, null);
        ((TextView) view2.findViewById(R.id.order_products)).setText(order.getProducts());
        ((TextView) view2.findViewById(R.id.order_priority)).setText(order.getPriority());
        ((TextView) view2.findViewById(R.id.order_status)).setText(order.getState());
        ((TextView) view2.findViewById(R.id.order_comment)).setText(order.getComment());
        view2.findViewById(R.id.order_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getState().equals("2"))
                    Toast.makeText(getContext(), "Status zadania jest już ukończony.", Toast.LENGTH_LONG).show();
                else loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
            }
        });
        builder.setView(view2);
        dialog = builder.create();
        dialog.show();
        return dialog;

    }
}
