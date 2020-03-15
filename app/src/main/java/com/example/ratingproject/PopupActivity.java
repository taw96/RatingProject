package com.example.ratingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.SaveCallback;

public class PopupActivity extends AppCompatActivity {

    Button toPlayStore, dismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        dismiss = (Button) findViewById(R.id.dismiss);
        toPlayStore = (Button) findViewById(R.id.toPlayStore);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        });

        toPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.eventloops.eventloops"));
                startActivity(intent);
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width *0.9),(int)(height*0.7));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


}
