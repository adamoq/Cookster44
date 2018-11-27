package com.example.adamo.cooksterapp;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

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
            case "0":
                ((ImageView) findViewById(R.id.dish_av)).setImageResource(R.drawable.ic_state_bad);
                findViewById(R.id.activity_dishes_linear).setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element_faded));
                ((TextView) findViewById(R.id.dish_price)).setTextColor(Color.GRAY);
                break;
            case "1":
                //((ImageView) findViewById(R.id.dish_av)).setVisibility(ImageView.INVISIBLE);//setImageResource(R.drawable.ic_state_ok);
                break;
        }
    }
}
