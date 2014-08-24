package com.ruenzuo.babel.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.extensions.AnimatedActivity;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class LicensesActivity extends AnimatedActivity {

    @InjectView(R.id.vwTextLicenses)
    TextView vwTextLicenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licenses_activity_layout);
        ButterKnife.inject(this);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("licenses/licenses.txt");
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(inputStream, stringWriter);
            String licenses = stringWriter.toString();
            vwTextLicenses.setText(licenses);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_while_loading_licenses), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean shouldRequestWindowFeature() {
        return false;
    }

}
