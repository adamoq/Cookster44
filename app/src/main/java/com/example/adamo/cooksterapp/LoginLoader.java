package com.example.adamo.cooksterapp;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

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
            client.setConnectTimeout(3500);
            client.setReadTimeout(3500);
            in = new BufferedInputStream(client.getInputStream());
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (!response.toString().equals("false") && !response.toString().equals("False")) {
                JSONArray array = new JSONArray(response.toString());
                obj = array.getJSONObject(0).getJSONObject("fields");
                obj.put("id", "" + (array.getJSONObject(0)).getInt("pk"));

                if (array.length() > 1) {
                    JSONObject resdeatails = array.getJSONObject(1);
                    obj.put("currency", "" + resdeatails.getJSONObject("fields").getInt("default_currency"));
                    obj.put("lang", "" + resdeatails.getJSONObject("fields").getInt("default_lang"));
                    obj.put("resname", "" + resdeatails.getJSONObject("fields").getString("name"));
                    obj.put("takeaway", "" + resdeatails.getJSONObject("fields").getString("takeaway"));
                    obj.put("supply", "" + resdeatails.getJSONObject("fields").getString("supply"));
                }


            } else if (response.toString().equals("False")) {
                obj = new JSONObject();
                obj.put("error", "Wpisane dane są niepoprawne");
            }
            if (reader != null) reader.close();

        } catch (UnknownHostException e) {
            obj = new JSONObject();
            obj.put("error", "Brak połączenia z internetem");
            client.disconnect();

        } catch (Exception e) {
            e.printStackTrace();

            obj = new JSONObject();
            try {
                obj.put("error", e.getMessage());
                client.disconnect();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            client.disconnect();
            return obj;
        }

    }
}
