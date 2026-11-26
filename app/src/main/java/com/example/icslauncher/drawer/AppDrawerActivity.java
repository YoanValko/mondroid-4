package com.example.icslauncher.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icslauncher.R;
import com.example.icslauncher.launcher.AppAdapter;
import com.example.icslauncher.launcher.AppModel;
import com.example.icslauncher.launcher.ICSLauncherActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Full-screen app drawer showing every installed activity that advertises
 * MAIN/LAUNCHER. Tapping an entry launches it through the public PackageManager API.
 * Includes a search bar to filter apps by name.
 */
public class AppDrawerActivity extends AppCompatActivity {

    private AppAdapter adapter;
    private TextView emptyView;
    private RecyclerView drawerGrid;
    private SearchView searchBar;
    private List<AppModel> allApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_drawer);

        emptyView = findViewById(R.id.empty_view);
        drawerGrid = findViewById(R.id.drawer_grid);
        searchBar = findViewById(R.id.search_bar);
        drawerGrid.setLayoutManager(new GridLayoutManager(this, 5));
        adapter = new AppAdapter(this, new ArrayList<>(), app -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(app.component);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                android.widget.Toast.makeText(this, "Cannot open " + app.label, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        drawerGrid.setAdapter(adapter);

        allApps = ICSLauncherActivity.queryLauncherApps(getPackageManager());
        if (allApps.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            drawerGrid.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            drawerGrid.setVisibility(View.VISIBLE);
            adapter.setItems(allApps);
        }

        // Pre-fill search if launched from launcher search bar
        String initialQuery = getIntent().getStringExtra("search_query");
        if (initialQuery != null && !initialQuery.isEmpty()) {
            searchBar.setQuery(initialQuery, true);
            filterApps(initialQuery);
        }

        // Search filter
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterApps(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterApps(newText);
                return true;
            }
        });
    }

    private void filterApps(String query) {
        if (TextUtils.isEmpty(query)) {
            adapter.setItems(allApps);
            emptyView.setVisibility(View.GONE);
            drawerGrid.setVisibility(View.VISIBLE);
            return;
        }
        List<AppModel> filtered = new ArrayList<>();
        String q = query.toLowerCase();
        for (AppModel app : allApps) {
            String label = app.label != null ? app.label.toString() : "";
            if (label.toLowerCase().contains(q)) {
                filtered.add(app);
            }
        }
        adapter.setItems(filtered);
        if (filtered.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            drawerGrid.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            drawerGrid.setVisibility(View.VISIBLE);
        }
    }
}
