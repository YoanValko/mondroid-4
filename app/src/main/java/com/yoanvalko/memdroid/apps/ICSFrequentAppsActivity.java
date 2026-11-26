package com.yoanvalko.memdroid.apps;

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

import com.yoanvalko.memdroid.R;
import com.yoanvalko.memdroid.launcher.AppFrequencyTracker;

import java.util.List;

/**
 * Shows the most frequently used apps based on launch tracking.
 */
public class ICSFrequentAppsActivity extends AppCompatActivity {

    private ListView listView;
    private AppFrequencyTracker tracker;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = new AppFrequencyTracker(this);
        pm = getPackageManager();

        List<String> topApps = tracker.getTopApps(20);

        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, topApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                String pkg = getItem(position);
                int count = tracker.getLaunchCount(pkg);
                try {
                    ResolveInfo ri = pm.resolveActivity(
                            new Intent(Intent.ACTION_MAIN).setPackage(pkg), 0);
                    String label = ri != null && ri.loadLabel(pm) != null
                            ? ri.loadLabel(pm).toString() : pkg;
                    tv.setText(label + "  (" + count + "x)");
                    tv.setTextSize(16);
                    tv.setPadding(16, 12, 16, 12);
                    tv.setCompoundDrawablesWithIntrinsicBounds(
                            ri != null ? ri.loadIcon(pm) : getDrawable(android.R.drawable.ic_dialog_info),
                            null, null, null);
                    tv.setCompoundDrawablePadding(8);
                } catch (Exception e) {
                    tv.setText(pkg + " (" + count + "x)");
                    tv.setTextSize(16);
                }
                return tv;
            }
        });
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String pkg = topApps.get(position);
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            if (intent != null) {
                startActivity(intent);
            }
        });

        setContentView(lv);
    }
}
