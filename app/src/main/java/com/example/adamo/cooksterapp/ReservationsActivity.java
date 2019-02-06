package com.example.adamo.cooksterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.LinearLayout;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@SuppressLint("MissingSuperCall")
@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationsActivity extends BaseActivity {


    private ArrayList<OrderWaiterModel> models;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, "api/reservations/");

        progress.hide();


    }

    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data, R.id.nav_orders);
        JSONArray arr = data.getJSONArray("objects");
        final LinearLayout linearLayout = findViewById(R.id.lidear_orders_cook);
        linearLayout.removeAllViews();
        int id = Integer.parseInt(getSharedPreferences("login", 0).getString("id", null));
        JSONObject jsonObject;
        final ArrayList<OrderWaiterModel> models = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            OrderWaiterModel order;
            jsonObject = arr.getJSONObject(i);


            String comment = jsonObject.getString("comment");
            String currency = jsonObject.getJSONObject("currency").getString("ab");
            String levels = jsonObject.getString("levels");
            String price = jsonObject.getString("price");
            String url = "api/waitertasks/" + jsonObject.getString("id");
            //String state = jsonObject.getString("state");

            HashMap<String, ArrayList<HashMap<String, String>>> dishes = makeOrders(jsonObject.getJSONArray("orders"), true);

            Log.d("x", "comment" + comment);
            Log.d("x", "currency" + currency);
            Log.d("x", "levels" + levels);
            Log.d("x", "price" + price);

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

                String taskId = "1---";
                if (dish.size() > 0) {
                    taskId = dish.get(0).get("task__id");
                }
                Log.d("XX", "TABLE" + jsonObject.getString("table"));
                String date = arr.getJSONObject(i).getString("reservation");
                if (jsonObject.getString("state").equals("1")) date = "Zapłacone";
                order = new OrderWaiterModel(1,//Integer.parseInt(jsonObject.getString("table")),
                        text,
                        dish,
                        price + currency,
                        date,
                        "0",
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
        final Context context = getBaseContext();
        CalendarView calendarView = findViewById(R.id.calendar);


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                // add one because month starts at 0
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = eventDay.getCalendar();
                String date = simpleDateFormat.format(cal.getTime());
                linearLayout.removeAllViews();

                String month1 = "" + Calendar.MONTH + 1;
                String day1 = "" + Calendar.DATE;
                if (month1.length() == 1) month1 = "0" + month1;
                if (day1.length() == 1) day1 = "0" + day1;
                for (OrderWaiterModel model : models) {
                    //  Log.d("date check", year + "-" + month + "-" + day);
                    Log.d("date check", date);
                    Log.d("date check", model.getDate().split("T")[0]);
                    if (model.getDate().split("T")[0].equals(date))
                        linearLayout.addView(new OrderWaiterView(ReservationsActivity.this, model, getSupportLoaderManager()));
                }
            }
        });

        List<EventDay> mEventDays = new ArrayList<>();

        //   MyEventDay myEventDay = …

        //  mEventDays.add(myEventDay);
        Calendar calendar = Calendar.getInstance();
        for (OrderWaiterModel model : models) {
            String date = model.getDate().split("T")[0];
            String[] datedetails = date.split("-");
            Log.d("Sett", "xx" + Integer.parseInt(datedetails[0]) + Integer.parseInt(datedetails[1]) + Integer.parseInt(datedetails[2]));
            calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(datedetails[0]), Integer.parseInt(datedetails[1]) - 1, Integer.parseInt(datedetails[2]));
            mEventDays.add(new EventDay(calendar, R.drawable.login_gradient_linear));
            linearLayout.addView(new OrderWaiterView(this, model, getSupportLoaderManager()));
        }
        calendarView.setEvents(mEventDays);
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
}
