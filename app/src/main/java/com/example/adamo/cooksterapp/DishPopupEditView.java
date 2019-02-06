package com.example.adamo.cooksterapp;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

import java.util.HashMap;

/**
 * Created by adamo on 14.02.2018.
 */

public class DishPopupEditView extends LinearLayout {
    private String taskId;

    public DishPopupEditView(Context context, HashMap<String, String> dish) {
        super(context);
        inflate(getContext(), R.layout.waiterorder_edit_popup_dish, this);
        this.taskId = dish.get("id");
        String name = dish.get("count") + " x " + dish.get("name");
        TextView dishText = findViewById(R.id.dish_name);
        dishText.setText(name);
        dishText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (dish.get("comment") != null) {
            ((TextView) findViewById(R.id.comment)).setText(dish.get("comment"));
        }

    }
}
