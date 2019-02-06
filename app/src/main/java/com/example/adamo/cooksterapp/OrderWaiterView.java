package com.example.adamo.cooksterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


public class OrderWaiterView extends LinearLayout {
    Context context;
    private OrderWaiterModel order;
    private LoaderManager loaderManager;
    private AlertDialog dialog;
    private String method = "PUT";
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pk", order.gettaskId());
            Log.d("XX", "trololo" + order.getState());
            if (order.getState().equals("0")) map.put("state", "1");
            if (order.getState().equals("1")) map.put("state", "2");
            if (order.getState().equals("2")) {
                map.put("state", "1");
                return new BaseLoader(getContext(), order.getUrl() + "/", map, "PUT");
            }
            return new BaseLoader(getContext(), "api/waiterorderdetails/" + order.gettaskId() + "/", map, method);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                Log.d("K", "TRETE" + data);
                if (data.has("code") || data.has("cook") || data.has("level")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.order_successful), Toast.LENGTH_LONG).show();
                } else throw new Exception();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Nie udało się zaktualizować zamówienia w bazie.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            dialog.dismiss();
            Intent newIntent = new Intent(getContext(), OrdersWaiterActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(newIntent);
            //startActivity(new Intent(OrderWaiterView.this, OrdersWaiterActivity.class));
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    public OrderWaiterView(Context context, OrderWaiterModel order, LoaderManager loaderManager) {
        super(context);
        this.context = context;
        this.order = order;
        this.loaderManager = loaderManager;
        inflate(getContext(), R.layout.activity_orders_cook_element, this);
        ((TextView) findViewById(R.id.order_id)).setText("" + order.getId());

        ((TextView) findViewById(R.id.order_products)).setText("Klik klik bang");
        ((TextView) findViewById(R.id.order_date)).setText(order.getDate());
        if (order.getProvider() == "" && !order.isActive())
            ((TextView) findViewById(R.id.order_provider)).setText(context.getResources().getString(R.string.planned));
        if (((TextView) findViewById(R.id.order_date)).getText() == "")
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
        View view2 = View.inflate(getContext(), R.layout.activity_orders_waiter_element_popup, null);
        //((TextView) view2.findViewById(R.id.order_products)).setText(order.getProducts());
        LinearLayout linearLayoutr = view2.findViewById(R.id.order_products);
        for (HashMap<String, String> dish : order.getProducts()) {
            linearLayoutr.addView(new DishPopupView(view2.getContext(), dish, loaderManager));
        }
        ((TextView) view2.findViewById(R.id.order_priority)).setText(order.getPriority());

        ((TextView) view2.findViewById(R.id.order_status)).setText(order.getLevel());
        Log.d("XXX", "xxxorder.getState()" + order.getState());
        Log.d("XXX", "xxxorder.getPosition()" + order.getPosition());
        if (order.getComment() == "null")
            ((LinearLayout) view2).removeView(view2.findViewById(R.id.linear_comment));
        else ((TextView) view2.findViewById(R.id.order_comment)).setText(order.getComment());
        if (((order.getState().equals("1") || order.getState().equals("2")) && order.getPosition().equals("Kucharz")) ||
                (order.getState().equals("0") && order.getPosition().equals("Kelner")))
            ((LinearLayout) view2).removeView(view2.findViewById(R.id.order_button));
        else if (order.isActive())
            view2.findViewById(R.id.order_button).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (order.getState().equals("2") && order.getDate() == "Zapłacone")
                        Toast.makeText(getContext(), "Status zadania jest już ukończony.", Toast.LENGTH_LONG).show();
                    else loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
                }
            });
        else
            ((LinearLayout) view2).removeView(view2.findViewById(R.id.order_button));
        builder.setView(view2);
        dialog = builder.create();
        if (order.getState().equals("0")) {
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    method = "DELETE";
                    loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
                }
            });
        }
        dialog.show();
        return dialog;

    }
}
