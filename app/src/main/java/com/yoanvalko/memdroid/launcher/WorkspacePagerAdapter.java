package com.yoanvalko.memdroid.launcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yoanvalko.memdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Pager adapter for multiple home screen workspace pages. Each page shows a grid of app
 * shortcuts. Extends RecyclerView.Adapter (compatible with ViewPager2).
 */
public class WorkspacePagerAdapter extends RecyclerView.Adapter<WorkspacePagerAdapter.PageViewHolder> {

    private final Context context;
    private final List<List<AppModel>> pages;

    public WorkspacePagerAdapter(Context context, List<List<AppModel>> pages) {
        this.context = context;
        this.pages = pages != null ? pages : new ArrayList<>();
    }

    public int getPageCount() { return pages.size(); }

    public List<AppModel> getPage(int position) {
        if (position >= 0 && position < pages.size()) return pages.get(position);
        return new ArrayList<>();
    }

    public void setPage(int position, List<AppModel> items) {
        while (pages.size() <= position) pages.add(new ArrayList<>());
        pages.set(position, items);
        notifyItemChanged(position);
    }

    public void addPage() {
        pages.add(new ArrayList<>());
        notifyItemInserted(pages.size() - 1);
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.page_workspace, parent, false);
        return new PageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<AppModel> items = getPage(position);
        holder.adapter.setItems(items);
    }

    @Override
    public int getItemCount() { return pages.size(); }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        RecyclerView grid;
        AppAdapter adapter;
        PageViewHolder(View itemView) {
            super(itemView);
            grid = itemView.findViewById(R.id.page_grid);
            grid.setLayoutManager(new GridLayoutManager(itemView.getContext(), 4));
            grid.setPadding(16, 16, 16, 16);
            grid.setClipToPadding(false);
            List<AppModel> emptyItems = new ArrayList<>();
            adapter = new AppAdapter(itemView.getContext(), emptyItems, app -> {
                if (app.component != null) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(app.component);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    try {
                        itemView.getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        android.widget.Toast.makeText(itemView.getContext(),
                                "Cannot open " + app.label, android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
            grid.setAdapter(adapter);
        }
    }
}
