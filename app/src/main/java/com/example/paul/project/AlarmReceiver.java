package com.example.paul.project;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Paul on 1/11/18.
 */

public class AlarmReceiver extends BroadcastReceiver {


    public static JSONObject JSON_ORDER;
    public static JSONObject JSON_SUBJECTS_BY_LETTER;
    public static JSONArray JSON_EVENTS;



    @Override
    public void onReceive(Context context, Intent intent) {



        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", 0);


        try {
            JSON_ORDER = new JSONObject(pref.getString("ORDER", ""));
            JSON_SUBJECTS_BY_LETTER = new JSONObject(pref.getString("SUBJECTS_BY_LETTER", ""));
            JSON_EVENTS = new JSONArray((pref.getString("EVENTS", "")));
        }
        catch (JSONException e) {

        }





        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null, endDate = null;
        try {
            startDate = formatter.parse("2018-01-04");
            endDate = formatter.parse(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        Integer dayCount = 0;
        String STRING_OF_BLOCKS = "";

        for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            String day = date.toString().split(" ")[0];

            Calendar cDate = Calendar.getInstance();
            cDate.setTime(date);

            if(cDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                if(cDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    dayCount++;
                }
            }
        }

        Integer dayOfTheWeek = dayCount % 8;

        if(dayOfTheWeek == 0) {
            dayOfTheWeek = 8;
        }


        try{
            String order = JSON_ORDER.getString("day" + dayOfTheWeek);

            for(Integer i = 0; i < 4; i++) {
                String blockName = "";
                blockName = order.split(",")[i];
                STRING_OF_BLOCKS = STRING_OF_BLOCKS + (JSON_SUBJECTS_BY_LETTER.getJSONObject(blockName).getString("name"));
                if(i != 3) {
                    STRING_OF_BLOCKS = STRING_OF_BLOCKS + ", ";
                }
            }

            Log.d("TAG", STRING_OF_BLOCKS);
        }
        catch (JSONException e) {

        }






        // TODO Auto-generated method stub

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.notification_data, STRING_OF_BLOCKS);
        contentView.setTextViewText(R.id.notification_title, "Block Order");


        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                //.setContentTitle("Block Order")
                //.setContentText(STRING_OF_BLOCKS)
                .setContent(contentView)
                .setPriority(Notification.PRIORITY_MIN)
                .setOngoing(true);



        int mNotificationId = 001;



        notificationManager.notify(mNotificationId, mBuilder.build());


        // START EVENT REMINDER SECTION


        String STRING_OF_EVENTS = "";

        Integer numberOfEvents = 0;

        for(Integer i = 0; i < JSON_EVENTS.length(); i++) {

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();



            try {

                String eventTime = JSON_EVENTS.getJSONObject(i).getString("date");
                Date eventDate = null, currentDate = null;

                try {

                    eventDate = formatter.parse(eventTime);
                    currentDate = formatter.parse(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));

                    cal1.setTime(eventDate);
                    cal2.setTime(currentDate);
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

                    if(sameDay) {
                        numberOfEvents++;
                        if(STRING_OF_EVENTS != "") {
                            STRING_OF_EVENTS += ", ";
                        }
                        STRING_OF_EVENTS += JSON_EVENTS.getJSONObject(i).getString("name");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            catch (JSONException e) {

            }



        }


        if(numberOfEvents > 0) {
            RemoteViews contentView2 = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            contentView2.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
            contentView2.setTextViewText(R.id.notification_data, STRING_OF_EVENTS);
            contentView2.setTextViewText(R.id.notification_title, "Events Today");


            NotificationCompat.Builder mBuilder2 =  new NotificationCompat.Builder(context);
            mBuilder2.setSmallIcon(R.mipmap.ic_launcher)
                    //.setContentTitle("Block Order")
                    //.setContentText(STRING_OF_BLOCKS)
                    .setContent(contentView2)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setOngoing(false);



            int mNotificationId2 = 002;



            notificationManager.notify(mNotificationId2, mBuilder2.build());
        }



    }

}
