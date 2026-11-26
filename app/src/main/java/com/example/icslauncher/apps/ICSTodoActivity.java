package com.example.icslauncher.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple todo list with add, delete, and clear functionality.
 */
public class ICSTodoActivity extends AppCompatActivity {

    private LinearLayout root;
    private LinearLayout tasksContainer;
    private Button addButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Todo List");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);

        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("New task");
        input.setHintTextColor(0xFF999999);
        input.setTextSize(18);
        input.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        inputRow.addView(input, inputParams);

        Button addBtn = new Button(this);
        addBtn.setText("Add");
        addBtn.setTextSize(16);
        addBtn.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                addTask(text);
                input.setText("");
            }
        });
        inputRow.addView(addBtn);

        root.addView(inputRow);

        Button clearBtn = new Button(this);
        clearBtn.setText("Clear All");
        clearBtn.setTextSize(16);
        clearBtn.setOnClickListener(v -> {
            tasksContainer.removeAllViews();
        });
        root.addView(clearBtn);

        tasksContainer = new LinearLayout(this);
        tasksContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(tasksContainer);

        setContentView(root);
    }

    private void addTask(String text) {
        LinearLayout taskRow = new LinearLayout(this);
        taskRow.setOrientation(LinearLayout.HORIZONTAL);
        taskRow.setPadding(4, 4, 4, 4);

        android.widget.CheckBox cb = new android.widget.CheckBox(this);
        cb.setText(text);
        cb.setTextSize(16);
        cb.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams cbParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        taskRow.addView(cb, cbParams);

        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setTextSize(12);
        deleteBtn.setOnClickListener(v -> tasksContainer.removeView(taskRow));
        taskRow.addView(deleteBtn);

        tasksContainer.addView(taskRow);
    }
}
