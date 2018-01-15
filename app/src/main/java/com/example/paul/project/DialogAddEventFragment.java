package com.example.paul.project;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogAddEventFragment extends DialogFragment {

    View dialogView;
    private String[] arraySpinner;
    String json2;
    JSONArray jsonArray;

    public EditText event_name;
    public Spinner blockSpinner;
    public DatePicker datePicker;


    public DialogAddEventFragment() {
        // Required empty public constructor
    }


    @Override
    @TargetApi(19)
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_dialog_add_event, null);
        // Inflate and set the layout for the dialog


        if (getArguments() != null) {

            json2 = getArguments().getString("event_subjects");
            try {
                jsonArray = new JSONArray(json2);
            } catch (JSONException e) {

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


        blockSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_event_subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, arraySpinner);
        blockSpinner.setAdapter(adapter);

        datePicker = (DatePicker) dialogView.findViewById(R.id.dialog_add_event_date);

        event_name = (EditText) dialogView.findViewById(R.id.dialog_add_event_name);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setTitle("Add Event")
                .setPositiveButton("Add Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0
                        String username = pref.getString("username", "");


                        Integer day = datePicker.getDayOfMonth();
                        Integer month = datePicker.getMonth() + 1;
                        Integer year = datePicker.getYear();

                        String letter = getArguments().getString("block_letter");

                        ServerCommunication serverCommunication = new ServerCommunication(getActivity(), SERVER_MODE.ADD_EVENT);
                        Map<String, String> args = new LinkedHashMap<String,String>();

                        args.put("URL","https://synfax.co/pp/android/addcalendar.php");

                        args.put("Name", event_name.getText().toString());
                        args.put("Subject", blockSpinner.getSelectedItem().toString());
                        args.put("Date", year.toString() + "-" + month.toString() + "-" + day.toString());


                        args.put("Username", username);


                        Set<String> s = args.keySet();
                        for(String k:s) {
                            Log.d(k, args.get(k));
                        }

                        serverCommunication.execute(args);




                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DialogAddEventFragment.this.getDialog().cancel();
                    }
                });



        return builder.create();
    }

}
