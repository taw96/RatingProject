package com.example.ratingproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;

public class TutorialActivity extends AppCompatActivity {

    public static final String SCHEME_CONTENT = "content";

    CustomGifView gifView;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        gifView = findViewById(R.id.ima);

//                Resources res = null;
//                assert res != null;
//            AnimatedImageDrawable decoded = null;
//            try {
//                decoded = (AnimatedImageDrawable) ImageDecoder.decodeDrawable(
//                        ImageDecoder.createSource(res,R.drawable.giphy));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            image.setImageDrawable(decoded);
//                decoded.start();


//                gifView = (CustomGifView) findViewById(R.id.ima);
        try {
            gifView.init(getApplicationContext(),"https://imgur.com/rZqtMDv.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
