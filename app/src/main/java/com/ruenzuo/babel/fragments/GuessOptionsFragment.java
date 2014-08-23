package com.ruenzuo.babel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.babel.R;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class GuessOptionsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_options_fragment_layout, container, false);
        return view;
    }

}
