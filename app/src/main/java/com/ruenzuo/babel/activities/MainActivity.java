package com.ruenzuo.babel.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.ruenzuo.babel.R;
import com.ruenzuo.babel.definitions.OnDifficultySelectedListener;
import com.ruenzuo.babel.extensions.TrackedActivity;
import com.ruenzuo.babel.fragments.AboutDialogFragment;
import com.ruenzuo.babel.fragments.DifficultyDialogFragment;
import com.ruenzuo.babel.helpers.AuthorisationHelper;
import com.ruenzuo.babel.helpers.ErrorNotificationHelper;
import com.ruenzuo.babel.helpers.GooglePlayGameServicesHelper;
import com.ruenzuo.babel.helpers.SecureStorageHelper;
import com.ruenzuo.babel.models.enums.BabelAchievementType;
import com.ruenzuo.babel.models.enums.BabelDifficultyType;
import com.ruenzuo.babel.models.enums.DifficultyDialogFragmentType;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;


public class MainActivity extends TrackedActivity implements OnDifficultySelectedListener {

    @InjectView(R.id.btnStart)
    FButton btnStart;

    private static final int AUTHORISATION_REQUEST_CODE = 1;
    private static final int LEADERBOARD_REQUEST_CODE = 2;
    private static final int GAME_REQUEST_CODE = 3;
    private static final int ACHIEVEMENT_REQUEST_CODE = 4;
    private SecureStorageHelper secureStorageHelper;
    private AuthorisationHelper authorisationHelper = new AuthorisationHelper();
    private GooglePlayGameServicesHelper gameServicesHelper = new GooglePlayGameServicesHelper();
    private String token;
    private GameHelper gameHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        setupGameHelper();
        ButterKnife.inject(this);
        checkTokenValidity();
        if (!getResources().getBoolean(R.bool.google_play_build)) {
            checkForUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHelper.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TextUtils.isEmpty(token)) {
            getMenuInflater().inflate(R.menu.main_menu_logged_out, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu_logged_in, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.main_menu_action_log_in) {
            Intent intent = new Intent(this, GitHubOAuthActivity.class);
            startActivityForResult(intent, AUTHORISATION_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.main_menu_action_log_out) {
            revokeToken();
            return true;
        } else if (item.getItemId() == R.id.main_menu_action_share) {
            shareThisApp();
            return true;
        } else if (item.getItemId() == R.id.main_menu_action_about) {
            aboutThisApp();
            return true;
        } else if (item.getItemId() == R.id.main_menu_action_leaderboards) {
            if (gameHelper.isSignedIn()) {
                DifficultyDialogFragment dialogFragment = DifficultyDialogFragment.newInstance(DifficultyDialogFragmentType.DIFFICULTY_DIALOG_FRAGMENT_TYPE_LEADERBOARDS);
                dialogFragment.show(getFragmentManager(), "DifficultyDialogFragment");
            } else {
                gameHelper.beginUserInitiatedSignIn();
            }
        } else if (item.getItemId() == R.id.main_menu_action_achievements) {
            if (gameHelper.isSignedIn()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), ACHIEVEMENT_REQUEST_CODE);
            } else {
                gameHelper.beginUserInitiatedSignIn();
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHORISATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                token = data.getStringExtra("Token");
                secureStorageHelper.store(token);
                showLogOutView();
            }
        } else if (requestCode == GAME_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (gameHelper.isSignedIn()) {
                    BabelDifficultyType babelDifficultyType = (BabelDifficultyType) data.getSerializableExtra("DifficultyType");
                    int points = data.getIntExtra("Points", 0);
                    if (points != 0) {
                        Games.Leaderboards.submitScore(gameHelper.getApiClient(), babelDifficultyType.toLeaderboardIdentifier(), points);
                    }
                    BabelAchievementType achievement = gameServicesHelper.achievementUnlocked(points, babelDifficultyType);
                    if (achievement != null) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), achievement.toAchievementIdentifier());
                    }
                }
            }
        } else {
            gameHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this, getString(R.string.hockeyapp_id));
    }

    private void checkForUpdates() {
        UpdateManager.register(this, getString(R.string.hockeyapp_id));
    }

    @OnClick(R.id.btnStart)
    void start() {
        DifficultyDialogFragment dialogFragment = DifficultyDialogFragment.newInstance(DifficultyDialogFragmentType.DIFFICULTY_DIALOG_FRAGMENT_TYPE_START);
        dialogFragment.show(getFragmentManager(), "DifficultyDialogFragment");
    }

    public void setupGameHelper() {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        GameHelper.GameHelperListener listener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInSucceeded() {

            }
            @Override
            public void onSignInFailed() {

            }
        };
        gameHelper.setup(listener);
    }

    private void showLogInView() {
        btnStart.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    private void showLogOutView() {
        btnStart.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    private void checkTokenValidity() {
        secureStorageHelper = new SecureStorageHelper(getApplicationContext());
        final String retrievedToken = secureStorageHelper.retrieveToken();
        if (!retrievedToken.equalsIgnoreCase(SecureStorageHelper.NO_KEY_FOUND)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.checking_session_validity));
            authorisationHelper.checkTokenValidity(retrievedToken).continueWith(new Continuation<Boolean, Void>() {
                @Override
                public Void then(Task<Boolean> task) throws Exception {
                    progressDialog.dismiss();
                    if (task.getError() != null) {
                        ErrorNotificationHelper.notifyError(MainActivity.this);
                    } else {
                        if (!task.getResult()) {
                            Toast.makeText(MainActivity.this, getString(R.string.session_expired), Toast.LENGTH_LONG).show();
                        } else {
                            MainActivity.this.token = retrievedToken;
                            showLogOutView();
                        }
                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);
        }
    }

    private void revokeToken() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.logging_out));
        authorisationHelper.revokeToken(token).continueWith(new Continuation<Boolean, Void>() {
            @Override
            public Void then(Task<Boolean> task) throws Exception {
                progressDialog.dismiss();
                if (task.getError() != null) {
                    ErrorNotificationHelper.notifyError(MainActivity.this);
                } else {
                    if (!task.getResult()) {
                        ErrorNotificationHelper.notifyError(MainActivity.this);
                    } else {
                        MainActivity.this.token = null;
                        secureStorageHelper.deleteToken();
                        showLogInView();
                    }
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private void shareThisApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check Babel on Google Play! https://play.google.com/store/apps/details?id=com.ruenzuo.babel");
        startActivity(Intent.createChooser(intent, "Share this app"));
    }

    private void aboutThisApp() {
        DialogFragment dialog = new AboutDialogFragment();
        dialog.show(getFragmentManager(), "InfoDialogFragment");
    }

    @Override
    public void onDifficultySelected(BabelDifficultyType babelDifficultyType, DifficultyDialogFragmentType difficultyDialogFragmentType) {
        switch (difficultyDialogFragmentType) {
            case DIFFICULTY_DIALOG_FRAGMENT_TYPE_START: {
                Intent intent = new Intent(this, BabelActivity.class);
                intent.putExtra("DifficultyType", babelDifficultyType);
                intent.putExtra("Token", token);
                startActivityForResult(intent, GAME_REQUEST_CODE);
                break;
            }
            case DIFFICULTY_DIALOG_FRAGMENT_TYPE_LEADERBOARDS: {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), babelDifficultyType.toLeaderboardIdentifier()), LEADERBOARD_REQUEST_CODE);
                break;
            }
        }
    }
}
