package com.example.ratingproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Button showPopup, serverPopup,toWebView, crashButton;
    public static Context contextOfApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ELRatingManager ratingManager = new ELRatingManager(MainActivity.this);

        contextOfApplication = getApplicationContext();

        ratingManager.appOpened();
        ratingManager.checkForShowDialog();

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        Fabric.with(this, new Crashlytics());

        //defining UI items

        showPopup = findViewById(R.id.showPopup);

        serverPopup = findViewById(R.id.serverPopup);

        toWebView =  findViewById(R.id.toWebView);

        crashButton = findViewById(R.id.crashButton);

        //write to shared preferences user is on app

        showPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();

            }
        });

        serverPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShowPopupValue();

            }
        });

        toWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });

        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.getInstance().crash();

            }
        });
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    // get data with retrofit from server
    private void getShowPopupValue() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //using GsonConverterFactory to directly convert json data to object
                .build();

        UserClient userClient = retrofit.create(UserClient.class);

        Call<List<PopupServer>> call = userClient.getShowPopupValue();

        call.enqueue(new Callback<List<PopupServer>>() {
            @Override
            public void onResponse(Call<List<PopupServer>> call, Response<List<PopupServer>> response) {

                List<PopupServer> arr = response.body();
                if (arr.get(0).getShow_rating_popup()) {
                    showPopup();
                    Toast.makeText(MainActivity.this, "show rating_dialog_layout!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "don't show the rating_dialog_layout..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PopupServer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPopup(){
        Intent i = new Intent(getApplicationContext(), PopupActivity.class);
        startActivityForResult(i,1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if(resultCode==RESULT_OK){
               Toast.makeText(contextOfApplication, "geef", Toast.LENGTH_SHORT).show();
               setDataToParse(ELRatingManager.yearlyRating,String.valueOf(ELRatingManager.MAX_YEARLY_RATING_DIALOG_APPEAR));
               Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.eventloops.eventloops"));
               startActivity(intent);

         }
           else if(resultCode==RESULT_CANCELED) {
             Toast.makeText(contextOfApplication, "hey there", Toast.LENGTH_SHORT).show();
             String str = ParseInstallation.getCurrentInstallation().getString(ELRatingManager.yearlyRating);
             int newInt;
             if (!TextUtils.isEmpty(str)) {
                 newInt = Integer.parseInt(str);
             } else {
                 newInt = 0;
             }               setDataToParse(ELRatingManager.yearlyRating,String.valueOf(newInt+1));
         }
    }

    public void setDataToParse(String key, String value){

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


}


