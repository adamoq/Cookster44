package com.example.adamo.cookster;

import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adamo on 01.03.2018.
 */

public class DishCountView extends LinearLayout {
    TextView textView;
    EditText editText;
    private String url;

    public DishCountView(Context context, String name, String id) {
        super(context);
        this.url = "/api/cooktasks/" + id + "/";

        this.setOrientation(LinearLayout.HORIZONTAL);
        editText = new EditText(context);
        editText.setText("0");
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        TextView textView = new TextView(context);
        textView.setText(name);
        this.addView(editText);
        this.addView(textView);

    }

    public String getUrl() {
        return url;
    }

    public void progressCount() {
        editText.setText("" + (Integer.parseInt(editText.getText().toString()) + 1));
    }

}
