package com.example.ratingproject;

import androidx.appcompat.app.AppCompatActivity;
import com.parse.Parse;
import android.app.Application;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Parse.initialize(new Parse.Configuration
//                .Builder(this).applicationId().clientKey().server().build());
    }
}
