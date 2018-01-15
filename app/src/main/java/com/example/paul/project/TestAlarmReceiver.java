package com.example.paul.project;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Paul on 1/11/18.
 */

public class TestAlarmReceiver extends BroadcastReceiver {


    public static JSONObject JSON_ORDER;
    public static JSONObject JSON_SUBJECTS_BY_LETTER;



    @Override
    public void onReceive(Context context, Intent intent) {



        // TODO Auto-generated method stub

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);



        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("ssss");


        int mNotificationId = 001;



        notificationManager.notify(mNotificationId, mBuilder.build());





    }

}
