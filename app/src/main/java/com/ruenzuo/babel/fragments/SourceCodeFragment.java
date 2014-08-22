package com.ruenzuo.babel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ruenzuo.babel.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class SourceCodeFragment extends Fragment {

    @InjectView(R.id.vwWeb)
    WebView vwWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.source_code_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        testWebView();
        return view;
    }

    private String loadTestData() {
        String data = "";
        try {
            InputStream inputStream = getActivity().getAssets().open("WebRoot/index.html");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            data = new String(buffer);
        } catch (IOException e) {
            //TODO: Handle exception.
        }
        return data;
    }

    private String loadTestFile() {
        String file = "";
        try {
            InputStream inputStream = getActivity().getAssets().open("BABBabelManager.m");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            file = new String(buffer);
        } catch (IOException e) {
            //TODO: Handle exception.
        }
        return file;
    }

    private void testWebView() {
        WebSettings webSettings = vwWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String data = loadTestData();
        data = data.replace("BABEL_LANGUAGE_PLACEHOLDER", "objectivec");
        String file = loadTestFile();
        data = data.replace("BABEL_CODE_PLACEHOLDER", file);
        vwWeb.loadDataWithBaseURL("file:///android_asset/WebRoot/index.html", data, "text/html", "UTF-8", null);
    }

}
