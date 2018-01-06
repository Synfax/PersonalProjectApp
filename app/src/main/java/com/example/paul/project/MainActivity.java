package com.example.paul.project;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MasterDataCallback {

    public static Activity mActivity;
    JSONObject jsonObject;

    public static JSONArray JSON_SUBJECTS;
    public static JSONArray JSON_BLOCKS;
    public static JSONArray JSON_EVENTS;
    public static JSONArray JSON_HLS;
    public static JSONArray JSON_COMBINED_SUBJECTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        setSupportActionBar(toolbar);




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

    public void init(JSONObject arr) throws JSONException {

        try {
            JSON_SUBJECTS = arr.getJSONArray("subjects");
            JSON_BLOCKS = arr.getJSONArray("blocks");
            JSON_EVENTS = arr.getJSONArray("calendar");
            JSON_HLS = arr.getJSONArray("extra-hls");


            setTimetable(JSON_BLOCKS);
            setEvents(JSON_EVENTS);
            //setUpEditBlocks(JSON_SUBJECTS);

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

        Customlist adapter = new Customlist(MainActivity.this, blocks, rooms, teachers, "list");
        list = (ListView) findViewById(R.id.TimetableList);
        //list.setOnItemLongClickListener(mHandler2);
        list.setAdapter(adapter);

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

        Customlist adapter = new Customlist(MainActivity.this, blocks, rooms, teachers, "list2");
        list = (ListView) findViewById(R.id.EventList);
        //list.setOnItemLongClickListener(mHandler);
        list.setAdapter(adapter);

    }



    public void refresh() {

        ListView timetableList = (ListView) findViewById(R.id.TimetableList);
        ListView eventList = (ListView) findViewById(R.id.EventList);



        timetableList.setAdapter(null);
        eventList.setAdapter(null);



        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("Username", getPreference("username", ""));
        arguments.put("URL", "https://www.synfax.co/pp/android/master.php");
        showProgress(true);
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
            super.onBackPressed();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

}
