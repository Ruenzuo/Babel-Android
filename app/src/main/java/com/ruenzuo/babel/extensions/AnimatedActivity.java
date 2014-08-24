package com.ruenzuo.babel.extensions;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.ruenzuo.babel.R;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public abstract class AnimatedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (shouldRequestWindowFeature()) {
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        }
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    public abstract boolean shouldRequestWindowFeature();

}
