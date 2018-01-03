package com.example.paul.personalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); // 0
        
        Boolean logged = pref.getBoolean("logged", false);

        hideControls(true);


        if(logged) {
            //confusing if statement but SharedPreferences inverts the Boolean
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button btn = (Button) findViewById(R.id.email_sign_in_button);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                String __username = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                Map<String, String> args = new LinkedHashMap<>();
                args.put("Url","https://synfax.co/pp/android/login.php");
                args.put("Username", __username);
                args.put("Password", password);
                GetJson j = new GetJson();
                j.execute(args);




            }
        });
    }

    public void init(String result) {Toast.makeText(LoginActivity.this, result.split(":")[0], Toast.LENGTH_LONG).show();
        if (result.split(":")[0].equalsIgnoreCase("true")) {
            //LOGGED IN
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0

            Editor editor = pref.edit();
            //on the login store the login
            editor.putString("username", username);
            editor.putBoolean("logged", true);
            editor.putString("mobileAuthKey", result.split(":")[1]);


            editor.commit();

            Intent x = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(x);
        }
        else {
            Toast toast = Toast.makeText(LoginActivity.this, "Incorrect Details", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void hideControls(boolean show) {

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button register = (Button) findViewById(R.id.register_button);
        Button signin = (Button) findViewById(R.id.email_sign_in_button);
        ProgressBar progress = (ProgressBar) findViewById(R.id.login_progress);

        username.setVisibility(show ? View.VISIBLE : View.GONE);
        password.setVisibility(show ? View.VISIBLE : View.GONE);
        register.setVisibility(show ? View.VISIBLE : View.GONE);
        signin.setVisibility(show ? View.VISIBLE : View.GONE);

        progress.setVisibility(show ? View.GONE : View.VISIBLE);

    }





    public class GetJson extends AsyncTask<Map, Integer, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)

        public void onPreExecute() {

            hideControls(false);

        }

        public String doInBackground(Map... data) {

            try {

                Map<String,String> params = data[0];
                URL url = new URL(params.get("Url"));
                params.remove("Apparently");
                params.remove("Url");

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
                username = params.get("Username");
                String response = sb.toString();

                return response;
            }
            catch(MalformedURLException e) {

            }
            catch(IOException e) {

            }

            return "0";
        }

        public void onPostExecute(String result) {

            init(result);
            hideControls(true);
        }

        public void t(String s) {
            Toast.makeText(LoginActivity.this,s,Toast.LENGTH_LONG).show();
        }


    }

    }















