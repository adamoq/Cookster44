package com.example.adamo.cooksterapp;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;


public class OrderWaiterAddProductPopupElement extends LinearLayout {


    String productName;
    String productUnit;
    String url;
    EditText editText;
    Double price, defaultPrice;

    public OrderWaiterAddProductPopupElement(Context context, String productName, String productUnit, String url, Double price, Double defaultPrice) {
        super(context);
        inflate(getContext(), R.layout.activity_order_cook_add_popup_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        this.productUnit = productUnit;
        this.productName = productName;
        this.url = url;
        this.price = price;
        this.defaultPrice = defaultPrice;
        ((TextView) findViewById(R.id.order_product_currency)).setText(" x " + price + " " + productUnit);
        editText = findViewById(R.id.order_product_count);/*
        ((Button) findViewById(R.id.add_progress)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCount(Math.round(getCount())+1);
            }
        });
        ((Button) findViewById(R.id.rem_progress)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCount(Math.round(getCount())-1);
            }
        });*/
    }

    public Double getDefaultPrice() {
        return defaultPrice;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double productId) {
        this.price = productId;
    }

    public Float getCount() {
        return Float.parseFloat(editText.getText().toString());
    }

    public String getProductName() {
        return productName;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public String getUrl() {
        return url;
    }

    public void progressCount(int x) {
        editText.setText("" + x);
    }
}
