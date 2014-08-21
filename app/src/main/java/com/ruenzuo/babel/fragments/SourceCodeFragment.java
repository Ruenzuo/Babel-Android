package com.ruenzuo.babel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.babel.R;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class SourceCodeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.source_code_fragment_layout, container, false);
        return view;
    }

}
