package com.ruenzuo.babel.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SpinnerAdapter;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.extensions.AnimatedActivity;
import com.ruenzuo.babel.extensions.BabelSpinnerAdapter;
import com.ruenzuo.babel.fragments.GuessOptionsFragment;
import com.ruenzuo.babel.fragments.SourceCodeFragment;
import com.ruenzuo.babel.managers.BabelManager;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.ruenzuo.babel.models.enums.BabelFragmentType;
import com.ruenzuo.babel.models.enums.DifficultyType;

import java.util.Hashtable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class BabelActivity extends AnimatedActivity implements ActionBar.OnNavigationListener {

    private BabelManager babelManager;
    private BabelFragmentType currentBabelFragmentType;
    private int remainingHints = 5;
    private int remainingSkips = 5;
    private boolean isLoading = false;
    private Language currentLanguage;
    private Repository currentRepository;
    private File currentFile;
    private String currentHTMLString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babel_activity_layout);
        SpinnerAdapter spinnerAdapter = new BabelSpinnerAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item,BabelFragmentType.babelFragmentTypes());
        getActionBar().setListNavigationCallbacks(spinnerAdapter, this);
        String token = getIntent().getStringExtra("Token");
        DifficultyType difficultyType = (DifficultyType) getIntent().getSerializableExtra("DifficultyType");
        setupManager(difficultyType, token);
        setLoadingIndicators();
        nextFile();
    }

    private void setLoadingIndicators() {
        setProgressBarIndeterminateVisibility(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        isLoading = true;
        invalidateOptionsMenu();
    }

    private void nextFile() {
        babelManager.loadNext().continueWith(new Continuation<Hashtable<String, Object>, Object>() {
            @Override
            public Object then(Task<Hashtable<String, Object>> task) throws Exception {
                if (task.getError() != null) {
                    //TODO: Handle error.
                } else {
                    currentLanguage = (Language) task.getResult().get("Language");
                    currentRepository = (Repository) task.getResult().get("Repository");
                    currentFile = (File) task.getResult().get("File");
                    currentHTMLString = (String) task.getResult().get("HTML");
                    Log.i("BabelActivity", "Loading file.");
                    Log.i("BabelActivity", "Current language: " + currentLanguage.getName());
                    Log.i("BabelActivity", "Current repository: " + currentRepository.getName());
                    Log.i("BabelActivity", "Current file: " + currentFile.getName());
                    loadCurrentFile();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private void loadCurrentFile() {
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        currentBabelFragmentType = BabelFragmentType.BABEL_FRAGMENT_TYPE_SOURCE_CODE;
        getFragmentManager().beginTransaction().replace(R.id.vwFrame, SourceCodeFragment.newInstance(currentHTMLString)).commit();
        isLoading = false;
        invalidateOptionsMenu();
    }

    private void setupManager(DifficultyType difficultyType, String token) {
        babelManager = new BabelManager(difficultyType, token, this);
        babelManager.setupQueue(this);
    }

    private String getHintTitle() {
        return getString(R.string.hint) + " (" + remainingHints + ")";
    }

    private String getSkipTitle() {
        return getString(R.string.skip) + " (" + remainingSkips + ")";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isLoading) {
            switch (currentBabelFragmentType) {
                case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                    menu.add(Menu.NONE, R.id.action_skip, Menu.NONE, getSkipTitle()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    return true;
                }
                case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                    menu.add(Menu.NONE, R.id.action_hint, Menu.NONE, getHintTitle()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    return true;
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        BabelFragmentType selectedBabelFragmentType = BabelFragmentType.values()[i];
        if (selectedBabelFragmentType == currentBabelFragmentType) {
            return true;
        } else {
            currentBabelFragmentType = selectedBabelFragmentType;
            switch (currentBabelFragmentType) {
                case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                    getFragmentManager().beginTransaction().replace(R.id.vwFrame, SourceCodeFragment.newInstance(currentHTMLString)).commit();
                    break;
                }
                case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                    getFragmentManager().beginTransaction().replace(R.id.vwFrame, GuessOptionsFragment.newInstance(babelManager.getLanguages())).commit();
                    break;
                }
            }
            invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean shouldRequestWindowFeature() {
        return true;
    }

}
