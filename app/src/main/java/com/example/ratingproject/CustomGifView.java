package com.example.ratingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.AnimatedImageDrawable;
import android.util.AttributeSet;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomGifView extends View {

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long mMovieStart;

    public CustomGifView(Context context) throws IOException {
        super(context);
    }

    public CustomGifView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
    }

    public CustomGifView(Context context, AttributeSet attrs,
                         int defStyleAttr) throws IOException {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ResourceType")
    public void init(Context context, String url) throws IOException {
        setFocusable(true);
        URL myURL = new URL(url);
        HttpURLConnection urlConnection =(HttpURLConnection) myURL.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        gifInputStream = urlConnection.getInputStream();

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public int getMovieWidth(){
        return movieWidth;
    }

    public int getMovieHeight(){
        return movieHeight;
    }

    public long getMovieDuration(){
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = System.currentTimeMillis();

        if (gifMovie != null) {

            int dur = gifMovie.duration();

            int relTime = (int)((now - mMovieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 0, 0);
            invalidate();

        }

    }

}
