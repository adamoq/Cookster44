package com.example.adamo.cookster;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adamo on 06.02.2018.
 */

public class LoginLoader extends AsyncTaskLoader<JSONObject> {
    String dir = "";

    public LoginLoader(Context context, String dir) {
        super(context);
        this.dir = dir;
    }

    @Override
    public JSONObject loadInBackground() {


        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONObject obj = null;
        try {
            URL url = new URL(getContext().getResources().getString(R.string.app_url) + this.dir);
            client = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(client.getInputStream());
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (response.toString() != "false") {
                obj = new JSONArray(response.toString()).getJSONObject(0).getJSONObject("fields");
                obj.put("id", "" + (new JSONArray(response.toString()).getJSONObject(0)).getInt("pk"));

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.disconnect();

        }
        if (reader != null) try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
}