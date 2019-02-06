package com.example.adamo.cooksterapp;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

public class OrderCookAddProductElement extends LinearLayout {
    String productName;
    String productUnit;
    Float productCount, defaultPrice;
    Integer productId;
    Integer Id;
    TextView tv;
    String url;
    String comment = "";

    public OrderCookAddProductElement(Context context, String productName, Float count, String unit, String url, Integer id, Float defaultPrice) {
        super(context);
        this.productUnit = unit;
        this.productName = productName;
        this.productCount = count;
        this.defaultPrice = defaultPrice;
        this.url = url;
        this.productId = id;
        inflate(getContext(), R.layout.activity_order_cook_add_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        if (count > 0) ((TextView) findViewById(R.id.order_product_count)).setText("" + count);
        ((TextView) findViewById(R.id.order_product_currency)).setText(unit);
        this.tv = findViewById(R.id.order_product_count);
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {

                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                return false;
            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundColor(0);
                    return true;
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }

                return false;
            }
        });

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrderCookAddProductElement(Context context, String productName, Float count, String unit, String url, Integer id) {
        super(context);
        this.productUnit = unit;
        this.productName = productName;
        this.productCount = count;
        this.url = url;
        this.productId = id;
        inflate(getContext(), R.layout.activity_order_cook_add_product, this);
        ((TextView) findViewById(R.id.order_product_name)).setText(productName);
        if (count > 0) ((TextView) findViewById(R.id.order_product_count)).setText("" + count);
        ((TextView) findViewById(R.id.order_product_currency)).setText(unit);
        this.tv = findViewById(R.id.order_product_count);
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {

                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                return false;
            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundColor(0);
                    return true;
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }

                return false;
            }
        });
    }

    public Integer getProductId() {
        return productId;
    }

    public void settProductId(Integer id) {
        productId = id;
    }

    public String getUrl() {
        return url;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public Float getProductCount() {
        return productCount;
    }

    public Float getDefaultPrice() {
        return defaultPrice;
    }
    public void setProductCount(Float productCount) {
        this.productCount = productCount;
        tv.setText("" + productCount);
    }
}
