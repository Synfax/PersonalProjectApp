package com.example.paul.project;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Paul on 4/16/17.
 */

public class Customlist extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] blocks;
    private final String[] rooms;
    private final String[] teacher;
    private final String listName;

    private ButtonInterface mOptionsButtonCallback;

    public Customlist(Activity context,
                      String[] blocks, String[] rooms, String[] teacher, String listName) {
        super(context, R.layout.list_row, blocks);
        this.context = context;
        this.blocks = blocks;
        this.rooms = rooms;
        this.teacher = teacher;
        this.listName = listName;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        try {

            mOptionsButtonCallback = (ButtonInterface) context;

        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesnt implement the interface");
        }


        View v = view;
        //Typeface typeface = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_row, null, true);
        TextView block = (TextView) rowView.findViewById(R.id.listrowName);
        TextView blockinfo = (TextView) rowView.findViewById(R.id.listrowInformation);


        block.setText(blocks[position]);
        //block.setTypeface(typeface, Typeface.BOLD);


        blockinfo.setText(teacher[position].toString() + " \u2022 " + rooms[position].toString() );
        //teacher2.setTypeface(typeface);


        //room.setTypeface(typeface);

        ImageButton optionsButton = (ImageButton) rowView.findViewById(R.id.listrowOptions);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionsButtonCallback.returnOptionsButton(v, position, listName);
            }
        });

        return rowView;
    }


}


