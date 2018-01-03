package com.example.paul.personalproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

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

public class AddCalendar extends AppCompatActivity {



    private String[] arraySpinner;
    String json2;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);

        final Button submitButton = (Button) findViewById(R.id.homeworkSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHomework();
            }
        });

        showProgress(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            json2 = extras.getString("Subjects");
            try {
                jsonArray = new JSONArray(json2);
            } catch (JSONException e) {
                t(e.toString());
            }

        }


        try {
            arraySpinner = new String[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); i++) {
                arraySpinner[i] = jsonArray.getJSONObject(i).getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Spinner s = (Spinner) findViewById(R.id.dialog_event_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

    }

    private void showProgress(final boolean show) {

        ProgressBar mProgressView = (ProgressBar) findViewById(R.id.progressBar2);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);


    }

    public void hideOthers(boolean show) {

        EditText name = (EditText) findViewById(R.id.dialog_event_name);
        name.setVisibility(show ? View.GONE : View.VISIBLE);

        Spinner subject = (Spinner) findViewById(R.id.dialog_event_subject);
        subject.setVisibility(show ? View.GONE : View.VISIBLE);

        DatePicker date = (DatePicker) findViewById(R.id.datePicker);
        date.setVisibility(show ? View.GONE : View.VISIBLE);

        Button submit = (Button) findViewById(R.id.homeworkSubmit);
        submit.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    @TargetApi(19)
    public void submitHomework() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        String username = pref.getString("username", "");


        EditText name = (EditText) findViewById(R.id.dialog_event_name);

        Spinner subject = (Spinner) findViewById(R.id.dialog_event_subject);
        DatePicker date = (DatePicker) findViewById(R.id.datePicker);

        Integer day = date.getDayOfMonth();
        Integer month = date.getMonth() + 1;
        Integer year = date.getYear();

        SERVER_MODE server_mode = SERVER_MODE.ADD_EVENT;
        Log.d("AtAddCalendar", server_mode.name());

        ServerCommunication serverCommunication = new ServerCommunication(getParent(), server_mode);
        Map<String, String> args = new LinkedHashMap<String,String>();

        args.put("URL", "https://synfax.co/pp/android/addcalendar.php");
        args.put("Name", name.getText().toString());
        args.put("Subject", subject.getSelectedItem().toString());
        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());
        args.put("Username", username);
        serverCommunication.execute(args);

        Intent intent = new Intent(AddCalendar.this, MainActivity.class);
        intent.putExtra("tabToSwitchTo", "Calendar");
        intent.putExtra("wasStarted", true);
        startActivity(intent);


    }



    public void t(String s) {
        Toast.makeText(AddCalendar.this,s,Toast.LENGTH_LONG).show();
    }
}
