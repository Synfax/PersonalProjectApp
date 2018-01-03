package com.example.paul.personalproject;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.view.LayoutInflater;
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


public class DialogEditBlockFragment extends DialogFragment {

    View dialogView;

    EditText dialog_name;
    EditText dialog_teacher;
    EditText dialog_room;



    @TargetApi(19)
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_block, null);
        // Inflate and set the layout for the dialog
        dialog_name = (EditText)  dialogView.findViewById(R.id.edit_name_dialog);
        dialog_name.setText(getArguments().getString("block_name"));

        dialog_teacher = (EditText)  dialogView.findViewById(R.id.edit_teacher_dialog);
        dialog_teacher.setText(getArguments().getString("block_teacher"));

        dialog_room = (EditText)  dialogView.findViewById(R.id.edit_room_dialog);
        dialog_room.setText(getArguments().getString("block_room"));
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setTitle("Edit Block")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0
                        String username = pref.getString("username", "");


                        String letter = getArguments().getString("block_letter");

                        ServerCommunication serverCommunication = new ServerCommunication(getActivity(), SERVER_MODE.EDIT_BLOCK);
                        Map<String, String> args = new LinkedHashMap<String,String>();

                        args.put("URL","https://synfax.co/pp/android/editblocks.php");
                        args.put("name", dialog_name.getText().toString());
                        args.put("teacher", dialog_teacher.getText().toString());
                        args.put("room", dialog_room.getText().toString());
                        args.put("letter", letter);
                        args.put("username", username);
                        args.put("type", "edit");
                        serverCommunication.execute(args);




                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DialogEditBlockFragment.this.getDialog().cancel();
                    }
                });



        return builder.create();
    }



}
