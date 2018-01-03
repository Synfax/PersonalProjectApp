package com.example.paul.personalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AddHomework extends AppCompatActivity {

    JSONArray json;
    private String[] arraySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        showProgress(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {

                json = new JSONArray(extras.getString("Subjects"));

            } catch (JSONException e) {

                t(e.toString());
            }

            arraySpinner = new String[json.length()];

            for(int i = 0; i < json.length(); i++) {
                try {

                    arraySpinner[i] = json.get(i).toString();

                } catch (JSONException e) {

                    t(e.toString());
                }
            }
            //The key argument here must match that used in the other activity
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

    public void submitHomework(View v) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        String username = pref.getString("username", "");

        addHomework add = new addHomework();
        EditText name = (EditText) findViewById(R.id.dialog_event_name);

        Spinner subject = (Spinner) findViewById(R.id.dialog_event_subject);
        DatePicker date = (DatePicker) findViewById(R.id.datePicker);

        Integer day = date.getDayOfMonth();
        Integer month = date.getMonth() + 1;
        Integer year = date.getYear();

        Map<String, String> args = new LinkedHashMap<String,String>();

        args.put("Name", name.getText().toString());
        args.put("Subject", subject.getSelectedItem().toString());
        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());
        args.put("Username", username);

        add.execute(args);
    }

    public class addHomework extends AsyncTask<Map,Integer,String> {
        public void onPreExecute() {
            showProgress(true);
            hideOthers(true);
        }

        public String doInBackground(Map... p) {
            Map<String,String> params = p[0];



            try {
                URL url = new URL("https://synfax.co/pp/android/addhomework.php");
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,String> param : params.entrySet()) {
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

            return "";
        }

        public void onPostExecute(String result) {
            showProgress(false);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setVisibility(View.VISIBLE);
            textView.setText(result);



            Intent intent = new Intent(AddHomework.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void t(String s) {
        Toast.makeText(AddHomework.this,s,Toast.LENGTH_LONG).show();
    }
}
