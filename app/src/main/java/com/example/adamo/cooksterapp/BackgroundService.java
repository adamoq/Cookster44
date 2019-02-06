package com.example.adamo.cooksterapp;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.adamo.cookster.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    int excCounter = 0;
    int NOTIFY_ID = 0;
    private String userLogin;
    private int updatePeriod = 5000;
    private Boolean isUserOnline = true;
    private Boolean isUserOffline = false;
    private Calendar userTime = null;
    private NotificationManager notifManager;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        userLogin = getSharedPreferences("login", 0).getString("login", "xxx");
//notifications
        final Context con = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();

                            //Log.d("TEST", "isPhoneLocked: onPauseisPhoneLocked" + isPhoneLocked);
                            checkUserStatus();
                            if (!isUserOffline) checkUserNotifications(con);
                            if (excCounter > 5) updatePeriod = 10000;
                            if (excCounter > 15) updatePeriod = 30000;
                            //createNotification("Message",con);

                        }
                    }, 0, updatePeriod);

                } catch (Exception e) {
                    Log.e("EXCEPT", e.getMessage());
                }
            }
        });
        thread.start();

    }

    private void checkUserStatus() {
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
        if (isPhoneLocked) {
            if (isUserOnline) { //first catch
                userTime = Calendar.getInstance();
                Log.d("TEST", "isUserOnline: " + isUserOnline);
                isUserOnline = false;
                Log.d("TEST", "isUserOnline: " + isUserOnline);
            } else if (!isUserOffline) {
                Calendar awayTime = Calendar.getInstance();
                awayTime.add(Calendar.MINUTE, -1);
                Calendar offlineTime = Calendar.getInstance();
                offlineTime.add(Calendar.MINUTE, -2);
                if (userTime.get(Calendar.MINUTE) == awayTime.get(Calendar.MINUTE) && (userTime.get(Calendar.SECOND) > awayTime.get(Calendar.SECOND) - 2)
                        && (userTime.get(Calendar.SECOND) < awayTime.get(Calendar.SECOND) + 2)) {
                    Log.d("TEST", "STATUS TO 1: " + isUserOnline);
                    //change user status
                    makeConnection("1");
                } else if (userTime.get(Calendar.MINUTE) == offlineTime.get(Calendar.MINUTE)) {
                    //change user status
                    makeConnection("0");
                    Log.d("TEST", "STATUS TO 0: " + isUserOnline);
                    userTime = null;
                    isUserOffline = true;
                }
            }
        } else if (!isUserOnline) { //log back user
            userTime = null;
            isUserOnline = true;
            isUserOffline = false;
            Log.d("TEST", "STATUS TO 1 isUserOnline: " + isUserOnline);
            makeConnection("2");
            //change user status
        }


    }

    private void checkUserNotifications(Context context) {
        JSONArray arr = makeConnection();
        if (arr != null) {
            Log.d("xx", arr.toString());
            int arrLength = arr.length();
            if (arrLength > 0) {
                for (int i = 0; i < arrLength; i++) {
                    try {
                        createNotification(arr.getJSONObject(i).getJSONObject("fields").getString("desc"), arr.getJSONObject(i).getJSONObject("fields").getString("title"), context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, "Dane zostały zaktualizowane", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onDestroy() {
        final Context context = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    makeConnection("0");
                    Log.d("DESTROY:", "DESTROY: service dest");

                } catch (Exception e) {
                    Log.e("EXCEPT", e.getMessage());
                }
            }
        });
        thread.start();

        Toast.makeText(context, "Aplikacja została zamknięta", Toast.LENGTH_LONG).show();

        System.exit(0);
    }

    public void createNotification(String aMessage, String aTitle, Context context) {

        String id = "XX"; // default_channel_id
        String title = "XX Title"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ReservationsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aTitle)
                    .setSmallIcon(R.drawable.cookster_orange)// required
                    .setBadgeIconType((R.drawable.cookster_orange)) // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ReservationsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aTitle)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notifManager.notify(NOTIFY_ID++, notification);
    }


    private Boolean makeConnection(String userStatus) {
        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(getResources().getString(R.string.app_url) + "mobileapistatus/?login=" +
                    userLogin + "&status=" + userStatus);
            client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(1500);
            client.setReadTimeout(1500);
            in = new BufferedInputStream(client.getInputStream());
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (response.toString() == "false" && response.toString() == "False") {
                return false;
                //obj = new JSONArray(response.toString()).getJSONObject(0).getJSONObject("fields");
                //obj.put("id", "" + (new JSONArray(response.toString()).getJSONObject(0)).getInt("pk"));

            }
            if (reader != null) reader.close();

        } catch (Exception e) {
            e.printStackTrace();

            excCounter = +1;

            client.disconnect();
            return false;

        } finally {
            client.disconnect();
            return true;
        }

    }

    private JSONArray makeConnection() {
        InputStream in = null;
        HttpURLConnection client = null;
        BufferedReader reader = null;
        JSONArray arr = null;
        SharedPreferences settings = getSharedPreferences("login", 0);
        try {
            URL url = new URL(getResources().getString(R.string.app_url) + "mobileapi/notif/?id=" +
                    getSharedPreferences("login", 0).getString("id", "xxx"));
            client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(2000);
            client.setReadTimeout(2000);
            in = new BufferedInputStream(client.getInputStream());
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            Log.d("xX", response.toString());
            if (response.toString() == "[ ]" && response.toString() == "[]") {
                reader.close();
                arr = new JSONArray();
                //obj = new JSONArray(response.toString()).getJSONObject(0).getJSONObject("fields");
                //obj.put("id", "" + (new JSONArray(response.toString()).getJSONObject(0)).getInt("pk"));

            } else {


                arr = new JSONArray(response.toString());
                reader.close();
                //obj = new JSONArray(response.toString()).getJSONObject(0).getJSONObject("fields");
                //obj.put("id", "" + (new JSONArray(response.toString()).getJSONObject(0)).getInt("pk"));
            }
            if (excCounter > 0) {
                excCounter = 0;
                updatePeriod = 3000;
            }


        } catch (Exception e) {
            e.printStackTrace();
            excCounter = +1;

        } finally {
            client.disconnect();

            return arr;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}