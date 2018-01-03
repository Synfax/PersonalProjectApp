package com.example.paul.personalproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
 * Created by Paul on 12/28/17.
 */

public class EditBlocksList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] blocks;
    private final String[] rooms;
    private final String[] teacher;
    private final String[] letters;

    private EditBlocksListCallback callback;
    private ButtonInterface mButtonCallback;

    public EditBlocksList(Activity context,String[] blocks, String[] rooms, String[] teacher, String[] letters) {

        super(context, R.layout.list_row_edit_blocks, blocks);
        this.context = context;
        this.blocks = blocks;
        this.rooms = rooms;
        this.teacher = teacher;
        this.letters = letters;

    }
    @Override
    @TargetApi(19)
    public View getView(int position, View view, ViewGroup parent) {

        try {

            mButtonCallback = (ButtonInterface) context;

        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesnt implement the interface");
        }

        final Integer listPosition = position;

        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.list_row, null, true);

        final TextView Name = (TextView) rowView.findViewById(R.id.editBlocksListName_2);
        final TextView Teacher = (TextView) rowView.findViewById(R.id.teacher);

        final TextView Room = (TextView) rowView.findViewById(R.id.room);
        //final TextView  = (TextView) rowView.findViewById(R.id.editBlocksList);

        Name.clearFocus();
        Name.setText(blocks[position]);
        Teacher.clearFocus();
        //Teacher.setText(teacher[position] + " \u2022 " + rooms[position].toString());
        Room.clearFocus();
        //Room.setText(rooms[position].toString());

        ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.block_options);
        imageButton.setImageResource(R.drawable.ic_edit_black_24dp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonCallback.returnOptionsButton(v, listPosition, "list3");
            }
        });

        //Letter.clearFocus();
        //Letter.setText("Block "+letters[position]);
        /*
        Button submitButton = (Button) rowView.findViewById(R.id.editBlocksList_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rowView.getWindowToken(), 0);



                SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("MyPref", 0); // 0
                String username = pref.getString("username", "");

                ServerCommunication serverCommunication = new ServerCommunication(context, SERVER_MODE.EDIT_BLOCK);

                Map<String, String> args = new LinkedHashMap<String,String>();

                args.put("URL", "https://synfax.co/pp/android/editblocks.php");
                args.put("name", Name.getText().toString());
                args.put("teacher", Teacher.getText().toString());
                args.put("room", Room.getText().toString());
                //args.put("letter", Letter.getText().toString().replace("Block ", ""));
                args.put("username", username);
                args.put("type", "edit");
                serverCommunication.execute(args);

            }
        });

        */


        return rowView;
    }

    public void setCallback(EditBlocksListCallback callback) {
        this.callback = callback;
    }

    public interface EditBlocksListCallback {
        void ResultCallback(String value);

    }
}


