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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by adamo on 14.02.2018.
 */

public class DishPopupView extends LinearLayout {
    private String taskId;
    private LoaderManager loaderManager;
    private AlertDialog dialog;
    private String comment, count, method;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pk", taskId);
            if (method.equals("PUT")) {
                map.put("count", count);
                if (!comment.isEmpty())
                    map.put("comment", comment);
            }

            return new BaseLoader(getContext(), "api/waiterorders/" + taskId + "/", map, method);
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

    public DishPopupView(Context context, final HashMap<String, String> dish, LoaderManager loaderManager) {
        super(context);
        inflate(getContext(), R.layout.waiterorder_popup_dish, this);
        this.loaderManager = loaderManager;
        this.taskId = dish.get("id");
        String name = dish.get("count") + " x " + dish.get("name");
        TextView dishText = findViewById(R.id.name);
        dishText.setText(name);
        if (dish.get("comment") != null) {
            ((TextView) findViewById(R.id.comment)).setText(dish.get("comment"));
            findViewById(R.id.comment).setVisibility(LinearLayout.VISIBLE);
        }
        dishText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(view, dish);
            }
        });


    }

    private AlertDialog openDialog(View view, HashMap<String, String> dish) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view2 = View.inflate(getContext(), R.layout.waiterorder_edit_popup_dish, null);
        final EditText dishCount = view2.findViewById(R.id.count);
        ((TextView) view2.findViewById(R.id.name)).setText(" x " + dish.get("name"));
        dishCount.setText(dish.get("count"));
        final EditText commentView = view2.findViewById(R.id.comment);
        if (dish.get("comment") != null) {
            commentView.setText(dish.get("comment"));
        }
        builder.setView(view2);

        dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!commentView.getText().toString().equals("Komentarz"))
                    comment = commentView.getText().toString();
                else comment = "";
                count = dishCount.getText().toString();
                method = "PUT";
                loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
                Log.d("OKOK", "SAVED");
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                method = "DELETE";
                loaderManager.initLoader(2, null, OrderCallbacks).forceLoad();
                Log.d("OKOK", "REMOVED");
            }
        });
        dialog.show();
        return dialog;

    }
}
