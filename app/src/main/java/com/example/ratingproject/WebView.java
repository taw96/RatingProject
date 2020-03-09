package com.example.ratingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebView extends AppCompatActivity {

    android.webkit.WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (android.webkit.WebView) findViewById(R.id.webiview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://beta.eloops.com/appviews/customPage/elns1cT3ublmGKUic8x5W1fVH/5654/ae52ef29-7457-4acf-a9cf-b75044dc125d");


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String request) {
//                return super.shouldOverrideUrlLoading(view, request);

                if (request.startsWith("eloops://")) {
                    Toast.makeText(WebView.this, "yew", Toast.LENGTH_SHORT).show();
                    if(request.endsWith("showRating"))
                startActivity(new Intent(WebView.this,Popup.class));
                } else {
                    Toast.makeText(WebView.this, "no", Toast.LENGTH_SHORT).show();
                }
                return true;

            }
        });

    }
}
