package com.ruenzuo.babel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.extensions.AnimatedActivity;
import com.ruenzuo.babel.helpers.TranslatorHelper;
import com.ruenzuo.babel.helpers.URLHelper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class GitHubOAuthActivity extends AnimatedActivity {

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private OkHttpClient httpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_oauth_activity_layout);
        ButterKnife.inject(this);
        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equalsIgnoreCase("babel")) {
                    webView.setVisibility(View.GONE);
                } else {
                    webView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equalsIgnoreCase("babel")) {
                    String code = uri.getQueryParameter("code");
                    if (code != null) {
                        getAccessToken(code);
                    } else {
                        Toast.makeText(GitHubOAuthActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        };
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(URLHelper.getURLForAuthorization());
    }

    public void getAccessToken(final String code) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Retrieving access token");
        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Request request = new Request.Builder()
                        .url(URLHelper.getURLForAccessToken(code))
                        .build();
                Response response = httpClient.newCall(request).execute();
                return response.body().string();
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                progressDialog.dismiss();
                if (task.getError() == null) {
                    Map<String, String> map = TranslatorHelper.translateToMap(task.getResult());
                    String token = map.get("access_token");
                    if (token != null) {
                        Intent intent = new Intent();
                        intent.putExtra("token", token);
                        setResult(RESULT_OK, intent);
                        return null;
                    }
                }
                Toast.makeText(GitHubOAuthActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                finish();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

}
