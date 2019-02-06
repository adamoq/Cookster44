package com.example.adamo.cooksterapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.support.v7.widget.AppCompatTextView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrderWaiterAddActivity extends BaseActivity implements RecognitionListener {
    LinearLayout linearLayout;
    private String TAG = "SPEECHER", dishesString = "";
    HashMap<String, ArrayList<HashMap<String, String>>> dishesMap;
    LinearLayout productsLinear;
    private SpeechRecognizer speechRecognizer;
    private String dish;
    int sectionCount = 0;
    private int loaderID = 2;
    LinearLayout linearLayout2;
    private Button dishes;
    //String name = "name";
    private ArrayList<HashMap<String, String>> dishesList;
    private float count;
    private Boolean isSuply = false;
    private Boolean isTakeaway = false;
    private Boolean isReservation = false;
    private Boolean popupClosed = false;
    private ArrayList<EmployeeModel> employeesList;
    private ArrayList<EmployeeModel> providersList;
    private String dishName = "name";
    private String defaultdishCurrency = "price";
    private String dishCurrency = "price";
    private LoaderManager.LoaderCallbacks<JSONObject> WaiterTaskCallbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progress.show();
            return new BaseLoader(OrderWaiterAddActivity.this, "api/waitertasks/", generateForm(), "POST");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {
                if (data.has("ok")) {
                    Toast.makeText(OrderWaiterAddActivity.this, "Pomyślnie dodano zamówienie do bazy.", Toast.LENGTH_LONG).show();
                } else throw new Exception();
            } catch (Exception e) {
                Toast.makeText(OrderWaiterAddActivity.this, "Nie udało się dodać zamówienai do bazy", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            progress.hide();
            startActivity(new Intent(OrderWaiterAddActivity.this, OrdersWaiterActivity.class));


        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            //progress.show();
        }
    };
    private String dishCurrencyAb = "zł";
    private String dishCurrencyValue = "1";
    private String langAb;
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks4 = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(OrderWaiterAddActivity.this, "api/lang/");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {

                if (isDataCorrect(data, R.id.nav_orders)) renderExternalData3(data);
            } catch (Exception e) {
                messageBox(OrderWaiterAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
                isDataCorrect(data, R.id.nav_orders);
            }
            progress.hide();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            progress.hide();
        }
    };
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks3 = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new BaseLoader(OrderWaiterAddActivity.this, "api/currency/");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {

                if (isDataCorrect(data, R.id.nav_orders)) renderExternalData2(data);
            } catch (Exception e) {
                messageBox(OrderWaiterAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
                isDataCorrect(data, R.id.nav_orders);
            }
            // progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            //  progress.dismiss();
        }
    };
    private LoaderManager.LoaderCallbacks<JSONObject> OrderCallbacks2 = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public android.support.v4.content.Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            //progress.show();
            return new BaseLoader(OrderWaiterAddActivity.this, "api/resemployees/?position__in=1,3");
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<JSONObject> loader, JSONObject data) {
            try {

                if (isDataCorrect(data, R.id.nav_orders)) renderExternalData(data);
            } catch (Exception e) {
                messageBox(OrderWaiterAddActivity.this, "errorMadafaka!", e.getMessage());
                e.printStackTrace();
                isDataCorrect(data, R.id.nav_orders);
            }
            //progress.dismiss();
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<JSONObject> loader) {
            // progress.dismiss();
        }
    };

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static HashMap<Integer, String[]> makeNumers() {

        HashMap<Integer, String[]> numbers = new HashMap<>();
        numbers.put(1, new String[]{"1", "jeden", "raz "});
        numbers.put(2, new String[]{"2", "dwa", "dwie"});
        numbers.put(3, new String[]{"3", "trzy"});
        numbers.put(4, new String[]{"4", "cztery"});
        numbers.put(5, new String[]{"5", "pięć",});
        numbers.put(6, new String[]{"6", "sześć"});
        numbers.put(7, new String[]{"7", "siedem"});
        numbers.put(8, new String[]{"8", "osiem"});
        numbers.put(9, new String[]{"9", "dziewięć"});
        numbers.put(10, new String[]{"10", "dziesięć"});

        return numbers;
    }

    private void clearFormError() {
        findViewById(R.id.dishes).setBackground(null);
        findViewById(R.id.table).setBackground(null);
    }

    private void sendTaskForm() {

        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(loaderID++) != null)
            loaderManager.restartLoader(loaderID++, null, WaiterTaskCallbacks).forceLoad();
        else loaderManager.initLoader(loaderID++, null, WaiterTaskCallbacks).forceLoad();
    }

    public void onError(int error) {
        Log.d(TAG, "error " + error);
        startListening();
    }

    public void onResults(Bundle results) {
        String str = new String();
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        startListening();
    }

    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_order_waiter_add);
        super.onCreate(savedInstanceState, "api/resdishes/");
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        dishes = findViewById(R.id.dishes);
        productsLinear = findViewById(R.id.order_products_selected);
        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(isTakeaway || isSuply) || productsLinear.getChildCount() < 2)
                    openDishesDialog(view);
            }
        });
        getSupportLoaderManager().initLoader(loaderID++, null, OrderCallbacks2).forceLoad();

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
        final Calendar myCalendar = Calendar.getInstance();
        final EditText dateOrder = findViewById(R.id.order_date);
        dateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(OrderWaiterAddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(OrderWaiterAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String myFormat = "MM/dd/yy"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                                dateOrder.setText(sdf.format(myCalendar.getTime()) + " " + selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.show();
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        final CheckBox takeaway = findViewById(R.id.takeaway);
        final CheckBox reservation = findViewById(R.id.reservation);
        final CheckBox supply = findViewById(R.id.supply);
        final LinearLayout satViewlinear = findViewById(R.id.takeaway_linear);
        final LinearLayout resViewlinear = findViewById(R.id.reservation_linear);
        takeaway.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (takeaway.isChecked()) {
                    System.out.println("Checked");
                    supply.setChecked(false);
                    reservation.setChecked(false);
                    isSuply = false;
                    isTakeaway = true;
                    isReservation = true;
                    satViewlinear.setVisibility(LinearLayout.GONE);
                    resViewlinear.setVisibility(LinearLayout.GONE);
                    ((TextView) findViewById(R.id.table_textview)).setText("Stolik");
                    ((EditText) findViewById(R.id.table)).setHint("Wpisz numer stolika");
                } else {
                    System.out.println("Un-Checked");
                    isTakeaway = false;
                    //satViewlinear.setVisibility(LinearLayout.GONE);
                }
            }
        });
        supply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (supply.isChecked()) {
                    System.out.println("Checked");
                    takeaway.setChecked(false);
                    reservation.setChecked(false);
                    isSuply = true;
                    isTakeaway = false;
                    isReservation = true;
                    satViewlinear.setVisibility(LinearLayout.VISIBLE);
                    resViewlinear.setVisibility(LinearLayout.GONE);
                    ((TextView) findViewById(R.id.table_textview)).setText("Adres");
                    ((EditText) findViewById(R.id.table)).setHint("Wpisz adres");
                } else {
                    System.out.println("Un-Checked");
                    satViewlinear.setVisibility(LinearLayout.GONE);
                    ((TextView) findViewById(R.id.table_textview)).setText("Stolik");
                    isSuply = false;
                    ((EditText) findViewById(R.id.table)).setHint("Wpisz numer stolika");
                }
            }
        });
        reservation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (reservation.isChecked()) {
                    System.out.println("Checked");
                    takeaway.setChecked(false);
                    supply.setChecked(false);
                    ((TextView) findViewById(R.id.table_textview)).setText("Stolik");
                    ((EditText) findViewById(R.id.table)).setHint("Wpisz numer stolika");
                    isSuply = false;
                    isTakeaway = false;
                    isReservation = true;
                    resViewlinear.setVisibility(LinearLayout.VISIBLE);
                    satViewlinear.setVisibility(LinearLayout.GONE);
                } else {
                    System.out.println("Un-Checked");
                    resViewlinear.setVisibility(LinearLayout.GONE);
                    ((TextView) findViewById(R.id.table_textview)).setText("Stolik");
                    isReservation = false;
                }
            }
        });
        // progress.hide();

    }

    private AlertDialog openDishesDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        final View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.activity_order_waiter_add_popup, null);
        linearLayout = view2.findViewById(R.id.linear_dishes);
        popupClosed = false;

        AlertDialog dialog;
        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        speechRecognizer.stopListening();
                        speechRecognizer.cancel();
                        popupClosed = true;
                        clearFormError();
                        final View productsLinearWrapper = getLayoutInflater().inflate(R.layout.activity_order_waiter_products_wrapper, null);//new LinearLayout(view2.getContext());
                        TextView tv = productsLinearWrapper.findViewById(R.id.section_id);
                        productsLinearWrapper.findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                productsLinear.removeView(productsLinearWrapper);
                                prepareSections();
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
                                    final OrderCookAddProductElement el = new OrderCookAddProductElement(view2.getContext(), ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getProductName(), new Float(count * ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getPrice()), dishCurrencyAb, ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getUrl(), (int) count, new Float(count * ((OrderWaiterAddProductPopupElement) linearLayout.getChildAt(i)).getDefaultPrice()));
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
                        popupClosed = true;
                        speechRecognizer.stopListening();
                        speechRecognizer.cancel();
                    }
                });
        for (int i = 0; i < dishesList.size(); i++) {
            Log.d("XX", "RDO: dishesList.get(i).get(dishName)" + dishName + dishesList.get(i).get(dishName));

            Log.d("XXX", "PRDO: dishesList.get(i))" + dishesList.get(i).toString());

            Log.d("XXX", "PRDO: dishCurrency)" + dishCurrency);

            Log.d("XXX", "PRDO: defaultdishCurrency)" + defaultdishCurrency);
            if (dishesList.get(i).get(dishName) == null) dishName = "name";
            final String name = dishesList.get(i).get("name");
            if (dishesList.get(i).get(dishCurrency) == null) dishCurrency = defaultdishCurrency;
            final OrderWaiterAddProductPopupElement dish = new OrderWaiterAddProductPopupElement(OrderWaiterAddActivity.this, dishesList.get(i).get(dishName), dishCurrencyAb, dishesList.get(i).get("url"), Double.parseDouble(dishesList.get(i).get(dishCurrency)), Double.parseDouble(dishesList.get(i).get(defaultdishCurrency)));//dishesList.get(i).get("zł")
            dish.setId(i);
            dish.setTag(dishesList.get(i).get("name"));
            dish.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    openDishDialog(dish, name);
                    return true;
                }
            });
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
        startListening();
        return dialog;
    }

    private AlertDialog openDishDialog(View view, String name) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        final View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.waiterorder_add_dish, null);
        ((TextView) view2.findViewById(R.id.name)).setText(name);
        for (HashMap<String, String> dish : dishesList) {
            if (dish.get("name").equals(name)) {
                ((TextView) view2.findViewById(R.id.comment)).setText(dish.get("desc"));
                ((TextView) view2.findViewById(R.id.products)).setText("Produkty: " + dish.get("products"));
                break;
            }
        }
        LinearLayout linearLayout = view2.findViewById(R.id.translist);
        ArrayList<HashMap<String, String>> dishTrans = dishesMap.get(name);
        if (!dishTrans.isEmpty()) {
            for (HashMap<String, String> lang : dishTrans) {
                linearLayout.addView(new OrderWaiterAddDishPopup(view2.getContext(), lang.get("lang"), lang.get("name"), lang.get("desc")));
            }
        }

        AlertDialog dialog;
        builder.setView(view2).setTitle("Dane dania");

        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private AlertDialog openDishPriceDialog(View view, final OrderCookAddProductElement element) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        final View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.activity_order_waiter_add_popup_price, null);

        final EditText tv = view2.findViewById(R.id.edit_price);
        final EditText tv2 = view2.findViewById(R.id.edit_comment);
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
                        element.setComment(tv2.getText().toString());
                    }
                });
        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    public void onPartialResults(Bundle partialResults) {
        String str = new String();
        Log.d(TAG, "onPartialResults " + partialResults);
        ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            Log.d(TAG, "result " + data.get(i));
            String name = null;
            if (!isNullOrWhiteSpace("" + data.get(i)))
                name = checkRecognition("" + data.get(i), dishesList);
            Log.d("XXX", "ELOO" + name);
            if (name != null && !name.equals("null")) {
                speechRecognizer.stopListening();
                makeDish(name);

            }
        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    private boolean formValidate() {
        if (productsLinear.getChildCount() > 2 && ((TextView) findViewById(R.id.table)).getText().length() > 0 &&
                ((((TextView) findViewById(R.id.order_date)).getText().length() > 0 && isReservation) || !isReservation))
            return true;
        else if (dishesString.length() == 0)
            findViewById(R.id.dishes).setBackground(getResources().getDrawable(R.drawable.error_view));
        else if (((TextView) findViewById(R.id.table)).getText().length() == 0)
            findViewById(R.id.table).setBackground(getResources().getDrawable(R.drawable.error_view));
        else if (((TextView) findViewById(R.id.order_date)).getText().length() == 0 && isReservation)
            findViewById(R.id.order_date).setBackground(getResources().getDrawable(R.drawable.error_view));
        return false;
    }

    String checkRecognition(String result, ArrayList<HashMap<String, String>> list) {
        HashMap<Integer, String[]> numbers = makeNumers();
        Boolean found = false;
        int a = 1;
        for (Integer number : numbers.keySet()) {
            for (String x : numbers.get(number)) {
                Log.d("sraka", result + " CONTAINS " + x);
                if (result.toLowerCase().contains(x.toLowerCase())) {
                    a = number;
                    found = true;
                    break;

                }
            }
            if (found) break;
        }
        for (int i = 0; i < list.size(); i++) {
            Log.d("sraka", result + " CONTAINS " + list.get(i).get(dishName).toLowerCase());

            if (result.toLowerCase().contains(list.get(i).get(dishName).toLowerCase()))
                return "" + a + '|' + list.get(i).get("name");
        }
        return null;
    }

    private void makeDish(String dish) {
        String[] tab = dish.split("\\|");
        for (int i = 0; i < dishesList.size(); i++) {

            if (linearLayout.findViewById(i).getTag().equals(tab[1])) {
                View view = linearLayout.findViewById(i);
                ((OrderWaiterAddProductPopupElement) view).progressCount(Integer.parseInt(tab[0]));
            }
        }

    }

    private void startListening() {
        if (!popupClosed) {
            speechRecognizer.cancel();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            Log.d("langAb", "langAb" + langAb);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langAb);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.example.adamo.cookster");
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            // intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 6000);
            speechRecognizer.startListening(intent);
        }
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

            if (!comment.isEmpty()) map.put("comment", comment);
            System.out.println("key map: " + map);
            products.add(0, map);

        }

        HashMap<String, String> map;
        LinearLayout view;
        int level = -1;
        float price = 0;
        float defaultPrice = 0;
        int count = 0;
        for (int i = 0; i < productsLinear.getChildCount(); i++) {
            Log.d("AS", "ELOgetUr123213l" + productsLinear.getChildAt(i).getClass());
            if (productsLinear.getChildAt(i).getClass() == LinearLayout.class) {
                view = (LinearLayout) productsLinear.getChildAt(i);
                level++;
                for (int y = 0; y < view.getChildCount(); y++) {
                    Log.d("AS", "ELOgetUrl" + view.getChildAt(y).getClass());
                    if (view.getChildAt(y).getClass() == OrderCookAddProductElement.class) {
                        OrderCookAddProductElement order = ((OrderCookAddProductElement) view.getChildAt(y));
                        Log.d("AS", "ELOgetUrl" + order.getUrl());// ok url
                        Log.d("AS", "ELOLEVEL" + level);// LEVEL
                        Log.d("AS", "ELOgetProductId" + order.getProductId());// ok ilość
                        Log.d("AS", "ELOgetProductUnit" + order.getProductUnit());//ok waluta zł
                        Log.d("AS", "ELOgetProductCount" + order.getProductCount());//ok cena
                        price += order.getProductCount();
                        defaultPrice += order.getDefaultPrice();
                        count += order.getProductId();
                        map = new HashMap<>();
                        map.put("dish", order.getUrl());//mam
                        map.put("count", "" + order.getProductId());//mam
                        map.put("price", "" + order.getProductCount());//mam

                        map.put("price_default", "" + order.getDefaultPrice());//mam
                        map.put("level", "" + level);//mam
                        if (!order.getComment().isEmpty())
                            map.put("comment", "" + order.getComment());//mam
                        products.add(map);
                    }
                }

            }
        }



        map = new HashMap<>();
        Spinner spinner = findViewById(R.id.spinner_employees);
        String provider = ((EmployeeModel) spinner.getSelectedItem()).getId();

        //foreign currency
        if (dishCurrency.equals(defaultdishCurrency))
            dishCurrency = settings.getString("currency", "0");
        String comment = "";
        if (!((EditText) findViewById(R.id.comment)).getText().toString().isEmpty())
            comment += ((EditText) findViewById(R.id.comment)).getText().toString();
        //supply
        if (isSuply) {
            double takeaway = Double.parseDouble(settings.getString("takeaway", "0"));
            double supply = Double.parseDouble(settings.getString("supply", "0"));
            price += count * takeaway;
            defaultPrice += count * takeaway;
            price += supply;
            defaultPrice += supply;
            Spinner spinner2 = findViewById(R.id.spinner_employees_takeaway);
            String supplier = ((EmployeeModel) spinner2.getSelectedItem()).getId();
            //map.put("supplier",  "/api/resemployees/"+supplier+"/");
            map.put("supplier", supplier);
            map.put("suply", "1");
            comment += "\nOpłata za opakowanie na wynos (" + count + " x " + takeaway + ")";
            comment += "\nOpłata za dostawę (" + supply + ")";

        } else if (isTakeaway) {
            double takeaway = Double.parseDouble(settings.getString("takeaway", "0"));
            price += count * takeaway;
            defaultPrice += count * takeaway;
            map.put("istakeaway", "1");
            comment += "\nOpłata za opakowanie na wynos (" + count + " x " + takeaway + ")";
        } else if (isReservation) {
            String dateReservation = ((TextView) findViewById(R.id.order_date)).getText().toString();

            map.put("reservation", dateReservation);
        }

        map.put("levels", "" + ++level);
        map.put("currency", "/api/currency/" + dishCurrency + "/");
        map.put("price", "" + price);//mam
        map.put("price_default", "" + defaultPrice);//mam


        map.put("waiter", "/api/resemployees/" + settings.getString("id", "null") + "/");
        map.put("cook", provider);
        map.put("table", ((EditText) findViewById(R.id.table)).getText().toString());
        if (!comment.isEmpty())
            map.put("comment", comment);
        products.add(0, map);


        return products;

    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data, R.id.nav_orders_add);
        if (data.has("error")) return;
        JSONArray arr = data.getJSONArray("objects");
        dishesList = new ArrayList<>();
        HashMap<String, String> dish;
        dishesMap = new HashMap<>();
        HashMap<String, String> dishMap = new HashMap<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            dish = new HashMap<String, String>();
            dish.put("name", obj.getString("name"));
            Log.d("NAME", "CHJE: " + obj.getString("name"));
            dish.put("url", obj.getString("resource_uri"));
            dish.put("price", obj.getString("price"));
            dish.put("tax", "" + obj.getInt("tax"));
            dish.put("desc", "" + obj.getString("description"));
            JSONArray products = obj.getJSONArray("dishproducts");
            if (products.length() > 0) {
                String productList = "";

                for (int x = 0; x < products.length(); x++) {
                    if (x == products.length() - 1)
                        productList += products.getJSONObject(x).getString("count") + products.getJSONObject(x).getString("product__unit") + " x " + products.getJSONObject(x).getString("product__name");
                    else
                        productList += products.getJSONObject(x).getString("count") + products.getJSONObject(x).getString("product__unit") + " x " + products.getJSONObject(x).getString("product__name") + ", ";

                }

                dish.put("products", productList);
            } else
                dish.put("products", "-");
            JSONArray lang = obj.getJSONArray("trans");
            JSONArray currencies = obj.getJSONArray("currencies");
            for (int x = 0; x < currencies.length(); x++) {
                dish.put(currencies.getJSONObject(x).getString("currency__id"), currencies.getJSONObject(x).getString("price"));

            }
            JSONObject lng;
            ArrayList<HashMap<String, String>> dishesTrans = new ArrayList<>();
            for (int x = 0; x < lang.length(); x++) {
                dishMap = new HashMap<>();
                lng = lang.getJSONObject(x);
                dish.put(lng.getString("lang__name"), lng.getString("name"));
                dishMap.put("name", lng.getString("name"));
                dishMap.put("lang", lng.getString("lang__name"));
                dishMap.put("desc", lng.getString("description"));
                dishesTrans.add(dishMap);
            }

            dishesMap.put(arr.getJSONObject(i).getString("name"), dishesTrans);
            dishesList.add(dish);
        }
    }

    private void renderExternalData(JSONObject data) throws JSONException {
        //super.renderData(data);//renderData(data);
        Log.d("xx", data.toString());
        JSONArray arr = data.getJSONArray("objects");
        employeesList = new ArrayList<EmployeeModel>();
        providersList = new ArrayList<EmployeeModel>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject employee = arr.getJSONObject(i);
            if (employee.getString("position").equals("1"))
                employeesList.add(new EmployeeModel(employee.getString("name") + " " + employee.getString("surname"), employee.getString("resource_uri")));
            else
                providersList.add(new EmployeeModel(employee.getString("name") + " " + employee.getString("surname"), employee.getString("resource_uri")));


        }
        Spinner spinner = findViewById(R.id.spinner_employees);
        ArrayAdapter<EmployeeModel> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, employeesList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner = findViewById(R.id.spinner_employees_takeaway);
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, providersList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        getSupportLoaderManager().initLoader(loaderID++, null, OrderCallbacks3).forceLoad();
    }

    private void renderExternalData2(JSONObject data) throws JSONException {
        //super.renderData(data);//renderData(data);
        ArrayList<EmployeeModel> currencyList;
        Log.d("xx", data.toString());
        JSONArray arr = data.getJSONArray("objects");
        currencyList = new ArrayList<EmployeeModel>();

        for (int i = 0; i < arr.length(); i++) {
            currencyList.add(new EmployeeModel(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("ab"), arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getString("value")));
        }
        //currencyList.add(new EmployeeModel("domyślny", arr.getJSONObject(i).getString("ab")));

        final Spinner spinner = findViewById(R.id.spinner_currencies);
        ArrayAdapter<EmployeeModel> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, currencyList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EmployeeModel model = ((EmployeeModel) spinner.getSelectedItem());
                dishCurrencyAb = model.getAb();
                dishCurrencyValue = model.getValue();
                if (Integer.parseInt(settings.getString("currency", "0")) == Integer.parseInt(model.getId())) {
                    dishCurrency = defaultdishCurrency;
                } else {
                    dishCurrency = model.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getSupportLoaderManager().initLoader(loaderID++, null, OrderCallbacks4).forceLoad();
    }

    private void renderExternalData3(JSONObject data) throws JSONException {
        //super.renderData(data);//renderData(data);
        ArrayList<EmployeeModel> currencyList;
        Log.d("xx", data.toString());
        JSONArray arr = data.getJSONArray("objects");
        currencyList = new ArrayList<EmployeeModel>();

        for (int i = 0; i < arr.length(); i++) {
            currencyList.add(new EmployeeModel(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getString("ab")));

        }
        //currencyList.add(new EmployeeModel("domyślny", arr.getJSONObject(i).getString("ab")));

        final Spinner spinner = findViewById(R.id.spinner_lang);
        ArrayAdapter<EmployeeModel> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, currencyList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinner.getSelectedItem().toString();
                EmployeeModel model = ((EmployeeModel) spinner.getSelectedItem());
                langAb = model.getAb();
                if (Integer.parseInt(model.getId()) == Integer.parseInt(settings.getString("lang", "0")))
                    dishName = "name";
                else dishName = text;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    private void prepareSections() {
        --sectionCount;
        LinearLayout view;
        int x = 1;
        for (int i = 0; i < productsLinear.getChildCount(); i++) {
            if (productsLinear.getChildAt(i).getClass() == LinearLayout.class) {
                view = (LinearLayout) productsLinear.getChildAt(i);
                for (int y = 0; y < view.getChildCount(); y++) {
                    if (view.getChildAt(y).getClass() == LinearLayout.class) {
                        for (int z = 0; z < ((LinearLayout) view.getChildAt(y)).getChildCount(); z++) {
                            Log.d("xxxtutaj", "xx" + ((LinearLayout) view.getChildAt(y)).getChildAt(z).getClass());
                            if (((LinearLayout) view.getChildAt(y)).getChildAt(z).getClass() == AppCompatTextView.class) {
                                TextView xx = ((TextView) (((LinearLayout) view.getChildAt(y)).getChildAt(z)));
                                if (xx.getId() == R.id.section_id)
                                    xx.setText("Serwis " + x++);
                            }
                        }

                    }
                }

            }
        }

    }
}
