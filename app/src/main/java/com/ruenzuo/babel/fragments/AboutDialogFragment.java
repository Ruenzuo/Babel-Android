package com.ruenzuo.babel.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ruenzuo.babel.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class AboutDialogFragment extends DialogFragment {

    @InjectView(R.id.vwTextDeveloper)
    TextView vwTextDeveloper;

    @InjectView(R.id.vwTextOpenSource)
    TextView vwTextOpenSource;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about_dialog_fragment, null);
        ButterKnife.inject(this, view);
        vwTextDeveloper.setText(Html.fromHtml("Babel by Renzo Crisóstomo <a href='http://www.twitter.com/Ruenzuo'>@Ruenzuo</a>"));
        vwTextDeveloper.setMovementMethod(LinkMovementMethod.getInstance());
        vwTextOpenSource.setText(Html.fromHtml("Babel for Android is built using open source software: <a href='com.ruenzuo.babel://open-source/license'>license</a>"));
        vwTextOpenSource.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view).setPositiveButton("Close", null);
        return builder.create();
    }

}
