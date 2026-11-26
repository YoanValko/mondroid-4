package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

public class ICSCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);

        TextView hint = new TextView(this);
        hint.setText("Calendar is handled by the system Calendar app.");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 16);
        root.addView(hint);

        Button open = new Button(this);
        open.setText("Open app drawer");
        open.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this,
                    com.yoanvalko.memdroid.drawer.AppDrawerActivity.class);
            startActivity(intent);
        });
        root.addView(open);

        setContentView(root);
    }
}
