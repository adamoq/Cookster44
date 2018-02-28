package com.example.adamo.cookster;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 07.02.2018.
 */

public class EmployeeExpendView extends LinearLayout {
    public EmployeeExpendView(final Context context, String number) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setMinimumHeight(110);
        this.setGravity(Gravity.CENTER_VERTICAL);
        final String phone = number;
        TextView tv = new TextView(context);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_iphone_black_24dp, 0, 0, 0);
        if (!number.isEmpty()) {
            tv.setText(number);
            this.addView(tv);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setMinimumHeight(110);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setOrientation(HORIZONTAL);
            Button button = new Button(context);
            button.setText("Zadzwoń do użytkownika");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call, 0, 0, 0);
            LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            button.setLayoutParams(params);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
                }
            });
            linearLayout.addView(button);
            button = new Button(context);
            button.setLayoutParams(params);
            button.setText("Wyślij wiadomość");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chat_black_24dp, 0, 0, 0);
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
