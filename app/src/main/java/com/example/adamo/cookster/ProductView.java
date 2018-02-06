package com.example.adamo.cookster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by adamo on 04.02.2018.
 */

public class ProductView extends LinearLayout {
    public ProductView(Context context, String title) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setMinimumHeight(50);
        this.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(context);
        tv.setText(title);
        LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2);
        tv.setLayoutParams(params);
        SeekBar sb = new SeekBar(context);
        sb.setMax(2);
        sb.setProgress(1);
        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3);
        sb.setLayoutParams(params);
        sb.setMinimumWidth(120);
        this.addView(tv);
        this.addView(sb);
        params = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 150);
        this.setLayoutParams(params);
        LayerDrawable bottomBorder = getBorders(
                Color.alpha(R.color.colorBackground), // Background color
                Color.alpha(R.color.colorBorder), // Border color
                0, // Left border in pixels
                0, // Top border in pixels
                0, // Right border in pixels
                3 // Bottom border in pixels
        );

        // Finally, apply the drawable as text view background
        this.setBackground(bottomBorder);
    }
    protected LayerDrawable getBorders(int bgColor, int borderColor,
                                       int left, int top, int right, int bottom){
        // Initialize new color drawables
        ColorDrawable borderColorDrawable = new ColorDrawable(borderColor);
        ColorDrawable backgroundColorDrawable = new ColorDrawable(bgColor);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                backgroundColorDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                left, // Number of pixels to add to the left bound [left border]
                top, // Number of pixels to add to the top bound [top border]
                right, // Number of pixels to add to the right bound [right border]
                bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }
}
