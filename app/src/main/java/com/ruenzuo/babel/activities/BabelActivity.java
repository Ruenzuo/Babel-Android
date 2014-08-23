package com.ruenzuo.babel.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.widget.SpinnerAdapter;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.extensions.AnimatedActivity;
import com.ruenzuo.babel.extensions.BabelSpinnerAdapter;
import com.ruenzuo.babel.fragments.GuessOptionsFragment;
import com.ruenzuo.babel.fragments.SourceCodeFragment;
import com.ruenzuo.babel.managers.BabelManager;
import com.ruenzuo.babel.models.enums.BabelFragmentType;
import com.ruenzuo.babel.models.enums.DifficultyType;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class BabelActivity extends AnimatedActivity implements ActionBar.OnNavigationListener {

    private BabelManager babelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babel_activity_layout);
        SpinnerAdapter spinnerAdapter = new BabelSpinnerAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item,BabelFragmentType.babelFragmentTypes());
        getActionBar().setListNavigationCallbacks(spinnerAdapter, this);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        String token = getIntent().getStringExtra("Token");
        DifficultyType difficultyType = (DifficultyType) getIntent().getSerializableExtra("DifficultyType");
        setupManager(difficultyType, token);
    }

    private void setupManager(DifficultyType difficultyType, String token) {
        babelManager = new BabelManager(difficultyType, token);
        babelManager.setupQueue(this);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        BabelFragmentType babelFragmentType = BabelFragmentType.values()[i];
        switch (babelFragmentType) {
            case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                getFragmentManager().beginTransaction().replace(R.id.vwFrame, new SourceCodeFragment()).commit();
                break;
            }
            case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                getFragmentManager().beginTransaction().replace(R.id.vwFrame, new GuessOptionsFragment()).commit();
                break;
            }
        }
        return true;
    }

}
