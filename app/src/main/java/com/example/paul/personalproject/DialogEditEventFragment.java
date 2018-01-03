package com.example.paul.personalproject;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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


public class DialogEditEventFragment extends DialogFragment {


    public DialogEditEventFragment() {
        // Required empty public constructor
    }

    View dialogEventView;
    private DataCallback mDataCallback;

    JSONArray calendar;
    JSONArray subs;
    String id;
    private String[] arraySpinner;

    @Override
    @TargetApi(19)
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogEventView = inflater.inflate(R.layout.fragment_dialog_edit_event, null);
        // Inflate and set the layout for the dialog
        TextView dialogName = (TextView) dialogEventView.findViewById(R.id.dialog_event_name);
        try {
            calendar = new JSONArray(getArguments().getString("event_array"));
            subs = new JSONArray(getArguments().getString("event_subjects"));
            dialogName.setText(calendar.getJSONObject(getArguments().getInt("event_name")).getString("name"));
            id = calendar.getJSONObject(getArguments().getInt("event_name")).getString("id");
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

        Spinner s = (Spinner) dialogEventView.findViewById(R.id.dialog_event_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        DatePicker datePicker = (DatePicker) dialogEventView.findViewById(R.id.datePicker);

        try {
            s.setSelection(adapter.getPosition(calendar.getJSONObject(getArguments().getInt("Name")).getString("subject")));
            String[] date = calendar.getJSONObject(getArguments().getInt("Name")).getString("date").split("-");
            datePicker.updateDate(Integer.parseInt(date[0]),Integer.parseInt(date[1]) - 1,Integer.parseInt(date[2]));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogEventView)
                // Add action buttons
                .setTitle("Edit Event")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int _id) {

                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0
                        TextView name = (TextView) dialogEventView.findViewById(R.id.dialog_event_name);
                        Spinner s = (Spinner) dialogEventView.findViewById(R.id.dialog_event_subject);
                        DatePicker date = (DatePicker) dialogEventView.findViewById(R.id.datePicker);
                        String username = pref.getString("username", "");

                        Integer day = date.getDayOfMonth();
                        Integer month = date.getMonth() + 1;
                        Integer year = date.getYear();

                        ServerCommunication serverCommunication = new ServerCommunication(getActivity(), SERVER_MODE.EDIT_EVENT);
                        Map<String, String> args = new LinkedHashMap<>();

                        args.put("URL", "https://synfax.co/pp/android/editcalendar.php");
                        args.put("Name", id.toString());
                        args.put("_Name", name.getText().toString());
                        args.put("Subject", s.getSelectedItem().toString());
                        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());
                        args.put("type", "edit");
                        args.put("Username", username);

                        serverCommunication.execute(args);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DialogEditEventFragment.this.getDialog().cancel();
                    }
                });



        return builder.create();
    }

    public void updateCalendar() {

    }



    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mDataCallback = (DialogEditEventFragment.DataCallback) activity;

        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesnt implement the interface");
        }
    }

    public interface DataCallback {
        void dataCallback(String string);
    }

}
