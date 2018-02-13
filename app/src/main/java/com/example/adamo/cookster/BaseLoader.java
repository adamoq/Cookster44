package com.example.adamo.cookster;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaseLoader extends AsyncTaskLoader<JSONObject> {
    private String dir = "";
    private Context context;
    BaseLoader(Context context, String dir) {
        super(context);
        this.dir = dir;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject loadInBackground() {

        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONObject obj = null;
        try {
            client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            obj = new JSONObject(response.toString());
            if (reader != null) reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            obj = new JSONObject();
            try {
                obj.put("error", e.getMessage());
                client.disconnect();
                return obj;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            client.disconnect();
        }

        return obj;
    }

}
