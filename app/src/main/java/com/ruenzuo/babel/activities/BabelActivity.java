package com.ruenzuo.babel.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.definitions.OnGuessSelectedListener;
import com.ruenzuo.babel.extensions.AnimatedActivity;
import com.ruenzuo.babel.extensions.GitHubAPIRateLimitException;
import com.ruenzuo.babel.fragments.GuessOptionsFragment;
import com.ruenzuo.babel.fragments.SourceCodeFragment;
import com.ruenzuo.babel.managers.BabelManager;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.ruenzuo.babel.models.enums.BabelDifficultyType;
import com.ruenzuo.babel.models.enums.BabelFragmentType;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class BabelActivity extends AnimatedActivity implements ActionBar.OnNavigationListener, OnGuessSelectedListener {

    private BabelManager babelManager;
    private BabelFragmentType currentBabelFragmentType = BabelFragmentType.BABEL_FRAGMENT_TYPE_SOURCE_CODE;
    private int remainingHints = 5;
    private int remainingSkips = 5;
    private int points = 0;
    private boolean isLoading = false;
    private boolean isPooling = false;
    private boolean isHintEnabled = false;
    private Language currentLanguage;
    private Repository currentRepository;
    private File currentFile;
    private String currentHTMLString;
    private Timer timer;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babel_activity_layout);
        getActionBar().addTab(getActionBar().newTab().setText("Code").setTabListener(listener));
        getActionBar().addTab(getActionBar().newTab().setText("Guess").setTabListener(listener));
        String token = getIntent().getStringExtra("Token");
        BabelDifficultyType babelDifficultyType = (BabelDifficultyType) getIntent().getSerializableExtra("DifficultyType");
        setupManager(babelDifficultyType, token);
        setLoadingIndicators();
        nextFile();
    }

    private ActionBar.TabListener listener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            BabelFragmentType selectedBabelFragmentType = BabelFragmentType.values()[tab.getPosition()];
            if (selectedBabelFragmentType != currentBabelFragmentType) {
                currentBabelFragmentType = selectedBabelFragmentType;
                switch (currentBabelFragmentType) {
                    case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                        getFragmentManager().beginTransaction().replace(R.id.vwFrame, SourceCodeFragment.newInstance(currentHTMLString), "SourceCodeFragment").commit();
                        break;
                    }
                    case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                        Fragment fragment;
                        if (isHintEnabled) {
                            fragment = GuessOptionsFragment.newInstance(babelManager.getHintLanguages());
                        } else {
                            fragment = GuessOptionsFragment.newInstance(babelManager.getLanguages());
                        }
                        getFragmentManager().beginTransaction().replace(R.id.vwFrame, fragment, "GuessOptionsFragment").commit();
                        break;
                    }
                }
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    };

    private void setLoadingIndicators() {
        switch (currentBabelFragmentType) {
            case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                Fragment fragment = getFragmentManager().findFragmentByTag("SourceCodeFragment");
                if (fragment != null) {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                }
                break;
            }
            case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                Fragment fragment = getFragmentManager().findFragmentByTag("GuessOptionsFragment");
                if (fragment != null) {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                }
                break;
            }
        }
        setProgressBarIndeterminateVisibility(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().selectTab(getActionBar().getTabAt(0));
        isLoading = true;
        invalidateOptionsMenu();
    }

    private void nextFile() {
        Log.d("BabelActivity", "Next file loading.");
        isHintEnabled = false;
        babelManager.loadNext().continueWith(new Continuation<Hashtable<String, Object>, Object>() {
            @Override
            public Object then(Task<Hashtable<String, Object>> task) throws Exception {
                Log.d("BabelActivity", "Load next done.");
                if (task.getError() != null) {
                    if (task.getError() instanceof GitHubAPIRateLimitException) {
                        Log.e("BabelActivity", "Rate limit reached.");
                        if (!isPooling) {
                            poolRate();
                        }
                    } else {
                        Log.e("BabelActivity", "Error while nextFile: " + task.getError().getLocalizedMessage());
                        nextFile();
                    }
                } else {
                    if (isPooling) {
                        stopPool();
                    }
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

    private void poolRate() {
        isPooling = true;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Pooling rate.");
        }
        progressDialog.show();
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                nextFile();
            }

        }, 0, 5 * 1000);
    }

    private void stopPool() {
        progressDialog.dismiss();
        isPooling = false;
        timer.cancel();
    }

    private void loadCurrentFile() {
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        currentBabelFragmentType = BabelFragmentType.BABEL_FRAGMENT_TYPE_SOURCE_CODE;
        getFragmentManager().beginTransaction().replace(R.id.vwFrame, SourceCodeFragment.newInstance(currentHTMLString), "SourceCodeFragment").commit();
        isLoading = false;
        invalidateOptionsMenu();
    }

    private void setupManager(BabelDifficultyType babelDifficultyType, String token) {
        babelManager = new BabelManager(babelDifficultyType, token, this);
        babelManager.setupQueue(this);
    }

    private void hint() {
        remainingHints--;
        isHintEnabled = true;
        invalidateOptionsMenu();
        babelManager.setupHintLanguages(currentLanguage);
        getFragmentManager().beginTransaction().replace(R.id.vwFrame, GuessOptionsFragment.newInstance(babelManager.getHintLanguages()), "GuessOptionsFragment").commit();
    }

    public String getSkippedString() {
        return getString(R.string.skipped) + ":\n" +
                getString(R.string.language) + ": " + currentLanguage.getName() + "\n" +
                getString(R.string.file) + ": " + currentFile.getName() + "\n" +
                getString(R.string.repository) + ": " + currentRepository.getName();
    }

    private void skip() {
        remainingSkips--;
        setLoadingIndicators();
        Toast.makeText(this, getSkippedString(), Toast.LENGTH_LONG).show();
        nextFile();
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
                    if (remainingSkips > 0) {
                        menu.add(Menu.NONE, R.id.action_skip, Menu.NONE, getSkipTitle()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    }
                    return true;
                }
                case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                    if (remainingHints > 0 && !isHintEnabled) {
                        menu.add(Menu.NONE, R.id.action_hint, Menu.NONE, getHintTitle()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    }
                    return true;
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_hint) {
            hint();
        } else if (item.getItemId() == R.id.action_skip) {
            skip();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean shouldRequestWindowFeature() {
        return true;
    }

    public String getSuccessString() {
        return getString(R.string.success) + "\n" +
                getString(R.string.file) + ": " + currentFile.getName() + "\n" +
                getString(R.string.repository) + ": " + currentRepository.getName();
    }

    public String getFailedString() {
        return getString(R.string.failed) + "\n" +
                getString(R.string.language) + ": " + currentLanguage.getName() + "\n" +
                getString(R.string.file) + ": " + currentFile.getName() + "\n" +
                getString(R.string.repository) + ": " + currentRepository.getName() + "\n" +
                getString(R.string.total_points) + ": " + points;
    }

    @Override
    public void onGuessSelected(Language language) {
        if (currentLanguage.getIndex() == language.getIndex()) {
            points++;
            Toast.makeText(this, getSuccessString(), Toast.LENGTH_LONG).show();
            setLoadingIndicators();
            nextFile();
        } else {
            Toast.makeText(this, getFailedString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("Points", points);
            intent.putExtra("DifficultyType", babelManager.getBabelDifficultyType());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
