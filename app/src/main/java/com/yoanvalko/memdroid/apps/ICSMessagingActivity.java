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

public class ICSMessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView hint = new TextView(this);
        hint.setText("Compose a message and tap Send to open your SMS app.");
        hint.setTextSize(14);
        root.addView(hint);

        final EditText to = new EditText(this);
        to.setHint("Phone number");
        root.addView(to);

        final EditText body = new EditText(this);
        body.setHint("Message");
        body.setLines(4);
        root.addView(body);

        Button send = new Button(this);
        send.setText("Send");
        send.setOnClickListener(v -> {
            String addr = to.getText().toString().trim();
            if (addr.isEmpty()) {
                Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent sms = new Intent(Intent.ACTION_SENDTO);
            sms.setData(android.net.Uri.parse("smsto:" + addr));
            sms.putExtra("sms_body", body.getText().toString());
            if (sms.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(sms, "Send via"));
            } else {
                Toast.makeText(this, "No messaging app installed", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(send);

        setContentView(root);
    }
}
