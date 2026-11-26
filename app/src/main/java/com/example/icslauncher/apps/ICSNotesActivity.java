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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

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
        NotesDbHelper(ICSNotesActivity context) {
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

        TextView title = new TextView(this);
        title.setText("Notes");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        titleInput = new EditText(this);
        titleInput.setHint("Title");
        titleInput.setTextSize(18);
        titleInput.setPadding(8, 8, 8, 8);
        root.addView(titleInput);

        bodyInput = new EditText(this);
        bodyInput.setHint("Body");
        bodyInput.setHintTextColor(0xFF999999);
        bodyInput.setTextSize(18);
        bodyInput.setPadding(8, 8, 8, 8);
        bodyInput.setMinLines(3);
        root.addView(bodyInput);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save");
        saveBtn.setTextSize(16);
        saveBtn.setOnClickListener(v -> saveNote());
        root.addView(saveBtn);

        TextView listHint = new TextView(this);
        listHint.setText("Your notes:");
        listHint.setTextSize(14);
        listHint.setPadding(0, 16, 0, 8);
        root.addView(listHint);

        notesList = new ListView(this);
        root.addView(notesList, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);

        NotesDbHelper dbHelper = new NotesDbHelper(this);
        db = dbHelper.getWritableDatabase();
        loadNotes();
    }

    private void saveNote() {
        String title = titleInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
