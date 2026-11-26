package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

/**
 * Reimplementation of the Ice Cream Sandwich Browser.
 */
public class ISBrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private EditText urlBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 0);

        LinearLayout urlBarLayout = new LinearLayout(this);
        urlBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        urlBarLayout.setPadding(8, 8, 8, 8);

        urlBar = new EditText(this);
        urlBar.setText("https://www.google.com");
        urlBar.setTextSize(16);
        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        urlBar.setLayoutParams(urlParams);

        Button goBtn = new Button(this);
        goBtn.setText("Go");
        goBtn.setTextSize(14);
        LinearLayout.LayoutParams goParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        goBtn.setLayoutParams(goParams);
        goBtn.setOnClickListener(this);

        urlBarLayout.addView(urlBar);
        urlBarLayout.addView(goBtn);
        root.addView(urlBarLayout);

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com");
        root.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        setContentView(root);
    }

    @Override
    public void onClick(View v) {
        String url = urlBar.getText().toString().trim();
        if (!url.isEmpty() && !url.contains("://")) url = "https://" + url;
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
