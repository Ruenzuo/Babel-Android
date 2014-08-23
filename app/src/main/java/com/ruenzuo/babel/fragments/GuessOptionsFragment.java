package com.ruenzuo.babel.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.adapters.LanguageAdapter;
import com.ruenzuo.babel.models.Language;

import java.util.ArrayList;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class GuessOptionsFragment extends ListFragment {

    public static GuessOptionsFragment newInstance(ArrayList<Language> languages) {
        GuessOptionsFragment fragment = new GuessOptionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("Languages", languages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_options_fragment_layout, container, false);
        ArrayList<Language> languages = (ArrayList<Language>) getArguments().getSerializable("Languages");
        setListAdapter(new LanguageAdapter(getActivity(), R.layout.language_row_layout, languages));
        return view;
    }

}
