package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONArray;


public class ProductExpandView extends LinearLayout {
    private int state, productID;
    private Context context;
    private LoaderManager loaderManager;
    private LoaderManager.LoaderCallbacks<JSONArray> ProductCallbacks = new LoaderManager.LoaderCallbacks<JSONArray>() {
        ProgressDialog progress;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONArray> onCreateLoader(int id, Bundle args) {
            progress = ProgressDialog.show(context, "Proszę czekać", "Pobieram dane z serwera...");
            progress.show();

            return new ArrayLoader(context, "mobilereset/product/?id=" + productID + "&state=" + state);

        }

        @SuppressLint("NewApi")
        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONArray> loader, JSONArray data) {
            /*try {
                renderData(data);
            } catch (Exception e) {
                messageBox(OrdersCookActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }*/

            Toast.makeText(context, "Poprawnie wykonano żadanie", Toast.LENGTH_SHORT).show();
            progress.dismiss();

            ((BaseActivity) context).reloadActivity();
        }


        public void onLoaderReset(android.support.v4.content.Loader<JSONArray> loader) {

        }
    };

    public ProductExpandView(Context context, int av, int id, LoaderManager loaderManager) {
        super(context);
        this.setOrientation(HORIZONTAL);
        this.productID = id;
        this.context = context;
        this.loaderManager = loaderManager;
        TextView tv = new TextView(context);
        tv.setText("Dostępność produktu");
        final Typeface regular = ResourcesCompat.getFont(context, R.font.roboto_light);
        tv.setTypeface(regular);
        tv.setPadding(30, 0, 0, 0);
        LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3);
        tv.setLayoutParams(params);
        SeekBar sb = new SeekBar(context);
        sb.setMax(2);
        sb.setProgress(av);
        Log.d("XD3", "x" + id);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setBackgroundColor(Color.CYAN);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                state = seekBar.getProgress();
                reloadProduct();
                seekBar.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);

        sb.setLayoutParams(params);
        sb.setMinimumWidth(120);

        this.addView(tv);
        this.addView(sb);
        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 80);
        this.setLayoutParams(params);

    }

    private void reloadProduct() {

        if (loaderManager.getLoader(2) != null)
            loaderManager.restartLoader(2, null, ProductCallbacks).forceLoad();
        else loaderManager.initLoader(2, null, ProductCallbacks).forceLoad();

    }
}
