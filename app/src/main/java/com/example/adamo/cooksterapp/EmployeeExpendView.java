package com.example.adamo.cooksterapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adamo.cookster.R;

/**
 * Created by adamo on 07.02.2018.
 */

public class EmployeeExpendView extends LinearLayout {
    Typeface font;

    public EmployeeExpendView(final Context context, String number) {
        super(context);
        font = ResourcesCompat.getFont(context, R.font.roboto_light);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setMinimumHeight(110);
        this.setGravity(Gravity.CENTER_VERTICAL);
        final String phone = number;
        TextView tv = new TextView(context);
        tv.setTypeface(font);

        tv.setTextSize(18);
        Drawable img = ContextCompat.getDrawable(context, R.drawable.ic_notebook);
        img.setBounds(0, 0, 60, 60);
        tv.setCompoundDrawables(img, null, null, null);

        if (!number.isEmpty()) {
            tv.setText(number);
            this.addView(tv);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setMinimumHeight(110);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setOrientation(HORIZONTAL);
            Button button = new Button(context);
            button.setText("Zadzwoń do użytkownika");
            img = ContextCompat.getDrawable(context, R.drawable.ic_call);
            img.setBounds(0, 0, 60, 60);
            button.setCompoundDrawables(img, null, null, null);
            //button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call, 0, 0, 0);
            LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            button.setLayoutParams(params);
            button.setTypeface(font);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
                }
            });
            linearLayout.addView(button);
            button = new Button(context);
            button.setTypeface(font);
            button.setLayoutParams(params);
            button.setText("Wyślij wiadomość");
            img = ContextCompat.getDrawable(context, R.drawable.ic_sms);
            img.setBounds(0, 0, 60, 60);
            button.setCompoundDrawables(img, null, null, null);
            //button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sms, 0, 0, 0);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
                }
            });
            linearLayout.addView(button);
            this.addView(linearLayout);
        } else {
            tv.setText("Brak numeru telefonu użytkownika");
            this.addView(tv);
        }

    }
}
