package com.example.adamo.cookster;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrderCookAddActivity extends BaseActivity {


    TextView products;
    private ArrayList<String> productList;
    private ArrayList<EmployeeModel> employeesList;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(OrderCookAddActivity.this, "api/resemployees/?position=2");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                renderExternalData(data);
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
    private LoaderManager.LoaderCallbacks<JSONObject> FormCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            return new BaseLoader(OrderCookAddActivity.this, "api/cooktasks/", generateForm(), "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {

            try {
                if (data.has("code")) {
                    Toast.makeText(OrderCookAddActivity.this, "Pomyślnie dodano zamówienie do bazy.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(OrderCookAddActivity.this, "Nie udało się dodać zamówienai do bazy", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            progress.dismiss();
            startActivity(new Intent(OrderCookAddActivity.this, OrdersCookActivity.class));


        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_cook_add);
        super.onCreate(savedInstanceState, "api/products/");
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        products = findViewById(R.id.products);
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


    }

    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        productList = new ArrayList<>();
        LinearLayout linear = this.findViewById(R.id.linear_products);
        for (int i = 0; i < arr.length(); i++) {
            productList.add(arr.getJSONObject(i).getString("name"));
            //linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("av")));
        }
    }

    private void renderExternalData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        employeesList = new ArrayList<EmployeeModel>();

        for (int i = 0; i < arr.length(); i++) {
            employeesList.add(new EmployeeModel(arr.getJSONObject(i).getString("name") + " " + arr.getJSONObject(i).getString("surname"), arr.getJSONObject(i).getInt("id")));
        }
        Spinner spinner = findViewById(R.id.spinner_employees);
        ArrayAdapter<EmployeeModel> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, employeesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner = findViewById(R.id.spinner_priority);
        employeesList = new ArrayList<>();
        employeesList.add(new EmployeeModel("Wysoki", 0));
        employeesList.add(new EmployeeModel("Średni", 1));
        employeesList.add(new EmployeeModel("Niski", 2));
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, employeesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private AlertDialog openProductsDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderCookAddActivity.this);
        View view2 = View.inflate(OrderCookAddActivity.this, R.layout.activity_order_cook_add_popup, null);
        final LinearLayout linearLayout = view2.findViewById(R.id.linear_products);

        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*if (phone.getText().length() > 8 && phone.getText().length() < 11) {
                            getSupportLoaderManager().initLoader(1, null, ResetPhoneCallbacks).forceLoad();
                        } else {
                            Toast.makeText(getBaseContext(), "Nie udało się zmienić numeru telefonu", Toast.LENGTH_LONG)
                                    .show();
                            phone.setText("");
                            phone.setHint("Zły format");
                        }*/
                        String result = "";
                        clearFormError();
                        for (int i = 0; i < linearLayout.getChildCount(); i++) {
                            if (linearLayout.getChildAt(i).getClass() == CheckBox.class)
                                if (((CheckBox) linearLayout.getChildAt(i)).isChecked())
                                    result += ((CheckBox) linearLayout.getChildAt(i)).getText() + ", ";
                        }
                        products.setText(result);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        for (int i = 0; i < productList.size(); i++) {
            CheckBox checkBox = new CheckBox(OrderCookAddActivity.this);
            checkBox.setText(productList.get(i));
            linearLayout.addView(checkBox);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
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

    private HashMap<String, Object> generateForm() {
        HashMap<String, Object> map = new HashMap<>();
        //if (order.getState().equals("0"))
        map.put("products", ((TextView) findViewById(R.id.products)).getText());
        if (((TextView) findViewById(R.id.comment)).length() > 0)
            map.put("comment", ((EditText) findViewById(R.id.comment)).getText().toString());
        Spinner spinner = findViewById(R.id.spinner_employees);
        map.put("provider", "" + ((EmployeeModel) spinner.getSelectedItem()).getId());
        spinner = findViewById(R.id.spinner_priority);
        map.put("priority", "" + ((EmployeeModel) spinner.getSelectedItem()).getId());


        //if (order.getState().equals("1")) map.put("state", "2");
        return map;
    }
}
