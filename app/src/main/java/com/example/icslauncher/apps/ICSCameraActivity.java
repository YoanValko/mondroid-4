package com.example.icslauncher.apps;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.icslauncher.R;

/**
 * Reimplementation of the ICS Camera. Delegates image capture to the system camera app via
 * {@link MediaStore#ACTION_IMAGE_CAPTURE} and displays the result. Requires the CAMERA
 * permission, requested at runtime. Photos are written to MediaStore under scoped storage.
 */
public class ICSCameraActivity extends AppCompatActivity {

    private static final int REQ_CAMERA_PERMISSION = 1003;
    private static final int REQ_CAPTURE_IMAGE = 1004;

    private ImageView photoView;
    private Uri capturedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);

        TextView hint = new TextView(this);
        hint.setText("Tap Capture to take a photo with the system camera.");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 24);
        root.addView(hint);

        photoView = new ImageView(this);
        photoView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoView.setImageResource(R.drawable.ic_camera_placeholder);
        root.addView(photoView);

        Button capture = new Button(this);
        capture.setText("Capture");
        capture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CAMERA},
                            REQ_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
            } else {
                openCamera();
            }
        });
        root.addView(capture);

        setContentView(root);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    if (thumbnail != null) photoView.setImageBitmap(thumbnail);
                }
                if (capturedPhotoUri != null) {
                    photoView.setImageURI(capturedPhotoUri);
                }
                Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Capture cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,
                "ICS_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/ICS Camera");

        capturedPhotoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedPhotoUri);
        startActivityForResult(intent, REQ_CAPTURE_IMAGE);
    }
}
