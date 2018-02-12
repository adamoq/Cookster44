package com.example.adamo.cookster;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by adamo on 07.02.2018.
 */

public class ProductExpandView extends LinearLayout {
    public ProductExpandView(Context context, int av) {
        super(context);
        this.setOrientation(HORIZONTAL);
        TextView tv = new TextView(context);
        tv.setText("Dostępność produktu");
        LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3);
        tv.setLayoutParams(params);
        SeekBar sb = new SeekBar(context);
        sb.setMax(2);
        sb.setProgress(av);
        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
        sb.setLayoutParams(params);
        sb.setMinimumWidth(120);
        this.addView(tv);
        this.addView(sb);
        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 150);
        this.setLayoutParams(params);
    }
}
