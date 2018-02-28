package com.example.adamo.cookster;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseLoader extends AsyncTaskLoader<JSONObject> {
    HashMap<String, String> args = null;
    private String dir = "";

    BaseLoader(Context context, String dir) {
        super(context);
        this.dir = dir;
    }

    BaseLoader(Context context, String dir, HashMap<String, String> args) {
        super(context);
        this.dir = dir;
        this.args = args;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject loadInBackground() {
        if (args == null) {
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

        } else {
            InputStream in = null;
            HttpURLConnection client = null;
            BufferedReader reader = null;
            JSONObject obj = null;
            try {
                client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
                client.setRequestMethod("PUT");
                client.setRequestProperty("Content-Type", "application/json; charset=utf8");
                //convert parameters into JSON object
                JSONObject jsonObject = new JSONObject();
                Iterator iterator = args.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry mentry2 = (Map.Entry) iterator.next();
                    jsonObject.put((String) mentry2.getKey(), mentry2.getValue());
                }
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setDoOutput(true);
                client.setDoInput(true);

                OutputStream os = client.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.close();
                Log.d("xddd", "" + client.getResponseCode());
                if (client.getResponseCode() >= 200 && client.getResponseCode() < 300) {
                    StringBuilder response = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
                    String line = "";
                    while ((line = reader.readLine()) != null) response.append(line);
                    if (response != null && !response.toString().equals(""))
                        obj = new JSONObject(response.toString());
                    else {
                        obj = new JSONObject();
                        obj.put("code", client.getResponseCode());
                    }
                    if (reader != null) reader.close();
                } else throw new Exception();

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
}
