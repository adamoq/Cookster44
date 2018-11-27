package com.example.adamo.cooksterapp;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseLoader extends AsyncTaskLoader<JSONObject> {
    HashMap<String, Object> args = null;
    private String dir = "", method = "";
    JSONObject jsonObjectArg;
    JSONArray jsonArray;
    ArrayList<HashMap<String, String>> productArray;
    BaseLoader(Context context, String dir) {
        super(context);
        this.dir = dir;
    }

    BaseLoader(Context context, String dir, HashMap<String, Object> args, String method) {
        super(context);
        this.dir = dir;
        this.args = args;
        this.method = method;
    }

    BaseLoader(Context context, String dir, JSONObject jsonObject, String method) {
        super(context);
        this.dir = dir;
        this.method = method;
        this.jsonObjectArg = jsonObject;
    }

    BaseLoader(Context context, String dir, JSONArray jsonArray, String method) {
        super(context);
        this.dir = dir;
        this.method = method;
        this.jsonArray = jsonArray;
    }

    BaseLoader(Context context, String dir, ArrayList<HashMap<String, String>> productArray, String method) {
        super(context);
        this.dir = dir;
        this.method = method;
        this.productArray = productArray;
    }

    private JSONObject sendObject(JSONObject jsonObject) {
        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONObject obj = null;
        try {
            Log.d("XXXX", "ELO " + this.dir);
            client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
            client.setConnectTimeout(1000);
            client.setReadTimeout(2000);
            client.setRequestMethod(method);
            client.setRequestProperty("Content-Type", "application/json; charset=utf8");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream os = client.getOutputStream();
            Log.d("adam króól", jsonObject.toString());
            os.write(jsonObject.toString().getBytes());
            os.close();
            Log.d("xddd", "" + client.getResponseCode());
            Log.d("xddd", "" + client.getResponseMessage());
            if (client.getResponseCode() > 199 && client.getResponseCode() < 300) {
                Log.d("xddd2222", "" + client.getResponseMessage());
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
                String line = "";

                while ((line = reader.readLine()) != null) response.append(line);
                Log.d("xddd2222", "xxxx" + response.toString());
                if (response != null && !response.toString().equals("")) {
                    obj = new JSONObject(response.toString());

                } else {
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

    private JSONObject request(JSONObject jsontask) {
        JSONObject obj = sendObject(jsontask);

        if (!obj.has("resource_uri")) {
            obj = sendObject(jsontask);
            if (!obj.has("resource_uri")) {
                try {
                    obj.put("error", "error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject loadInBackground() {
        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONObject obj = null;
        ArrayList<String> ids = new ArrayList<>();
        HashMap<Integer, String> tasks = new HashMap<>();

        if (productArray != null) {
            try {
                JSONObject jsonprod;
                JSONObject jsontask = new JSONObject();
                String taskId = "";

                for (HashMap<String, String> product : productArray) {
                    jsonprod = new JSONObject();

                    if (product.containsKey("provider")) {
                        for (Map.Entry<String, String> entry : product.entrySet())
                            jsontask.put(entry.getKey(), entry.getValue());

                        if (!(jsontask == null) && jsontask.has("provider")) {
                            obj = request(jsontask);
                            taskId = obj.getString("resource_uri");
                            this.dir = "api/cookorders/";
                        }
                    } else if (product.containsKey("price_default")) {
                        for (Map.Entry<String, String> entry : product.entrySet())
                            jsontask.put(entry.getKey(), entry.getValue());

                        if (jsontask != null && jsontask.has("price_default")) {
                            obj = request(jsontask);
                            taskId = obj.getString("resource_uri");
                            int levels = Integer.parseInt(jsontask.getString("levels"));
                            this.dir = "api/waiterorderdetails/";
                            for (int i = 0; i < levels; i++) {
                                jsonprod = new JSONObject();
                                jsonprod.put("task", taskId);
                                jsonprod.put("level", "" + i);
                                obj = request(jsonprod);
                                tasks.put(i, obj.getString("resource_uri"));
                            }
                            this.dir = "api/waiterorders/";
                        }
                    } else {
                        for (Map.Entry<String, String> entry : product.entrySet())
                            jsonprod.put(entry.getKey(), entry.getValue());
                        if (jsonprod.has("level"))
                            jsonprod.put("task", tasks.get(Integer.parseInt(jsonprod.get("level").toString())));
                        else jsonprod.put("task", taskId);
                        obj = request(jsonprod);
                        ids.add(obj.getString("resource_uri"));
                    }
                }
                Log.d("xdd", jsontask.toString());


                if (ids.size() == productArray.size() + 1) {
                    Log.d("XX", "XXXXXX" + ids.toString());
                    obj = new JSONObject();//
                    obj.put("ok", "ok");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return obj;
            }
            return obj;

        } else if (args == null && jsonObjectArg == null && jsonArray == null) {
            try {
                client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
                client.setConnectTimeout(3000);
                client.setReadTimeout(3000);
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream())));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                obj = new JSONObject(response.toString());
                if (reader != null) reader.close();
            } catch (Exception e) {
                client.disconnect();
                e.printStackTrace();
                obj = new JSONObject();
                try {
                    obj.put("error", e.getMessage());

                    return obj;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } finally {
                client.disconnect();
            }

            return obj;

        } else {

            try {
                client = (HttpURLConnection) new URL(getContext().getResources().getString(R.string.app_url) + this.dir).openConnection();
                client.setConnectTimeout(4000);
                client.setReadTimeout(4000);
                client.setRequestMethod(method);
                client.setRequestProperty("Content-Type", "application/json; charset=utf8");
                client.setDoOutput(true);
                client.setDoInput(true);

                OutputStream os = client.getOutputStream();
                if (jsonArray == null) {
                    JSONObject jsonObject;
                    if (jsonObjectArg == null) jsonObject = getJsonObject();
                    else jsonObject = jsonObjectArg;
                    Log.d("xddd", "xdxdxdx" + jsonObject);
                    Log.d("XDD", jsonObject.toString());
                    os.write(jsonObject.toString().getBytes());
                } else {
                    Log.d("xddd", "xdxdxdx" + jsonArray);
                    Log.d("XDD", jsonArray.toString());
                    os.write(jsonArray.toString().getBytes());
                }





                os.close();
                Log.d("xddd", "" + client.getResponseCode());
                Log.d("xddd", "" + client.getResponseMessage());
                Log.d("xddd", client.getContent().toString());
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

    private JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Iterator iterator = args.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry2 = (Map.Entry) iterator.next();
            if (mentry2.getKey().equals("provider") || mentry2.getKey().equals("waiter"))
                jsonObject.put((String) mentry2.getKey(), "/api/resemployees/" + mentry2.getValue() + "/");
            else jsonObject.put((String) mentry2.getKey(), mentry2.getValue());
        }
        return jsonObject;
    }
}
