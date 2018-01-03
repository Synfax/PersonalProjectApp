package com.example.paul.personalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class edit_activity extends AppCompatActivity {
    JSONArray calendar;
    JSONArray subs;
    String id;
    private String[] arraySpinner;
    public Map<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        TextView name = (TextView) findViewById(R.id.dialog_event_name);

        Bundle extras = getIntent().getExtras();


        Button editb = (Button) findViewById(R.id.editbutton);
        editb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCalendar();
            }
        });
        Button deleteb = (Button) findViewById(R.id.button);
        deleteb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendar();
            }
        });


        try {
           calendar = new JSONArray(extras.getString("Array"));
            subs = new JSONArray(extras.getString("Subjects"));
            name.setText(calendar.getJSONObject(extras.getInt("Name")).getString("name"));
            id = calendar.getJSONObject(extras.getInt("Name")).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            arraySpinner = new String[subs.length()];
            for(int i = 0; i < subs.length(); i++) {
                arraySpinner[i] = subs.getJSONObject(i).getString("name");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Spinner s = (Spinner) findViewById(R.id.dialog_event_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

        try {
            s.setSelection(adapter.getPosition(calendar.getJSONObject(extras.getInt("Name")).getString("subject")));
            String[] date = calendar.getJSONObject(extras.getInt("Name")).getString("date").split("-");
            datePicker.updateDate(Integer.parseInt(date[0]),Integer.parseInt(date[1]) - 1,Integer.parseInt(date[2]));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        hideControls(false);
    }

    public void updateCalendar() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        TextView name = (TextView) findViewById(R.id.dialog_event_name);
        Spinner s = (Spinner) findViewById(R.id.dialog_event_subject);
        DatePicker date = (DatePicker) findViewById(R.id.datePicker);
        String username = pref.getString("username", "");

        Integer day = date.getDayOfMonth();
        Integer month = date.getMonth() + 1;
        Integer year = date.getYear();

        edit_activity.GetJson json = new edit_activity.GetJson();
        Map<String, String> args = new LinkedHashMap<String,String>();

        args.put("Name", id.toString());
        args.put("_Name", name.getText().toString());
        args.put("Subject", s.getSelectedItem().toString());
        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());
        args.put("type", "edit");
        args.put("Username", username);

        json.execute(args);
    }

    public void deleteCalendar() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        TextView name = (TextView) findViewById(R.id.dialog_event_name);
        Spinner s = (Spinner) findViewById(R.id.dialog_event_subject);
        DatePicker date = (DatePicker) findViewById(R.id.datePicker);
        String username = pref.getString("username", "");

        Integer day = date.getDayOfMonth();
        Integer month = date.getMonth() + 1;
        Integer year = date.getYear();

        edit_activity.GetJson json = new edit_activity.GetJson();
        Map<String, String> args = new LinkedHashMap<String,String>();

        args.put("Name", id.toString());
        args.put("_Name", name.getText().toString());
        args.put("Subject", s.getSelectedItem().toString());
        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());
        args.put("type", "delete");
        args.put("Username", username);

        json.execute(args);
    }

    public void hideControls(boolean show) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public class GetJson extends AsyncTask<Map, Integer, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        JSONObject JsonArr;

        public void onPreExecute() {
            //showProgress(true);
        }

        public String doInBackground(Map... p) {
            Map<String,String> params2 = p[0];

            try {


                URL url = new URL("https://synfax.co/pp/android/editcalendar.php");
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,String> param : params2.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString();
                //return params.toString().split(",")[1].split("=")[1];
                return response;
            }
            catch(MalformedURLException e) {

            }
            catch(IOException e) {
                
            }


            return "XD";
        }

        public void onPostExecute(String result) {

            if(result.equalsIgnoreCase("true")) {
                Intent i = new Intent(edit_activity.this, MainActivity.class);
                startActivity(i);
            }
            else {
                Toast.makeText(edit_activity.this, "An error occured, please try again.", Toast.LENGTH_LONG).show();
            }

        }


    }

}
