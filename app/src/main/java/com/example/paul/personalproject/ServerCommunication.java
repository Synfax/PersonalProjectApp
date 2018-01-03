package com.example.paul.personalproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

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

/**
 * Created by Paul on 12/30/17.
 */

public class ServerCommunication extends AsyncTask<Map, Integer, String> {

    private MasterDataCallback mMasterDataCallback;
    private LoginDataCallback mLoginDataCallback;
    private RegisterDataCallback mRegisterDataCallback;

    public SERVER_MODE server_mode;
    public SERVER_RESPONSE server_response;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public ServerCommunication(Activity activity, SERVER_MODE _server_mode) {
        Log.d("SC_CONSTRUCTOR", _server_mode.name());
        this.server_mode = _server_mode;

        if(server_mode == SERVER_MODE.LOGIN) {
            try {

                mLoginDataCallback = (ServerCommunication.LoginDataCallback) activity;

            }
            catch (ClassCastException e) {
                Log.d("MyDialog", "Activity doesnt implement the interface");
            }
        }
        else if(server_mode == SERVER_MODE.REGISTER) {
            try {
                mRegisterDataCallback = (ServerCommunication.RegisterDataCallback) activity;
            }
            catch(ClassCastException e) {
                Log.d("b", "Activity Doesn't implement interface");
            }
        }
        else {
            try {

                mMasterDataCallback = (MasterDataCallback) MainActivity.mActivity;
                Log.d("MyDialog", "Activity does implement interface");
            }
            catch (ClassCastException e) {
                Log.d("MyDialog", "Activity doesnt implement the interface");
            }
        }

    }



    public void onPreExecute() {
    }

    public String doInBackground(Map... p) {
        Map<String,String> params = p[0];

        try {
            URL url = new URL(params.get("URL"));
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


            server_response =  SERVER_RESPONSE.SUCCESS;
            return response;


        }
        catch(MalformedURLException e) {
            server_response = SERVER_RESPONSE.FAILURE;
        }
        catch(IOException e) {
            server_response = SERVER_RESPONSE.FAILURE;
        }


        server_response = SERVER_RESPONSE.FAILURE;
        return "k";
    }


    public void onPostExecute(String result) {
        Log.d("RESULT", result);
        Log.d("SERVERMODE", server_mode.name());
        Log.d("SERVERRESPONSE", server_response.toString());
        if(server_mode == SERVER_MODE.LOGIN) {
            mLoginDataCallback.returnLoginData(result, server_response);
        }
        else if(server_mode == SERVER_MODE.REGISTER) {
            mRegisterDataCallback.returnRegisterData(result, server_response);
        }
        else {
            mMasterDataCallback.returnMasterData(result, server_mode, server_response);
        }
    }


    public interface LoginDataCallback {
        void returnLoginData(String data, SERVER_RESPONSE server_response);
    }

    public interface RegisterDataCallback {
        void returnRegisterData(String data, SERVER_RESPONSE server_response);
    }

}
