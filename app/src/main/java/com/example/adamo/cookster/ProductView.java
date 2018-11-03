package com.example.adamo.cookster;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 04.02.2018.
 */

public class ProductView extends LinearLayout {
    public ProductView(Context context, String title, String av, String position, int id, final LoaderManager loaderManager) {
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
        tv.setTextSize(18);
        final Typeface regular = ResourcesCompat.getFont(context, R.font.roboto_light);
        tv.setTypeface(regular);
        tv.setPadding(30, 0, 0, 0);
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

        this.setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element));

        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 150);
        linker.setLayoutParams(params);

        if (position.equals("Kucharz")) {
            final ImageView arrow = new ImageView(context);
            arrow.setImageResource(R.drawable.ic_arrow_drop);
            params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            arrow.setLayoutParams(params);
            linker.addView(arrow);
            final View content = this;
            final int av2 = Integer.parseInt(av);
            final int id2 = id;
            Log.d("XDD2", "x" + id2 + " x " + id);
            linker.setOnClickListener(new OnClickListener() {
                private boolean ifNotSeen = true;
                private ProductExpandView productExpandView;

                @Override
                public void onClick(View view) {
                    if (ifNotSeen) {
                        ifNotSeen = false;

                        productExpandView = new ProductExpandView(content.getContext(), av2, id2, loaderManager);
                        ((LinearLayout) content).addView(productExpandView);


                        //content.animate().scaleY(+1.5f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
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

        }
        this.addView(linker);
    }
}
