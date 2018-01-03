package com.example.paul.personalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFour extends Fragment {


    public FragmentFour() {
        // Required empty public constructor
    }

    LayoutInflater _inflater;
    ViewGroup _container;
    VerticalViewPager verticalViewPager;

    View thisFragment;
    TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _inflater = inflater;
        _container = container;

        thisFragment = _inflater.inflate(R.layout.fragment_fragment_four, _container, false);
        /*
        Button btn = (Button) thisFragment.findViewById(R.id.testbtn);
        textView = (TextView) thisFragment.findViewById(R.id.testText);
        verticalViewPager = (VerticalViewPager) _container.findViewById(R.id.verticalviewpager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verticalViewPager.setCurrentItem(0, true);
            }
        });
        */

        return thisFragment;
    }



}
