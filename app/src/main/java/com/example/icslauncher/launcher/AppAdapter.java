package com.example.icslauncher.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icslauncher.R;

import java.util.List;

/**
 * Adapter for grids of app shortcuts (workspace and app drawer).
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private final Context context;
    private final List<AppModel> items;
    private final OnAppClickListener listener;

    public interface OnAppClickListener {
        void onAppClick(AppModel app);
    }

    public AppAdapter(Context context, List<AppModel> items, OnAppClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<AppModel> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = items.get(position);
        holder.icon.setImageDrawable(app.icon != null ? app.icon : context.getDrawable(R.drawable.ic_launcher));
        holder.label.setText(app.label);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAppClick(app);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;
        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_icon);
            label = itemView.findViewById(R.id.item_label);
        }
    }
}
