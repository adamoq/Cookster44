package com.example.adamo.cookster;


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
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.O)
@SuppressLint("MissingSuperCall")
public class OrderWaiterAddActivity extends BaseActivity implements RecognitionListener {
    LinearLayout linearLayout;
    private String TAG = "SPEECHER";
    private EditText dishes;
    private ArrayList<EmployeeModel> dishesList;
    private SpeechRecognizer speechRecognizer;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_waiter_add);
        super.onCreate(savedInstanceState, "api/resdishes/");
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        dishes = findViewById(R.id.dishes);

        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDishesDialog(view);
            }
        });


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        progress.dismiss();

    }

    @Override
    protected void renderData(JSONObject data) throws JSONException {
        super.renderData(data);
        JSONArray arr = data.getJSONArray("objects");
        dishesList = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            dishesList.add(new EmployeeModel(arr.getJSONObject(i).getString("name"), Integer.parseInt(arr.getJSONObject(i).getString("id"))));
            // linearLayout.addView(new DishView(this,new DishModel(arr.getJSONObject(i).getString("name"),arr.getJSONObject(i).getString("id"),"Nazwa kategorii",arr.getJSONObject(i).getString("category"),"Produkty, produkty, produkty, produkty...",arr.getJSONObject(i).getString("av"))));
        }
    }

    private AlertDialog openDishesDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderWaiterAddActivity.this);
        View view2 = View.inflate(OrderWaiterAddActivity.this, R.layout.activity_order_waiter_add_popup, null);
        linearLayout = view2.findViewById(R.id.linear_dishes);

        builder.setView(view2)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String result = "";
                        clearFormError();
                        for (int i = 0; i < linearLayout.getChildCount(); i++) {
                            if (linearLayout.getChildAt(i).getClass() == CheckBox.class)
                                if (((CheckBox) linearLayout.getChildAt(i)).isChecked())
                                    result += ((CheckBox) linearLayout.getChildAt(i)).getText() + ", ";
                        }
                        dishes.setText(result);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        speechRecognizer.stopListening();
                        speechRecognizer.cancel();
                    }
                });
        for (int i = 0; i < dishesList.size(); i++) {
            DishCountView dish = new DishCountView(OrderWaiterAddActivity.this, dishesList.get(i).toString(), "" + dishesList.get(i).getId());
            dish.setId(i);
            dish.setTag(dishesList.get(i).toString());
            linearLayout.addView(dish);
        }

        AlertDialog dialog = builder.create();
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

    private boolean formValidate() {
        if (((TextView) findViewById(R.id.dishes)).getText().length() > 0 && ((TextView) findViewById(R.id.table)).getText().length() > 0)
            return true;
        else if (((TextView) findViewById(R.id.dishes)).getText().length() == 0)
            findViewById(R.id.dishes).setBackground(getResources().getDrawable(R.drawable.error_view));
        else
            findViewById(R.id.table).setBackground(getResources().getDrawable(R.drawable.error_view));
        return false;
    }

    private void clearFormError() {
        findViewById(R.id.dishes).setBackground(null);
        findViewById(R.id.table).setBackground(null);
    }

    private void sendForm() {
        // getSupportLoaderManager().initLoader(2, null, FormCallbacks).forceLoad();
    }

    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    public void onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech");
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

    String checkRecognition(String result, ArrayList<EmployeeModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (result.toLowerCase().contains(list.get(i).toString().toLowerCase()))
                return list.get(i).toString();
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
                ((DishCountView) view).progressCount();
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
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 6000);
        speechRecognizer.startListening(intent);
    }
}
