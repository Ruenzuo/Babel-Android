package com.ruenzuo.babel.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ruenzuo.babel.definitions.OnDifficultySelectedListener;
import com.ruenzuo.babel.models.enums.BabelDifficultyType;
import com.ruenzuo.babel.models.enums.DifficultyDialogFragmentType;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class DifficultyDialogFragment extends DialogFragment {

    private OnDifficultySelectedListener listener;

    public static DifficultyDialogFragment newInstance(DifficultyDialogFragmentType difficultyDialogFragmentType) {
        DifficultyDialogFragment fragment = new DifficultyDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("DifficultyDialogFragmentType", difficultyDialogFragmentType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnDifficultySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDifficultySelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(BabelDifficultyType.difficultyTypes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DifficultyDialogFragmentType difficultyDialogFragmentType = (DifficultyDialogFragmentType) getArguments().getSerializable("DifficultyDialogFragmentType");
                listener.onDifficultySelected(BabelDifficultyType.values()[which], difficultyDialogFragmentType);
            }
        });
        return builder.create();
    }

}
