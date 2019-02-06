package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrdersWaiterActivity extends BaseActivity {
    private String provider, state = null;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {

            HashMap<String, String> map = new HashMap<>();
            String position = "cook";
            if (provider != null) {
                if (provider.equals(getResources().getString(R.string.waiter))) position = "waiter";
                if (provider.equals(getResources().getString(R.string.supplier)))
                    position = "supplier";
            }

            if (provider != null && state != null)
                if (state.equals("0"))
                    return new BaseLoader(OrdersWaiterActivity.this, "api/waitertasksget/?" + position + "=" + provider);
                else
                    return new BaseLoader(OrdersWaiterActivity.this, "api/waitertasksget" + state + "/?" + position + "=" + provider);
            else if (provider != null)
                return new BaseLoader(OrdersWaiterActivity.this, "api/waitertasksget/?" + position + "=" + provider);
            else if (state.equals("0"))
                return new BaseLoader(OrdersWaiterActivity.this, "api/waitertasksget/");
            else
                return new BaseLoader(OrdersWaiterActivity.this, "api/waitertasksget" + state + "/");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                renderData(data);
            } catch (Exception e) {
                messageBox(OrdersWaiterActivity.this, "Brak zamówień", e.getMessage());
                e.printStackTrace();
            }
            progress.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data, R.id.nav_orders);
        JSONArray arr = data.getJSONArray("objects");
        LinearLayout linearLayout = findViewById(R.id.lidear_orders_cook);
        linearLayout.removeAllViews();
        int id = Integer.parseInt(getSharedPreferences("login", 0).getString("id", null));
        JSONObject jsonObject;
        ArrayList<OrderWaiterModel> models = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            OrderWaiterModel order;
            jsonObject = arr.getJSONObject(i);


            String comment = jsonObject.getString("comment");
            String currency = jsonObject.getJSONObject("currency").getString("ab");
            String levels = jsonObject.getString("levels");
            String price = jsonObject.getString("price");
            String url = "api/waitertasks/" + jsonObject.getString("id");
            //String state = jsonObject.getString("state");
            Boolean merged = false;
            if (state.equals("2")) merged = true;
            HashMap<String, ArrayList<HashMap<String, String>>> dishes = makeOrders(jsonObject.getJSONArray("orders"), merged);

            Log.d("x", "comment" + comment);
            Log.d("x", "currency" + currency);
            Log.d("x", "levels" + levels);
            Log.d("x", "price" + price);
            Log.d("x", "url" + state);

            Log.d("x", "dishes" + dishes);
            String text;
            if (jsonObject.getJSONObject("waiter").getInt("id") == id)
                text = "";
            else
                text = arr.getJSONObject(i).getJSONObject("waiter").getString("name") + " " + arr.getJSONObject(i).getJSONObject("waiter").getString("surname");
            int xx = -1;
            for (String value : dishes.keySet()) {

                if (xx == -1) xx = Integer.parseInt(value);
                else if (xx > Integer.parseInt(value)) xx = Integer.parseInt(value);
            }
            for (String value : dishes.keySet()) {
                ArrayList<HashMap<String, String>> dish = dishes.get(value);
                String[] parts = new String[2];
                String taskId = dish.get(0).get("task__id");
                Log.d("XX", "TABLE" + jsonObject.getString("table"));
                String date = arr.getJSONObject(i).getString("created_at");
                if (jsonObject.getString("state").equals("1")) date = "Zapłacone";
                order = new OrderWaiterModel(1,//Integer.parseInt(jsonObject.getString("table")),
                        text,
                        dish,
                        price + currency,
                        date,
                        state,
                        value,
                        arr.getJSONObject(i).getString("comment"),
                        xx == Integer.parseInt(value),
                        taskId,
                        url,
                        getSharedPreferences("login", 0).getString("position", null));

                models.add(order);
            }


            //linearLayout.addView(new OrderWaiterView(this, order, getSupportLoaderManager()));


        }
        if (state.equals("0"))

            models.sort(new Comparator<OrderWaiterModel>() {
                @Override
                public int compare(OrderWaiterModel t1, OrderWaiterModel orderCookModel) {
                    if (orderCookModel.isActive() && !t1.isActive()) return 1;
                    else if (!orderCookModel.isActive() && t1.isActive()) return -1;
                    else if ((orderCookModel.isActive() && t1.isActive()) || (!orderCookModel.isActive() && !t1.isActive())) {
                        String date1 = orderCookModel.getDate();
                        String date2 = t1.getDate();
                        int hour1 = Integer.parseInt(date1.substring(0, 2));
                        int hour2 = Integer.parseInt(date2.substring(0, 2));
                        int minute1 = Integer.parseInt(date1.substring(date1.length() - 2));
                        int minute2 = Integer.parseInt(date2.substring(date2.length() - 2));
                        if (hour1 < hour2) return 1;
                        else if (hour1 > hour2) return -1;
                        else if (minute1 < minute2) return 1;
                        else if (minute1 > minute2) return -1;
                        return 0;
                    }
                    return 0;
                }
            });
        else models.sort(new Comparator<OrderWaiterModel>() {
            @Override
            public int compare(OrderWaiterModel orderCookModel, OrderWaiterModel t1) {
                String date1 = orderCookModel.getDate();
                String date2 = t1.getDate();
                if (date1.equals("Zapłacone")) return 1;
                if (date2.equals("Zapłacone")) return -1;
                int hour1 = Integer.parseInt(date1.substring(0, 2));
                int hour2 = Integer.parseInt(date2.substring(0, 2));
                int minute1 = Integer.parseInt(date1.substring(date1.length() - 2));
                int minute2 = Integer.parseInt(date2.substring(date2.length() - 2));
                if (hour1 < hour2) return 1;
                else if (hour1 > hour2) return -1;
                else if (minute1 < minute2) return 1;
                else if (minute1 > minute2) return -1;
                return 0;


            }
        });


        for (OrderWaiterModel model : models)
            linearLayout.addView(new OrderWaiterView(this, model, getSupportLoaderManager()));
        progress.hide();

    }

    private HashMap<String, ArrayList<HashMap<String, String>>> makeOrders(JSONArray dishes, Boolean merged) {
        JSONObject dish;
        String result = "";
        HashMap<String, ArrayList<HashMap<String, String>>> levels = new HashMap<>();
        HashMap<String, String> temp = null;
        ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
        for (int i = 0; i < dishes.length(); i++) {
            try {
                if (!merged) tempList = new ArrayList<>();
                temp = new HashMap<>();
                result = "";
                dish = dishes.getJSONObject(i);
                temp.put("count", dish.getString("count"));
                temp.put("name", dish.getString("dish__name"));
                temp.put("task__id", dish.getString("task__id"));
                temp.put("id", dish.getString("id"));
                result += dish.getString("count") + " x ";
                result += dish.getString("dish__name") + "\n";
                String taskid = dish.getString("task__id");

                if (dish.getString("comment") != "null")
                    temp.put("comment", dish.getString("comment"));
                //result+=dish.getString("comment")+ "\n";
                String level = dish.getString("level");
                if (!merged) {
                    if (levels.containsKey(level)) {
                        ArrayList<HashMap<String, String>> arr = levels.get(level);
                        arr.add(temp);
                        levels.put(level, arr);
                    } else {
                        ArrayList<HashMap<String, String>> arr = new ArrayList<>();
                        arr.add(temp);
                        levels.put(level, arr);
                    }
                } else {
                    tempList.add(temp);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (merged) levels.put("0", tempList);
        return levels;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_orders_container);
        final String id = getSharedPreferences("login", 0).getString("id", null);
        super.onCreate(savedInstanceState, "api/waitertasksget/?waiter=" + id);


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.orders_av, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = "" + i;
                reloadOrders();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ((ToggleButton) findViewById(R.id.toggleButton)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) provider = null;
                else provider = id;
                reloadOrders();
            }
        });
    }

    private void reloadOrders() {
        progress.show();
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(3) != null)
            loaderManager.restartLoader(3, null, OrderCallbacks).forceLoad();
        else loaderManager.initLoader(3, null, OrderCallbacks).forceLoad();

    }
}
