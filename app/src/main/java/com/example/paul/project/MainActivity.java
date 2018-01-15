package com.example.paul.project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MasterDataCallback, ButtonInterface {

    public static Activity mActivity;
    JSONObject jsonObject;

    SharedPreferences settingsPreferences;

    public static JSONArray JSON_SUBJECTS;
    public static JSONArray JSON_BLOCKS;
    public static JSONArray JSON_EVENTS;
    public static JSONArray JSON_HLS;
    public static JSONObject JSON_ORDER;
    public static JSONObject JSON_SUBJECTS_BY_LETTER;
    public static JSONArray JSON_COMBINED_SUBJECTS;

    public SwipeRefreshLayout timetableSwipeRefreshLayout, eventSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        setSupportActionBar(toolbar);

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        mActivity = MainActivity.this;


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                getResources().getColor(R.color.White),
                getResources().getColor(R.color.White)
        );




        getSupportActionBar().setElevation(0);

        setupViewPager(viewPager);
        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("Username", getPreference("username", ""));
        arguments.put("URL", "https://www.synfax.co/pp/android/master.php");
        showProgress(true);


        ServerCommunication serverCommunication = new ServerCommunication(MainActivity.this, SERVER_MODE.MASTER);
        serverCommunication.execute(arguments);
    }

    public void logOut() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("username");
        editor.remove("logged");
        editor.commit();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    void setUpNotifications() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        Boolean settings_notifications_daily = pref.getBoolean("settings_notifications_daily", false);
        Integer hourOfTheDay = pref.getInt("settings_notifications_daily_hour", 0);
        Integer minuteOfTheDay = pref.getInt("settings_notifications_daily_minute", 0);
        Integer secondOfTheDay = pref.getInt("settings_notifications_daily_second", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);

        intent1.putExtra("JSON_SUBJECTS_BY_LETTER", JSON_SUBJECTS_BY_LETTER.toString());
        intent1.putExtra("JSON_ORDER", JSON_ORDER.toString());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void init(JSONObject arr) throws JSONException {
        Button addeventbutton = (Button) findViewById(R.id.addeventbutton);
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog();
            }
        });
        try {
            JSON_SUBJECTS = arr.getJSONArray("subjects");
            JSON_BLOCKS = arr.getJSONArray("blocks");
            JSON_EVENTS = arr.getJSONArray("calendar");
            JSON_HLS = arr.getJSONArray("extra-hls");
            JSON_ORDER = arr.getJSONObject("order");
            JSON_SUBJECTS_BY_LETTER = arr.getJSONObject("subjects_by_letter");

            setPreference("SUBJECTS_BY_LETTER", JSON_SUBJECTS_BY_LETTER.toString());
            setPreference("ORDER", JSON_ORDER.toString());
            setPreference("EVENTS", JSON_EVENTS.toString());


            setTimetable(JSON_BLOCKS);
            setEvents(JSON_EVENTS);
            //setUpEditBlocks(JSON_SUBJECTS);

            Log.d("BOOL", Boolean.toString(settingsPreferences.getBoolean("notifications_daily", false)));
            t(Boolean.toString(settingsPreferences.getBoolean("notifications_daily", false)));
            if(settingsPreferences.getBoolean("notifications_daily", false)) {
                setUpNotifications();
            }




        } catch (JSONException e) {

            t(e.toString());

        }
        //setupButtons();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        //showNewUserDialog();
        if (pref.getBoolean("newUser", true)) {
            //t("new user");
            //pref.edit().putBoolean("newUser",false).commit();
            //showNewUserDialog();

        } else if (pref.getBoolean("newUser", true) == false) {
            //t("not a new user");
        }
    }

    public void setTimetable(JSONArray j) throws JSONException {

        JSONArray json = j;


        ListView list;
        String[] blocks = new String[4];
        String[] rooms = new String[4];
        String[] teachers = new String[4];

        for (int i = 0; i <= 3; i++) {
            try {
                blocks[i] = (json.getJSONObject(i).getString("name"));
                teachers[i] = json.getJSONObject(i).getString("teacher");
                rooms[i] = json.getJSONObject(i).getString("room");
            } catch (JSONException e) {
                t(e.toString());
            }
        }

        Customlist adapter = new Customlist(MainActivity.this, blocks, rooms, teachers, "TimetableList");
        list = (ListView) findViewById(R.id.TimetableList);
        //list.setOnItemLongClickListener(mHandler2);
        list.setAdapter(adapter);

        timetableSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.timetable_swiperefresh);
                timetableSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refresh();
                    }
                }
        );

    }

    public void setEvents(JSONArray json) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        String username = pref.getString("username", "");

        ListView list;

        String[] blocks = new String[json.length()];
        String[] rooms = new String[json.length()];
        String[] teachers = new String[json.length()];

        for (int i = 0; i < json.length(); i++) {
            try {
                blocks[i] = json.getJSONObject(i).getString("name");
                teachers[i] = json.getJSONObject(i).getString("subject");
                rooms[i] = json.getJSONObject(i).getString("date");
            } catch (JSONException e) {

            }

        }

        Customlist adapter = new Customlist(MainActivity.this, blocks, rooms, teachers, "EventList");
        list = (ListView) findViewById(R.id.EventList);
        //list.setOnItemLongClickListener(mHandler);
        list.setAdapter(adapter);

        eventSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.event_swiperefresh);
        eventSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refresh();
                    }
                }
        );

    }




    public void refresh() {
        if(timetableSwipeRefreshLayout != null) {
            timetableSwipeRefreshLayout.setRefreshing(true);
        }
        if(eventSwipeRefreshLayout != null) {
            eventSwipeRefreshLayout.setRefreshing(true);
        }

        ListView timetableList = (ListView) findViewById(R.id.TimetableList);
        ListView eventList = (ListView) findViewById(R.id.EventList);



        timetableList.setAdapter(null);
        eventList.setAdapter(null);





        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("Username", getPreference("username", ""));
        arguments.put("URL", "https://www.synfax.co/pp/android/master.php");
        //showProgress(true);
        ServerCommunication serverCommunication = new ServerCommunication(MainActivity.this, SERVER_MODE.MASTER);
        serverCommunication.execute(arguments);




    }

    private void showProgress(final boolean show) {

        ProgressBar mProgressView = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        TextView loadingTextView = (TextView) findViewById(R.id.loadingTextView);
        loadingTextView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimetableFragment(), "Timetable");
        adapter.addFragment(new EventFragment(), "Events");


        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            if(viewPager.getCurrentItem() == 1) {
                viewPager.setCurrentItem(0, true);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Log Out")
                        .setMessage("Are you sure you would like to log out?")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MainActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_blocks) {

            Intent EditBlocksIntent = new Intent(MainActivity.this, EditBlocksActivity.class);
            startActivity(EditBlocksIntent);
            // Handle the camera action

        } else if (id == R.id.nav_edit_hls) {
            //TODO add extrahls
            String username = getPreference("username", "");

            if(username.equalsIgnoreCase("synfax")) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

                Boolean settings_notifications_daily = pref.getBoolean("settings_notifications_daily", false);
                Integer hourOfTheDay = pref.getInt("settings_notifications_daily_hour", 0);
                Integer minuteOfTheDay = pref.getInt("settings_notifications_daily_minute", 0);
                Integer secondOfTheDay = pref.getInt("settings_notifications_daily_second", 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, 2);
                t("Alarm set for: " + calendar.getTime().toString());
                Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            }

        } else if (id == R.id.nav_add_event) {

            showAddEventDialog();

        } else if (id == R.id.nav_calendar) {
            //TODO add calendar
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancelAll();
        } else if (id == R.id.nav_refresh) {
            refresh();
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if(id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("About")
                    .setMessage("Made by: Paul Spasojevic \n\n" +
                            "Icons: https://www.material.io \n\n" +
                            "Last Updated: 2018/01/02\n\n" +
                            "Website: https://synfax.co/pp")
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
        else if(id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        return pref.getString(key, defaultValue);

    }

    public void setPreference(String key, String value) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        pref.edit().putString(key, value).commit();
    }



    public void t(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
    }
    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_LONG).show();
    }

    public void returnMasterData(String value, SERVER_MODE server_mode, SERVER_RESPONSE server_response) {
        String result = value;

        if(server_mode == SERVER_MODE.MASTER) {
            if (server_response == SERVER_RESPONSE.FAILURE) {

                t("Request Failed: Attempting to load older saved data.");
                if (getPreference("cachedData", "0") != "0") {
                    t("Loaded older data successfully, however data may be inaccurate.");
                    try {
                        showProgress(false);
                        jsonObject = new JSONObject(getPreference("cachedData", "0"));
                        init(jsonObject);
                    } catch (JSONException e) {
                        t("An error occured, please retry.");
                    }

                } else {
                    t("Failed to load older data. Please check your internet connection and retry");
                }
                showProgress(false);
            }
            if (server_response == SERVER_RESPONSE.SUCCESS) {
                if(timetableSwipeRefreshLayout != null) {
                    timetableSwipeRefreshLayout.setRefreshing(false);
                }
                if(eventSwipeRefreshLayout != null) {
                    eventSwipeRefreshLayout.setRefreshing(false);
                }

                try {
                    showProgress(false);
                    jsonObject = new JSONObject(result);
                    setPreference("cachedData", result);
                    init(jsonObject);
                } catch (JSONException e) {
                }
            }
        }
        else {
            if (server_response == SERVER_RESPONSE.SUCCESS) {
                if (value.equalsIgnoreCase("true")) {
                    refresh();
                    showSnackBar("Success!");
                } else {
                    showSnackBar("Failure: Please Retry");
                }
            } if (server_response == SERVER_RESPONSE.FAILURE) {
                showSnackBar("Failure: Please Retry");
            }
        }
    }
    public void showEditEventFragment(final Integer position, View view) {


        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.popup_event_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getTitle().toString()) {
                    case "Edit":
                    {
                        DialogEditEventFragment DFragmentTwo = new DialogEditEventFragment();
                        Bundle args = new Bundle();

                        args.putString("event_name", Integer.toString(position));
                        args.putString("event_array", JSON_EVENTS.toString());
                        args.putString("event_subjects", JSON_SUBJECTS.toString());


                        DFragmentTwo.setArguments(args);


                        DFragmentTwo.show(getSupportFragmentManager(), "DialogEditEventFragment");


                    }
                    break;

                    case "Delete":
                        //delete
                    {
                        ServerCommunication serverCommunication = new ServerCommunication(MainActivity.this, SERVER_MODE.DELETE_EVENT);
                        Map<String, String> args = new LinkedHashMap<String,String>();

                        try {
                            args.put("Name", JSON_EVENTS.getJSONObject(position).getString("id").toString());
                        }
                        catch(JSONException e) {
                            Log.e("JSONERROR", e.toString());
                        }
                        args.put("URL", "https://synfax.co/pp/android/editcalendar.php");
                        //args.put("_Name", "");
                        //args.put("Subject", "");
                        //args.put("Date", "");
                        args.put("type", "delete");
                        args.put("Username", getPreference("username", ""));

                        serverCommunication.execute(args);

                    }
                    break;



                }
                    /*
                    Intent intent = new Intent(MainActivity.this, edit_activity.class);
                    intent.putExtra("Name", pos);
                    intent.putExtra("Array",calendarArray.toString());
                    intent.putExtra("Subjects",subs.toString());
                    startActivity(intent);
                    */

                return false;
            }
        });
        popup.show();
    }

    public void showEditBlockFragment(final Integer position, View view, final String type) {

        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getGroupId()) {
                    case 0:

                        DialogEditBlockFragment DFragment = new DialogEditBlockFragment();
                        Bundle args = new Bundle();
                        try {
                            if(type == "timetable") {

                                args.putString("block_name", JSON_BLOCKS.getJSONObject(position).getString("name"));
                                args.putString("block_teacher", JSON_BLOCKS.getJSONObject(position).getString("teacher"));
                                args.putString("block_room", JSON_BLOCKS.getJSONObject(position).getString("room"));
                                args.putString("block_letter", JSON_BLOCKS.getJSONObject(position).getString("letter"));
                            }
                            else if(type == "subjects") {
                                args.putString("block_name", JSON_SUBJECTS.getJSONObject(position).getString("name"));
                                args.putString("block_teacher", JSON_SUBJECTS.getJSONObject(position).getString("teacher"));
                                args.putString("block_room", JSON_SUBJECTS.getJSONObject(position).getString("room"));
                                args.putString("block_letter", JSON_SUBJECTS.getJSONObject(position).getString("letter"));
                            }

                        } catch (JSONException e) {

                        }


                        DFragment.setArguments(args);


                        DFragment.show(getSupportFragmentManager(), "DialogEditBlockFragment");


                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void showAddEventDialog() {

                /*
                Intent i = new Intent(MainActivity.this, AddCalendar.class);
                i.putExtra("Subjects", subs.toString());
                //FIX THIS FUCKING SHIT
                startActivity(i);
                */

                DialogAddEventFragment DFragmentAddEvent = new DialogAddEventFragment();
                Bundle args = new Bundle();

                args.putString("event_subjects", JSON_SUBJECTS.toString());



                DFragmentAddEvent.setArguments(args);


                DFragmentAddEvent.show(getSupportFragmentManager(), "DialogAddEventFragment");



    }

    public void returnOptionsButton(View view, Integer position, String listName) {

        switch(listName) {
            case "TimetableList":
                showEditBlockFragment(position, view, "timetable");
                break;

            case "EventList":
                showEditEventFragment(position,view);
                break;

            case "EditBlocksList":

                break;
        }

    }

}
