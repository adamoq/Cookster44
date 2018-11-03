package com.example.adamo.cookster;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArrayLoader extends AsyncTaskLoader<JSONArray> {
    JSONObject array = null;
    private String dir = "";

    ArrayLoader(Context context, String dir) {
        super(context);
        this.dir = dir;
    }

    ArrayLoader(Context context, String dir, JSONObject array) {
        super(context);
        this.dir = dir;
        this.array = array;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONArray loadInBackground() {
        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONArray obj = null;
        if (array == null) {
            try {
                client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                obj = new JSONArray(response.toString());
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                obj = new JSONArray();
                try {
                    obj.put("error");
                    client.disconnect();
                    return obj;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                client.disconnect();
            }

            return obj;

        } else {

            try {
                client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json; charset=utf8");
                //convert parameters into JSON object
               /* JSONObject jsonObject = new JSONObject();
                Iterator iterator = args.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry mentry2 = (Map.Entry) iterator.next();
                    if (mentry2.getKey().equals("provider") || mentry2.getKey().equals("waiter"))
                        jsonObject.put((String) mentry2.getKey(), "/api/resemployees/" + mentry2.getValue() + "/");
                    else jsonObject.put((String) mentry2.getKey(), mentry2.getValue());
                }*/
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setDoOutput(true);
                client.setDoInput(true);

                OutputStream os = client.getOutputStream();
                Log.d("XDD", array.toString());
                os.write(array.toString().getBytes());
                os.close();

                if (client.getResponseCode() >= 200 && client.getResponseCode() < 300) {
                    StringBuilder response = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
                    String line = "";
                    while ((line = reader.readLine()) != null) response.append(line);
                    if (response != null && !response.toString().equals(""))
                        obj = new JSONArray(response.toString());
                    else {
                        obj = new JSONArray();
                        obj.put(client.getResponseCode());
                    }
                    if (reader != null) reader.close();
                } else throw new Exception();

            } catch (Exception e) {
                e.printStackTrace();
                obj = new JSONArray();
                try {
                    obj.put("error");
                    client.disconnect();
                    return obj;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                client.disconnect();
            }

            return obj;

        }
    }
}


