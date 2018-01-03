package com.example.paul.personalproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.GestureDetector;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;


public class MainActivity extends AppCompatActivity implements  DialogEditEventFragment.DataCallback, EditBlocksList.EditBlocksListCallback, MasterDataCallback, ButtonInterface {

    public static Activity mActivity;

    public static JSONObject JsonArr;

    public static JSONArray JSON_SUBJECTS;
    public static JSONArray JSON_BLOCKS;
    public static JSONArray JSON_EVENTS;
    public static JSONArray JSON_HLS;
    public static JSONArray JSON_COMBINED_SUBJECTS;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button addeventbutton;
    private TextView loadingTextView;

    @Override
    @TargetApi(19)
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = MainActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white)
        );


        getSupportActionBar().setElevation(0);
        //setTabs();
        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("Username", getPreference("username", ""));
        arguments.put("URL", "https://www.synfax.co/pp/android/master.php");
        showProgress(true);


        ServerCommunication serverCommunication = new ServerCommunication(MainActivity.this, SERVER_MODE.MASTER);
        serverCommunication.execute(arguments);


    }

    public void onBackPressed() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalviewpager);

        if (viewPager.getCurrentItem() == 0 && verticalViewPager.getCurrentItem() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to Logout?");
            builder.setTitle("Warning:");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity.super.onBackPressed();
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (verticalViewPager.getCurrentItem() > 0) {
            verticalViewPager.setCurrentItem(0, true);
        }
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(0, true);
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Timetable");
        adapter.addFragment(new TwoFragment(), "Events");


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


    public void init(JSONObject arr) throws JSONException {


        try {


            //setHomework(arr.getJSONArray("homework"));
            //subs = new String[arr.getJSONArray("subjects").length()];
            //for(int i = 0; i < arr.getJSONArray("subjects").length(); i++) {
            //    subs[i] = (arr.getJSONArray("subjects").getString(i)).toLowerCase();
            //   subs[i] = subs[i].substring(0, 1).toUpperCase() + subs[i].substring(1);
            //}
            JSON_SUBJECTS = arr.getJSONArray("subjects");
            JSON_BLOCKS = arr.getJSONArray("blocks");
            JSON_EVENTS = arr.getJSONArray("calendar");
            JSON_HLS = arr.getJSONArray("extra-hls");

            JSON_COMBINED_SUBJECTS = JSON_SUBJECTS;
            for (int i = 0; i < JSON_HLS.length(); i++) {
                JSON_COMBINED_SUBJECTS.put(JSON_HLS.getJSONObject(i));
            }

            setTimetable(JSON_BLOCKS);
            setCalendar(JSON_EVENTS);
            setUpEditBlocks(JSON_SUBJECTS);

            Log.d("JSON_COMBINED_SUBJECTS", JSON_COMBINED_SUBJECTS.toString());



        } catch (JSONException e) {

            t(e.toString());

        }
        setupButtons();
        setUpFab();

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

    public void showNewUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Tutorial").setMessage("bbbb");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setupButtons() {



        /*
        editclassesbutton = (Button) findViewById(R.id.editclassesbutton);
        editclassesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t("Not Supported Yet :(");
            }
        });
        editclassesbutton.bringToFront();
        */

        addeventbutton = (Button) findViewById(R.id.addeventbutton);
        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        addeventbutton.bringToFront();


    }


    public void setUpFab() {


        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
                switch (vp.getCurrentItem()) {
                    case 0:

                        Toast.makeText(MainActivity.this, "Sorry, this feature is not available yet.", Toast.LENGTH_LONG).show();

                        break;

                    case 1:

                        Intent i = new Intent(MainActivity.this, AddCalendar.class);
                        i.putExtra("Subjects", JSON_SUBJECTS.toString());
                        startActivity(i);

                        break;
                }
            }
        });
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
        list = (ListView) findViewById(R.id.list);
        list.setOnItemLongClickListener(mHandler2);
        list.setAdapter(adapter);

    }

    public void setUpEditBlocks(JSONArray json) {

        Integer length = json.length();

        ListView list;
        String[] blocks = new String[length];
        String[] rooms = new String[length];
        String[] teachers = new String[length];
        String[] letters = new String[length];

        for (int i = 0; i < length; i++) {
            try {
                blocks[i] = (json.getJSONObject(i).getString("name"));
                teachers[i] = json.getJSONObject(i).getString("teacher");
                rooms[i] = json.getJSONObject(i).getString("room");
                letters[i] = json.getJSONObject(i).getString("letter");

            } catch (JSONException e) {

                t(e.toString());
            }
        }

        EditBlocksList adapter = new EditBlocksList(MainActivity.this, blocks, rooms, teachers, letters);
        list = (ListView) findViewById(R.id.list3);
        //list.setOnItemLongClickListener(mHandler2);
        adapter.setCallback(this);
        list.setAdapter(adapter);
    }

    public void setCalendar(JSONArray json) {

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
        list = (ListView) findViewById(R.id.list2);
        list.setOnItemLongClickListener(mHandler);
        list.setAdapter(adapter);

    }


    public void logOut() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        Editor editor = pref.edit();
        editor.remove("username");
        editor.remove("logged");
        editor.commit();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.LogOut:
                logOut();
                return true;

            case R.id.Refresh:
                refresh();
                return true;

            case R.id.AboutSection:
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
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProgress(final boolean show) {

        ProgressBar mProgressView = (ProgressBar) findViewById(R.id.progressBar);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        loadingTextView = (TextView) findViewById(R.id.loadingTextView);
        loadingTextView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    @TargetApi(19)
    public void refresh() {

        ListView list = (ListView) findViewById(R.id.list);
        ListView list2 = (ListView) findViewById(R.id.list2);
        ListView list3 = (ListView) findViewById(R.id.list3);


        list.setAdapter(null);
        list2.setAdapter(null);
        list3.setAdapter(null);


        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("Username", getPreference("username", ""));
        arguments.put("URL", "https://www.synfax.co/pp/android/master.php");
        showProgress(true);
        ServerCommunication serverCommunication = new ServerCommunication(MainActivity.this, SERVER_MODE.MASTER);
        serverCommunication.execute(arguments);


    }

    @TargetApi(19)
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

    private AdapterView.OnItemLongClickListener mHandler = new AdapterView.OnItemLongClickListener() {


        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            showEditEventFragment(position, view);

            return false;

        }
    };

    private AdapterView.OnItemLongClickListener mHandler2 = new AdapterView.OnItemLongClickListener() {


        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            showEditBlockFragment(position, view, "timetable");

            return false;
        }
    };


    public void onSelectedData(String value) {
        if (value.equalsIgnoreCase("true")) {
            refresh();
            showSnackBar("Success!");
        } else {
            showSnackBar("Failure: Please Retry");

        }
    }

    public void dataCallback(String value) {
        if (value.equalsIgnoreCase("true")) {
            refresh();
            showSnackBar("Success!");
        } else {
            showSnackBar("Failure: Please Retry");
        }
    }

    public void ResultCallback(String value) {
        if (value.equalsIgnoreCase("true")) {
            refresh();
            showSnackBar("Success!");
        } else {

            showSnackBar("Failure: Please Retry");
        }
    }

    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_LONG).show();
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

    public void returnMasterData(String value, SERVER_MODE server_mode, SERVER_RESPONSE server_response) {
        String result = value;

             if(server_mode == SERVER_MODE.MASTER) {
                 if (server_response == SERVER_RESPONSE.FAILURE) {

                     t("Request Failed: Attempting to load older saved data.");
                     if (getPreference("cachedData", "0") != "0") {
                         t("Loaded older data successfully, however data may be inaccurate.");
                         try {
                             showProgress(false);
                             JsonArr = new JSONObject(getPreference("cachedData", "0"));
                             init(JsonArr);
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
                         JsonArr = new JSONObject(result);
                         setPreference("cachedData", result);
                         init(JsonArr);
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

    public void returnOptionsButton(View view, Integer position, String listName) {

        switch(listName) {
            case "list":
                showEditBlockFragment(position, view, "timetable");
                break;

            case "list2":
                showEditEventFragment(position,view);
                break;

            case "list3":
                DialogEditBlockFragment DFragment = new DialogEditBlockFragment();
                Bundle args = new Bundle();
                try {
                        args.putString("block_name", JSON_SUBJECTS.getJSONObject(position).getString("name"));
                        args.putString("block_teacher", JSON_SUBJECTS.getJSONObject(position).getString("teacher"));
                        args.putString("block_room", JSON_SUBJECTS.getJSONObject(position).getString("room"));
                        args.putString("block_letter", JSON_SUBJECTS.getJSONObject(position).getString("letter"));

                } catch (JSONException e) {

                }

                DFragment.setArguments(args);
                DFragment.show(getSupportFragmentManager(), "DialogEditBlockFragment");
                break;
        }

    }


}


