package com.example.icslauncher.apps;

import android.content.ContentValues;
import android.content.Context;
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
public class ICSNotesActivity extends AppCompatActivity {

    private static final String DB_NAME = "ics_notes.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;
    private EditText titleInput;
    private EditText bodyInput;
    private ListView notesList;

    private static final class NotesDbHelper extends SQLiteOpenHelper {
        NotesDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        android.widget.TextView hint = new android.widget.TextView(this);
        hint.setText("Create a new note:");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 8);
        root.addView(hint);

        titleInput = new EditText(this);
        titleInput.setHint("Title");
        root.addView(titleInput);

        bodyInput = new EditText(this);
        bodyInput.setHint("Body");
        bodyInput.setLines(3);
        root.addView(bodyInput);

        Button save = new Button(this);
        save.setText("Save");
        save.setOnClickListener(v -> saveNote());
        root.addView(save);

        android.widget.TextView listHint = new android.widget.TextView(this);
        listHint.setText("Your notes:");
        listHint.setTextSize(14);
        listHint.setPadding(0, 16, 0, 8);
        root.addView(listHint);

        notesList = new ListView(this);
        root.addView(notesList, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);

        // Initialize database.
        NotesDbHelper dbHelper = new NotesDbHelper(this);
        db = dbHelper.getWritableDatabase();
        loadNotes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }

    private void saveNote() {
        String title = titleInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
            Toast.makeText(this, "Enter a title or body", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_NOTES, null, values);
        titleInput.setText("");
        bodyInput.setText("");
        loadNotes();
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
    }

    private void loadNotes() {
        Cursor c = db.query(TABLE_NOTES,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_BODY},
                null, null, null, null, COLUMN_TIMESTAMP + " DESC");
        String[] from = {COLUMN_TITLE, COLUMN_BODY};
        int[] to = {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, c, from, to, 0);
        notesList.setAdapter(adapter);
    }
}
