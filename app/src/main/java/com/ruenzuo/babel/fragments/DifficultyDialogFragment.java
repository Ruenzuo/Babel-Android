package com.ruenzuo.babel.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ruenzuo.babel.definitions.OnDifficultyDialogFragmentListener;
import com.ruenzuo.babel.models.enums.DifficultyType;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class DifficultyDialogFragment extends DialogFragment {

    private OnDifficultyDialogFragmentListener listener;
    private DifficultyType difficultyType;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnDifficultyDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDifficultyDialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(DifficultyType.difficultyTypes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                difficultyType = DifficultyType.values()[which];
                listener.onDifficultySelected(difficultyType);
            }
        });
        return builder.create();
    }

}
