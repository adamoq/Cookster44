package com.example.adamo.cooksterapp;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;


public class OrderCookAddProductPopupElement extends LinearLayout {


    String productName;
    String productUnit;
    String url;
    EditText editText;
    Integer productId;

    public OrderCookAddProductPopupElement(Context context, String productName, String productUnit, String url, Integer id) {
        super(context);
        inflate(getContext(), R.layout.activity_order_cook_add_popup_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        this.productUnit = productUnit;
        this.productName = productName;
        this.url = url;
        this.productId = id;
        ((TextView) findViewById(R.id.order_product_currency)).setText(productUnit);

    }

    public OrderCookAddProductPopupElement(Context context, String productName, String productUnit) {
        super(context);
        inflate(getContext(), R.layout.activity_order_cook_add_popup_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        this.productUnit = productUnit;
        this.productName = productName;
        ((TextView) findViewById(R.id.order_product_currency)).setText(productUnit);
    }

    public OrderCookAddProductPopupElement(Context context, String productName, Integer id) {
        super(context);
        inflate(getContext(), R.layout.activity_order_waiter_add_popup_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        editText = findViewById(R.id.order_product_count);
        this.productName = productName;
        this.url = "/api/resdishes/" + id + "/";

    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Float getCount() {
        return Float.parseFloat(((EditText) findViewById(R.id.order_product_count)).getText().toString());
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
