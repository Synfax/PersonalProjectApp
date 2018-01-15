package com.example.paul.project;

import android.annotation.TargetApi;
import android.app.Activity;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import static com.example.paul.project.MainActivity.JSON_SUBJECTS;

/**
 * Created by Paul on 12/28/17.
 */


public class EditBlocksListAdapter extends ArrayAdapter<String>   {

    private final Activity context;
    private final Activity origin;
    private final String[] blocks;
    private final String[] rooms;
    private final String[] teacher;
    private final String[] letters;



    private ButtonInterface mButtonCallback;

    public EditBlocksListAdapter(Activity context, Activity origin, String[] blocks, String[] rooms, String[] teacher, String[] letters) {

        super(context, R.layout.list_row, blocks);
        this.context = context;
        this.blocks = blocks;
        this.origin = origin;
        this.rooms = rooms;
        this.teacher = teacher;
        this.letters = letters;



    }
    @Override
    @TargetApi(19)
    public View getView(final int position, View view, ViewGroup parent) {

        try {

            mButtonCallback = (ButtonInterface) context;

        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesnt implement the interface");
        }

        final Integer listPosition = position;

        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.list_row, null, true);

        final TextView Name = (TextView) rowView.findViewById(R.id.listrowName);
        final TextView Information = (TextView) rowView.findViewById(R.id.listrowInformation);



        //final TextView  = (TextView) rowView.findViewById(R.id.editBlocksList);

        Name.clearFocus();
        Name.setText(blocks[position]);
        //TODO why dont i just use a CustomList?
        Information.setText(teacher[position] + " \u2022 " + rooms[position].toString());

        //Room.setText(rooms[position].toString());

        ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.listrowOptions);
        imageButton.setImageResource(R.drawable.ic_edit_black_24dp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mButtonCallback.returnOptionsButton(v, listPosition, "EditBlocksList");


            }
        });



        return rowView;
    }






}


