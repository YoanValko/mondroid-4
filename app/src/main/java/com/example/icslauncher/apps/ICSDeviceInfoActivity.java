package com.example.icslauncher.apps;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

/**
 * Device Info screen showing system information.
 */
public class ICSDeviceInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Device Info");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        addInfoRow(root, "Model", Build.MODEL);
        addInfoRow(root, "Manufacturer", Build.MANUFACTURER);
        addInfoRow(root, "Brand", Build.BRAND);
        addInfoRow(root, "Device", Build.DEVICE);
        addInfoRow(root, "Product", Build.PRODUCT);
        addInfoRow(root, "Board", Build.BOARD);
        addInfoRow(root, "Hardware", Build.HARDWARE);
        addInfoRow(root, "Android Version", Build.VERSION.RELEASE);
        addInfoRow(root, "API Level", String.valueOf(Build.VERSION.SDK_INT));
        addInfoRow(root, "Codename", Build.VERSION.CODENAME);
        addInfoRow(root, "Incremental", Build.VERSION.INCREMENTAL);
        addInfoRow(root, "Fingerprint", Build.FINGERPRINT);
        addInfoRow(root, "Host", Build.HOST);
        addInfoRow(root, "Tags", Build.TAGS);
        addInfoRow(root, "Type", Build.TYPE);
        addInfoRow(root, "User", Build.USER);
        addInfoRow(root, "Radio", "N/A");
        addInfoRow(root, "Serial", Build.SERIAL);
        addInfoRow(root, "Time", String.valueOf(Build.TIME));

        // Storage info
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long total = stat.getTotalBytes();
            long free = stat.getFreeBytes();
            long used = total - free;
            addInfoRow(root, "Storage Total", formatBytes(total));
            addInfoRow(root, "Storage Used", formatBytes(used));
            addInfoRow(root, "Storage Free", formatBytes(free));
        } catch (Exception e) {
            addInfoRow(root, "Storage", "N/A");
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
        val.setText(value != null ? value : "N/A");
        val.setTextSize(14);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        val.setSingleLine(false);

        row.addView(lbl);
        row.addView(val);
        root.addView(row);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), units[exp]);
    }
}
