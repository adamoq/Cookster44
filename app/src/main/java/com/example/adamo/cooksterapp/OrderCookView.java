package com.example.adamo.cooksterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONObject;

import java.util.HashMap;


public class OrderCookView extends LinearLayout {
    private OrderCookModel order;
    private LoaderManager loaderManager;
    private AlertDialog dialog;
    Context baseContext;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            HashMap<String, Object> map = new HashMap<>();
            if (order.getState().equals("0")) map.put("state", "1");
            if (order.getState().equals("1")) map.put("state", "2");
            return new BaseLoader(getContext(), "api/cooktasks/" + order.getId() + "/", map, "PUT");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                Log.d("K", "TRETE" + data);
                if (data.has("code") || data.has("cook") || data.has("model")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.order_successful), Toast.LENGTH_LONG).show();
                    ((OrdersCookActivity) baseContext).reloadOrders();
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
        this.baseContext = context;
        this.loaderManager = loaderManager;
        inflate(getContext(), R.layout.activity_orders_cook_element, this);
        ((TextView) findViewById(R.id.order_id)).setText("" + order.getId());
        ((TextView) findViewById(R.id.order_products)).setText("KLIK KLIK BEnG");
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
        ((TextView) view2.findViewById(R.id.order_priority)).setText(order.getPriority());
        ((TextView) view2.findViewById(R.id.order_status)).setText(order.getState());
        ((TextView) view2.findViewById(R.id.order_comment)).setText(order.getComment());
        view2.findViewById(R.id.order_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getState().equals("2"))
                    Toast.makeText(getContext(), "Status zadania jest już ukończony.", Toast.LENGTH_LONG).show();
                else changeTaskStatus();
                dialog.dismiss();
            }
        });


        LinearLayout linearLayout = view2.findViewById(R.id.order_products);
        for (HashMap<String, String> order : order.getProducts()) {
            linearLayout.addView(new ProductPopupView(view.getContext(), order, loaderManager));
        }
        builder.setView(view2);
        dialog = builder.create();
        dialog.show();
        return dialog;

    }

    private void changeTaskStatus() {


        if (loaderManager.getLoader(2) != null)
            loaderManager.restartLoader(2, null, OrderCallbacks).forceLoad();
        else loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
    }



}
