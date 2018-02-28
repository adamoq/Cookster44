package com.example.adamo.cookster;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 14.02.2018.
 */

public class DishView extends LinearLayout {
    public DishView(Context context, DishModel dish) {
        super(context);
        inflate(getContext(), R.layout.activity_dishes_element, this);
        ((TextView) findViewById(R.id.dish_name)).setText(dish.getName());
        ((TextView) findViewById(R.id.dish_category)).setText(dish.getCategory());
        ((TextView) findViewById(R.id.dish_products)).setText(dish.getProducts());
        switch (dish.getAv()) {
            case "1":
                ((ImageView) findViewById(R.id.dish_av)).setImageResource(R.drawable.ic_state_bad);
                break;
            case "0":
                ((ImageView) findViewById(R.id.dish_av)).setImageResource(R.drawable.ic_state_ok);
                break;
        }
    }
}
