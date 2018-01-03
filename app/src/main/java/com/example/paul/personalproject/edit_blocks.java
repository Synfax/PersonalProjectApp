package com.example.paul.personalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class edit_blocks extends AppCompatActivity {

    String letter;
    String _name;
    String _teacher;
    String _room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blocks);
        Bundle extras = getIntent().getExtras();
        EditText name = (EditText) findViewById(R.id.block_name_edit);
        EditText teacher = (EditText) findViewById(R.id.block_teacher_edit);
        EditText room = (EditText) findViewById(R.id.editBlocksListName_2);

        name.setText(extras.getString("name"));
        teacher.setText(extras.getString("teacher"));
        room.setText(extras.getString("room"));

        letter = extras.getString("letter");


        Button updateb = (Button) findViewById(R.id.editbutton);
        updateb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBlocks();
            }
        });
        Button deleteb = (Button) findViewById(R.id.button);
        deleteb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBlocks();
            }
        });
    }

    public void updateBlocks() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        String username = pref.getString("username", "");

        EditText name = (EditText) findViewById(R.id.block_name_edit);
        EditText teacher = (EditText) findViewById(R.id.block_teacher_edit);
        EditText room = (EditText) findViewById(R.id.editBlocksListName_2);

        edit_blocks.GetJson json = new edit_blocks.GetJson();
        Map<String, String> args = new LinkedHashMap<String,String>();


        args.put("name", name.getText().toString());
        args.put("teacher", teacher.getText().toString());
        args.put("room", room.getText().toString());
        args.put("letter", letter);
        args.put("username", username);
        args.put("type", "edit");
        json.execute(args);
    }

    public void deleteBlocks() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0
        String username = pref.getString("username", "");

        edit_blocks.GetJson json = new edit_blocks.GetJson();
        Map<String, String> args = new LinkedHashMap<String,String>();



        args.put("letter", letter);
        args.put("username", username);
        args.put("type", "edit");
        json.execute(args);
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


                URL url = new URL("https://synfax.co/pp/android/editblocks.php");
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

            Toast.makeText(edit_blocks.this, result, Toast.LENGTH_LONG).show();

            if(result.equalsIgnoreCase("true")) {
                Intent i = new Intent(edit_blocks.this, MainActivity.class);
                startActivity(i);
            }
            else {
                Toast.makeText(edit_blocks.this, "An error occured, please try again.", Toast.LENGTH_LONG).show();
            }

        }


    }
}
