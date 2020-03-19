package com.example.ratingproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.JsonObject;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static android.content.Context.ACTIVITY_SERVICE;

public class JSONCustomReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {

        Intent activityIntent = new Intent(context, RewardPopupActivity.class);

        //gather data from push notification
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                final String title = json.getString("title");
                final String message = json.getString("message");
                final String coinsAmount = json.getString("coinsAmount");
                final String imageUrl = json.getString("imageUrl");

                activityIntent.putExtra("title", title);
                activityIntent.putExtra("message", message);
                activityIntent.putExtra("coinsAmount", coinsAmount);
                activityIntent.putExtra("imageUrl", imageUrl);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        //Check if app is currently runnning on the device to decide whether to open both main activity and dialog activity
        // or in case user is using the app now - open only dialog activity.

        ActivityManager am =(ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo task = tasks.get(0); // current task
            ComponentName rootActivity = task.baseActivity;

            String currentPackageName = rootActivity.getPackageName();

            if(currentPackageName.equals("com.example.ratingproject")) {

                openNotificationDialog(activityIntent,context);

            }else {

                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

                openNotificationDialog(activityIntent,context);
            }
    }

    //method to open the dialog activity ("RewardPopupActivity"-name might change)

    private void openNotificationDialog(Intent intent, Context ctx){
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "CHANNEL_ID");
        builder.setContentIntent(activityPendingIntent)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(0, builder.build());

        ctx.startActivity(intent);
    }
}


