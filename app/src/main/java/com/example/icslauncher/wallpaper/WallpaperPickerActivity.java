package com.example.icslauncher.wallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icslauncher.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 4.0.4-style wallpaper picker: static bundled wallpapers, the ICS live wallpaper, and an option
 * to choose a photo from the user's media (scoped-storage friendly: we only read a URI the user
 * explicitly picked).
 */
public class WallpaperPickerActivity extends AppCompatActivity
        implements WallpaperAdapter.OnItemSelected {

    private static final int REQ_PICK_PHOTO = 1005;

    private WallpaperManager wallpaperManager;
    private WallpaperAdapter adapter;
    private ImageView preview;
    private Button btnSet;
    private int selectedDrawableRes = -1;
    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_picker);

        wallpaperManager = WallpaperManager.getInstance(this);

        preview = findViewById(R.id.wallpaper_preview);
        btnSet = findViewById(R.id.btn_set_wallpaper);

        RecyclerView grid = findViewById(R.id.wallpaper_grid);
        grid.setLayoutManager(new GridLayoutManager(this, 4));

        List<WallpaperAdapter.Item> items = new ArrayList<>();
        items.add(new WallpaperAdapter.Item(R.drawable.ic_cream_sandwich, getString(R.string.wallpaper_ics_default)));
        items.add(new WallpaperAdapter.Item(R.drawable.ic_ics_android, getString(R.string.wallpaper_ics_blue)));
        items.add(new WallpaperAdapter.Item(R.drawable.ic_wallpaper_thumb, getString(R.string.wallpaper_ics_sand)));

        adapter = new WallpaperAdapter(this, items, this);
        grid.setAdapter(adapter);

        // Default preview.
        preview.setImageResource(R.drawable.ic_cream_sandwich);
        selectedDrawableRes = R.drawable.ic_cream_sandwich;
        selectedPhotoUri = null;
        btnSet.setEnabled(true);

        Button btnLive = findViewById(R.id.btn_live_wallpaper);
        btnLive.setOnClickListener(v -> setLiveWallpaper());

        Button btnPhoto = findViewById(R.id.btn_choose_photo);
        btnPhoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_PICK_PHOTO);
        });

        btnSet.setOnClickListener(v -> setSelectedWallpaper());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedPhotoUri = uri;
            selectedDrawableRes = -1;
            // Show the picked photo in the preview (downsampled for safety).
            preview.setImageURI(uri);
            btnSet.setEnabled(true);
            btnSet.setText(R.string.wallpaper_set_done);
        }
    }

    @Override
    public void onItemSelected(int position, WallpaperAdapter.Item item) {
        selectedDrawableRes = item.drawableRes;
        selectedPhotoUri = null;
        preview.setImageResource(item.drawableRes);
        btnSet.setEnabled(true);
        btnSet.setText(R.string.wallpaper_set_done);
    }

    private void setSelectedWallpaper() {
        if (selectedPhotoUri != null) {
            setWallpaperFromUri(selectedPhotoUri);
        } else if (selectedDrawableRes != -1) {
            setStaticWallpaper(selectedDrawableRes);
        }
    }

    private void setStaticWallpaper(int resId) {
        try {
            wallpaperManager.setResource(resId);
            Toast.makeText(this, R.string.wallpaper_set_done, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.wallpaper_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setLiveWallpaper() {
        try {
            ComponentName cn = new ComponentName(this, ICSWallpaperService.class);
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, cn);
            startActivity(intent);
            Toast.makeText(this, "Opening live wallpaper picker…", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.wallpaper_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setWallpaperFromUri(Uri uri) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream s1 = getContentResolver().openInputStream(uri)) {
                if (s1 == null) throw new Exception("Unable to open image");
                BitmapFactory.decodeStream(s1, null, bounds);
            }

            int reqW = wallpaperManager.getDesiredMinimumWidth();
            int reqH = wallpaperManager.getDesiredMinimumHeight();
            int sample = 1;
            while ((bounds.outWidth / sample) > reqW * 2 && (bounds.outHeight / sample) > reqH * 2) {
                sample *= 2;
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bitmap;
            try (InputStream s2 = getContentResolver().openInputStream(uri)) {
                if (s2 == null) throw new Exception("Unable to open image");
                bitmap = BitmapFactory.decodeStream(s2, null, opts);
            }
            if (bitmap == null) throw new Exception("Failed to decode image");

            wallpaperManager.setBitmap(bitmap);
            bitmap.recycle();
            Toast.makeText(this, R.string.wallpaper_set_done, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.wallpaper_error, Toast.LENGTH_SHORT).show();
        }
    }
}
