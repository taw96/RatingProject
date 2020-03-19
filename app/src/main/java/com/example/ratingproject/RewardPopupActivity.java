package com.example.ratingproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class RewardPopupActivity extends AppCompatActivity {

    Button gotIt;
    ImageView avatar;
    TextView title, message, ePointsAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_popup);


        gotIt = findViewById(R.id.gotIt_button);;
        gotIt.setTransformationMethod(null);

        String popupTitle = getIntent().getStringExtra("title");
        String popupMessage = getIntent().getStringExtra("message");
        String popupCoinsAmount = getIntent().getStringExtra("coinsAmount");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        avatar = findViewById(R.id.avatarImageView);
        title = findViewById(R.id.popupTitle);
        message = findViewById(R.id.popupMessage);
        ePointsAmount = findViewById(R.id.ePointsAmount);

        title.setText(popupTitle);
        message.setText(popupMessage);
        ePointsAmount.setText(popupCoinsAmount);

        Picasso.with(this.getApplicationContext()).load(imageUrl)
                .transform(new CircleTransform()).centerCrop()
                .fit().into(avatar);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8),(int)(height*0.6));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


}
