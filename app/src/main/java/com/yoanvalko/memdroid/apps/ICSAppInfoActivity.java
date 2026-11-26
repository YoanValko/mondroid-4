package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

/**
 * App Info screen showing details about the ICS Home Launcher itself.
 */
public class ICSAppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("App Info");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), 0);

            addInfoRow(root, "App name", getString(R.string.app_name));
            addInfoRow(root, "Package name", getPackageName());
            addInfoRow(root, "Version", android.os.Build.VERSION.RELEASE);
            addInfoRow(root, "API level", String.valueOf(android.os.Build.VERSION.SDK_INT));
            addInfoRow(root, "Target SDK", String.valueOf(android.os.Build.VERSION.SDK_INT));
            addInfoRow(root, "Storage", appInfo.sourceDir);
            addInfoRow(root, "Data dir", appInfo.dataDir);

        } catch (PackageManager.NameNotFoundException e) {
            TextView error = new TextView(this);
            error.setText("Error loading app info: " + e.getMessage());
            error.setTextColor(0xFFFF0000);
            root.addView(error);
        }

        Button btnOk = new Button(this);
        btnOk.setText("OK");
        btnOk.setOnClickListener(v -> finish());
        root.addView(btnOk);

        setContentView(root);
    }

    private void addInfoRow(LinearLayout root, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 4, 0, 4);

        TextView lbl = new TextView(this);
        lbl.setText(label + ":");
        lbl.setTextSize(14);
        lbl.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        lbl.setPadding(0, 0, 8, 0);

        TextView val = new TextView(this);
        val.setText(value);
        val.setTextSize(14);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        val.setSingleLine(false);

        row.addView(lbl);
        row.addView(val);
        root.addView(row);
    }
}
