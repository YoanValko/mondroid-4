package com.yoanvalko.memdroid.apps;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

/**
 * Reimplementation of the ICS Browser: a simple WebView browser using public APIs.
 */
public class ISBrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private EditText urlBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        urlBar = findViewById(R.id.url_bar);
        urlBar.setText("https://www.example.com");

        Button go = findViewById(R.id.btn_go);
        go.setOnClickListener(this);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(urlBar.getText().toString());
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
