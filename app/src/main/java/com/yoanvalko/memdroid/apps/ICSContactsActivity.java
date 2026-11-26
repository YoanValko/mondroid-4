package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yoanvalko.memdroid.R;

/**
 * Reimplementation of the ICS Contacts entry point. Reads contacts only through the system picker
 * (which grants a temporary URI permission), so no READ_CONTACTS permission is required.
 */
public class ICSContactsActivity extends AppCompatActivity {

    private static final int REQ_PICK_CONTACT = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);

        TextView hint = new TextView(this);
        hint.setText("Contacts are provided by the system Contacts app.");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 16);
        root.addView(hint);

        Button pick = new Button(this);
        pick.setText("Pick a contact (system picker)");
        pick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQ_PICK_CONTACT);
        });
        root.addView(pick);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(contactUri,
                            new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                            null, null, null);
                    String name = (c != null && c.moveToFirst())
                            ? c.getString(0) : "Unknown";
                    Toast.makeText(this, "Selected: " + name, Toast.LENGTH_SHORT).show();
                } finally {
                    if (c != null) c.close();
                }
            }
        }
    }
}
