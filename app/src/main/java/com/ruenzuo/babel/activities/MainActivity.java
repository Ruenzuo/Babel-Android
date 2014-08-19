package com.ruenzuo.babel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.helpers.AuthorisationHelper;
import com.ruenzuo.babel.helpers.SecureStorageHelper;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;


public class MainActivity extends Activity {

    private static final int AUTHORISATION_REQUEST_CODE = 1;
    private SecureStorageHelper secureStorageHelper;
    private AuthorisationHelper authorisationHelper = new AuthorisationHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        checkTokenValidity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.main_menu_log_in) {
            Intent intent = new Intent(this, GitHubOAuthActivity.class);
            startActivityForResult(intent, AUTHORISATION_REQUEST_CODE);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHORISATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String token = data.getStringExtra("token");
                secureStorageHelper.store(token);
            }
        }
    }

    private void checkTokenValidity() {
        secureStorageHelper = new SecureStorageHelper(getApplicationContext());
        String token = secureStorageHelper.retrieveToken();
        if (!token.equalsIgnoreCase(SecureStorageHelper.NO_KEY_FOUND)) {
            authorisationHelper.checkTokenValidity(token);
        }
    }

}
