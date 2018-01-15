package com.example.paul.project;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import org.json.JSONException;

import static com.example.paul.project.MainActivity.JSON_SUBJECTS;
import static com.example.paul.project.MainActivity.mActivity;



public class EditBlocksActivity extends AppCompatActivity implements ButtonInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blocks);


        Integer length = 8;

        ListView list;
        String[] blocks = new String[length];
        String[] rooms = new String[length];
        String[] teachers = new String[length];
        String[] letters = new String[length];

        for (int i = 0; i < length; i++) {
            try {
                blocks[i] = (JSON_SUBJECTS.getJSONObject(i).getString("name"));
                teachers[i] = JSON_SUBJECTS.getJSONObject(i).getString("teacher");
                rooms[i] = JSON_SUBJECTS.getJSONObject(i).getString("room");
                letters[i] = JSON_SUBJECTS.getJSONObject(i).getString("letter");

            } catch (JSONException e) {


            }
        }

        EditBlocksListAdapter adapter = new EditBlocksListAdapter(mActivity, EditBlocksActivity.this, blocks, rooms, teachers, letters);
        list = (ListView) findViewById(R.id.EditBlocksList);
        //list.setOnItemLongClickListener(mHandler2);
        list.setAdapter(adapter);


    }

    public void returnOptionsButton(View view, Integer position, String s) {
        DialogEditBlockFragment DFragment = new DialogEditBlockFragment();
        Bundle args = new Bundle();
        try {
            args.putString("block_name", JSON_SUBJECTS.getJSONObject(position).getString("name"));
            args.putString("block_teacher", JSON_SUBJECTS.getJSONObject(position).getString("teacher"));
            args.putString("block_room", JSON_SUBJECTS.getJSONObject(position).getString("room"));
            args.putString("block_letter", JSON_SUBJECTS.getJSONObject(position).getString("letter"));

        } catch (JSONException e) {

        }


        DFragment.setArguments(args);

        //DFragment.show(context.getFragmentManager(), "s");
    }
}
