package com.yoanvalko.memdroid.apps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.io.InputStream;

/**
 * Full-screen photo view opened from the Gallery. Reads only the URI the user selected in the
 * gallery (scoped-storage friendly).
 */
public class ICSPhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String uriString = getIntent().getStringExtra("uri");
        ImageView photo = findViewById(R.id.photo);
        if (uriString != null) {
            try {
                Uri uri = Uri.parse(uriString);
                InputStream in = getContentResolver().openInputStream(uri);
                if (in != null) {
                    BitmapFactory.Options bounds = new BitmapFactory.Options();
                    bounds.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(in, null, bounds);
                    in.close();

                    int max = 1024;
                    int sample = 1;
                    while (bounds.outWidth / sample > max || bounds.outHeight / sample > max) {
                        sample *= 2;
                    }

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = sample;
                    InputStream in2 = getContentResolver().openInputStream(uri);
                    Bitmap b = BitmapFactory.decodeStream(in2, null, opts);
                    if (in2 != null) in2.close();
                    if (b != null) photo.setImageBitmap(b);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
