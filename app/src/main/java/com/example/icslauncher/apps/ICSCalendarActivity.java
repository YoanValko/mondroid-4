package com.example.icslauncher.apps;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

/**
 * Reimplementation of a Nexus-era Notes app. Stores notes in a local SQLite database
 * (no network, no proprietary code).
 */
public class ICSCalendarActivity extends AppCompatActivity {

    // Placeholder: the real calendar integration requires system permissions.
    // This opens the app drawer so the user can launch the system Calendar app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);

        android.widget.TextView hint = new android.widget.TextView(this);
        hint.setText("Calendar is handled by the system Calendar app.");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 16);
        root.addView(hint);

        Button open = new Button(this);
        open.setText("Open app drawer");
        open.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this,
                    com.example.icslauncher.drawer.AppDrawerActivity.class);
            startActivity(intent);
        });
        root.addView(open);

        setContentView(root);
    }
}
