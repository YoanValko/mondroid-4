package com.example.icslauncher.wallpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icslauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid adapter for the static wallpaper choices bundled with the launcher.
 */
public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    public static class Item {
        public int drawableRes;
        public CharSequence label;
        public Item(int drawableRes, CharSequence label) {
            this.drawableRes = drawableRes;
            this.label = label;
        }
    }

    private final Context context;
    private final List<Item> items = new ArrayList<>();
    private final OnItemSelected listener;
    private int selectedPosition = -1;

    public interface OnItemSelected {
        void onItemSelected(int position, Item item);
    }

    public WallpaperAdapter(Context context, List<Item> items, OnItemSelected listener) {
        this.context = context;
        if (items != null) this.items.addAll(items);
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        int prev = selectedPosition;
        selectedPosition = position;
        if (prev != -1) notifyItemChanged(prev);
        if (selectedPosition != -1) notifyItemChanged(selectedPosition);
        if (listener != null && selectedPosition != -1) {
            listener.onItemSelected(selectedPosition, items.get(selectedPosition));
        }
    }

    public int getSelectedPosition() { return selectedPosition; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.thumb.setImageResource(item.drawableRes);
        holder.thumb.setContentDescription(item.label);
        holder.check.setSelected(position == selectedPosition);
        holder.check.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> setSelectedPosition(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        ImageView check;
        ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.wallpaper_thumb);
            check = itemView.findViewById(R.id.wallpaper_check);
        }
    }
}
