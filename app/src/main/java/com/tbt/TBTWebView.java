package com.tbt;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by bradley on 20-08-2016.
 */
public class TBTWebView extends AppCompatActivity {
    WebView webView;
    ProgressBar spinner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cutom_web_view_layout);
        initElements();

        final String destinationURL = getIntent().getExtras().getString("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spinner.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(destinationURL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.stopLoading();
    }

    void initElements() {
        webView = (WebView) findViewById(R.id.tbt_web_view);
        spinner = (ProgressBar) findViewById(R.id.tbt_web_view_progress_circle);
        spinner.setVisibility(View.GONE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
