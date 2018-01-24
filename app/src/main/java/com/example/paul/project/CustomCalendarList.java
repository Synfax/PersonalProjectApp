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

import com.example.paul.project.datatypes.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 4/16/17.
 */

public class CustomCalendarList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<ArrayList<Subject>> subjectArrayList;
    private final Integer number;

    private ButtonInterface mOptionsButtonCallback;

    public CustomCalendarList(Activity context, ArrayList<ArrayList<Subject>> subjectArrayList, String[] Length, Integer number) {
        super(context, R.layout.list_row, Length);
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.number = number;
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


        block.setText(subjectArrayList.get(number - 1).get(position).getName());
        blockinfo.setText(subjectArrayList.get(number - 1).get(position).getTeacher() + " \u2022 " +  subjectArrayList.get(number - 1).get(position).getRoom());
        //block.setTypeface(typeface, Typeface.BOLD);


        //blockinfo.setText(teacher[position].toString() + " \u2022 " + rooms[position].toString() );
        //teacher2.setTypeface(typeface);


        //room.setTypeface(typeface);

        ImageButton optionsButton = (ImageButton) rowView.findViewById(R.id.listrowOptions);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mOptionsButtonCallback.returnOptionsButton(v, position, listName);
            }
        });

        return rowView;
    }


}


