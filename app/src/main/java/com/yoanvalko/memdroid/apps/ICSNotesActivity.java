package com.yoanvalko.memdroid.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.util.Date;

/**
 * Simple notes app.
 */
public class ICSNotesActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText bodyInput;
    private TextView notesView;

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
        bodyInput.setHint("Note text");
        bodyInput.setHintTextColor(0xFF999999);
        bodyInput.setTextSize(16);
        bodyInput.setPadding(8, 8, 8, 8);
        bodyInput.setMinLines(4);
        root.addView(bodyInput);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save");
        saveBtn.setTextSize(16);
        saveBtn.setOnClickListener(v -> saveNote());
        root.addView(saveBtn);

        notesView = new TextView(this);
        notesView.setTextSize(14);
        notesView.setPadding(0, 16, 0, 0);
        root.addView(notesView);

        setContentView(root);
    }

    private void saveNote() {
        String title = titleInput.getText().toString().trim();
        String body = bodyInput.getText().toString().trim();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
            return;
        }
        String timestamp = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());
        String note = "[" + timestamp + "] " + title + "\n" + body + "\n\n";
        notesView.setText(note + notesView.getText());
        titleInput.setText("");
        bodyInput.setText("");
    }
}
