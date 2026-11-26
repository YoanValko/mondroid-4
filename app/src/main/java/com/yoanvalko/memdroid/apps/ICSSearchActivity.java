package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Search for installed apps by name.
 */
public class ICSSearchActivity extends AppCompatActivity {

    private ListView lv;
    private EditText searchInput;
    private PackageManager pm;
    private List<AppItem> allApps = new ArrayList<>();
    private ArrayAdapter<AppItem> adapter;

    private static class AppItem {
        String label;
        String packageName;
        String className;
        android.graphics.drawable.Drawable icon;

        AppItem(String label, String packageName, String className, android.graphics.drawable.Drawable icon) {
            this.label = label;
            this.packageName = packageName;
            this.className = className;
            this.icon = icon;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Search Apps");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        searchInput = new EditText(this);
        searchInput.setHint("Search apps...");
        searchInput.setTextSize(16);
        searchInput.setPadding(8, 8, 8, 8);
        root.addView(searchInput);

        lv = new ListView(this);
        adapter = new ArrayAdapter<AppItem>(this, android.R.layout.simple_list_item_2, allApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                AppItem item = getItem(position);
                tv.setText(item.label);
                tv.setTextSize(16);
                tv.setPadding(16, 12, 16, 12);
                tv.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null);
                tv.setCompoundDrawablePadding(8);
                return tv;
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            AppItem item = allApps.get(position);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new android.content.ComponentName(item.packageName, item.className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        });
        root.addView(lv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        // Load all apps
        loadApps();

        // Search as you type
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        setContentView(root);
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo ri : resolveInfos) {
            if (ri.activityInfo == null) continue;
            allApps.add(new AppItem(
                    ri.loadLabel(pm) != null ? ri.loadLabel(pm).toString() : ri.activityInfo.name,
                    ri.activityInfo.packageName,
                    ri.activityInfo.name,
                    ri.loadIcon(pm)
            ));
        }
        adapter.notifyDataSetChanged();
    }

    private void filterApps(String query) {
        if (TextUtils.isEmpty(query)) {
            adapter.clear();
            adapter.addAll(allApps);
            return;
        }
        String q = query.toLowerCase();
        List<AppItem> filtered = new ArrayList<>();
        for (AppItem item : allApps) {
            if (item.label.toLowerCase().contains(q)) {
                filtered.add(item);
            }
        }
        adapter.clear();
        adapter.addAll(filtered);
    }
}
