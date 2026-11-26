package com.yoanvalko.memdroid.apps;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yoanvalko.memdroid.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ICSGalleryActivity extends AppCompatActivity {

    private static final int REQ_IMAGES = 1002;

    private GridView grid;
    private TextView emptyView;
    private final List<Long> ids = new ArrayList<>();
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        emptyView = new TextView(this);
        emptyView.setText("No photos found");
        emptyView.setTextSize(16);
        emptyView.setPadding(0, 24, 0, 0);
        emptyView.setVisibility(View.GONE);
        root.addView(emptyView);

        grid = new GridView(this);
        grid.setNumColumns(3);
        grid.setVerticalSpacing(4);
        grid.setHorizontalSpacing(4);
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        adapter = new PhotoAdapter();
        grid.setAdapter(adapter);
        root.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        setContentView(root);

        ensureImagesPermission();
    }

    private void ensureImagesPermission() {
        String perm = imagesPermission();
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            loadPhotos();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{perm}, REQ_IMAGES);
        }
    }

    private String imagesPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return android.Manifest.permission.READ_MEDIA_IMAGES;
        }
        return android.Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    private void loadPhotos() {
        ids.clear();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor c = null;
        try {
            c = getContentResolver().query(uri, projection, null, null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (c != null) {
                int idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                while (c.moveToNext()) ids.add(c.getLong(idCol));
            }
        } finally {
            if (c != null) c.close();
        }
        adapter.notifyDataSetChanged();
        if (ids.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            grid.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
        }
    }

    private class PhotoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ids.size();
        }

        @Override
        public Long getItem(int position) {
            return ids.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position);
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            ImageView iv;
            if (convertView == null) {
                iv = new ImageView(ICSGalleryActivity.this);
                int size = ICSGalleryActivity.this.getResources().getDisplayMetrics().widthPixels / 3;
                iv.setLayoutParams(new GridView.LayoutParams(size, size));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                iv = (ImageView) convertView;
            }
            long imageId = getItem(position);
            Bitmap thumb = android.provider.MediaStore.Images.Thumbnails.getThumbnail(
                    ICSGalleryActivity.this.getContentResolver(), imageId,
                    android.provider.MediaStore.Images.Thumbnails.MINI_KIND, null);
            iv.setImageBitmap(thumb);
            return iv;
        }
    }
}
