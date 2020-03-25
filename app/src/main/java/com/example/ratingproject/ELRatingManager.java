package com.example.ratingproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ELRatingManager {

    //constant declarations for checking relevance of last crash date and entrances amount in specific amount of days.
    private Activity activity;
    final public static String yearlyRating = "yearly_ratings_amount";
    final private static String firstRatingDate = "first_rating_date";
    public final static int MAX_YEARLY_RATING_DIALOG_APPEAR = 3;
    private final static long DAYS_CALCULATION_TO_MILLISEC = 24 * 60 * 60 * 1000;
    private final static int LAUNCHES_UNTIL_PROMPT = 1;
    private final static int DAYS_UNTIL_PROMPT_IN_MILLISEC = 7;
    private final static int DAYS_TO_CHECK_FROM_FIRST_RATING = 365;
    private final static double DAYS_WITH_NO_CRASH_UNTIL_PROMPT = 0.0000001;
    private final static String SHARED_PREFERENCE_FILE_NAME = "SharedPrefsFile";
    private final static String SHARED_PREFERENCE_ARRAY_NAME = "AppLaunchesArray";
    private int yearlyRatingsConvertedToInt;
    private ArrayList<String> appLaunches = new ArrayList<String>();


    public ELRatingManager(Activity ac){
        this.activity = ac;
        String ratingPopupAppearances = ParseInstallation.getCurrentInstallation().getString(yearlyRating);
        if (!TextUtils.isEmpty(ratingPopupAppearances)) {
            yearlyRatingsConvertedToInt = Integer.parseInt(ratingPopupAppearances);
        } else {
            yearlyRatingsConvertedToInt = 0;
        }
    }

    //main method of this class, gets called only once at the "on create" of the main activity -
    //suppose to check how many times user launched the app and detect if he launched it enough times and had no crash in
    //the app for enough time so it make sense to pop him the rating dialog.

    public void appOpened() {

        //"appLaunches" is holding all the dates of user entrances to the app

        //first of all, load the current data from shared preference, it stored as a string so i've converted it with gson-json
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFERENCE_ARRAY_NAME, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        //conversion
        appLaunches = gson.fromJson(json, type);

        //check for values in the array - if it's the first time the app launched, then it supposed to be empty-null.
        if (appLaunches == null) {
            Toast.makeText(activity, "app launches array is null", Toast.LENGTH_SHORT).show();

            //if so, initialize a new arrayList and add the current launchTime using the "addNewLaunchTime" method(down below).
            appLaunches = new ArrayList<String>();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

        } else {
            //else means it's not the first time the launched, add the current initialization, but also
            //loop through the array and convert the string date elements to milliseconds.
            Toast.makeText(activity, "app launches array is not null", Toast.LENGTH_SHORT).show();
            addNewLaunchTime(appLaunches, sharedPreferences, activity);

//            System.out.println("size: " + appLaunches.size());

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

        }
    }

    public void checkForDialogRelevanceByCrashAndEntrances(){

        //get last crash from shared preferences
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String LastCrashTimeString = sharedPreferences.getString("CrashDate", null);

        //if there was no crash and there were enough entrances-> proceed to check by appearances
        if (LastCrashTimeString == null && appLaunches.size() > LAUNCHES_UNTIL_PROMPT) {
            this.checkForDialogRelevanceByDialogAppearances();

            //else if there was a crash and there were enough entrances-> proceed to check whether the crash relevant or not
        } else if ( LastCrashTimeString != null && appLaunches.size() > LAUNCHES_UNTIL_PROMPT) {

            //System.out.println("Last Crash Time String: " + LastCrashTimeString);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date newDate = sdf.parse(LastCrashTimeString, new ParsePosition(0));
            long LastCrashInMillis = newDate.getTime();

            //if the crash happened in the time we want -> proceed to check by appearances
            if(LastCrashInMillis + DAYS_WITH_NO_CRASH_UNTIL_PROMPT * DAYS_CALCULATION_TO_MILLISEC < System.currentTimeMillis()){
                this.checkForDialogRelevanceByDialogAppearances();

            //else-> Do nothing
            } else {
                Toast.makeText(activity, "Not ready to show rating_dialog_layout", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkForDialogRelevanceByDialogAppearances(){
        //check for a value of the first time the dialog popped to the user which stored in Parse
        String firstRatingDateInMillie = ParseInstallation.getCurrentInstallation().getString(firstRatingDate);

        //if its not null-> convert it to number
        //else pop the dialog because this means it's the user first time
        if (firstRatingDateInMillie != null) {
            long firstDateInMillieAsLong = Long.parseLong(firstRatingDateInMillie);
        //if this date is in the previous amount of time we want(currently, a year back)-> proceed.

            if (firstDateInMillieAsLong + (DAYS_TO_CHECK_FROM_FIRST_RATING * DAYS_CALCULATION_TO_MILLISEC) > System.currentTimeMillis()) {

                if (yearlyRatingsConvertedToInt < MAX_YEARLY_RATING_DIALOG_APPEAR) {
                    this.showDialog(this.activity);
                }
                return;
            }
            this.setDataToParse(firstRatingDate, String.valueOf(System.currentTimeMillis()));
            this.setDataToParse(yearlyRating, String.valueOf(0));
            return;
        }
        this.showDialog(this.activity);
        this.setDataToParse(firstRatingDate, String.valueOf(System.currentTimeMillis()));
    }



    private static void addNewLaunchTime(ArrayList<String> array, SharedPreferences sharedPreferences, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        array.add(date);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        editor.putString(SHARED_PREFERENCE_ARRAY_NAME, json);
        editor.apply();
    }


    private void setDataToParse(String key, String value){

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(key,value);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.e("Debug", "Save successfully");
                } else {
                    Log.e("Debug", "Error Saving: " + e.getMessage());
                }
            }
        });
    }
    private void showDialog(Activity activity){
        Intent i = new Intent(this.activity, PopupActivity.class);
        this.activity.startActivityForResult(i,1);
    }

}
