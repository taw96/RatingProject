package com.example.ratingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class PopupActivity extends AppCompatActivity {

    Button toPlayStore, dismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_dialog_layout);

        dismiss =  findViewById(R.id.dismiss);
        dismiss.setTransformationMethod(null);
        toPlayStore =  findViewById(R.id.toPlayStore);
        toPlayStore.setTransformationMethod(null);

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

        getWindow().setLayout((int) (width),(int)(height));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

}
