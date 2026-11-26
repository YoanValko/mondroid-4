package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

/**
 * Reimplementation of the ICS Email compose experience. Because there is no mail backend bundled,
 * "Send" hands the message to the user's chosen email app via a mailto: intent (sandbox-safe).
 */
public class ISEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 8, 16, 8);

        TextView hint = new TextView(this);
        hint.setText("Compose a message and tap Send to open your email app.");
        hint.setTextSize(14);
        root.addView(hint);

        final EditText to = new EditText(this);
        to.setHint("To");
        root.addView(to);

        final EditText subject = new EditText(this);
        subject.setHint("Subject");
        root.addView(subject);

        final EditText body = new EditText(this);
        body.setHint("Body");
        body.setLines(4);
        root.addView(body);

        Button send = new Button(this);
        send.setText("Send");
        send.setOnClickListener(v -> {
            String toAddr = to.getText().toString().trim();
            if (toAddr.isEmpty()) {
                Toast.makeText(this, "Enter a recipient", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:" + toAddr));
            email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
            email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
            if (email.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(email, "Send via"));
            } else {
                Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(send);

        setContentView(root);
    }
}
