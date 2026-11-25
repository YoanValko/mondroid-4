package com.example.icslauncher.apps;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.icslauncher.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reimplementation of the ICS Gallery. Browses images from MediaStore and opens a selection
 * full-screen. Requires READ_MEDIA_IMAGES (API 33+) / READ_EXTERNAL_STORAGE (API <= 32).
 */
public class ICSGalleryActivity extends AppCompatActivity {

    private static final int REQ_IMAGES = 1002;

    private GridView grid;
    private TextView emptyView;
    private final List<Long> ids = new ArrayList<>();
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        grid = findViewById(R.id.gallery_grid);
        emptyView = findViewById(R.id.gallery_empty);
        adapter = new PhotoAdapter(this);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, view, position, id) -> {
            long imageId = ids.get(position);
            Uri uri = android.content.ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
            android.content.Intent intent = new android.content.Intent(this, ICSPhotoViewActivity.class);
            intent.putExtra("uri", uri.toString());
            startActivity(intent);
        });

        ensureImagesPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotos();
            } else {
                emptyView.setText(R.string.perm_denied);
            }
        }
    }

    private void ensureImagesPermission() {
        String perm = imagesPermission();
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            loadPhotos();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(perm)) {
                    Toast.makeText(this, R.string.perm_storage_rationale, Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{perm}, REQ_IMAGES);
            } else {
                loadPhotos();
            }
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

    private static class PhotoAdapter extends BaseAdapter {
        private final android.content.Context context;
        PhotoAdapter(android.content.Context context) { this.context = context; }
        @Override public int getCount() { return ((ICSGalleryActivity) context).ids.size(); }
        @Override public Long getItem(int position) { return ((ICSGalleryActivity) context).ids.get(position); }
        @Override public long getItemId(int position) { return getItem(position); }
        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            ImageView iv;
            if (convertView == null) {
                iv = new ImageView(context);
                int size = context.getResources().getDisplayMetrics().widthPixels / 3;
                iv.setLayoutParams(new GridView.LayoutParams(size, size));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                iv = (ImageView) convertView;
            }
            long imageId = getItem(position);
            Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(
                    context.getContentResolver(), imageId,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            iv.setImageBitmap(thumb);
            return iv;
        }
    }
}
