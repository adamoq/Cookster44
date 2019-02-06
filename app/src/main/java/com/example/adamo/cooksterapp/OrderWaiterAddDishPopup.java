package com.example.adamo.cooksterapp;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;


public class OrderWaiterAddDishPopup extends LinearLayout {

    public OrderWaiterAddDishPopup(Context context, String lang, String name, String desc) {
        super(context);
        inflate(getContext(), R.layout.waiterorder_popup_dish_trans, this);
        ((TextView) findViewById(R.id.lang)).setText("Język " + lang);
        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.desc)).setText("Opis: " + desc);

    }
}

