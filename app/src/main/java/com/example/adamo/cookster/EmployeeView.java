package com.example.adamo.cookster;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 07.02.2018.
 */

public class EmployeeView extends LinearLayout {
    String phonenumber = null;

    public EmployeeView(final Context context, String name, String position, final String phonenumber) {
        super(context);
        this.phonenumber = phonenumber;
        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linker = new LinearLayout(context);
        linker.setOrientation(LinearLayout.HORIZONTAL);
        linker.setMinimumHeight(150);
        this.setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element));
        linker.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2);
        TextView tv = new TextView(context);
        switch (position) {
            case "0":
                position = "Kelner";
                break;
            case "1":
                position = "Kucharz";
                break;
            case "2":
                position = "Dostawca";
                break;
        }
        tv.setText(position);
        tv.setLayoutParams(params);
        linker.addView(tv);
        tv = new TextView(context);
        tv.setText(name);
        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 5);
        tv.setLayoutParams(params);
        tv.setTextSize(18);
        tv.setTextColor(Color.BLACK);
        linker.addView(tv);
        final ImageView i = new ImageView(context);
        i.setImageResource(R.drawable.ic_arrow_drop);
        linker.addView(i);
        final LinearLayout lay = this;
        this.setOnClickListener(new OnClickListener() {
            private boolean ifNotSeen = true;
            private EmployeeExpendView employeeExpendView = null;

            @Override
            public void onClick(View view) {

                if (ifNotSeen) {
                    ifNotSeen = false;
                    employeeExpendView = new EmployeeExpendView(context, phonenumber);
                    lay.addView(employeeExpendView);
                    i.setImageResource(R.drawable.ic_arrow_drop_up);
                    lay.setBackgroundColor(Color.rgb(230, 230, 230));

                } else {
                    lay.removeView(employeeExpendView);
                    lay.setBackground(getResources().getDrawable(R.drawable.borderd_bottom_element));
                    i.setImageResource(R.drawable.ic_arrow_drop);
                    ifNotSeen = true;
                }
            }
        });
        this.addView(linker);
    }
}
