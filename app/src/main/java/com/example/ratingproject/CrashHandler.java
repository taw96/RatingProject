package com.example.ratingproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.services.common.Crash;


public class CrashHandler extends MainActivity implements Thread.UncaughtExceptionHandler {

    final private static String CrashTime = "Crash_Time";
    Context applicationContext = MainActivity.getContextOfApplication();

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());

        setDataToParse(CrashTime, date);
        addCrashTimeToSharedPref(date);

        Log.e("Crash", "crash timing is " + date);

    }

    private void addCrashTimeToSharedPref(String date){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("SharedPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CrashDate", date);
        editor.apply();

    }
}

