package com.example.ratingproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

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

    final private static String Rating = "user_choice";

    Button showPopup, serverPopup,toWebView, crashButton;
    ListView fetch;
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ELratingManager eLratingManager = new ELratingManager(MainActivity.this);

        contextOfApplication = getApplicationContext();

        eLratingManager.appOpened(MainActivity.this);

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        Fabric.with(this, new Crashlytics());

        //defining UI items
        fetch = (ListView) findViewById(R.id.fetchData);

        showPopup = (Button) findViewById(R.id.showPopup);

        serverPopup = (Button) findViewById(R.id.serverPopup);

        toWebView = (Button) findViewById(R.id.toWebView);

        crashButton = (Button) findViewById(R.id.crashButton);


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

    private void getShowPopupValue() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        UserClient userClient = retrofit.create(UserClient.class);

        Call<List<ShowPopup>> call = userClient.getShowPopupValue();

        call.enqueue(new Callback<List<ShowPopup>>() {
            @Override
            public void onResponse(Call<List<ShowPopup>> call, Response<List<ShowPopup>> response) {

                List<ShowPopup> heroList = response.body();
                if (heroList.get(0).getShow_rating_popup()) {
                    showPopup();
                    Toast.makeText(MainActivity.this, "show popup!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "don't show the popup..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ShowPopup>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopup(){
        Intent i = new Intent(getApplicationContext(), PopupActivity.class);
        startActivityForResult(i,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            setDataToParse(Rating,"rated");

        }else {
            setDataToParse(Rating,"Didn't rate");
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


