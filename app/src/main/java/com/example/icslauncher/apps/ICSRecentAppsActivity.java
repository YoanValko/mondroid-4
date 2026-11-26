package com.example.icslauncher.apps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;
import com.example.icslauncher.launcher.AppRecencyTracker;

import java.util.List;

/**
 * Shows the most recently launched apps based on launch tracking.
 */
public class ICSRecentAppsActivity extends AppCompatActivity {

    private AppRecencyTracker tracker;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = new AppRecencyTracker(this);
        pm = getPackageManager();

        List<String> recentApps = tracker.getRecentApps(10);

        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, recentApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                String pkg = getItem(position);
                try {
                    ResolveInfo ri = pm.resolveActivity(
                            new Intent(Intent.ACTION_MAIN).setPackage(pkg), 0);
                    String label = ri != null && ri.loadLabel(pm) != null
                            ? ri.loadLabel(pm).toString() : pkg;
                    tv.setText(label);
                    tv.setTextSize(16);
                    tv.setPadding(16, 12, 16, 12);
                    tv.setCompoundDrawablesWithIntrinsicBounds(
                            ri != null ? ri.loadIcon(pm) : getDrawable(android.R.drawable.ic_dialog_info),
                            null, null, null);
                    tv.setCompoundDrawablePadding(8);
                } catch (Exception e) {
                    tv.setText(pkg);
                    tv.setTextSize(16);
                }
                return tv;
            }
        });
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String pkg = recentApps.get(position);
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            if (intent != null) {
                startActivity(intent);
            }
        });

        setContentView(lv);
    }
}
