package com.ruenzuo.babel.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.definitions.OnDifficultySelectedListener;
import com.ruenzuo.babel.fragments.AboutDialogFragment;
import com.ruenzuo.babel.fragments.DifficultyDialogFragment;
import com.ruenzuo.babel.helpers.AuthorisationHelper;
import com.ruenzuo.babel.helpers.ErrorNotificationHelper;
import com.ruenzuo.babel.helpers.SecureStorageHelper;
import com.ruenzuo.babel.models.enums.DifficultyType;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;


public class MainActivity extends Activity implements OnDifficultySelectedListener {

    @InjectView(R.id.btnStart)
    FButton btnStart;

    private static final int AUTHORISATION_REQUEST_CODE = 1;
    private SecureStorageHelper secureStorageHelper;
    private AuthorisationHelper authorisationHelper = new AuthorisationHelper();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.inject(this);
        checkTokenValidity();
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
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHORISATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                token = data.getStringExtra("token");
                secureStorageHelper.store(token);
                showLogOutView();
            }
        }
    }

    @OnClick(R.id.btnStart)
    void start() {
        DifficultyDialogFragment dialogFragment = new DifficultyDialogFragment();
        dialogFragment.show(getFragmentManager(), "DifficultyDialogFragment");
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
    public void onDifficultySelected(DifficultyType difficultyType) {
        Intent intent = new Intent(this, BabelActivity.class);
        intent.putExtra("DifficultyType", difficultyType);
        intent.putExtra("Token", token);
        startActivity(intent);
    }

}
