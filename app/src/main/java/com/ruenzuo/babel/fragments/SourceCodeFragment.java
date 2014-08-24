package com.ruenzuo.babel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.models.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class SourceCodeFragment extends Fragment {

    @InjectView(R.id.vwWeb)
    WebView vwWeb;

    public static SourceCodeFragment newInstance(String HTMLString) {
        SourceCodeFragment fragment = new SourceCodeFragment();
        Bundle args = new Bundle();
        args.putString("HTMLString", HTMLString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.source_code_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        WebSettings webSettings = vwWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String HTMLString = getArguments().getString("HTMLString");
        vwWeb.loadDataWithBaseURL("file:///android_asset/WebRoot/index.html", HTMLString, "text/html", "UTF-8", null);
        return view;
    }

}
