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

public class ELRatingManager {

    //constant declarations for checking relevance of last crash date and entrances amount in specific amount of days.

    private Activity activity;

    private final static long DAYS_CALCULATION_TO_MILLISEC = 24 * 60 * 60 * 1000;
    private final static double LAUNCHES_UNTIL_PROMPT = 2;
    private final static double DAYS_UNTIL_PROMPT_IN_MILLISEC = 7;
    private final static double DAYS_WITHOUT_CRASHED_UNTIL_PROMPT = 30;
    private final static String SHARED_PREFERENCE_FILE_NAME = "SharedPrefsFile";
    private final static String SHARED_PREFERENCE_ARRAY_NAME = "AppLaunchesArray";

    public ELRatingManager(Activity ac){
        this.activity = ac;
    }

    //main method of this class, gets called only once at the "on create" of the main activity -
    //suppose to check how many times user launched the app and detect if he launched it enough times and had no crash in
    //the app for enough time so it make sense to pop him the rating dialog.

    public void appOpened(Activity activity) {

        //allowing use sharedPreference even though it is not an activity
        Context applicationContext = MainActivity.getContextOfApplication();

        //"appLaunches" is holding all the dates of user entrances to the app
        ArrayList<String> appLaunches = new ArrayList<String>();

        //first of all, load the current data from shared preference, it stored as a string so i've converted it with gson-json
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, applicationContext.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFERENCE_ARRAY_NAME, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        //conversion
        appLaunches = gson.fromJson(json, type);

        //check for values in the array - if it's the first time the app launched, then it supposed to be empty-null.
        if (appLaunches == null) {
            Toast.makeText(activity, "it's null", Toast.LENGTH_SHORT).show();

            //if so, initialize a new arrayList and add the current launchTime using the "addNewLaunchTime" method(down below).
            appLaunches = new ArrayList<String>();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

        } else {
            //else means it's not the first time the launched, add the current initialization, but also
            //loop through the array and convert the string date elements to milliseconds.
            Toast.makeText(activity, "it's not null", Toast.LENGTH_SHORT).show();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

            System.out.println("size: " + appLaunches.size());

            for (int i = 0; i <= appLaunches.size() - 1; i++) {
                String myDate = appLaunches.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date newDate = sdf.parse(myDate, new ParsePosition(0));
                long ElementTimeInMillis = newDate.getTime();
                System.out.println("Entrance " + i + ":  " + myDate + " " + ElementTimeInMillis);

                //check if the DATE element is out dated (it's been before the earliest time we want to check-"DAYS_UNTIL_PROMPT_IN_MILLISEC")
                if (ElementTimeInMillis + DAYS_UNTIL_PROMPT_IN_MILLISEC*DAYS_CALCULATION_TO_MILLISEC < System.currentTimeMillis()) {
                    //if so, remove the item and keep in the array only the items the dectate
                    appLaunches.remove(i);
                }
            }
            String LastCrashTimeString = sharedPreferences.getString("CrashDate", null);
            if (LastCrashTimeString != null) {

                System.out.println("Last Crash Time String: " + LastCrashTimeString);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date newDate = sdf.parse(LastCrashTimeString, new ParsePosition(0));
                long LastCrashInMillis = newDate.getTime();

                if (appLaunches.size() > LAUNCHES_UNTIL_PROMPT && LastCrashInMillis + DAYS_WITHOUT_CRASHED_UNTIL_PROMPT * DAYS_CALCULATION_TO_MILLISEC < System.currentTimeMillis()) {
                    Intent i = new Intent(activity, PopupActivity.class);
                    activity.startActivityForResult(i, 1);
                    return;

                } else {

                    Toast.makeText(activity, "Not ready to show rating_dialog_layout", Toast.LENGTH_SHORT).show();
                }
            } else {
                System.out.println("no crash occurred yet");
                return;
            }
        }
    }

    public static void addNewLaunchTime(ArrayList<String> array, SharedPreferences sharedPreferences, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        array.add(date);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        editor.putString(SHARED_PREFERENCE_ARRAY_NAME, json);
        editor.apply();
    }
}
