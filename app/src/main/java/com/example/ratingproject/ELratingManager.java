package com.example.ratingproject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ELratingManager extends Application {

    private final static long DAYS_CALCULATION_TO_MILLISEC = 24 * 60 * 60 * 1000;
    private final static double LAUNCHES_UNTIL_PROMPT = 7;
    private final static double DAYS_UNTIL_PROMPT_IN_MILLISEC = 7;
    private final static double DAYS_WITHOUT_CRASHED_UNTIL_PROMPT = 30;
    private final static String SHARED_PREFERENCE_FILE_NAME = "SharedPrefsFile";
    private final static String SHARED_PREFERENCE_ARRAY_NAME = "AppLaunchesArray";

    private Activity activity;

    public ELratingManager(Activity ac){
        this.activity = ac;
    }

    public void appOpened(Activity activity) {

        Context applicationContext = MainActivity.getContextOfApplication();

        ArrayList<String> appLaunches = new ArrayList<String>();

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFERENCE_ARRAY_NAME, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        appLaunches = gson.fromJson(json, type);

        if (appLaunches == null) {
            Toast.makeText(activity, "it's null", Toast.LENGTH_SHORT).show();
            appLaunches = new ArrayList<String>();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

        } else {

            Toast.makeText(activity, "it's not null", Toast.LENGTH_SHORT).show();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

            System.out.println("size: " + appLaunches.size());

            for (int i = 0; i <= appLaunches.size() - 1; i++) {
                String myDate = appLaunches.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date newDate = sdf.parse(myDate, new ParsePosition(0));
                long timeInMillis = newDate.getTime();
                System.out.println("Entrance " + i + ":  " + myDate + " " + timeInMillis);
                if (timeInMillis + DAYS_UNTIL_PROMPT_IN_MILLISEC < System.currentTimeMillis()) {
                    appLaunches.remove(i);

                    }
                }
            String LastCrashTimeString = sharedPreferences.getString("CrashDate"," ");
            System.out.println("Last Crash Time String: " + LastCrashTimeString);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date newDate = sdf.parse(LastCrashTimeString, new ParsePosition(0));
            long LastCrashInMillis = newDate.getTime();

            if (appLaunches.size() > LAUNCHES_UNTIL_PROMPT && LastCrashInMillis + DAYS_WITHOUT_CRASHED_UNTIL_PROMPT*DAYS_CALCULATION_TO_MILLISEC < System.currentTimeMillis()) {
                Intent i = new Intent(activity, PopupActivity.class);
                activity.startActivityForResult(i, 1);

             }else{

                Toast.makeText(activity, "Not ready to show popup", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public static void addNewLaunchTime(ArrayList<String> array, SharedPreferences sharedPreferences, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        array.add(date);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        editor.putString(SHARED_PREFERENCE_ARRAY_NAME, json);
        editor.apply();
    }
}
