package com.ruenzuo.babel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.helpers.SecureStorageHelper;

import butterknife.InjectView;


public class MenuActivity extends Activity {

    private static final int AUTHORISATION_REQUEST_CODE = 1;
    private SecureStorageHelper secureStorageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secureStorageHelper = new SecureStorageHelper(getApplicationContext());
        setContentView(R.layout.menu_activity_layout);
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
}
