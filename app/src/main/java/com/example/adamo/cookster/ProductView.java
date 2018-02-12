package com.example.adamo.cookster;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 04.02.2018.
 */

public class ProductView extends LinearLayout {
    public ProductView(Context context, String title, String av) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linker = new LinearLayout(context);
        linker.setOrientation(LinearLayout.HORIZONTAL);
        linker.setMinimumHeight(50);
        linker.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(context);
        tv.setText(title);
        LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
        tv.setLayoutParams(params);
        linker.addView(tv);
        ImageView i = new ImageView(context);
        switch (av) {
            case "0":
                i.setImageResource(R.drawable.ic_state_bad);
                break;
            case "1":
                i.setImageResource(R.drawable.ic_state_middle);
                break;
            case "2":
                i.setImageResource(R.drawable.ic_state_ok);
                break;
        }

        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        i.setLayoutParams(params);
        linker.addView(i);
        final ImageView arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.ic_arrow_drop);
        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        arrow.setLayoutParams(params);
        linker.addView(arrow);
        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 150);
        linker.setLayoutParams(params);

        this.setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element));
        final View content = this;
        final int av2 = Integer.parseInt(av);
        linker.setOnClickListener(new OnClickListener() {
            private boolean ifNotSeen = true;
            private ProductExpandView productExpandView;

            @Override
            public void onClick(View view) {
                if (ifNotSeen) {
                    ifNotSeen = false;
                    productExpandView = new ProductExpandView(content.getContext(), av2);
                    ((LinearLayout) content).addView(productExpandView);
                    arrow.setImageResource(R.drawable.ic_arrow_drop_up);
                    content.setBackgroundColor(Color.rgb(230, 230, 230));
                } else {
                    ifNotSeen = true;
                    ((LinearLayout) content).removeView(productExpandView);
                    arrow.setImageResource(R.drawable.ic_arrow_drop);
                    content.setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element));
                }
            }
        });

        this.addView(linker);

    }
}
