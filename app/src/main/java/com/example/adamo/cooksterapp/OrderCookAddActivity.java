package com.example.adamo.cooksterapp;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrderCookAddActivity extends BaseActivity {

    LinearLayout productsLinear;
    TextView products;
    LinearLayout linearLayout2;
    private ArrayList<EmployeeModel> employeesList;
    private ArrayList<HashMap<String, String>> productList;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks2 = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(OrderCookAddActivity.this, "api/resemployees/?position=2");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {

                if (data.has("error")) throw new Exception();
                else renderExternalData(data);
            } catch (Exception e) {
                messageBox(OrderCookAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(OrderCookAddActivity.this, "api/resemployees/?position=2");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {

                if (data.has("error")) throw new Exception();
                else renderData(data);
            } catch (Exception e) {
                messageBox(OrderCookAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
            getSupportLoaderManager().initLoader(3, null, OrderCallbacks2).forceLoad();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.dismiss();
        }
    };
    private LoaderManager.LoaderCallbacks<JSONObject> FormCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            return new BaseLoader(OrderCookAddActivity.this, "api/cooktasks/", generateForm(), "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {

            try {
                if (data.has("ok")) {
                    Toast.makeText(OrderCookAddActivity.this, "Pomyślnie dodano zamówienie do bazy.", Toast.LENGTH_LONG).show();
                } else throw new Exception();
            } catch (Exception e) {
                Toast.makeText(OrderCookAddActivity.this, "Nie udało się dodać zamówienai do bazy", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            progress.hide();
            //startActivity(new Intent(OrderCookAddActivity.this, OrderCookAddActivity.class));


        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };
    private NotificationCompat.Builder notification_builder;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_cook_add);
        super.onCreate(savedInstanceState, "api/products/");
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        products = findViewById(R.id.products);
        productsLinear = findViewById(R.id.order_products_selected);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductsDialog(view);
            }
        });
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formValidated()) sendForm();
                else showFormError();
            }
        });
        getSupportLoaderManager().initLoader(1, null, OrderCallbacks).forceLoad();

        //notifications
        Intent open_activity_intent = new Intent(this, OrderCookAddActivity.class);
        PendingIntent pending_intent = PendingIntent
                .getActivity(this, 0, open_activity_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification_builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cookster_orange)
                .setContentTitle("Notification Title")
                .setContentText("Notification Body")
                .setAutoCancel(true)
                .setContentIntent(pending_intent);


        NotificationManager notification_manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            //mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(this);
        }
        notification_builder.setSmallIcon(R.drawable.cookster_orange)
                .setContentTitle("Notification Title")
                .setContentText("Notification Body")
                .setAutoCancel(true)
                .setContentIntent(pending_intent);


// notificationId is a unique int for each notification that you must define
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification_builder.build());

    }

    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        Log.d("XX", "XXXX" + arr);
        productList = new ArrayList<>();
        LinearLayout linearLayout = this.findViewById(R.id.linear_products);
        HashMap<String, String> product;
        for (int i = 0; i < arr.length(); i++) {
            product = new HashMap<String, String>();
            product.put("name", arr.getJSONObject(i).getString("name"));
            product.put("url", arr.getJSONObject(i).getString("resource_uri"));
            product.put("unit", arr.getJSONObject(i).getString("unit"));
            product.put("id", "" + arr.getJSONObject(i).getInt("id"));
            Log.d("XX", "xxx" + arr.getJSONObject(i).getInt("id"));
            productList.add(product);
            Log.d("XX", "XXXX" + i);
            //linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("av")));
        }
    }

    private void renderExternalData(JSONObject data) throws JSONException {
        super.renderData(data);//renderData(data);
        Log.d("XX", "XXXX" + productList);
        Log.d("gf", "fgdfdg" + data);
        JSONArray arr = data.getJSONArray("objects");
        employeesList = new ArrayList<EmployeeModel>();

        for (int i = 0; i < arr.length(); i++) {
            employeesList.add(new EmployeeModel(arr.getJSONObject(i).getString("name") + " " + arr.getJSONObject(i).getString("surname"), arr.getJSONObject(i).getString("resource_uri")));
        }
        Spinner spinner = findViewById(R.id.spinner_employees);
        ArrayAdapter<EmployeeModel> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, employeesList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner = findViewById(R.id.spinner_priority);
        employeesList = new ArrayList<>();
        employeesList.add(new EmployeeModel("Wysoki", 0));
        employeesList.add(new EmployeeModel("Średni", 1));
        employeesList.add(new EmployeeModel("Niski", 2));
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, employeesList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

    }

    private boolean formValidated() {
        return ((TextView) findViewById(R.id.products)).getText().length() > 0;
    }

    private void showFormError() {
        findViewById(R.id.products).setBackground(getResources().getDrawable(R.drawable.error_view));
    }

    private void clearFormError() {
        findViewById(R.id.products).setBackground(null);
    }

    private void sendForm() {
        getSupportLoaderManager().initLoader(2, null, FormCallbacks).forceLoad();
    }

    private AlertDialog openProductsDialog(final View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderCookAddActivity.this);
        final View view2 = View.inflate(OrderCookAddActivity.this, R.layout.activity_order_cook_add_popup, null);
        linearLayout2 = view2.findViewById(R.id.linear_dishes);
        final AlertDialog dialog;
        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        String result = "";
                        clearFormError();
                        // productsLinear.removeAllViews();

                        products.setVisibility(View.INVISIBLE);
                        products.setTextSize(0);
                        OrderCookAddProductPopupElement productElement;
                        for (int i = 0; i < linearLayout2.getChildCount(); i++) {
                            if (linearLayout2.getChildAt(i).getClass() == OrderCookAddProductPopupElement.class) {
                                productElement = (OrderCookAddProductPopupElement) linearLayout2.getChildAt(i);
                                if (productElement.getCount() > 0) {
                                    result += productElement.getCount() + productElement.getProductName() + ", ";
                                    Log.d("XDDD", "xxx" + productElement.getProductId());
                                    OrderCookAddProductElement el = new OrderCookAddProductElement(view.getContext(), productElement.getProductName(), productElement.getCount(), productElement.getProductUnit(), productElement.getUrl(), productElement.getProductId());
                                    productsLinear.addView(el);

                                    el.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {
                                            ((AlertDialog) dialog).show();
                                            return true;
                                        }
                                    });
                                }
                            }
                        }
                        products.setText(result);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        for (int i = 0; i < productList.size(); i++) {
            HashMap<String, String> product = productList.get(i);
            OrderCookAddProductPopupElement productElement = new OrderCookAddProductPopupElement(OrderCookAddActivity.this, product.get("name"),
                    product.get("unit"), product.get("url"), Integer.parseInt(product.get("id")));
            linearLayout2.addView(productElement);
        }

        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private ArrayList<HashMap<String, String>> generateForm() {

        ArrayList<HashMap<String, String>> products = getProducts();

        if (products.isEmpty()) return null;
        else {
            Spinner spinner = findViewById(R.id.spinner_employees);
            String provider = ((EmployeeModel) spinner.getSelectedItem()).getId();
            spinner = findViewById(R.id.spinner_priority);
            int priority = ((EmployeeModel) spinner.getSelectedItem()).getResId();
            String comment = ((EditText) findViewById(R.id.comment)).getText().toString();
            HashMap<String, String> map = new HashMap<>();

            map.put("priority", "" + priority);
            map.put("provider", provider);
            map.put("cook", "/api/resemployees/" + settings.getString("id", "null") + "/");
            System.out.println("key priority: " + priority);
            System.out.println("value provider: " + provider);
            if (!comment.isEmpty()) map.put("comment", comment);
            System.out.println("key map: " + map);
            products.add(0, map);

        }

        System.out.println("key products: " + products);
        return products;

    }

    private ArrayList<HashMap<String, String>> getProducts() {
        ArrayList<HashMap<String, String>> products = new ArrayList<>();
        HashMap<String, String> ids;
        OrderCookAddProductElement productElement;

        for (int i = 0; i < productsLinear.getChildCount(); i++) {
            if (productsLinear.getChildAt(i).getClass() == OrderCookAddProductElement.class) {
                ids = new HashMap<>();
                productElement = (OrderCookAddProductElement) productsLinear.getChildAt(i);
                ids.put("product", "" + productElement.getUrl());
                ids.put("count", "" + Math.round(productElement.getProductCount()));
                products.add(ids);
            }
        }

        return products;
    }
}
