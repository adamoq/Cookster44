package com.example.adamo.cooksterapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
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
public class OrderWaiterAddActivity extends BaseActivity implements RecognitionListener {
    LinearLayout linearLayout;
    private String TAG = "SPEECHER", dishesString = "";
    private EditText dishes;
    LinearLayout productsLinear;
    private SpeechRecognizer speechRecognizer;
    private String dish;
    int sectionCount = 0;
    private int loaderID = 2;
    LinearLayout linearLayout2;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks2 = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {

            HashMap<String, Object> map = new HashMap<>();
            map.put("dish", dish);
            map.put("count", count);


            return new BaseLoader(OrderWaiterAddActivity.this, "api/dishorder/", map, "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                Log.d("XDD", data.toString() + "  LOADER ID " + loader.getId());
                dishesString += "\"" + data.get("resource_uri").toString() + "\",";
                Log.d("XDD", dishesString);
            } catch (Exception e) {
                messageBox(OrderWaiterAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };
    private ArrayList<HashMap<String, String>> dishesList;
    private float count;
    private LoaderManager.LoaderCallbacks<JSONObject> WaiterTaskCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            HashMap<String, Object> map = new HashMap<>();
            map.put("waiter", "/api/resemployees/" + settings.getString("id", null));
            map.put("table", ((TextView) findViewById(R.id.table)).getText());
            map.put("dishes", "{" + dishesString + "}");
            Log.d("xD", map.toString());
            return new BaseLoader(OrderWaiterAddActivity.this, "api/waitertasks/", generateForm(), "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                Log.d("XDD", data.toString());
            } catch (Exception e) {
                messageBox(OrderWaiterAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_waiter_add);
        super.onCreate(savedInstanceState, "api/resdishes/");
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        dishes = findViewById(R.id.dishes);
        productsLinear = findViewById(R.id.order_products_selected);
        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDishesDialog(view);
            }
        });


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formValidate()) sendTaskForm();
                else
                    Toast.makeText(OrderWaiterAddActivity.this, "Nie udało się dodać zamówienia", Toast.LENGTH_LONG).show();
            }
        });

        progress.dismiss();

    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        dishesList = new ArrayList<>();
        HashMap<String, String> dish;
        for (int i = 0; i < arr.length(); i++) {

            dish = new HashMap<String, String>();
            dish.put("name", arr.getJSONObject(i).getString("name"));
            dish.put("url", arr.getJSONObject(i).getString("resource_uri"));
            dish.put("price", arr.getJSONObject(i).getString("price"));
            dish.put("tax", "" + arr.getJSONObject(i).getInt("tax"));

            dishesList.add(dish);
            Log.d("XX", "XXXX" + i);
            //linear.addView(new ProductView(this, arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("av")));

            //dishesList.add(new EmployeeModel(arr.getJSONObject(i).getString("name"), Integer.parseInt(arr.getJSONObject(i).getString("id"))));
            // linearLayout.addView(new DishView(this,new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av"))));
        }
    }

    private AlertDialog openDishesDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        final View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.activity_order_waiter_add_popup, null);
        linearLayout = view2.findViewById(R.id.linear_dishes);


        AlertDialog dialog;
        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        speechRecognizer.stopListening();
                        speechRecognizer.cancel();
                        clearFormError();
                        final View productsLinearWrapper = getLayoutInflater().inflate(R.layout.activity_order_waiter_products_wrapper, null);//new LinearLayout(view2.getContext());
                        TextView tv = productsLinearWrapper.findViewById(R.id.section_id);
                        productsLinearWrapper.findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                productsLinear.removeView(productsLinearWrapper);
                                return false;
                            }
                        });
                        productsLinearWrapper.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((AlertDialog) dialog).show();
                            }
                        });
                        tv.setText("" + tv.getText() + ++sectionCount);
                        for (int i = 0; i < linearLayout.getChildCount(); i++) {
                            if (linearLayout.getChildAt(i).getClass() == OrderWaiterAddProductPopupElement.class)
                                if (((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getCount() > 0) {
                                    count = ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getCount();
                                    dish = ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getUrl();
                                    final OrderCookAddProductElement el = new OrderCookAddProductElement(view2.getContext(), ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getProductName(), new Float(count * ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getPrice()), "zł");
                                    ((LinearLayout) productsLinearWrapper).addView(el);
                                    el.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {
                                            openDishPriceDialog(view2, el);
                                            return false;
                                        }
                                    });
                                   /*  try {
                                        synchronized (this) {
                                            sendForm();
                                            wait(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } }*/
                                }

                        }
                        if (((LinearLayout) productsLinearWrapper).getChildCount() > 0)
                            productsLinear.addView(productsLinearWrapper);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        speechRecognizer.stopListening();
                        speechRecognizer.cancel();
                    }
                });
        for (int i = 0; i < dishesList.size(); i++) {
            OrderWaiterAddProductPopupElement dish = new OrderWaiterAddProductPopupElement(OrderWaiterAddActivity.this, dishesList.get(i).get("name"), dishesList.get(i).get("zł"), dishesList.get(i).get("url"), Double.parseDouble(dishesList.get(i).get("price")));
            dish.setId(i);
            dish.setTag(dishesList.get(i).toString());
            linearLayout.addView(dish);
        }

        dialog = builder.create();
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
        //startListening();
        return dialog;
    }

    private AlertDialog openDishPriceDialog(View view, final OrderCookAddProductElement element) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        final View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.activity_order_waiter_add_popup_price, null);

        final EditText tv = view2.findViewById(R.id.edit_price);
        tv.setText("" + element.getProductCount());
        tv.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                Float x = Float.parseFloat(spanned.toString() + charSequence.toString());
                if (element.getProductCount() > 0 ? x >= 0 && x <= element.getProductCount() : x >= element.getProductCount() && x <= 0)
                    return null;
                return "";
            }
        }});
        AlertDialog dialog;
        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        element.setProductCount(Float.parseFloat(tv.getText().toString()));
                    }
                });
        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void clearFormError() {
        findViewById(R.id.dishes).setBackground(null);
        findViewById(R.id.table).setBackground(null);
    }

    private void sendForm() {
        progress.show();
        getSupportLoaderManager().initLoader(loaderID++, null, OrderCallbacks2).forceLoad();
    }

    private void sendTaskForm() {

        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(loaderID++) != null)
            loaderManager.restartLoader(loaderID++, null, WaiterTaskCallbacks).forceLoad();
        else loaderManager.initLoader(loaderID++, null, WaiterTaskCallbacks).forceLoad();
    }

    public void onReadyForSpeech(Bundle params) {
    }

    public void onBeginningOfSpeech() {
    }

    public void onRmsChanged(float rmsdB) {
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    public void onEndOfSpeech() {
    }

    public void onError(int error) {
        Log.d(TAG, "error " + error);
        startListening();
    }

    public void onResults(Bundle results) {
        String str = new String();
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
       /* for (int i = 0; i < data.size(); i++)
        {
            Log.d(TAG, "result " + data.get(i));
           // Log.d("OKEEEEEEJ", checkRecognition(""+data.get(i), dishesList));
            str += data.get(i);
        }
        Log.d(TAG,"results: "+String.valueOf(data.size()));*/
        startListening();
    }

    public void onPartialResults(Bundle partialResults) {
        String str = new String();
        Log.d(TAG, "onPartialResults " + partialResults);
        ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            Log.d(TAG, "result " + data.get(i));
            String name = checkRecognition("" + data.get(i), dishesList);
            if (name != null) {
                speechRecognizer.stopListening();
                makeDish(name);

            }
        }

    }

    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }

    private boolean formValidate() {
        if (dishesString.length() > 0 && ((TextView) findViewById(R.id.table)).getText().length() > 0)
            return true;
        else if (dishesString.length() == 0)
            findViewById(R.id.dishes).setBackground(getResources().getDrawable(R.drawable.error_view));
        else
            findViewById(R.id.table).setBackground(getResources().getDrawable(R.drawable.error_view));
        return true;
    }

    String checkRecognition(String result, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            if (result.toLowerCase().contains(list.get(i).get("name").toLowerCase()))
                return list.get(i).get("name");
        }
        return null;
    }


    private void makeDish(String dish) {
        for (int i = 0; i < dishesList.size(); i++) {
            //DishCountView dish = new DishCountView(OrderWaiterAddActivity.this, dishesList.get(i).toString(),""+dishesList.get(i).getId());
            //dish.setId(i);

            if (linearLayout.findViewById(i).getTag().equals(dish)) {
                View view = linearLayout.findViewById(i);
                //Log.d("MAMTO ", view.toString());
                ((OrderCookAddProductPopupElement) view).progressCount();
            }
        }

    }

    private void startListening() {
        speechRecognizer.cancel();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl-PL");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.example.adamo.cookster");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        // intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);
        speechRecognizer.startListening(intent);
    }

    private ArrayList<HashMap<String, String>> generateForm() {

        // ArrayList<HashMap<String, String>> products = getDishes();
        ArrayList<HashMap<String, String>> products = new ArrayList<>();

        if (products.isEmpty()) Log.d("xx", "xx");
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

        HashMap<String, String> map;
        int levels = 3;


        for (int i = 0; i < levels; i++) {
            map = new HashMap<>();
            map.put("dish", "/api/resdishes/1/");
            map.put("count", "2");
            map.put("price", "20");
            map.put("price_default", "20");
            map.put("level", "" + (i + 1));
            products.add(map);
        }
        map = new HashMap<>();
        map.put("waiter", "/api/resemployees/" + settings.getString("id", "null") + "/");
        map.put("cook", "/api/resemployees/" + settings.getString("id", "null") + "/");
        map.put("price", "20");
        map.put("price_default", "20");
        map.put("table", "1");
        map.put("comment", "test");
        map.put("levels", "" + levels);
        map.put("currency", "/api/currency/1/");

        products.add(0, map);

        System.out.println("key products: " + products);
        return products;

    }

    private ArrayList<HashMap<String, String>> getDishes() {
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
